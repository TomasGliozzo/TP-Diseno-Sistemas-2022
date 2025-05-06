package domain.modelo.entities.MedioTransporte;


import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "medio_de_transporte")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_medio_transporte")
@Getter @Setter
public abstract class MedioTransporte {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_fe",referencedColumnName = "id")
    public FE fe;

    @Transient
    ServicioDistancia servicioDistancia = ServicioDistancia.instancia();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado = Estado.ACTIVO;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;


    public MedioTransporte() {
    }


    abstract public boolean esCompartido();

    public Double calcularDistancia(Direccion direccionInicio,Direccion direccionFin) {
        return servicioDistancia.distancia(direccionInicio, direccionFin).valor;
    }
}
