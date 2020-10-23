/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.exception;

import java.util.Set;

import com.diehl.invoice.dto.InvoiceKey;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Exception for when there is duplicate entries in the upload file.
 * 
 * @author danieldiehl
 */
@AllArgsConstructor
public class DuplicateEntriesUploadException extends Exception {
	private static final long serialVersionUID = -2879369250875962480L;

	@Getter
	private Set<InvoiceKey> ids;
	
}
