package domain.modelo.entities.Mediciones.TipoActividad;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Mediciones.Unidad;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Entity
@Table(name = "fe")
@Setter @Getter
public class FE {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "valor")
    private Double valor;

    @Enumerated(EnumType.STRING)
    private Unidad unidad;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado = Estado.ACTIVO;


    public FE(Double valor, Unidad unidad){
        this.valor = valor;
        this.unidad = unidad;
    }

    public FE() {}
}
