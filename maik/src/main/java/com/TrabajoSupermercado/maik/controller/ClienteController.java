package com.TrabajoSupermercado.maik.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.TrabajoSupermercado.maik.dto.ClienteRequest;
import com.TrabajoSupermercado.maik.dto.ClienteResponse;
import com.TrabajoSupermercado.maik.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse crear(@Valid @RequestBody ClienteRequest request) {
        return clienteService.crear(request);
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    public ClienteResponse obtener(@PathVariable("id") Long id) {
        return clienteService.obtener(id);
    }

    @GetMapping("/documento/{documento}")
    public ClienteResponse obtenerPorDocumento(@PathVariable("documento") String documento) {
        return clienteService.obtenerPorDocumento(documento);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizar(@PathVariable("id") Long id, @Valid @RequestBody ClienteRequest request) {
        return clienteService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable("id") Long id) {
        clienteService.eliminar(id);
    }
}
