/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.controller;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.diehl.invoice.dto.InvoiceKey;
import com.diehl.invoice.exception.DuplicateEntriesUploadException;
import com.diehl.invoice.exception.SupplierNotFoundException;

/**
 * Handles the exceptions in the controller.
 * 
 * @author danieldiehl
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

	/**
	 * Handles Duplicate Exception.
	 * 
	 * @param ex exception
	 * @return formatted response
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(value = DuplicateEntriesUploadException.class)
	public String handleDuplicateException(DuplicateEntriesUploadException ex) {
		return  ex.getIds().stream().map(InvoiceKey::toString).collect(Collectors.joining("\n")); 
	}
	
	/**
	 * Handles any other exception unexpected.
	 * 
	 * @param t exception
	 * @return error message
	 */
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(value = SupplierNotFoundException.class)
	public String handleSupplierNotFoundExceptions(SupplierNotFoundException t) {
		return "Supplier not found";
	}
	
	/**
	 * Handles any other exception unexpected.
	 * 
	 * @param t exception
	 * @return error message
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(value = Exception.class)
	public String handleUnexpectedExceptions(Exception t) {
		return "Error processing request";
	}
	
}
