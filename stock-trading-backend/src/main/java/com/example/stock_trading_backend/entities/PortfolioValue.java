/**
 * The PortfolioValue class represents the calculated value of a portfolio at a specific point in time. This is
 * important for tracking and analyzing the performance of a portfolio over time, especially in terms of gains or losses.
 */

package com.example.stock_trading_backend.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "portfoliovalues")
public class PortfolioValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("portfolio-portfolio-values")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    private LocalDateTime date;

    private double totalValue;
}
