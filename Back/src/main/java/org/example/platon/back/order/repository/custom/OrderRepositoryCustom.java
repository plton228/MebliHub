package org.example.platon.back.order.repository.custom;

import org.example.platon.back.order.dto.OrderStats;
import org.example.platon.back.order.dto.RevenueByProduct;

import java.util.List;

/**
 * Кастомні агрегаційні запити для замовлень через MongoTemplate.
 */
public interface OrderRepositoryCustom {

    /**
     * Загальна статистика замовлень (агрегація по всій колекції).
     * Аналог SQL:
     *   SELECT COUNT(*), SUM(totalPrice),
     *          SUM(CASE WHEN status='CONFIRMED' THEN 1 END) ...
     *   FROM orders
     */
    OrderStats getOrderStats();

    /**
     * Виручка та кількість замовлень по кожному товару.
     * Аналог SQL:
     *   SELECT productId, productName,
     *          COUNT(*) AS orderCount,
     *          SUM(totalPrice) AS totalRevenue
     *   FROM orders WHERE status = 'CONFIRMED'
     *   GROUP BY productId, productName
     *   ORDER BY totalRevenue DESC
     */
    List<RevenueByProduct> getRevenueByProduct();
}
