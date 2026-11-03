package com.TrabajoSupermercado.maik.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.TrabajoSupermercado.maik.dto.VentaRequest;
import com.TrabajoSupermercado.maik.dto.VentaResponse;
import com.TrabajoSupermercado.maik.service.VentaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaResponse registrar(@Valid @RequestBody VentaRequest request) {
        return ventaService.registrar(request);
    }

    @GetMapping
    public List<VentaResponse> listar() {
        return ventaService.listar();
    }

    @GetMapping("/{id}")
    public VentaResponse obtener(@PathVariable Long id) {
        return ventaService.obtener(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        ventaService.eliminar(id);
    }

    // ---------- Consultas adicionales ----------

    @GetMapping("/cliente/{clienteId}")
    public List<VentaResponse> porCliente(@PathVariable Long clienteId) {
        return ventaService.listarPorCliente(clienteId);
    }

    // Formato de fecha: yyyy-MM-dd  (ej. /api/ventas/fecha?inicio=2026-01-01&fin=2026-12-31)
    @GetMapping("/fecha")
    public List<VentaResponse> porFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ventaService.listarPorFechas(inicio, fin);
    }
}
