package com.TrabajoSupermercado.maik.dto;

import com.TrabajoSupermercado.maik.entity.Cliente;

public record ClienteResponse(Long id, String nombre, String documento, String telefono) {

    public static ClienteResponse from(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNombre(), c.getDocumento(), c.getTelefono());
    }
}
