package br.com.comunicacaomicrosservicos.productapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/")
public class StatusController {

    @GetMapping //Quando não coloca o endereço, é como se fosse o "/"
    public ResponseEntity<HashMap<String, Object>> getApiRootStatus(){ //Mapeia a raiz da API
        return ResponseEntity.ok(getSucessResponse());
    }
    @GetMapping("api/status")
    public ResponseEntity<HashMap<String, Object>> getApiStatus(){

        return ResponseEntity.ok(getSucessResponse());
    }

    private HashMap<String, Object> getSucessResponse(){
        var response = new HashMap<String, Object>();
        response.put("service", "Product-Api");
        response.put("status", "up");
        response.put("httpStatus", HttpStatus.OK.value());

        return response;
    }
}
