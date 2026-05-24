package org.example.platon.back.order.service.impl;

import org.example.platon.back.auth.model.UserEntity;
import org.example.platon.back.auth.repository.UserRepository;
import org.example.platon.back.order.dto.OrderRequest;
import org.example.platon.back.order.dto.OrderStats;
import org.example.platon.back.order.dto.RevenueByProduct;
import org.example.platon.back.order.model.Order;
import org.example.platon.back.order.model.OrderStatus;
import org.example.platon.back.order.repository.OrderRepository;
import org.example.platon.back.order.service.OrderService;
import org.example.platon.back.product.model.Product;
import org.example.platon.back.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // ── Основні операції ─────────────────────────────────────────────────────

    @Override
    public Order createOrder(String userEmail, OrderRequest request) {
        // findByEmail → { "email": userEmail }
        UserEntity user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // findById → { "_id": ObjectId("...") }
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found: " + request.productId()));

        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(request.quantity()));

        Order order = new Order(null, user.getId(), product.getId(), product.getName(),
                request.quantity(), totalPrice, OrderStatus.CONFIRMED, LocalDateTime.now());

        return orderRepository.save(order);
    }

    @Override
    public List<Order> getMyOrders(String userEmail) {
        UserEntity user = resolveUser(userEmail);
        // Derived: findByUserIdOrderByCreatedAtDesc → { "userId": id }, sort: { "createdAt": -1 }
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public List<Order> getMyOrdersByStatus(String userEmail, OrderStatus status) {
        UserEntity user = resolveUser(userEmail);
        // @Query: { "userId": ?0, "status": ?1 }
        return orderRepository.findByUserIdAndStatus(user.getId(), status.name());
    }

    // ── Адмін-операції ───────────────────────────────────────────────────────

    @Override
    public List<Order> getAllOrders() {
        // MongoRepository.findAll() — вся колекція
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        // Derived: findByStatus → { "status": status }
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> getAllConfirmedOrders() {
        // @Query: { "status": "CONFIRMED" }, sort: { "createdAt": -1 }
        return orderRepository.findAllConfirmedOrderByDateDesc();
    }

    @Override
    public long countUserOrders(String userEmail) {
        UserEntity user = resolveUser(userEmail);
        // Derived: countByUserId → COUNT WHERE userId = id
        return orderRepository.countByUserId(user.getId());
    }

    // ── Аналітика ─────────────────────────────────────────────────────────────

    @Override
    public OrderStats getOrderStats() {
        // MongoTemplate aggregation: COUNT + SUM + conditional SUM by status
        return orderRepository.getOrderStats();
    }

    @Override
    public List<RevenueByProduct> getRevenueByProduct() {
        // MongoTemplate aggregation: GROUP BY productId + SUM(totalPrice)
        return orderRepository.getRevenueByProduct();
    }

    // ─────────────────────────────────────────────────────────────────────────

    private UserEntity resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
