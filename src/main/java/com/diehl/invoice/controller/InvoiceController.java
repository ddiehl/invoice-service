/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.diehl.invoice.domain.InvoiceSummary;
import com.diehl.invoice.dto.InvoiceInfo;
import com.diehl.invoice.service.InvoiceService;

/**
 * Invoice controller class.
 * 
 * @author danieldiehl
 */
@RestController
public class InvoiceController {
	
	@Autowired
	private InvoiceService service;
	
	/**
	 * endpoint for uploading a file.
	 * 
	 * @param file uploaded
	 * @throws Exception in case anything fails
	 */
	@PostMapping("/upload")
	public void uploadFile(MultipartFile file) throws Exception {
		service.loadInvoices(file.getResource());
	}

	
	/**
	 * Retrieves Summary of invoices for a given supplier.
	 * 
	 * @param supplierId id for the search
	 * @return invoices if found
	 * @throws Exception
	 */
	@GetMapping("/ListSupplierSummary")
	public InvoiceSummary getinvoiceSummary(String supplierId) throws Exception {
		return service.retrieveSupplierSummary(supplierId);
	}
	
	/**
	 * Retrieves Invoice info for a given supplier.
	 * 
	 * @param supplierId id for the search
	 * @return invoices if found
	 * @throws Exception
	 */
	@GetMapping("/ListInvoiceSummary")
	public InvoiceInfo getinvoiceInfo(String supplierId, String invoiceId) throws Exception {
		return service.retrieveInvoiceSummaryById(invoiceId, supplierId);
	}
}
