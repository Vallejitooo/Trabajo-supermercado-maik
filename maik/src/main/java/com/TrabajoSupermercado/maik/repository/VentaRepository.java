package com.TrabajoSupermercado.maik.repository;

import com.TrabajoSupermercado.maik.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByClienteId(Long clienteId);

    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    boolean existsByClienteId(Long clienteId);
}
