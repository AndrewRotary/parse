package com.currencyparser.domain;



import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * Currency domain
 */
@Entity
public class Currency implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Double buyers;

    private Double sellers;

    @ManyToOne
    private Company company;

    @JsonFormat(pattern = "dd::MM::yyyy")
    private LocalDate date;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBuyers() {
        return buyers;
    }

    public void setBuyers(Double buyers) {
        this.buyers = buyers;
    }

    public Double getSellers() {
        return sellers;
    }

    public void setSellers(Double sellers) {
        this.sellers = sellers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
