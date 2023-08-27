package br.com.comunicacaomicrosservicos.productapi.modules.category.dto;

import br.com.comunicacaomicrosservicos.productapi.modules.category.model.Category;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class CategoryResponse {
    //Nós nao mandamos a Model na Response, a gente manda essa classe com os dados da model -> Segurança
    private Integer id;
    private String description;

    public static CategoryResponse of(Category category){
        var response = new CategoryResponse();
        BeanUtils.copyProperties(category, response); //Copia os atributos de categoria para categoriaResponse
        return response;
    }
}
