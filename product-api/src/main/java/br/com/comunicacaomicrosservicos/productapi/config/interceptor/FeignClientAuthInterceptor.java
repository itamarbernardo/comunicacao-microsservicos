package br.com.comunicacaomicrosservicos.productapi.config.interceptor;

import br.com.comunicacaomicrosservicos.productapi.config.exception.ValidationException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class FeignClientAuthInterceptor implements RequestInterceptor {

    /*
    * Sempre que o Client for chamado, esta classe executa primeiro e pega o token de autenticacao
    * para toda a aplicacao
    * */
    private static final String AUTHORIZATION = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        var currentRequest = getCurrentRequest();
        template.header(AUTHORIZATION, currentRequest.getHeader(AUTHORIZATION));
    }

    private HttpServletRequest getCurrentRequest(){
        try{
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }catch (Exception ex){
            ex.printStackTrace();
            throw new ValidationException("A requisição atual não pode ser processada.");
        }
    }
}
