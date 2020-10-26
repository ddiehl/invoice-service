/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.util;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.diehl.invoice.domain.Invoice;
import com.diehl.invoice.dto.InvoiceStatus;

/**
 * Units test For invoice util.
 * 
 * @author danieldiehl
 */
public class InvoiceUtilTest {

	@Test	
	public void testGetInvoiceStatus() {
		
		//not due, not paid. Should show as open
		var invoice = new Invoice();
		invoice.setInvoiceDate(LocalDate.now().minusDays(10));
		invoice.setTerms(15);
		invoice.setInvoiceAmount(BigDecimal.TEN);
		assertEquals(InvoiceStatus.OPEN, InvoiceUtil.getInvoiceStatus(invoice));
		
		//due today, not paid. Should show as open
		invoice = new Invoice();
		invoice.setInvoiceDate(LocalDate.now().minusDays(10));
		invoice.setTerms(10);
		invoice.setInvoiceAmount(BigDecimal.TEN);
		assertEquals(InvoiceStatus.OPEN, InvoiceUtil.getInvoiceStatus(invoice));
		
		//not due, partial payment. Should show as open
		invoice = new Invoice();
		invoice.setInvoiceDate(LocalDate.now().minusDays(10));
		invoice.setTerms(15);
		invoice.setInvoiceAmount(BigDecimal.TEN);
		invoice.setPaymentAmount(BigDecimal.valueOf(5));
		invoice.setPaymentDate(LocalDate.now().minusDays(1));
		assertEquals(InvoiceStatus.OPEN, InvoiceUtil.getInvoiceStatus(invoice));
		
		//Paid yesterday, should be CLOSED
		invoice = new Invoice();
		invoice.setInvoiceDate(LocalDate.now().minusDays(10));
		invoice.setTerms(15);
		invoice.setInvoiceAmount(BigDecimal.TEN);
		invoice.setPaymentAmount(BigDecimal.TEN);
		invoice.setPaymentDate(LocalDate.now().minusDays(1));
		assertEquals(InvoiceStatus.CLOSED, InvoiceUtil.getInvoiceStatus(invoice));
		
		//Paid today, should be CLOSED
		invoice = new Invoice();
		invoice.setInvoiceDate(LocalDate.now().minusDays(10));
		invoice.setTerms(15);
		invoice.setInvoiceAmount(BigDecimal.TEN);
		invoice.setPaymentAmount(BigDecimal.TEN);
		invoice.setPaymentDate(LocalDate.now());
		assertEquals(InvoiceStatus.CLOSED, InvoiceUtil.getInvoiceStatus(invoice));
		
		//Future
		invoice = new Invoice();
		invoice.setInvoiceDate(LocalDate.now().minusDays(10));
		invoice.setTerms(15);
		invoice.setInvoiceAmount(BigDecimal.TEN);
		invoice.setPaymentAmount(BigDecimal.TEN);
		invoice.setPaymentDate(LocalDate.now().plusDays(5));
		assertEquals(InvoiceStatus.SCHEDULED, InvoiceUtil.getInvoiceStatus(invoice));

		//LATE no Payment
		invoice = new Invoice();
		invoice.setInvoiceDate(LocalDate.now().minusDays(10));
		invoice.setTerms(5);
		invoice.setInvoiceAmount(BigDecimal.TEN);
		assertEquals(InvoiceStatus.LATE, InvoiceUtil.getInvoiceStatus(invoice));
		
		//LATE Partial payment
		invoice = new Invoice();
		invoice.setInvoiceDate(LocalDate.now().minusDays(10));
		invoice.setTerms(5);
		invoice.setInvoiceAmount(BigDecimal.TEN);
		invoice.setPaymentAmount(BigDecimal.valueOf(2));
		invoice.setPaymentDate(LocalDate.now());
		assertEquals(InvoiceStatus.LATE, InvoiceUtil.getInvoiceStatus(invoice));
	}
	
}
