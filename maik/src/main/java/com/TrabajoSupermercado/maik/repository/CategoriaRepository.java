package com.TrabajoSupermercado.maik.repository;

import com.TrabajoSupermercado.maik.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
