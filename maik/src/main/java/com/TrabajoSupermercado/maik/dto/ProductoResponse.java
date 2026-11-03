package com.TrabajoSupermercado.maik.dto;

import com.TrabajoSupermercado.maik.entity.Producto;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        BigDecimal precio,
        Integer stock,
        String codigoBarras,
        Long categoriaId,
        String categoriaNombre,
        Long proveedorId,
        String proveedorNombre) {

    public static ProductoResponse from(Producto p) {
        return new ProductoResponse(
                p.getId(), p.getNombre(), p.getPrecio(), p.getStock(), p.getCodigoBarras(),
                p.getCategoria().getId(), p.getCategoria().getNombre(),
                p.getProveedor().getId(), p.getProveedor().getNombre());
    }
}
