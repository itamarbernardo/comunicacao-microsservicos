package br.com.comunicacaomicrosservicos.productapi.modules.category.service;

import br.com.comunicacaomicrosservicos.productapi.config.exception.SuccessResponse;
import br.com.comunicacaomicrosservicos.productapi.config.exception.ValidationException;
import br.com.comunicacaomicrosservicos.productapi.modules.category.dto.CategoryResponse;
import br.com.comunicacaomicrosservicos.productapi.modules.category.repository.CategoryRepository;
import br.com.comunicacaomicrosservicos.productapi.modules.category.dto.CategoryRequest;
import br.com.comunicacaomicrosservicos.productapi.modules.category.model.Category;
import br.com.comunicacaomicrosservicos.productapi.modules.product.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@AllArgsConstructor(onConstructor_ = { @Lazy})
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Lazy
    private final ProductService productService;
    public List<CategoryResponse> findByDescription(String description) {
        if (isEmpty(description)) {
            throw new ValidationException("A descricao da categoria não foi informada.");
        }
        return categoryRepository
                .findByDescriptionIgnoreCaseContaining(description)
                .stream()
                .map(CategoryResponse::of) //Issoeh uma funcao lambda: é a mesma coisa de escrever map(category -> CategoryResponse.of(category) => Vai Converter cada categoria encontrada na query em CategoryResponse
                .collect(Collectors.toList());
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository
                .findAll()
                .stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    public CategoryResponse findByIdResponse(Integer id) {
        return CategoryResponse.of(findById(id)); //Procura a categoria e retorna convertendo em CategoryResponse
    }
    public Category findById(Integer id) {
        validateInformedId(id);
        return categoryRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("Não há uma categoria com este ID."));
    }

    public CategoryResponse save(CategoryRequest request){
        validateCategoryNameInformed(request);
        var category = categoryRepository.save(Category.of(request));

        return CategoryResponse.of(category);
    }

    public CategoryResponse update(CategoryRequest request,
                                   Integer id) {
        validateCategoryNameInformed(request);
        validateInformedId(id);
        var category = Category.of(request);
        category.setId(id); //Quando setamos o ID, o Spring identifica que é um Update apenas
        categoryRepository.save(category);
        return CategoryResponse.of(category);
    }
    public SuccessResponse delete(Integer id) {
        validateInformedId(id);
        if (productService.existsByCategoryId(id)) {
            throw new ValidationException("Você não pode deletar esta categoria pois ela está associada a um ou mais produtos.");
        }
        categoryRepository.deleteById(id);
        return SuccessResponse.create("A categoria foi deletada com sucesso.");
    }
    private void validateInformedId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("The category ID must be informed.");
        }
    }
    private void validateCategoryNameInformed(CategoryRequest request){
        System.out.println("DESCRICAO: " + request.getDescription());
        if(isEmpty(request.getDescription())){
            throw new ValidationException("A descrição da categoria não foi informada");
        }
    }
}
