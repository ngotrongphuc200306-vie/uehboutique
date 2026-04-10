package com.uehboutique.controller;

import com.uehboutique.entity.Invoice;
import com.uehboutique.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    private Invoice sampleInvoice;

    @BeforeEach
    void setUp() {
        // Khởi tạo đối tượng giả để Mockito trả về (Không dùng Lombok)
        sampleInvoice = new Invoice();
    }

    // =========================================================================
    // TEST API: POST /api/invoices/checkout/{bookingId}
    // =========================================================================
    @Test
    @DisplayName("POST /api/invoices/checkout - Checkout thành công (200 OK)")
    void testCheckout_Success() throws Exception {
        Mockito.doReturn(sampleInvoice).when(invoiceService).checkout(eq(1), anyString());

        mockMvc.perform(post("/api/invoices/checkout/{bookingId}", 1)
                        .param("paymentMethod", "Card")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/invoices/checkout - Checkout lỗi (400 Bad Request)")
    void testCheckout_BadRequest() throws Exception {
        Mockito.doThrow(new RuntimeException("Lỗi thanh toán")).when(invoiceService).checkout(anyInt(), anyString());

        mockMvc.perform(post("/api/invoices/checkout/{bookingId}", 99)
                        .param("paymentMethod", "Card")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // TEST API: GET /api/invoices/preview/{bookingId}
    // =========================================================================
    @Test
    @DisplayName("GET /api/invoices/preview - Xem trước hóa đơn thành công (200 OK)")
    void testPreviewCheckout_Success() throws Exception {
        // Có thể preview trả về Invoice hoặc Map/DTO, dùng doReturn(null) để cover mọi kiểu trả về
        Mockito.doReturn(null).when(invoiceService).previewCheckout(eq(1));

        mockMvc.perform(get("/api/invoices/preview/{bookingId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/invoices/preview - Xem trước hóa đơn lỗi (400 Bad Request)")
    void testPreviewCheckout_BadRequest() throws Exception {
        Mockito.doThrow(new RuntimeException("Không tìm thấy booking")).when(invoiceService).previewCheckout(anyInt());

        mockMvc.perform(get("/api/invoices/preview/{bookingId}", 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // TEST API: GET /api/invoices
    // =========================================================================
    @Test
    @DisplayName("GET /api/invoices - Lấy danh sách hóa đơn thành công (200 OK)")
    void testGetAllInvoices_Success() throws Exception {
        Mockito.doReturn(Collections.emptyList()).when(invoiceService).getAllInvoices();

        mockMvc.perform(get("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/invoices - Lấy danh sách hóa đơn lỗi (400 Bad Request)")
    void testGetAllInvoices_BadRequest() throws Exception {
        Mockito.doThrow(new RuntimeException("Lỗi database")).when(invoiceService).getAllInvoices();

        mockMvc.perform(get("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}