package com.coderarmy.hibernate.entity;

import jakarta.persistence.*;

/** Finished for you: the owning side of Order ↔ OrderItem. */
@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    protected OrderItem() {
    }

    public OrderItem(String product) {
        this.product = product;
    }

    public Long getId() { return id; }
    public String getProduct() { return product; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
}
