/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Unique Key for invoice.
 * Used to validate duplication and return error response
 * 
 * @author danieldiehl
 */
@Data
@AllArgsConstructor
public class InvoiceKey {
	private String supplierId;
	private String invoiceId;
	
	@Override
	public String toString() {
		return "supplierId=" + supplierId + ", invoiceId=" + invoiceId;
	}
}
