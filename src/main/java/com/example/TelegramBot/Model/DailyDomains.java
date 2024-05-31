package com.example.TelegramBot.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "daily_domains")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyDomains {
    @Id
    @JsonIgnore
   /* @SequenceGenerator(name="seq",sequenceName = "daily_domains_id_seq")
    @GeneratedValue(strategy =  GenerationType.AUTO, generator = "seq")*/
    private long id;
    @JsonProperty("domainname")
    @Column(name = "domain_name", nullable = true)
    private String domainName;
    @JsonProperty("hotness")
    @Column(name = "hotness", nullable = true)
    private int hotness;
    @JsonProperty("price")
    @Column(name = "price", nullable = true)
    private int price;
    @JsonProperty("registrar")
    @Column(name = "registrar", nullable = true)
    private String registrar;
    @JsonProperty("old")
    @Column(name = "olds", nullable = true)
    private int old;
    @JsonProperty("delete_date")
    @Column(name = "delete_date", nullable = true)
    private String deleteDate;
    @JsonProperty("rkn")
    @Column(name = "rkn", nullable = true)
    private boolean rkn;
    @JsonProperty("judicial")
    @Column(name = "judicial", nullable = true)
    private boolean judicial;

    @Column(name = "block", nullable = true)
    @JsonProperty("block")
    private boolean block;
}
