/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import lombok.Data;

/**
 * View entity for retrieving payments by supplier.
 * 
 * @author danieldiehl
 */
@Data
public class ListPaymentsSummary {
	private String supplierId;
	private LocalDate paymentDate;
	private BigDecimal paymentAmount;
	private BigInteger numberOfInvoicesPaid;
}
