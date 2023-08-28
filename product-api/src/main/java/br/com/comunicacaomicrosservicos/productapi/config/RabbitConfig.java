package br.com.comunicacaomicrosservicos.productapi.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    //Pega as variaveis definidas em application.yml
    @Value("${app-config.rabbit.exchange.product}")
    private String productTopicExchange;

    @Value("${app-config.rabbit.routingKey.product-stock}")
    private String productStockKey;

    @Value("${app-config.rabbit.routingKey.sales-confirmation}")
    private String salesConfirmationKey;

    @Value("${app-config.rabbit.queue.product-stock}")
    private String productStockMq;

    @Value("${app-config.rabbit.queue.sales-confirmation}")
    private String salesConfirmationMq;

    //Definicao do Topic
    @Bean
    public TopicExchange productTopicExchange() {
        return new TopicExchange(productTopicExchange);
    }

    //Definicao da Fila
    @Bean
    public Queue productStockMq() {
        return new Queue(productStockMq, true);
    }

    @Bean
    public Queue salesConfirmationMq() {
        return new Queue(salesConfirmationMq, true);
    }

    @Bean
    public Binding productStockMqBinding(TopicExchange topicExchange) {
        return BindingBuilder
                .bind(productStockMq()) //Essa fila criada
                .to(topicExchange) //Vai estar vinculada a este Exchange
                .with(productStockKey);  //Através dessa chave
        //Então se publicarmos uma mensagem nesse tópico, através dessa chave, ele já redireciona a mensagem para esta fila
    }

    @Bean
    public Binding salesConfirmationMqBinding(TopicExchange topicExchange) {
        return BindingBuilder
                .bind(salesConfirmationMq())
                .to(topicExchange)
                .with(salesConfirmationKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}