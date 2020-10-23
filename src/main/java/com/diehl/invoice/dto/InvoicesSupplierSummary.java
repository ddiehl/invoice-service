/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * Data transfer object for Summary.
 * 
 * @author danieldiehl
 */
@Data
@Builder
public class InvoicesSupplierSummary {
    private long totalInvoices;
    private long openInvoices;
    private long lateInvoices;
    private BigDecimal totalOpenInvoiceAmount;
    private BigDecimal totalLateInvoiceAmount;
}
