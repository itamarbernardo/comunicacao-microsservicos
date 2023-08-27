package br.com.comunicacaomicrosservicos.productapi.modules.supplier.dto;

import br.com.comunicacaomicrosservicos.productapi.modules.category.model.Category;
import br.com.comunicacaomicrosservicos.productapi.modules.supplier.model.Supplier;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class SupplierResponse {
    //Nós nao mandamos a Model na Response, a gente manda essa classe com os dados da model -> Segurança
    private Integer id;
    private String name;

    public static SupplierResponse of(Supplier supplier){
        var response = new SupplierResponse();
        BeanUtils.copyProperties(supplier, response); //Copia os atributos de categoria para categoriaResponse
        return response;
    }
}
