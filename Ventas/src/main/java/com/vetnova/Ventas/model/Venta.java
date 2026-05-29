package com.vetnova.Ventas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productoId; // Solo guardamos el ID, el inventario es dueño del resto de los datos

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double total;

    private LocalDateTime fechaVenta;
}
