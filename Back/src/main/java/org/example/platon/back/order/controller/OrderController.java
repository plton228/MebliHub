package org.example.platon.back.order.controller;

import jakarta.validation.Valid;
import org.example.platon.back.order.dto.OrderRequest;
import org.example.platon.back.order.dto.OrderStats;
import org.example.platon.back.order.dto.RevenueByProduct;
import org.example.platon.back.order.model.Order;
import org.example.platon.back.order.model.OrderStatus;
import org.example.platon.back.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ── Операції поточного юзера ─────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderRequest request,
                                              Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(auth.getName(), request));
    }

    /**
     * Мої замовлення (sorted by date, derived method).
     * GET /api/v1/orders/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getMyOrders(auth.getName()));
    }

    /**
     * Мої замовлення з фільтром по статусу (@Query).
     * GET /api/v1/orders/my/status?status=CONFIRMED
     */
    @GetMapping("/my/status")
    public ResponseEntity<List<Order>> getMyOrdersByStatus(
            @RequestParam OrderStatus status,
            Authentication auth) {
        return ResponseEntity.ok(orderService.getMyOrdersByStatus(auth.getName(), status));
    }

    /**
     * Кількість моїх замовлень (derived: countByUserId).
     * GET /api/v1/orders/my/count
     */
    @GetMapping("/my/count")
    public ResponseEntity<Long> countMyOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.countUserOrders(auth.getName()));
    }

    // ── Адмін-операції ───────────────────────────────────────────────────────

    /**
     * Всі замовлення в системі (findAll).
     * GET /api/v1/orders/all
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /**
     * Замовлення за статусом (derived: findByStatus).
     * GET /api/v1/orders/status/CONFIRMED
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    /**
     * Всі підтверджені замовлення, відсортовані (@Query).
     * GET /api/v1/orders/confirmed
     */
    @GetMapping("/confirmed")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getConfirmedOrders() {
        return ResponseEntity.ok(orderService.getAllConfirmedOrders());
    }

    // ── Аналітика (Admin only) ────────────────────────────────────────────────

    /**
     * Загальна статистика замовлень (MongoTemplate aggregation).
     * GET /api/v1/orders/stats
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderStats> getOrderStats() {
        return ResponseEntity.ok(orderService.getOrderStats());
    }

    /**
     * Виручка по кожному товару (MongoTemplate GROUP BY aggregation).
     * GET /api/v1/orders/revenue-by-product
     */
    @GetMapping("/revenue-by-product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RevenueByProduct>> getRevenueByProduct() {
        return ResponseEntity.ok(orderService.getRevenueByProduct());
    }
}
