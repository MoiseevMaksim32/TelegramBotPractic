package com.example.TelegramBot.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "daily_domains")
public class DailyDomains {
    @Id
    @Column(name = "domain_name", nullable = true)
    private String domainName;
    @Column(name = "hotness", nullable = true)
    private int hotness;
    @Column(name = "price", nullable = true)
    private int price;
    @Column(name = "x_value", nullable = true)
    private int xValue;
    @Column(name = "yandex_tic", nullable = true)
    private int yandexTic;
    @Column(name = "links", nullable = true)
    private int links;
    @Column(name = "visitors", nullable = true)
    private int visitors;
    @Column(name = "registrar", nullable = true)
    private String registrar;
    @Column(name = "olds", nullable = true)
    private int old;
    @Column(name = "delete_date", nullable = true)
    private String deleteDate;
    @Column(name = "rkn", nullable = true)
    private boolean rkn;
    @Column(name = "judicial", nullable = true)
    private boolean judicial;
    @Column(name = "block", nullable = true)
    private boolean block;
}
