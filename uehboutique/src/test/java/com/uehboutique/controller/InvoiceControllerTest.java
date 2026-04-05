package com.uehboutique.controller;

import com.uehboutique.entity.Invoice;
import com.uehboutique.service.InvoiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvoiceController.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    private Invoice mockInvoice;

    @BeforeEach
    void setUp() {
        mockInvoice = new Invoice();
        // LƯU Ý: Nếu Entity Invoice của bạn dùng `id` thì đổi `setInvoiceId` thành `setId` nhé!
        mockInvoice.setInvoiceId(1);
        mockInvoice.setPaymentMethod("Card");
    }

    // ==========================================
    // 1. TEST XỬ LÝ CHECK-OUT (POST /checkout/{bookingId})
    // ==========================================
    @Test
    void testCheckout_Success() throws Exception {
        when(invoiceService.checkout(anyInt(), anyString())).thenReturn(mockInvoice);

        mockMvc.perform(post("/api/invoices/checkout/1")
                        .param("paymentMethod", "Card"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentMethod").value("Card")); // Đổi tên trường nếu Entity của bạn khác
    }

    @Test
    void testCheckout_Failure() throws Exception {
        when(invoiceService.checkout(anyInt(), anyString()))
                .thenThrow(new RuntimeException("Phòng chưa thanh toán đủ"));

        mockMvc.perform(post("/api/invoices/checkout/1")
                        .param("paymentMethod", "Card"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Lỗi Check-out: Phòng chưa thanh toán đủ"));
    }

    // ==========================================
    // 2. TEST XEM TRƯỚC HÓA ĐƠN (GET /preview/{bookingId})
    // ==========================================
    // ==========================================
    // 2. TEST XEM TRƯỚC HÓA ĐƠN (GET /preview/{bookingId})
    // ==========================================
    @Test
    void testPreviewCheckout_Success() throws Exception {
        // Tạo Map giả lập dữ liệu y hệt như bên InvoiceService
        java.util.Map<String, Object> mockMap = new java.util.HashMap<>();
        mockMap.put("daysStayed", 2);
        mockMap.put("roomTotal", 1000000);
        mockMap.put("serviceTotal", 500000);
        mockMap.put("grandTotal", 1500000);

        when(invoiceService.previewCheckout(anyInt())).thenReturn(mockMap);

        mockMvc.perform(get("/api/invoices/preview/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.daysStayed").value(2))
                .andExpect(jsonPath("$.grandTotal").value(1500000));
    }

    @Test
    void testPreviewCheckout_Failure() throws Exception {
        when(invoiceService.previewCheckout(anyInt()))
                .thenThrow(new RuntimeException("Không tìm thấy Booking"));

        mockMvc.perform(get("/api/invoices/preview/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Lỗi Preview: Không tìm thấy Booking"));
    }

    // ==========================================
    // 3. TEST LẤY DANH SÁCH HÓA ĐƠN (GET /)
    // ==========================================
    @Test
    void testGetAllInvoices_Success() throws Exception {
        List<Invoice> invoiceList = List.of(mockInvoice);
        when(invoiceService.getAllInvoices()).thenReturn(invoiceList);

        mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isOk())
                // Kiểm tra xem danh sách trả về có phần tử đầu tiên mang paymentMethod là "Card" không
                .andExpect(jsonPath("$.[0].paymentMethod").value("Card"));
    }

    @Test
    void testGetAllInvoices_Failure() throws Exception {
        when(invoiceService.getAllInvoices())
                .thenThrow(new RuntimeException("Lỗi kết nối cơ sở dữ liệu"));

        mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Lỗi tải danh sách hóa đơn: Lỗi kết nối cơ sở dữ liệu"));
    }
}