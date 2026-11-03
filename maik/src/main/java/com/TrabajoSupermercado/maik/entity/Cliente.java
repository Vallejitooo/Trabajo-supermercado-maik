package com.TrabajoSupermercado.maik.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    // El documento del cliente es único
    @Column(nullable = false, unique = true, length = 30)
    private String documento;

    @Column(length = 30)
    private String telefono;

    // Un cliente tiene muchas ventas
    @OneToMany(mappedBy = "cliente")
    private List<Venta> ventas = new ArrayList<>();
}
