package com.example.myevent_be.enums;

public enum ContractStatus {
    DRAFT,    // Nháp
    DEPOSITED,   // đã đặt cọc
    PROGRESS,   //  thực hiện
    PENDING,    // Đang chờ
    PENDING_PAYMENT,  // Chờ thanh toán
    COMPLETED,// Hoàn thành
    CANCELLED,//Đã huỷ
    CANCELLED_BY_ADMIN// Đã hủy bởi admin
}