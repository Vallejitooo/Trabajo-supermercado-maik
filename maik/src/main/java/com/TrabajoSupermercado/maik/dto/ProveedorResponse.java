package com.TrabajoSupermercado.maik.dto;

import com.TrabajoSupermercado.maik.entity.Proveedor;

public record ProveedorResponse(Long id, String nombre, String telefono, String correo) {

    public static ProveedorResponse from(Proveedor p) {
        return new ProveedorResponse(p.getId(), p.getNombre(), p.getTelefono(), p.getCorreo());
    }
}
