/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

/**
 * Invoice info for ListInvoiceSummary.
 * 
 * @author danieldiehl
 */
@Data
public class InvoiceInfo {

    private String invoiceId;
    private String supplierId;
    private String status;
    private LocalDate invoiceDate;
    private BigDecimal invoiceAmount;
    private BigDecimal invoiceOpenBalance;
    private LocalDate dueDate;
    private Integer daysPastDue;
    private LocalDate paymentDate;
    private BigDecimal paymentAmount;
}
