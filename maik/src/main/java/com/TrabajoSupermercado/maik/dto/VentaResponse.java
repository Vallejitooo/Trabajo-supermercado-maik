package com.TrabajoSupermercado.maik.dto;

import com.TrabajoSupermercado.maik.entity.Venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
        Long id,
        LocalDateTime fecha,
        BigDecimal total,
        Long clienteId,
        String clienteNombre,
        List<DetalleVentaResponse> detalles) {

    public static VentaResponse from(Venta v) {
        return new VentaResponse(
                v.getId(), v.getFecha(), v.getTotal(),
                v.getCliente().getId(), v.getCliente().getNombre(),
                v.getDetalles().stream().map(DetalleVentaResponse::from).toList());
    }
}
