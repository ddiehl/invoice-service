/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.diehl.invoice.domain.Invoice;
import com.diehl.invoice.domain.InvoiceId;
import com.diehl.invoice.domain.InvoiceSummary;
import com.diehl.invoice.dto.InvoiceCsvEntry;
import com.diehl.invoice.dto.InvoiceInfo;
import com.diehl.invoice.dto.InvoiceKey;
import com.diehl.invoice.exception.DuplicateEntriesUploadException;
import com.diehl.invoice.exception.InvoiceNotFoundException;
import com.diehl.invoice.exception.SupplierNotFoundException;
import com.diehl.invoice.repository.InvoiceRepository;
import com.diehl.invoice.repository.InvoiceSummaryRepository;
import com.diehl.invoice.util.InvoiceUtil;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import lombok.extern.slf4j.XSlf4j;

/**
 * Business Service for Invoice service.
 * 
 * @author danieldiehl
 */
@Service
@XSlf4j
public class InvoiceService {

	@Autowired
	private InvoiceRepository invoiceRepo;
	
	@Autowired
	private InvoiceSummaryRepository summaryRepo;
	
	/**
	 * Load invoices into the database.
	 * Process for duplicates
	 * 
	 * @param file CSV sent bu user
	 * @throws IllegalStateException
	 * @throws IOException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws DuplicateEntriesUploadException 
	 */
	public void loadInvoices(Resource file) throws IllegalStateException, IOException, IllegalAccessException, InvocationTargetException, DuplicateEntriesUploadException {
		log.entry(file.getFilename());
		
		final CsvToBean<InvoiceCsvEntry> reader = new CsvToBeanBuilder<InvoiceCsvEntry>(new InputStreamReader(file.getInputStream()))
				.withType(InvoiceCsvEntry.class)
				.build();
	
		final Map<InvoiceKey, Integer> idsCount = new HashMap<>();
		final Set<InvoiceKey> duplicates = new HashSet<>();
		final AtomicBoolean hasDuplicates = new AtomicBoolean(false);
		final List<Invoice> dbObjects = new ArrayList<>();
		
		for (InvoiceCsvEntry csvEntry : reader) {
			final InvoiceKey key = new InvoiceKey(csvEntry.getSupplierId(), csvEntry.getInvoiceId());
			idsCount.compute(key, (k, old) -> {
				if (old != null) {
					hasDuplicates.set(true);
					dbObjects.clear();
					duplicates.add(key);
					return old++;
				} else {
					return 1;
				}
			});
			
			if (!hasDuplicates.get()) {
				final Invoice dbEntry = new Invoice();
				BeanUtils.copyProperties(dbEntry, csvEntry);
				dbObjects.add(dbEntry);
			}
		}
		
		if (hasDuplicates.get()) {
			log.debug("There was duplicates, returning exception");
			throw new DuplicateEntriesUploadException(duplicates);
		}
		
		invoiceRepo.saveAll(dbObjects);
		
		log.exit();
	}
	
	/**
	 * Retrieves summary for supplier.
	 * 
	 * @param supplierId for the search
	 * @return summary 
	 * @throws SupplierNotFoundException in case not found.
	 */
	public InvoiceSummary retrieveSupplierSummary(String supplierId) throws SupplierNotFoundException {
		return summaryRepo.findById(supplierId).orElseThrow(SupplierNotFoundException::new);
	}
	
	/**
	 * Finds invoice by Id.
	 * 
	 * @param invoiceId invoice id
	 * @param supplierId supplier id
	 * @return invoice if found
	 * @throws InvoiceNotFoundException if not found
	 */
	public InvoiceInfo retrieveInvoiceSummaryById(String invoiceId, String supplierId) throws InvoiceNotFoundException {
		final Invoice invoice = invoiceRepo.findById(new InvoiceId(invoiceId, supplierId)).orElseThrow(InvoiceNotFoundException::new);
		final InvoiceInfo response = new InvoiceInfo();
		
		response.setInvoiceAmount(invoice.getInvoiceAmount());
		response.setInvoiceId(invoice.getInvoiceId());
		response.setPaymentAmount(invoice.getPaymentAmount());
		response.setPaymentDate(invoice.getPaymentDate());
		response.setSupplierId(invoice.getSupplierId());
		response.setDueDate(invoice.getInvoiceDate().plusDays(invoice.getTerms()));
		if (invoice.getPaymentAmount() != null) {
			response.setInvoiceOpenBalance(invoice.getInvoiceAmount().subtract(invoice.getPaymentAmount()));
		} else {
			response.setInvoiceOpenBalance(invoice.getInvoiceAmount());
		}
		response.setStatus(InvoiceUtil.getInvoiceStatus(invoice));
		//Not paid
		if (invoice.getPaymentAmount() == null || invoice.getInvoiceAmount().compareTo(invoice.getPaymentAmount()) == 1) {
			//Late
			if (invoice.getInvoiceDate().plusDays(invoice.getTerms()).isBefore(LocalDate.now())) {
				response.setDaysPastDue(invoice.getInvoiceDate().plusDays(invoice.getTerms()).until(LocalDate.now()).getDays());			
				
			}
		}
		return response;
	}
	
}
