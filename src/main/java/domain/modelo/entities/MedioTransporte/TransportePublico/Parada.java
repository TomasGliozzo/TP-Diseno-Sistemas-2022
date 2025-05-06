package domain.modelo.entities.MedioTransporte.TransportePublico;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "parada")
@Getter @Setter
public class Parada {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "direccion",referencedColumnName = "id")
    private Direccion direccion;

    @Column(name = "distancia_dire_anterior")
    private Double distanciaDireccionAnterior;

    @Column(name = "distancia_dire_siguiente")
    private Double distanciaDireccionSiguiente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transporte_publico_id",referencedColumnName = "id")
    private TransportePublico transportePublico;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado = Estado.ACTIVO;


    public Parada(String nombre, String descripcion, Direccion direccion, Double distanciaDireccionAnterior, Double distanciaDireccionSiguiente){
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.distanciaDireccionAnterior = distanciaDireccionAnterior;
        this.distanciaDireccionSiguiente = distanciaDireccionSiguiente;
    }

    public Parada() {}
}
