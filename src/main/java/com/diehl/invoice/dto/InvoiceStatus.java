/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.dto;

import lombok.Getter;

/**
 * Enum indicating possible invoice statuses.
 * 
 * @author danieldiehl
 */
public enum InvoiceStatus {

	OPEN("Open"),
	LATE("Late"),
	CLOSED("Closed"),
	SCHEDULED("Payment Scheduled");
	
	@Getter
	private String text;
	
	
	/**
	 * Constructor.
	 * 
	 * @param text to be displayed in the response.
	 */
	private InvoiceStatus(String text) {
		this.text = text;
	}
}
