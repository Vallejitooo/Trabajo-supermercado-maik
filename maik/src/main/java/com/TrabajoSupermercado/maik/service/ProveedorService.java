package com.TrabajoSupermercado.maik.service;

import lombok.RequiredArgsConstructor;
import com.TrabajoSupermercado.maik.dto.ProveedorRequest;
import com.TrabajoSupermercado.maik.dto.ProveedorResponse;
import com.TrabajoSupermercado.maik.entity.Proveedor;
import com.TrabajoSupermercado.maik.exception.ConflictoException;
import com.TrabajoSupermercado.maik.exception.RecursoNoEncontradoException;
import com.TrabajoSupermercado.maik.repository.ProductoRepository;
import com.TrabajoSupermercado.maik.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;

    @Transactional
    public ProveedorResponse crear(ProveedorRequest request) {
        Proveedor proveedor = new Proveedor();
        copiarDatos(proveedor, request);
        return ProveedorResponse.from(proveedorRepository.save(proveedor));
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponse> listar() {
        return proveedorRepository.findAll().stream().map(ProveedorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ProveedorResponse obtener(Long id) {
        return ProveedorResponse.from(buscarEntidad(id));
    }

    @Transactional
    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
        Proveedor proveedor = buscarEntidad(id);
        copiarDatos(proveedor, request);
        return ProveedorResponse.from(proveedorRepository.save(proveedor));
    }

    @Transactional
    public void eliminar(Long id) {
        Proveedor proveedor = buscarEntidad(id);
        if (productoRepository.existsByProveedorId(id)) {
            throw new ConflictoException("No se puede eliminar el proveedor porque tiene productos asociados");
        }
        proveedorRepository.delete(proveedor);
    }

    public Proveedor buscarEntidad(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proveedor no encontrado con id " + id));
    }

    private void copiarDatos(Proveedor proveedor, ProveedorRequest request) {
        proveedor.setNombre(request.nombre().trim());
        proveedor.setTelefono(request.telefono());
        proveedor.setCorreo(request.correo());
    }
}
