package br.com.comunicacaomicrosservicos.productapi.config.interceptor;

import java.util.List;

public class Urls {

    //URLs protegidas na aplicação (vão precisar de Token (Authorization) e TransactionId
    public static final List<String> PROTECTED_URLs = List.of(
            "api/product",
            "api/supplier",
            "api/category"
            );
}
