package com.coderarmy.hibernate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Finished for you: a value type (no @Id) that lives inside the owning row. */
@Embeddable
public class Address {

    @Column(name = "address_line")
    private String addressLine;

    private String city;

    private String state;

    @Column(name = "postal_code")
    private String postalCode;

    protected Address() {
    }

    public Address(String addressLine, String city, String state, String postalCode) {
        this.addressLine = addressLine;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
    }

    public String getAddressLine() { return addressLine; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
}
