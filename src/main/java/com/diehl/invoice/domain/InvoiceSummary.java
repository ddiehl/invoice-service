/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Subselect;
import org.springframework.data.annotation.Immutable;

import lombok.Data;

/**
 * View entity for retrieving summary information per supplier.
 * 
 * @author danieldiehl
 */
@Data
@Entity
@Immutable
@Subselect(
"""
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
"""
)
public class InvoiceSummary {

	@Id
	private String supplierId;
	private long totalInvoices;
	private long openInvoices;
	private long lateInvoices;
	private BigDecimal totalOpenInvoices;
	private BigDecimal totalLateInvoices;
	
}
