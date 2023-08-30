package br.com.comunicacaomicrosservicos.productapi.modules.sales.client;

import br.com.comunicacaomicrosservicos.productapi.modules.sales.dto.SalesProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Optional;

@FeignClient(name = "salesClient", contextId = "salesClient", url = "${app-config.services.sales}")
public interface SalesClient {

    @GetMapping("/api/orders/product/{productId}") //Vai fazer uma requisicao a SALES-API pra pegar os IDs das vendas que contém o produto
    Optional<SalesProductResponse> findSalesByProductId(@PathVariable Integer productId);
}
