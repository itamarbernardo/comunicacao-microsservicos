package br.com.comunicacaomicrosservicos.productapi.config;

import br.com.comunicacaomicrosservicos.productapi.config.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtil {

    //Pega a Request Atual
    public static HttpServletRequest getCurrentRequest(){
        try{
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }catch (Exception ex){
            ex.printStackTrace();
            throw new ValidationException("A requisição atual não pode ser processada.");
        }
    }
}
