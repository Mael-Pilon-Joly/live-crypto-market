package com.example.stock_trading_backend.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonManagedReference("portfolio-user")
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Portfolio> listPortfolios = new ArrayList<>();

    private String email;
    private String userId;
}
