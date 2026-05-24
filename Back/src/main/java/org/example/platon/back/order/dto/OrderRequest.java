package org.example.platon.back.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotBlank String productId,
        @NotNull @Min(1) Integer quantity
) {}
