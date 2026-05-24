package org.example.platon.back.order.dto;

import java.math.BigDecimal;

public record RevenueByProduct(
        String productId,
        String productName,
        long orderCount,
        BigDecimal totalRevenue
) {}
