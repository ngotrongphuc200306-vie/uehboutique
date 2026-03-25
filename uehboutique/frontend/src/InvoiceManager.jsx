import React, { useState, useEffect } from 'react';
import axios from 'axios';

function InvoiceManager() {
    const [invoices, setInvoices] = useState([]);

    useEffect(() => {
        // Gọi API lấy danh sách toàn bộ hóa đơn từ Database
        axios.get('http://localhost:8080/api/invoices')
            .then(res => {
                setInvoices(res.data);
            })
            .catch(err => {
                console.error("Lỗi tải danh sách hóa đơn:", err);
            });
    }, []);

    // Hàm format tiền tệ
    const formatCurrency = (val) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(val);

    // Hàm format ngày tháng cho đẹp
    const formatDate = (dateString) => {
        if (!dateString) return "---";
        const d = new Date(dateString);
        return d.toLocaleString('vi-VN');
    };

    const thStyle = { padding: '15px 10px', textAlign: 'center', fontWeight: 'bold', fontSize: '13px' };
    const tdStyle = { padding: '15px 10px', textAlign: 'center', borderBottom: '1px solid #eee', fontSize: '14px' };

    return (
        <div style={{ padding: '30px', fontFamily: "'Segoe UI', Tahoma, sans-serif" }}>
            <div style={{ backgroundColor: 'white', borderRadius: '10px', padding: '20px', boxShadow: '0 4px 8px rgba(0,0,0,0.05)' }}>

                <h2 style={{ color: '#125c61', textAlign: 'center', marginBottom: '5px' }}>
                    📑 LỊCH SỬ HÓA ĐƠN
                </h2>
                <p style={{ textAlign: 'center', color: '#777', fontSize: '14px', marginBottom: '25px' }}>
                    Danh sách các hóa đơn đã thanh toán thành công
                </p>

                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                    <thead style={{ backgroundColor: '#125c61', color: 'white' }}>
                    <tr>
                        <th style={{...thStyle, borderTopLeftRadius: '8px'}}>MÃ HÓA ĐƠN</th>
                        <th style={thStyle}>MÃ BOOKING</th>
                        <th style={thStyle}>NGÀY THANH TOÁN</th>
                        <th style={thStyle}>PHƯƠNG THỨC</th>
                        <th style={{...thStyle, borderTopRightRadius: '8px'}}>TỔNG TIỀN</th>
                    </tr>
                    </thead>
                    <tbody>
                    {invoices.length > 0 ? invoices.map((inv) => (
                        <tr key={inv.invoiceId || inv.invoice_id}>
                            <td style={{...tdStyle, fontWeight: 'bold', color: '#125c61'}}>#{inv.invoiceId || inv.invoice_id}</td>
                            <td style={tdStyle}>{inv.booking?.bookingId || inv.booking_id || "---"}</td>
                            <td style={tdStyle}>{formatDate(inv.paymentDate || inv.payment_date)}</td>
                            <td style={tdStyle}>
                                    <span style={{ backgroundColor: '#eee', padding: '4px 8px', borderRadius: '4px', fontSize: '12px', fontWeight: 'bold' }}>
                                        {inv.paymentMethod || inv.payment_method}
                                    </span>
                            </td>
                            <td style={{...tdStyle, color: '#e74c3c', fontWeight: 'bold'}}>
                                {formatCurrency(inv.totalAmount || inv.total_amount)}
                            </td>
                        </tr>
                    )) : (
                        <tr>
                            <td colSpan="5" style={{ textAlign: 'center', padding: '30px', color: '#777' }}>
                                Hệ thống chưa có hóa đơn nào.
                            </td>
                        </tr>
                    )}
                    </tbody>
                </table>

            </div>
        </div>
    );
}

export default InvoiceManager;