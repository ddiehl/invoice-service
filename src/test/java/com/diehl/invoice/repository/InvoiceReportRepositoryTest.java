/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.diehl.invoice.SpringEnabledTest;
import com.diehl.invoice.domain.Invoice;

/**
 * Unit tests for invoice summary repository.
 * 
 * @author danieldiehl
 */
public class InvoiceReportRepositoryTest extends SpringEnabledTest {

	@Autowired
	private InvoiceReportRepository summaryRepo;

	@Autowired
	private InvoiceRepository invoiceRepo;

	/**
	 * Test scenario where there is no data in the DB for given supplier
	 */
	@Test
	public void testRetrieveSupplierSummaryNoData() {
		var supplierId = "1";
		var resp = summaryRepo.retrieveSupplierSummary(supplierId);
		assertFalse(resp.isPresent());
	}

	/**
	 * Scenario with data.
	 */
	@Test
	public void testRetrieveSupplierSummaryWithData() {
		var supplierId = "sup_1";

		// @formatter:off
		//Open
		invoiceRepo.save(
			Invoice.builder()
				.invoiceAmount(BigDecimal.valueOf(120))
				.invoiceDate(LocalDate.now())
				.invoiceId("1")
				.supplierId(supplierId)
				.terms(20)
				.build()
			);

		//Closed
		invoiceRepo.save(
			Invoice.builder()
				.invoiceAmount(BigDecimal.TEN)
				.invoiceDate(LocalDate.now())
				.invoiceId("2")
				.paymentAmount(BigDecimal.TEN)
				.paymentDate(LocalDate.now())
				.supplierId(supplierId)
				.terms(1)
				.build()
			);
		
		//Late
		invoiceRepo.save(
			Invoice.builder()
				.invoiceAmount(BigDecimal.valueOf(20))
				.invoiceDate(LocalDate.now().minus(10, ChronoUnit.DAYS))
				.invoiceId("3")
				.paymentAmount(BigDecimal.TEN)
				.paymentDate(LocalDate.now())
				.supplierId(supplierId)
				.terms(2)
				.build()
			);
		
		//Another supplier
		invoiceRepo.save(
			Invoice.builder()
				.invoiceAmount(BigDecimal.valueOf(20))
				.invoiceDate(LocalDate.now().minus(10, ChronoUnit.DAYS))
				.invoiceId("1")
				.paymentAmount(BigDecimal.TEN)
				.paymentDate(LocalDate.now())
				.supplierId("sup_2")
				.terms(1)
				.build()
			);
		// @formatter:on

		invoiceRepo.findAll().forEach(System.out::println);
		
		var resp = summaryRepo.retrieveSupplierSummary(supplierId).get();
		assertEquals(supplierId, resp.getSupplierId());
		assertEquals(3, resp.getTotalInvoices());
		assertEquals(2, resp.getOpenInvoices());
		assertEquals(1, resp.getLateInvoices());
		assertEquals(20d, resp.getTotalLateInvoices().doubleValue());
		assertEquals(130d, resp.getTotalOpenInvoices().doubleValue());
	}
	
	@Test
	public void testClosedPaymentsSummaryBySupplier() {
		var supplierId = "sup_1";
		
		// @formatter:off
		//Open
		invoiceRepo.save(
			Invoice.builder()
				.invoiceAmount(BigDecimal.valueOf(120))
				.invoiceDate(LocalDate.now())
				.invoiceId("1")
				.supplierId(supplierId)
				.terms(20)
				.build()
			);

		//Closed
		invoiceRepo.save(
			Invoice.builder()
				.invoiceAmount(BigDecimal.TEN)
				.invoiceDate(LocalDate.now())
				.invoiceId("2")
				.paymentAmount(BigDecimal.TEN)
				.paymentDate(LocalDate.now().minusDays(1))
				.supplierId(supplierId)
				.terms(1)
				.build()
			);
		
		//Closed
		invoiceRepo.save(
			Invoice.builder()
				.invoiceAmount(BigDecimal.valueOf(10))
				.invoiceDate(LocalDate.now().minus(10, ChronoUnit.DAYS))
				.invoiceId("3")
				.paymentAmount(BigDecimal.TEN)
				.paymentDate(LocalDate.now())
				.supplierId(supplierId)
				.terms(2)
				.build()
			);
		
		//Closed
		invoiceRepo.save(
				Invoice.builder()
					.invoiceAmount(BigDecimal.valueOf(115.15))
					.invoiceDate(LocalDate.now().minus(10, ChronoUnit.DAYS))
					.invoiceId("4")
					.paymentAmount(BigDecimal.valueOf(115.15))
					.paymentDate(LocalDate.now())
					.supplierId(supplierId)
					.terms(2)
					.build()
				);
		
		//Another supplier
		invoiceRepo.save(
			Invoice.builder()
				.invoiceAmount(BigDecimal.valueOf(10))
				.invoiceDate(LocalDate.now().minus(10, ChronoUnit.DAYS))
				.invoiceId("1")
				.paymentAmount(BigDecimal.TEN)
				.paymentDate(LocalDate.now())
				.supplierId("sup_2")
				.terms(1)
				.build()
			);
		// @formatter:on
		
		var summary = summaryRepo.retrieveClosedPaymentSummaryBySupplierId(supplierId);
		assertEquals(2, summary.size());
		assertEquals(supplierId, summary.get(0).getSupplierId());
		assertEquals(LocalDate.now(), summary.get(0).getPaymentDate());
		assertEquals(2, summary.get(0).getNumberOfInvoicesPaid().intValue());
		assertEquals(BigDecimal.valueOf(125.15), summary.get(0).getPaymentAmount());
		
		assertEquals(supplierId, summary.get(1).getSupplierId());
		assertEquals(LocalDate.now().minusDays(1), summary.get(1).getPaymentDate());
		assertEquals(1, summary.get(1).getNumberOfInvoicesPaid().intValue());
		assertEquals(BigDecimal.valueOf(10.00).setScale(2), summary.get(1).getPaymentAmount());

		
		summary = summaryRepo.retrieveClosedPaymentSummaryBySupplierId("sup_2");
		assertEquals(1, summary.size());
		assertEquals("sup_2", summary.get(0).getSupplierId());
		assertEquals(LocalDate.now(), summary.get(0).getPaymentDate());
		assertEquals(1, summary.get(0).getNumberOfInvoicesPaid().intValue());
		assertEquals(BigDecimal.valueOf(10.00).setScale(2), summary.get(0).getPaymentAmount());

	}
}
