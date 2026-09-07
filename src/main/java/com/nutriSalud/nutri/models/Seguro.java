package com.nutriSalud.nutri.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seguros")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Seguro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 15, unique = true)
    private TipoSeguro tipo;

    @Column(name = "nombre_legible", nullable = false, length = 120)
    private String nombreLegible;

    public Seguro(TipoSeguro tipo) {
        this.tipo = tipo;
        this.nombreLegible = tipo.getEtiqueta();
    }

    public void setTipo(TipoSeguro tipo) {
        this.tipo = tipo;
        if (tipo != null) {
            this.nombreLegible = tipo.getEtiqueta();
        }
    }
}
