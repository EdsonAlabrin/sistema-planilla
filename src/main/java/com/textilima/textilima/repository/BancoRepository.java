package com.textilima.textilima.repository;

import com.textilima.textilima.entities.Banco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Marca esta interfaz como un componente de repositorio de Spring
public interface BancoRepository extends JpaRepository<Banco, Integer> {
    // Encuentra un banco por codigo 
    Optional<Banco> findByCodigoBanco(String codigoBanco);
    // Encuentra un banco por nombre
    Optional<Banco> findByNombreBanco(String nombreBanco);
}


