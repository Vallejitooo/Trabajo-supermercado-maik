package com.TrabajoSupermercado.maik.repository;

import com.TrabajoSupermercado.maik.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    boolean existsByProductoId(Long productoId);
}
