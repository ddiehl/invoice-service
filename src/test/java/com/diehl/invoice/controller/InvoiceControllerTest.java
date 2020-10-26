/**
 * Diehl code. 
 * 2020
 */
package com.diehl.invoice.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.diehl.invoice.SpringEnabledTest;
import com.diehl.invoice.dto.InvoiceInfo;
import com.diehl.invoice.dto.InvoiceKey;
import com.diehl.invoice.dto.ListPaymentsSummary;
import com.diehl.invoice.dto.SupplierSummary;
import com.diehl.invoice.exception.DuplicateEntriesUploadException;
import com.diehl.invoice.exception.InvoiceNotFoundException;
import com.diehl.invoice.exception.SupplierNotFoundException;
import com.diehl.invoice.service.InvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for controller
 * 
 * @author danieldiehl
 */
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class InvoiceControllerTest extends SpringEnabledTest {

	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	private InvoiceService service;
	
	@Autowired
	private InvoiceController controller;
	
	@BeforeEach
	public void initMocks() {
		ReflectionTestUtils.setField(controller, "service", service);
	}
	
	@Test
	public void testUploadSuccess() throws Exception {
		var filename = "file.csv";
		var content = "123";
		
		final MockMultipartFile file = new MockMultipartFile("file", filename, "text/plain", content.getBytes());
		mockMvc.perform(multipart("/upload").file(file)).andExpect(status().isOk());
		
		ArgumentCaptor<Resource> resCaptor = ArgumentCaptor.forClass(Resource.class);
		verify(service).loadInvoices(resCaptor.capture());
		assertEquals(content, new String(resCaptor.getValue().getInputStream().readAllBytes()));
	}
	
	@Test
	public void testUploadDuplicate() throws Exception {
		var filename = "file.csv";
		var content = "123";
		
		var invoiceKey = new InvoiceKey("1", "2");
		var ex = new DuplicateEntriesUploadException(Sets.newSet(invoiceKey));
		
		doThrow(ex).when(service).loadInvoices(any());  
		
		final MockMultipartFile file = new MockMultipartFile("file", filename, "text/plain", content.getBytes());
		mockMvc.perform(multipart("/upload").file(file))
		.andExpect(status().isBadRequest())
		.andExpect(content().bytes("supplierId=1, invoiceId=2".getBytes()));	
	}
	
	@Test
	public void testSupplierSumarySuccess() throws Exception {
		var supplierId = "1";
		
		var summary = new SupplierSummary();
		summary.setLateInvoices(1);
		summary.setOpenInvoices(2);
		summary.setSupplierId(supplierId);
		summary.setTotalInvoices(3);
		summary.setTotalLateInvoices(BigDecimal.ONE);
		summary.setTotalOpenInvoices(BigDecimal.TEN);
		
		when(service.retrieveSupplierSummary(supplierId)).thenReturn(summary);
		
		mockMvc.perform(get("/ListSupplierSummary").param("supplierId", supplierId))
		.andExpect(status().isOk())
		.andExpect(content().json(mapper.writeValueAsString(summary)));
	}

	@Test
	public void testSupplierSumaryNotFound() throws Exception {
		var supplierId = "1";
		
		when(service.retrieveSupplierSummary(supplierId)).thenThrow(new SupplierNotFoundException());
		
		mockMvc.perform(get("/ListSupplierSummary").param("supplierId", supplierId))
		.andExpect(status().isNotFound())
		.andExpect(content().string("Supplier not found"));
	}

	
	/*************** invoice summary**********/
	@Test
	public void testInvoiceSumarySuccess() throws Exception {
		var supplierId = "1";
		var invoiceId = "inv-1";
		
		var summary = new InvoiceInfo();
		summary.setDaysPastDue(1);
		summary.setDueDate(LocalDate.now());
		summary.setInvoiceAmount(BigDecimal.TEN);
		summary.setInvoiceDate(LocalDate.now());
		summary.setInvoiceId(invoiceId);
		summary.setInvoiceOpenBalance(BigDecimal.ZERO);
		summary.setPaymentAmount(BigDecimal.TEN);
		summary.setPaymentDate(LocalDate.now());
		summary.setStatus("Open");
		summary.setSupplierId(supplierId);
		
		
		when(service.retrieveInvoiceSummaryById(invoiceId, supplierId)).thenReturn(summary);
		
		mockMvc.perform(get("/ListInvoiceSummary")
				.param("supplierId", supplierId)
				.param("invoiceId", invoiceId))
		.andExpect(status().isOk())
		.andExpect(content().json(mapper.writeValueAsString(summary)));
	}
	
	@Test
	public void testInvoiceSumaryNotFound() throws Exception {
		var supplierId = "1";
		var invoiceId = "inv-1";
		
		when(service.retrieveInvoiceSummaryById(invoiceId, supplierId)).thenThrow(new InvoiceNotFoundException());
		
		mockMvc.perform(get("/ListInvoiceSummary")
				.param("supplierId", supplierId)
				.param("invoiceId", invoiceId))
		.andExpect(status().isNotFound())
		.andExpect(content().string("Invoice not found"));
	}
	
	/*************** list payments**********/
	@Test
	public void testListPaymentsSuccess() throws Exception {
		var supplierId = "1";

		var summary = new ListPaymentsSummary();
		summary.setNumberOfInvoicesPaid(BigInteger.ONE);
		summary.setPaymentAmount(BigDecimal.TEN);
		summary.setPaymentDate(LocalDate.now());
		summary.setSupplierId(supplierId);

		var summaries = List.of(summary);
		
		when(service.retrievePaymentSummaryBySupplier(supplierId)).thenReturn(summaries);
		
		mockMvc.perform(get("/ListPayments")
				.param("supplierId", supplierId))
		.andExpect(status().isOk())
		.andExpect(content().json(mapper.writeValueAsString(summaries)));
	}
}
