package br.com.comunicacaomicrosservicos.productapi.modules.sales.rabbitmq;

import br.com.comunicacaomicrosservicos.productapi.modules.sales.dto.SalesConfirmationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor //Coloca no construtor apenas os atributos que estiverem como "final"
public class SalesConfirmationSender {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    @Value("${app-config.rabbit.exchange.product}")
    private String productTopicExchange;

    @Value("${app-config.rabbit.routingKey.sales-confirmation}")
    private String salesConfirmationKey;

    public void sendSalesConfirmationMessage(SalesConfirmationDTO message) {
        try {
            log.info("Sending message: {}", objectMapper.writeValueAsString(message));
            rabbitTemplate.convertAndSend(productTopicExchange, salesConfirmationKey, message);
            log.info("Message was sent successfully!");
        } catch (Exception ex) {
            log.info("Error while trying to send sales confirmation message: ", ex);
        }
    }
}