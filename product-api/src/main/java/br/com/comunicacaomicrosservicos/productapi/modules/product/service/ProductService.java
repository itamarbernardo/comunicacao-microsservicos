package br.com.comunicacaomicrosservicos.productapi.modules.product.service;

import br.com.comunicacaomicrosservicos.productapi.config.exception.SuccessResponse;
import br.com.comunicacaomicrosservicos.productapi.config.exception.ValidationException;
import br.com.comunicacaomicrosservicos.productapi.modules.category.service.CategoryService;
import br.com.comunicacaomicrosservicos.productapi.modules.product.dto.*;
import br.com.comunicacaomicrosservicos.productapi.modules.product.model.Product;
import br.com.comunicacaomicrosservicos.productapi.modules.product.repository.ProductRepository;
import br.com.comunicacaomicrosservicos.productapi.modules.sales.client.SalesClient;
import br.com.comunicacaomicrosservicos.productapi.modules.sales.dto.SalesConfirmationDTO;
import br.com.comunicacaomicrosservicos.productapi.modules.sales.dto.SalesProductResponse;
import br.com.comunicacaomicrosservicos.productapi.modules.sales.enums.SalesStatus;
import br.com.comunicacaomicrosservicos.productapi.modules.sales.rabbitmq.SalesConfirmationSender;
import br.com.comunicacaomicrosservicos.productapi.modules.supplier.service.SupplierService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.comunicacaomicrosservicos.productapi.config.RequestUtil.getCurrentRequest;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j //Permite que coloquemos log's
@Service
@AllArgsConstructor
public class ProductService {

    private static final String TRANSACTION_ID = "transactionid";
    private static final String SERVICE_ID = "serviceid";

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SalesConfirmationSender salesConfirmationSender;
    @Autowired
    private SalesClient salesClient;

    public ProductResponse save(ProductRequest request){
        validateProductDataInformed(request);
        validateCategoryAndSupplierIdInformed(request);

        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = productRepository.save(Product.of(request, supplier, category));

        return ProductResponse.of(product);
    }

    public List<ProductResponse> findAll() {
        return productRepository
                .findAll()
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByName(String name) {
        if (isEmpty(name)) {
            throw new ValidationException("O Nome do Produto precisa ser informado.");
        }
        return productRepository
                .findByNameIgnoreCaseContaining(name)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findBySupplierId(Integer supplierId) {
        if (isEmpty(supplierId)) {
            throw new ValidationException("O ID do Produto precisa ser informado.");
        }
        return productRepository
                .findBySupplierId(supplierId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> findByCategoryId(Integer categoryId) {
        if (isEmpty(categoryId)) {
            throw new ValidationException("Deve ser informado o nome do ID da categoria do produto.");
        }
        return productRepository
                .findByCategoryId(categoryId)
                .stream()
                .map(ProductResponse::of)
                .collect(Collectors.toList());
    }

    public ProductResponse findByIdResponse(Integer id) {
        return ProductResponse.of(findById(id));
    }

    public Product findById(Integer id) {
        validateInformedId(id);
        return productRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("Não há produto com este ID"));
    }

    private void validateInformedId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("O ID do Fornecedor precisa ser informado");
        }
    }
    private void validateProductDataInformed(ProductRequest request){
        if(isEmpty(request.getName())){
            throw new ValidationException("O nome do Produto não foi informado");
        }
        if(isEmpty(request.getQuantityAvailable())){
            throw new ValidationException("A quantidade disponível do Produto não foi informado");
        }
        if(request.getQuantityAvailable() <= 0){
            throw new ValidationException("A quantidade disponível do Produto tem que ser maior ou igual a 1");
        }
    }

    private void validateCategoryAndSupplierIdInformed(ProductRequest request){
        if(isEmpty(request.getCategoryId())){
            throw new ValidationException("O Id da Categoria não foi informado");
        }
        if(isEmpty(request.getSupplierId())){
            throw new ValidationException("O Id do Fornecedor não foi informado");
        }
    }

    public Boolean existsByCategoryId(Integer categoryId) {
        return productRepository.existsByCategoryId(categoryId);
    }

    public Boolean existsBySupplierId(Integer supplierId) {
        return productRepository.existsBySupplierId(supplierId);
    }

    public ProductResponse update(ProductRequest request, Integer id) {
        validateProductDataInformed(request);
        validateInformedId(id);
        validateCategoryAndSupplierIdInformed(request);
        var category = categoryService.findById(request.getCategoryId());
        var supplier = supplierService.findById(request.getSupplierId());
        var product = Product.of(request, supplier, category);
        product.setId(id); //QUando Setamos o ID, o Spring identifica que se trata de uma atualizacao, então quando salvar, ele vai salvar no lugar do produto com o mesmo ID, atualizando os campos
        productRepository.save(product);
        return ProductResponse.of(product);
    }
    public SuccessResponse delete(Integer id) {
        validateInformedId(id);

        if (!productRepository.existsById(id)) {
            throw new ValidationException("The product does not exists.");
        }
        var sales = getSalesByProductId(id);
        if (!isEmpty(sales.getSalesIds())) {
            throw new ValidationException("The product cannot be deleted. There are sales for it.");
        }

        productRepository.deleteById(id);
        return SuccessResponse.create("O Produto foi deletado com sucesso.");
    }

    public void updateProductStock(ProductStockDTO product) {
        try {
            validateStockUpdateData(product);
            updateStock(product);
        } catch (Exception ex) {
            log.error("Erro ao tentar atualizar o Estoque for message with error: {}", ex.getMessage(), ex);
            var rejectedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.REJECTED, product.getTransactionid());
            salesConfirmationSender.sendSalesConfirmationMessage(rejectedMessage);
        }
    }

    @Transactional //Se algo der errado, ele faz um Rollback no Banco de dados
    private void updateStock(ProductStockDTO product) {
        var productsForUpdate = new ArrayList<Product>();
        product
                .getProducts()
                .forEach(salesProduct -> {
                    var existingProduct = findById(salesProduct.getProductId());
                    validateQuantityInStock(salesProduct, existingProduct);
                    existingProduct.updateStock(salesProduct.getQuantity());
                    productsForUpdate.add(existingProduct);
                });
        if (!isEmpty(productsForUpdate)) {
            productRepository.saveAll(productsForUpdate);
            var approvedMessage = new SalesConfirmationDTO(product.getSalesId(), SalesStatus.APPROVED, product.getTransactionid());
            salesConfirmationSender.sendSalesConfirmationMessage(approvedMessage);
        }
    }

    private void validateQuantityInStock(ProductQuantityDTO salesProduct,
                                         Product existingProduct) {
        if (salesProduct.getQuantity() > existingProduct.getQuantityAvailable()) {
            throw new ValidationException(
                    String.format("Não há unidades suficientes do produto %s em estoque.", existingProduct.getId()));
        }
    }
    private void validateStockUpdateData(ProductStockDTO product) {
        if (isEmpty(product)
                || isEmpty(product.getSalesId())) {
            throw new ValidationException("Os produtos e o ID da Venda precisam ser informados.");
        }
        if (isEmpty(product.getProducts())) {
            throw new ValidationException("Os produdos da venda precisam ser informados.");
        }
        product
                .getProducts()
                .forEach(salesProduct -> {
                    if (isEmpty(salesProduct.getQuantity())
                            || isEmpty(salesProduct.getProductId())) {
                        throw new ValidationException("O productID e a quantidade precisa ser informada.");
                    }
                });
    }


    public ProductSalesResponse findProductSales(Integer id) {
        var product = findById(id);
        try {
            //var sales = salesClient.findSalesByProductId(product.getId()).orElseThrow( () -> new ValidationException("Não foram encontradas vendas para este produto"));
            var sales = getSalesByProductId(product.getId());
            return ProductSalesResponse.of(product, sales.getSalesIds());
        }catch (Exception ex){
            ex.printStackTrace();
            throw new ValidationException("Houve um erro ao tentar recuperar as vendas do produto");
        }
    }

    private SalesProductResponse getSalesByProductId(Integer productId) {
        try {
            var currentRequest = getCurrentRequest();
            var transactionid = currentRequest.getHeader(TRANSACTION_ID);
            var serviceid = currentRequest.getAttribute(SERVICE_ID);
            log.info("Sending GET request to orders by productId with data {} | [transactionID: {} | serviceID: {}]",
                    productId, transactionid, serviceid);
            var response = salesClient
                    .findSalesByProductId(productId)
                    .orElseThrow(() -> new ValidationException("The sales was not found by this product."));
            log.info("Recieving response from orders by productId with data {} | [transactionID: {} | serviceID: {}]",
                    new ObjectMapper().writeValueAsString(response), transactionid, serviceid);
            return response;
        } catch (Exception ex) {
            log.error("Error trying to call Sales-API: {}", ex.getMessage());
            throw new ValidationException("The sales could not be found.");
        }
    }
    public SuccessResponse checkProductsStock(ProductCheckStockRequest request) {
        try {
            var currentRequest = getCurrentRequest();
            var transactionid = currentRequest.getHeader(TRANSACTION_ID);
            var serviceid = currentRequest.getAttribute(SERVICE_ID);
            log.info("Request to POST product stock with data {} | [transactionID: {} | serviceID: {}]",
                    new ObjectMapper().writeValueAsString(request), transactionid, serviceid);

            log.info("Request {} ", request);
            if (isEmpty(request) || isEmpty(request.getProducts())) {

                throw new ValidationException("O Request data e os produtos precisam ser informados.");
            }
            request
                    .getProducts()
                    .forEach(this::validateStock);
            var response = SuccessResponse.create("O estoque está OK!");

            log.info("Response to POST product stock with data {} | [transactionID: {} | serviceID: {}]",
                    new ObjectMapper().writeValueAsString(response), transactionid, serviceid);
            return response;
        } catch (Exception ex) {
            throw new ValidationException(ex.getMessage());
        }
    }

    private void validateStock(ProductQuantityDTO productQuantity) {
        if (isEmpty(productQuantity.getProductId()) || isEmpty(productQuantity.getQuantity())) {
            throw new ValidationException("ID do Produto e a quantidade precisam ser informados.");
        }
        var product = findById(productQuantity.getProductId());
        if (productQuantity.getQuantity() > product.getQuantityAvailable()) {
            throw new ValidationException(String.format("O produto %s está sem estoque.", product.getId()));
        }
    }

}
