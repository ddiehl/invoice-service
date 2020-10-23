/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import lombok.Data;

/**
 * Data Transfer Object for Converting from CSV file into bean.
 * 
 * @author danieldiehl
 */
@Data
public class InvoiceCsvEntry {

	@CsvBindByName(column="Invoice Id")
    private String invoiceId;

	@CsvBindByName(column="Supplier Id")
    private String supplierId;

	@CsvDate(value = "yyy-MM-dd")
	@CsvBindByName(column="Invoice Date")
    private LocalDate invoiceDate;

	@CsvBindByName(column="Invoice Amount")
    private BigDecimal invoiceAmount;

	@CsvBindByName(column="Terms")
    private int terms;

	@CsvDate(value = "yyy-MM-dd")
	@CsvBindByName(column="Payment Date")
    private LocalDate paymentDate;

	@CsvBindByName(column="Payment Amount")
    private BigDecimal paymentAmount;
}
