package domain.modelo.entities.Mediciones.Consumo;

import domain.modelo.entities.Mediciones.MedicionCompuesta.CategoriaLogistica;
import domain.modelo.entities.Mediciones.MedicionCompuesta.MedioTransporteLogistica;
import domain.modelo.entities.Mediciones.Unidad;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity @Table(name = "tipo_consumo")
@Getter @Setter
public class TipoConsumo {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "nombre")
    private String nombre;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "unidad")
    private Unidad unidad;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "categoria")
    private CategoriaLogistica categoria;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "transporte")
    private MedioTransporteLogistica transporte;

    @Column(name = "fk")
    private Double fk;


    public TipoConsumo(String nombre, Unidad unidad) {
        this.nombre = nombre;
        this.unidad = unidad;
    }

    public TipoConsumo() {
    }

    public TipoConsumo(String nombre, Unidad unidad, CategoriaLogistica categoria,
                       MedioTransporteLogistica transporte, Double fk) {
        this(nombre, unidad);
        this.categoria = categoria;
        this.transporte = transporte;
        this.fk = fk;
    }
}
