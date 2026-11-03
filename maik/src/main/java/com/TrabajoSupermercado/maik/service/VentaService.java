package com.TrabajoSupermercado.maik.service;

import lombok.RequiredArgsConstructor;
import com.TrabajoSupermercado.maik.dto.DetalleVentaRequest;
import com.TrabajoSupermercado.maik.dto.VentaRequest;
import com.TrabajoSupermercado.maik.dto.VentaResponse;
import com.TrabajoSupermercado.maik.entity.Cliente;
import com.TrabajoSupermercado.maik.entity.DetalleVenta;
import com.TrabajoSupermercado.maik.entity.Producto;
import com.TrabajoSupermercado.maik.entity.Venta;
import com.TrabajoSupermercado.maik.exception.RecursoNoEncontradoException;
import com.TrabajoSupermercado.maik.exception.ReglaNegocioException;
import com.TrabajoSupermercado.maik.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    /**
     * Registra una venta. Todo ocurre dentro de UNA transacción: si algo falla
     * (producto inexistente, stock insuficiente...) se hace rollback y no queda
     * ningún cambio a medias en la base de datos.
     */
    @Transactional
    public VentaResponse registrar(VentaRequest request) {
        // 1. Verificar que el cliente exista (lanza 404 si no)
        Cliente cliente = clienteService.buscarEntidad(request.clienteId());

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(LocalDateTime.now()); // 6. fecha actual

        BigDecimal total = BigDecimal.ZERO;

        for (DetalleVentaRequest item : request.detalles()) {
            // 1. Verificar que el producto exista (lanza 404 si no)
            Producto producto = productoService.buscarEntidad(item.productoId());

            // 2. Verificar stock suficiente
            if (producto.getStock() < item.cantidad()) {
                throw new ReglaNegocioException("Stock insuficiente para el producto '" + producto.getNombre()
                        + "'. Disponible: " + producto.getStock() + ", solicitado: " + item.cantidad());
            }

            // 3. Tomar el precio actual del producto
            BigDecimal precioUnitario = producto.getPrecio();

            // 4. Subtotal = cantidad x precioUnitario
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.cantidad()));

            // 5. Descontar stock (Hibernate hace el UPDATE al terminar la transacción)
            producto.setStock(producto.getStock() - item.cantidad());

            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);
            venta.agregarDetalle(detalle);

            total = total.add(subtotal);
        }

        venta.setTotal(total);
        // El cascade ALL guarda también los detalles
        return VentaResponse.from(ventaRepository.save(venta));
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listar() {
        return ventaRepository.findAll().stream().map(VentaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public VentaResponse obtener(Long id) {
        return VentaResponse.from(buscarEntidad(id));
    }

    /** Elimina la venta y devuelve al stock lo que se había vendido. */
    @Transactional
    public void eliminar(Long id) {
        Venta venta = buscarEntidad(id);
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
        }
        ventaRepository.delete(venta);
    }

    // ---------- Consultas adicionales ----------

    @Transactional(readOnly = true)
    public List<VentaResponse> listarPorCliente(Long clienteId) {
        clienteService.buscarEntidad(clienteId); // 404 si el cliente no existe
        return ventaRepository.findByClienteId(clienteId).stream().map(VentaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> listarPorFechas(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new ReglaNegocioException("La fecha de inicio no puede ser posterior a la fecha fin");
        }
        // Desde las 00:00 del día inicio hasta las 23:59:59 del día fin
        LocalDateTime desde = inicio.atStartOfDay();
        LocalDateTime hasta = fin.atTime(LocalTime.MAX);
        return ventaRepository.findByFechaBetween(desde, hasta).stream().map(VentaResponse::from).toList();
    }

    private Venta buscarEntidad(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada con id " + id));
    }
}
