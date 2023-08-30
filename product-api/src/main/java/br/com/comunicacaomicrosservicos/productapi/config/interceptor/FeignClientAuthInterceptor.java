package br.com.comunicacaomicrosservicos.productapi.config.interceptor;

import br.com.comunicacaomicrosservicos.productapi.config.exception.ValidationException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static br.com.comunicacaomicrosservicos.productapi.config.RequestUtil.getCurrentRequest;

@Component
public class FeignClientAuthInterceptor implements RequestInterceptor {

    /*
    * Sempre que o Client for chamado, esta classe executa primeiro e pega o token de autenticacao
    * para toda a aplicacao
    * */


    private static final String AUTHORIZATION = "Authorization";
    private static final String TRANSACTION_ID = "transactionid";

    @Override
    public void apply(RequestTemplate template) {
        var currentRequest = getCurrentRequest();
        template.header(AUTHORIZATION, currentRequest.getHeader(AUTHORIZATION))
                .header(TRANSACTION_ID, currentRequest.getHeader(TRANSACTION_ID));
    }


}
