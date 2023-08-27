package br.com.comunicacaomicrosservicos.productapi.modules.supplier.repository;

import br.com.comunicacaomicrosservicos.productapi.modules.supplier.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

    List<Supplier> findByNameIgnoreCaseContaining(String name); //Ja faz a query automaticamente ignorando letra maiuscula/minuscula e procurando dentro da string %BUSCA%
}
