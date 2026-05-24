package org.example.platon.back.product.dto;

import java.math.BigDecimal;

public record ProductStats(
        long totalCount,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal avgPrice,
        long totalStock
) {}
