package com.example.stock_trading_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "portfolios")

public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("portfolio-user")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonManagedReference("portfolio-portfolio-cryptos")
    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY)
    private List<PortfolioCrypto> listPortfolioCryptos = new ArrayList<>();

    @JsonManagedReference("portfolio-portfolio-values")
    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY)
    private List<PortfolioValue> listPortfolioValues = new ArrayList<>();

    @JsonManagedReference("portfolio-transactions")
    @OneToMany(mappedBy = "portfolio", fetch = FetchType.LAZY)
    private List<Transaction> listTransactions = new ArrayList<>();

    private String name;

    private LocalDateTime createdAt;
}
