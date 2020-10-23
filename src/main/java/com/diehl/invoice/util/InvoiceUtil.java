/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.util;

import java.time.LocalDate;

import com.diehl.invoice.domain.Invoice;

/**
 * TODO Class description
 * 
 * @author danieldiehl
 */
public class InvoiceUtil {

	public static String getInvoiceStatus(Invoice invoice) {
		//Late
		if (invoice.getInvoiceDate().plusDays(invoice.getTerms()).isBefore(LocalDate.now())) {
			//Not paid
			if (invoice.getPaymentAmount() == null || invoice.getInvoiceAmount().compareTo(invoice.getPaymentAmount()) == 1) {
				return "Late";
			} else {
				return "Closed";
			}
		} else {
			if (invoice.getPaymentDate() != null && invoice.getPaymentDate().isBefore(LocalDate.now())) { 
				return "Payment Scheduled";
			} else {
				return "Open";
			}
		}
	}
	
}
