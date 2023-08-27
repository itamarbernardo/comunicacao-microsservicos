package br.com.comunicacaomicrosservicos.productapi.modules.product.repository;

import br.com.comunicacaomicrosservicos.productapi.modules.product.model.Product;
import br.com.comunicacaomicrosservicos.productapi.modules.supplier.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByNameIgnoreCaseContaining(String name); //Ja faz a query automaticamente ignorando letra maiuscula/minuscula e procurando dentro da string %BUSCA%

    List<Product> findByCategoryId(Integer id); //Query automatica do Spring

    List<Product> findBySupplierId(Integer id); //Query automatica do Spring

    Boolean existsByCategoryId(Integer id);

    Boolean existsBySupplierId(Integer id);
}
