package com.coderarmy.hibernate.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customer;

    /** TODO Q4a: items are created with their order and deleted with it. */
    @OneToMany(mappedBy = "order")
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
    }

    public Order(String customer) {
        this.customer = customer;
    }

    /** TODO Q4a: add to the list AND set the owning side (OrderItem.order owns order_id). */
    public void addItem(OrderItem item) {
        throw new UnsupportedOperationException("TODO Q4a: addItem()");
    }

    public Long getId() { return id; }
    public String getCustomer() { return customer; }
    public List<OrderItem> getItems() { return items; }
}
