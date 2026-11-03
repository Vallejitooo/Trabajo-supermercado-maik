package com.TrabajoSupermercado.maik.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VentaRequest(
        @NotNull(message = "El cliente es obligatorio")
        Long clienteId,

        // @Valid hace que también se validen los elementos de la lista
        @NotEmpty(message = "La venta debe tener al menos un detalle")
        @Valid
        List<DetalleVentaRequest> detalles) {
}
