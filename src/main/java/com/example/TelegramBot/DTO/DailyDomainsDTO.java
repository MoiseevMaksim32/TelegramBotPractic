package com.example.TelegramBot.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyDomainsDTO {
    @JsonProperty("domainname")
    private String domainName;
    @JsonProperty("hotness")
    private int hotness;
    @JsonProperty("price")
    private int price;
    @JsonProperty("registrar")
    private String registrar;
    @JsonProperty("old")
    private int old;
    @JsonProperty("delete_date")
    private String deleteDate;
    @JsonProperty("rkn")
    private boolean rkn;
    @JsonProperty("judicial")
    private boolean judicial;


    @JsonProperty("block")
    private boolean block;
}
