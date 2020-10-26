/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * View entity for retrieving summary information per supplier.
 * 
 * @author danieldiehl
 */
@Data
public class SupplierSummary {

	private String supplierId;
	private long totalInvoices;
	private long openInvoices;
	private long lateInvoices;
	private BigDecimal totalOpenInvoices;
	private BigDecimal totalLateInvoices;
}
