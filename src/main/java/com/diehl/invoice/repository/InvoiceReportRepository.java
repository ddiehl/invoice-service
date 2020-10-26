/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.sparta.springwebutils.jdbc.SpartaNamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import com.diehl.invoice.dto.ListPaymentsSummary;
import com.diehl.invoice.dto.SupplierSummary;

/**
 * DAO for retrieving list of payments by supplier
 * 
 * @author danieldiehl
 */
@Repository
public class InvoiceReportRepository {

	private static final String LIST_BY_SUPPLIER = """
			SELECT SUPPLIER_ID, PAYMENT_DATE, sum(payment_amount) AS payment_amount, count(1) AS number_of_invoices_paid
			 FROM invoice
			 WHERE INVOICE_AMOUNT <= PAYMENT_AMOUNT
			   AND PAYMENT_DATE <= now()
			   AND SUPPLIER_ID = :supplierId
			 GROUP BY supplier_id, payment_date
			 ORDER BY PAYMENT_DATE desc
			""";

	private static final String SUPPLIER_SUMMARY = """
			SELECT i.supplier_id, i.val AS total_invoices, COALESCE(oi.val, 0) AS open_invoices, COALESCE (li.val, 0) AS late_invoices, COALESCE (toi.val) AS total_open_invoices, COALESCE (tli.val, 0) AS total_late_invoices
			FROM
			    (SELECT supplier_id, count(1) AS val FROM invoice GROUP BY supplier_id) i
			    LEFT OUTER join
				    (SELECT supplier_id, COUNT(1) AS val FROM invoice WHERE COALESCE(payment_amount, 0) < invoice_amount GROUP BY supplier_id) oi
				ON i.SUPPLIER_ID = oi.supplier_id
			    LEFT OUTER JOIN
			    	(SELECT supplier_id, COUNT(1) AS val FROM invoice WHERE COALESCE(payment_amount, 0) < invoice_amount AND invoice_date + terms < now() GROUP BY supplier_id) li
			    ON i.SUPPLIER_ID = li.supplier_id
			    LEFT OUTER JOIN
			    	(SELECT supplier_id, SUM(invoice_amount - COALESCE(payment_amount, 0)) AS val FROM invoice WHERE COALESCE(payment_amount, 0) < invoice_amount GROUP BY supplier_id) toi
			    ON i.SUPPLIER_ID = toi.supplier_id
			    LEFT OUTER JOIN
			    	(SELECT supplier_id, SUM(COALESCE(invoice_amount, 0)) AS val FROM invoice WHERE COALESCE(payment_amount, 0) < invoice_amount AND invoice_date + terms < now() GROUP BY supplier_id) tli
			    ON i.SUPPLIER_ID = tli.supplier_id
			where i.supplier_id = :supplierId;
			""";

	@Autowired
	private SpartaNamedParameterJdbcTemplate namedJdbcTemplate;

	/**
	 * Retrieves list of closed payments by supplier id.
	 * 
	 * @param supplierId for the search
	 * @return Optional with the object if found
	 */
	public List<ListPaymentsSummary> retrieveClosedPaymentSummaryBySupplierId(String supplierId) {
		return namedJdbcTemplate.query(LIST_BY_SUPPLIER, Map.of("supplierId", supplierId),
				new BeanPropertyRowMapper<>(ListPaymentsSummary.class));
	}

	/**
	 * Retrieves supplier summary.
	 * 
	 * @param supplierId id for the search
	 * @return optional of summary
	 */
	public Optional<SupplierSummary> retrieveSupplierSummary(String supplierId) {
		return namedJdbcTemplate.queryForOptionalObject(SUPPLIER_SUMMARY, Map.of("supplierId", supplierId),
				new BeanPropertyRowMapper<>(SupplierSummary.class));
	}
}
