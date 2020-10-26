/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.util;

import java.time.LocalDate;

import com.diehl.invoice.domain.Invoice;
import com.diehl.invoice.dto.InvoiceStatus;

/**
 * Utility methods for Invoice
 * 
 * @author danieldiehl
 */
public final class InvoiceUtil {

	/**
	 * Prevents instantiation.
	 */
	private InvoiceUtil() {
		//NOOP
	}
	
	/**
	 * Discover invoice status for a given Invoice.
	 * 
	 * @param invoice to figure out status
	 * @return Status
	 */
	public static InvoiceStatus getInvoiceStatus(Invoice invoice) {
		//SCHEDULED
		if (invoice.getPaymentDate() != null 
				&& invoice.getPaymentDate().isAfter(LocalDate.now())
				&& invoice.getPaymentAmount() != null) {
			return InvoiceStatus.SCHEDULED;
		}

		//CLOSED
		if (invoice.getPaymentAmount() != null 
				&& invoice.getInvoiceAmount().compareTo(invoice.getPaymentAmount()) <= 0) {
			return InvoiceStatus.CLOSED;
		}
		
		if (invoice.getInvoiceDate().plusDays(invoice.getTerms()).isBefore(LocalDate.now())) {
			return InvoiceStatus.LATE;
		} else {
			return InvoiceStatus.OPEN;
		}
	}
	
}
