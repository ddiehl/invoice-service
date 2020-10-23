/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.repository;

import java.util.Optional;

import org.springframework.data.repository.Repository;

import com.diehl.invoice.domain.InvoiceSummary;

/**
 * DAO for retrieving Invoice summary information for supplier.
 * 
 * @author danieldiehl
 */
@org.springframework.stereotype.Repository
public interface InvoiceSummaryRepository extends Repository<InvoiceSummary, String> {

	/**
	 * Retrieves the content by Id.
	 * 
	 * @param supplierId for the search
	 * @return Optional with the object if found
	 */
	public Optional<InvoiceSummary> findById(String supplierId);
	
}
