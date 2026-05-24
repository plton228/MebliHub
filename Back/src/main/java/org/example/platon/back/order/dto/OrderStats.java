package org.example.platon.back.order.dto;

import java.math.BigDecimal;

public record OrderStats(
        long totalOrders,
        BigDecimal totalRevenue,
        long confirmedCount,
        long pendingCount,
        long cancelledCount
) {}
