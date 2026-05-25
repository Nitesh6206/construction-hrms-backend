package com.example.demo.worker;

import javax.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "workers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Designation designation;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyWage;

    private boolean active = true;
}