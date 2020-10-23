/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Class create to set composite key into invoices
 * 
 * @author danieldiehl
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InvoiceId implements Serializable {
	private static final long serialVersionUID = 6721524477292755884L;

	private String invoiceId;
	private String supplierId;

}
