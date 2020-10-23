/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.diehl.invoice.domain.Invoice;
import com.diehl.invoice.domain.InvoiceId;

/**
 * DAO for Invoice.
 * 
 * @author danieldiehl
 */
@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, InvoiceId> {
	
}
