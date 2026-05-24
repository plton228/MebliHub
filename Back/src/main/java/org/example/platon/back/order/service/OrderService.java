package org.example.platon.back.order.service;

import org.example.platon.back.order.dto.OrderRequest;
import org.example.platon.back.order.dto.OrderStats;
import org.example.platon.back.order.dto.RevenueByProduct;
import org.example.platon.back.order.model.Order;
import org.example.platon.back.order.model.OrderStatus;

import java.util.List;

public interface OrderService {

    // ── Основні операції ────────────────────────────────────────────────────
    Order createOrder(String userEmail, OrderRequest request);

    /** Замовлення поточного юзера (sorted by date desc, derived method). */
    List<Order> getMyOrders(String userEmail);

    /** Замовлення юзера з фільтром по статусу (@Query). */
    List<Order> getMyOrdersByStatus(String userEmail, OrderStatus status);

    // ── Адмін-операції ──────────────────────────────────────────────────────

    /** Всі замовлення в системі (findAll). */
    List<Order> getAllOrders();

    /** Замовлення за статусом (derived method). */
    List<Order> getOrdersByStatus(OrderStatus status);

    /** Всі підтверджені замовлення, відсортовані за датою (@Query). */
    List<Order> getAllConfirmedOrders();

    /** Кількість замовлень конкретного юзера (derived method). */
    long countUserOrders(String userEmail);

    // ── Аналітика ───────────────────────────────────────────────────────────

    /** Загальна статистика (MongoTemplate aggregation). */
    OrderStats getOrderStats();

    /** Виручка по товарах (MongoTemplate aggregation GROUP BY). */
    List<RevenueByProduct> getRevenueByProduct();
}
