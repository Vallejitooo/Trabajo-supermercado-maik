package com.TrabajoSupermercado.maik.service;

import lombok.RequiredArgsConstructor;
import com.TrabajoSupermercado.maik.dto.ClienteRequest;
import com.TrabajoSupermercado.maik.dto.ClienteResponse;
import com.TrabajoSupermercado.maik.entity.Cliente;
import com.TrabajoSupermercado.maik.exception.ConflictoException;
import com.TrabajoSupermercado.maik.exception.RecursoNoEncontradoException;
import com.TrabajoSupermercado.maik.repository.ClienteRepository;
import com.TrabajoSupermercado.maik.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;

    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        String documento = request.documento().trim();
        if (clienteRepository.existsByDocumento(documento)) {
            throw new ConflictoException("Ya existe un cliente con el documento " + documento);
        }
        Cliente cliente = new Cliente();
        copiarDatos(cliente, request);
        return ClienteResponse.from(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream().map(ClienteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtener(Long id) {
        return ClienteResponse.from(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorDocumento(String documento) {
        return clienteRepository.findByDocumento(documento)
                .map(ClienteResponse::from)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con documento " + documento));
    }

    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscarEntidad(id);
        String documento = request.documento().trim();
        if (clienteRepository.existsByDocumentoAndIdNot(documento, id)) {
            throw new ConflictoException("Ya existe otro cliente con el documento " + documento);
        }
        copiarDatos(cliente, request);
        return ClienteResponse.from(clienteRepository.save(cliente));
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente cliente = buscarEntidad(id);
        if (ventaRepository.existsByClienteId(id)) {
            throw new ConflictoException("No se puede eliminar el cliente porque tiene ventas asociadas");
        }
        clienteRepository.delete(cliente);
    }

    public Cliente buscarEntidad(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id " + id));
    }

    private void copiarDatos(Cliente cliente, ClienteRequest request) {
        cliente.setNombre(request.nombre().trim());
        cliente.setDocumento(request.documento().trim());
        cliente.setTelefono(request.telefono());
    }
}
