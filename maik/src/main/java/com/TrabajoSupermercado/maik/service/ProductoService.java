package com.TrabajoSupermercado.maik.service;

import lombok.RequiredArgsConstructor;
import com.TrabajoSupermercado.maik.dto.ProductoRequest;
import com.TrabajoSupermercado.maik.dto.ProductoResponse;
import com.TrabajoSupermercado.maik.entity.Producto;
import com.TrabajoSupermercado.maik.exception.ConflictoException;
import com.TrabajoSupermercado.maik.exception.RecursoNoEncontradoException;
import com.TrabajoSupermercado.maik.repository.DetalleVentaRepository;
import com.TrabajoSupermercado.maik.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final CategoriaService categoriaService;
    private final ProveedorService proveedorService;

    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        String codigo = request.codigoBarras().trim();
        if (productoRepository.existsByCodigoBarras(codigo)) {
            throw new ConflictoException("Ya existe un producto con el código de barras " + codigo);
        }
        Producto producto = new Producto();
        copiarDatos(producto, request);
        return ProductoResponse.from(productoRepository.save(producto));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar() {
        return productoRepository.findAll().stream().map(ProductoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return ProductoResponse.from(buscarEntidad(id));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarEntidad(id);
        String codigo = request.codigoBarras().trim();
        if (productoRepository.existsByCodigoBarrasAndIdNot(codigo, id)) {
            throw new ConflictoException("Ya existe otro producto con el código de barras " + codigo);
        }
        copiarDatos(producto, request);
        return ProductoResponse.from(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscarEntidad(id);
        // Si el producto ya fue vendido, borrarlo dejaría ventas "huérfanas"
        if (detalleVentaRepository.existsByProductoId(id)) {
            throw new ConflictoException("No se puede eliminar el producto porque aparece en ventas registradas");
        }
        productoRepository.delete(producto);
    }

    // ---------- Consultas adicionales ----------

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorCategoria(Long categoriaId) {
        categoriaService.buscarEntidad(categoriaId); // lanza 404 si la categoría no existe
        return productoRepository.findByCategoriaId(categoriaId).stream().map(ProductoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre).stream()
                .map(ProductoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> stockBajo(Integer limite) {
        return productoRepository.findStockBajo(limite).stream().map(ProductoResponse::from).toList();
    }

    public Producto buscarEntidad(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id " + id));
    }

    private void copiarDatos(Producto producto, ProductoRequest request) {
        producto.setNombre(request.nombre().trim());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setCodigoBarras(request.codigoBarras().trim());
        producto.setCategoria(categoriaService.buscarEntidad(request.categoriaId()));
        producto.setProveedor(proveedorService.buscarEntidad(request.proveedorId()));
    }
}
