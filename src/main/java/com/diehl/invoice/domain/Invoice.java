/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Database domain class for Invoice.
 * 
 * @author danieldiehl
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
@IdClass(InvoiceId.class)
public class Invoice {

	@Id
	private String invoiceId;

	@Id
	private String supplierId;

	@Column(nullable = false)
	public LocalDate invoiceDate;

	@Column(nullable = false)
	public BigDecimal invoiceAmount;

	@Column(nullable = false)
	private int terms;

	@Column
	private LocalDate paymentDate;

	@Column
	private BigDecimal paymentAmount;
}
