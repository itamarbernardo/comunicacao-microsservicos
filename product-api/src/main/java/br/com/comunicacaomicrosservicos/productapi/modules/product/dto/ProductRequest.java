package br.com.comunicacaomicrosservicos.productapi.modules.product.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductRequest {

    private String name;

    @JsonProperty("quantity_available")
    private Integer quantityAvailable;

    private Integer categoryId;
    private Integer supplierId;
}

