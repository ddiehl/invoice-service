/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.diehl.invoice.domain.Invoice;
import com.diehl.invoice.domain.InvoiceSummary;
import com.diehl.invoice.dto.InvoiceKey;
import com.diehl.invoice.exception.DuplicateEntriesUploadException;
import com.diehl.invoice.exception.SupplierNotFoundException;
import com.diehl.invoice.repository.InvoiceRepository;
import com.diehl.invoice.repository.InvoiceSummaryRepository;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

/**
 * Invoice Business service test. 
 * 
 * @author danieldiehl
 */
@ExtendWith(MockitoExtension.class)
public class InvoiceServiceTest {

	@Mock
	private InvoiceRepository invoiceRepo;
	
	@Mock
	private InvoiceSummaryRepository summaryRepo;
	
	@InjectMocks
	private InvoiceService tested;
	
	/**
	 * Unit tests with successful upload.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUploadSuccess() throws Exception {
		final Resource res = new ClassPathResource("/upload_test_1.csv");
		
		tested.loadInvoices(res);
		
		@SuppressWarnings("unchecked")
		final ArgumentCaptor<List<Invoice>> dbObjectsCaptor = ArgumentCaptor.forClass(List.class);
		
		verify(invoiceRepo, times(1)).saveAll(dbObjectsCaptor.capture());
	 	final List<Invoice> dbObjects = dbObjectsCaptor.getValue();
	 	final Map<String, List<Invoice>> map = dbObjects.stream().collect(Collectors.groupingBy(i -> i.getInvoiceId()+"#"+i.getSupplierId()));
	 	
		assertEquals(3, dbObjects.size());
		
		assertEquals(Invoice.builder().
				supplierId("supplier_7").invoiceId("8E74BED1-D7C0-4655-A528-0C9B67102C31")
				.invoiceDate(LocalDate.of(2020, 05, 28))
				.invoiceAmount(BigDecimal.valueOf(9208.95))
				.terms(90).build(), 
				map.get("8E74BED1-D7C0-4655-A528-0C9B67102C31#supplier_7").get(0));
		
		assertEquals(Invoice.builder().
				supplierId("supplier_8").invoiceId("1936AE64-2407-413D-975B-7FACC6004DEA")
				.invoiceDate(LocalDate.of(2020, 04, 15))
				.invoiceAmount(BigDecimal.valueOf(1875.53))
				.terms(60).build(), 
				map.get("1936AE64-2407-413D-975B-7FACC6004DEA#supplier_8").get(0));
		
		assertEquals(Invoice.builder().
				supplierId("supplier_8").invoiceId("A9B125D9-8E07-4AC8-998C-413B15722048")
				.invoiceDate(LocalDate.of(2020, 06, 01))
				.invoiceAmount(BigDecimal.valueOf(1658.12))
				.terms(30)
				.paymentDate(LocalDate.of(2020, 01, 01))
				.paymentAmount(BigDecimal.valueOf(10.12)).build(),
				map.get("A9B125D9-8E07-4AC8-998C-413B15722048#supplier_8").get(0));
	}
	
	/**
	 * Scenario with duplicates.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testUploadDuplicated() throws Exception {
		final Resource res = new ClassPathResource("/upload_test_dups.csv");
		var ex = assertThrows(DuplicateEntriesUploadException.class, () -> tested.loadInvoices(res));
		assertEquals(2, ex.getIds().size());
		assertTrue(ex.getIds().contains(new InvoiceKey("supplier_8", "A9B125D9-8E07-4AC8-998C-413B15722048")));
		assertTrue(ex.getIds().contains(new InvoiceKey("supplier_8", "944DA6C8-C24F-4F6F-8830-A85CEA0D1744")));

	}
	
	/**
	 * Unit test for invalid file.
	 */
	@Test
	public void testUploadInvalidFile() {
		final Resource res = new ClassPathResource("/upload_test_invalid.csv");
		var ex = assertThrows(RuntimeException.class, () -> tested.loadInvoices(res));
		assertEquals(CsvRequiredFieldEmptyException.class, ex.getCause().getClass());
	}
	
	
	/************ summary **************/
	@Test
	public void testSupplierSummarySuccess() throws Exception {
		final var id = "123";
		final var resp = Optional.of(new InvoiceSummary());
		when(summaryRepo.findById(id)).thenReturn(resp);
		
		assertEquals(resp.get(), tested.retrieveSupplierSummary(id));
	}
	
	@Test
	public void testSupplierSummarySupplierNotFound() throws Exception {
		final var id = "123";
		final Optional<InvoiceSummary> resp = Optional.empty();
		when(summaryRepo.findById(id)).thenReturn(resp);
		
		assertThrows(SupplierNotFoundException.class, () -> tested.retrieveSupplierSummary(id));
	}
	
	
}
