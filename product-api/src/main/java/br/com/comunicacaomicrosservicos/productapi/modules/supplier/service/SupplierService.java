package br.com.comunicacaomicrosservicos.productapi.modules.supplier.service;

import br.com.comunicacaomicrosservicos.productapi.config.exception.SuccessResponse;
import br.com.comunicacaomicrosservicos.productapi.config.exception.ValidationException;
import br.com.comunicacaomicrosservicos.productapi.modules.product.service.ProductService;
import br.com.comunicacaomicrosservicos.productapi.modules.supplier.dto.SupplierRequest;
import br.com.comunicacaomicrosservicos.productapi.modules.supplier.dto.SupplierResponse;
import br.com.comunicacaomicrosservicos.productapi.modules.supplier.model.Supplier;
import br.com.comunicacaomicrosservicos.productapi.modules.supplier.repository.SupplierRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@AllArgsConstructor(onConstructor_ = { @Lazy })
public class SupplierService {

    private final SupplierRepository supplierRepository;
    @Lazy
    private final ProductService productService;

    public List<SupplierResponse> findAll() {
        return supplierRepository
                .findAll()
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public List<SupplierResponse> findByName(String name) {
        if (isEmpty(name)) {
            throw new ValidationException("O Nome do Fornecedor precisa ser informado.");
        }
        return supplierRepository
                .findByNameIgnoreCaseContaining(name)
                .stream()
                .map(SupplierResponse::of)
                .collect(Collectors.toList());
    }

    public SupplierResponse findByIdResponse(Integer id) {
        return SupplierResponse.of(findById(id));
    }
    public Supplier findById(Integer id) {
        validateInformedId(id);
        return supplierRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException("Não há um Fornecedor com este ID."));
    }
    public SupplierResponse save(SupplierRequest request){
        validateSupplierNameInformed(request);
        var supplier = supplierRepository.save(Supplier.of(request));

        return SupplierResponse.of(supplier);
    }

    public SupplierResponse update(SupplierRequest request, Integer id) {
        validateSupplierNameInformed(request);
        validateInformedId(id);
        var supplier = Supplier.of(request);
        supplier.setId(id); //Quando setamos o Id, o Spring sabe que é uma atualizacao apenas
        supplierRepository.save(supplier);
        return SupplierResponse.of(supplier);
    }
    public SuccessResponse delete(Integer id) {
        validateInformedId(id);
        if (productService.existsBySupplierId(id)) {
            throw new ValidationException("Você não pode deletar este Fornecedor porque ele está associado a um ou mais produtos.");
        }
        supplierRepository.deleteById(id);
        return SuccessResponse.create("O Fornecedor foi deletado com sucesso.");
    }
    private void validateSupplierNameInformed(SupplierRequest request) {
        if (isEmpty(request.getName())) {
            throw new ValidationException("The supplier's name was not informed.");
        }
    }

    private void validateInformedId(Integer id) {
        if (isEmpty(id)) {
            throw new ValidationException("O ID do Fornecedor precisa ser informado.");
        }
    }

}
