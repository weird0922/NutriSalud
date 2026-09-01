package com.nutriSalud.nutri.models;

import jakarta.persistence.*;

@Entity
@Table(name = "seguros")
public class Seguro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 15, unique = true)
    private TipoSeguro tipo;

    @Column(name = "nombre_legible", nullable = false, length = 120)
    private String nombreLegible;

    public Seguro() {}

    public Seguro(TipoSeguro tipo) {
        this.tipo = tipo;
        this.nombreLegible = tipo.getEtiqueta();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoSeguro getTipo() { return tipo; }
    public void setTipo(TipoSeguro tipo) {
        this.tipo = tipo;
        this.nombreLegible = tipo.getEtiqueta();
    }

    public String getNombreLegible() { return nombreLegible; }
    public void setNombreLegible(String nombreLegible) { this.nombreLegible = nombreLegible; }
}
