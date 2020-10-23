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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import com.diehl.invoice.SpringEnabledTest;
import com.diehl.invoice.domain.Invoice;
import com.diehl.invoice.domain.InvoiceId;

/**
 * Unit test the invoice repo.
 * 
 * @author danieldiehl
 */
@AutoConfigureTestDatabase(replace = Replace.ANY)
public class InvoiceRepositoryTest extends SpringEnabledTest {

	@Autowired
	private InvoiceRepository invoiceRepo;

	
	/**
	 * Scenario to add only unique invoices, multiple suppliers.
	 */
	@Test
	public void testInsertScenario() {

		var supplierId = "sup_1";

		// @formatter:off
		var invoice1 = 
			Invoice.builder()
				.invoiceAmount(BigDecimal.valueOf(120, 2))
				.invoiceDate(LocalDate.now())
				.invoiceId("1")
				.paymentAmount(BigDecimal.valueOf(10, 2))
				.paymentDate(LocalDate.now())
				.supplierId(supplierId)
				.terms(1)
				.build();
		invoiceRepo.save(invoice1);

		var invoice2 = 
			Invoice.builder()
				.invoiceAmount(BigDecimal.valueOf(10, 2))
				.invoiceDate(LocalDate.now())
				.invoiceId("2")
				.paymentAmount(BigDecimal.valueOf(10, 2))
				.paymentDate(LocalDate.now())
				.supplierId(supplierId)
				.terms(1)
				.build();
		invoiceRepo.save(invoice2);
		
		var invoice3 = 
			Invoice.builder()
				.invoiceAmount(BigDecimal.valueOf(20, 2))
				.invoiceDate(LocalDate.now().minus(10, ChronoUnit.DAYS))
				.invoiceId("3")
				.paymentAmount(BigDecimal.valueOf(10, 2))
				.paymentDate(LocalDate.now())
				.supplierId(supplierId)
				.terms(2)
				.build();
		invoiceRepo.save(invoice3);
		
		var invoice4 = 
			Invoice.builder()
				.invoiceAmount(BigDecimal.valueOf(20, 2))
				.invoiceDate(LocalDate.now().minus(10, ChronoUnit.DAYS))
				.invoiceId("1")
				.paymentAmount(BigDecimal.valueOf(10, 2))
				.paymentDate(LocalDate.now())
				.supplierId("sup_2")
				.terms(1)
				.build();
		invoiceRepo.save(invoice4);
		// @formatter:on

		assertEquals(4, invoiceRepo.count());
		assertEquals(invoice1, invoiceRepo.findById(new InvoiceId("1", supplierId)).get());
		assertEquals(invoice2, invoiceRepo.findById(new InvoiceId("2", supplierId)).get());
		assertEquals(invoice3, invoiceRepo.findById(new InvoiceId("3", supplierId)).get());
		assertEquals(invoice4, invoiceRepo.findById(new InvoiceId("1", "sup_2")).get());
	}

	@Test
	public void testUpdateScenario() {
		var supplierId = "sup_1";

		// @formatter:off
		var invoice = 
			Invoice.builder()
				.invoiceAmount(BigDecimal.valueOf(120, 2))
				.invoiceDate(LocalDate.now())
				.invoiceId("1")
				.paymentAmount(BigDecimal.valueOf(10, 2))
				.paymentDate(LocalDate.now())
				.supplierId(supplierId)
				.terms(1)
				.build();
		invoiceRepo.save(invoice);
		
		//Asserts
		assertEquals(1, invoiceRepo.count());
		assertEquals(invoice, invoiceRepo.findById(new InvoiceId("1", supplierId)).get());
		
		//now save again and make sure still new values are there
		invoice = Invoice.builder()
					.invoiceAmount(BigDecimal.valueOf(5, 2))
					.invoiceDate(LocalDate.now().minusDays(10))
					.invoiceId("1")
					.paymentAmount(BigDecimal.valueOf(40, 2))
					.paymentDate(LocalDate.now().plusDays(12))
					.supplierId(supplierId)
					.terms(12)
					.build();
		invoiceRepo.save(invoice);

		//Asserts
		assertEquals(invoice, invoiceRepo.findById(new InvoiceId("1", supplierId)).get());
	}
	
}
