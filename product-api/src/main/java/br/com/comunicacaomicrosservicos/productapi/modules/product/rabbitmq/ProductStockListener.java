package br.com.comunicacaomicrosservicos.productapi.modules.product.rabbitmq;

import br.com.comunicacaomicrosservicos.productapi.modules.product.dto.ProductStockDTO;
import br.com.comunicacaomicrosservicos.productapi.modules.product.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductStockListener {

    @Autowired
    private ProductService productService;
    @Autowired
    private ObjectMapper objectMapper;

    @RabbitListener(queues = "${app-config.rabbit.queue.product-stock}")
    public void recieveProductStockMessage(ProductStockDTO product) throws JsonProcessingException {
        log.info("Recebendo mensagem com conteudo: {} e TransactionId {}",
                objectMapper.writeValueAsString(product), product.getTransactionid());
        //Lê a fila e manda pro ProductService pra atualizar o estoque dos produtos
        productService.updateProductStock(product);
    }
}