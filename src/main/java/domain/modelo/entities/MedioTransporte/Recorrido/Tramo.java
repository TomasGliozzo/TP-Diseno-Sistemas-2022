package domain.modelo.entities.MedioTransporte.Recorrido;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.MedioTransporte.MedioTransporte;
import domain.exception.Excepcion;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tramo")
@Getter @Setter
public class Tramo {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    private MedioTransporte medioTransporteUtilizado;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_direccion_inicial",referencedColumnName = "id")
    private Direccion direccionInicio;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_direccion_final",referencedColumnName = "id")
    private Direccion direccionFin;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Miembro> miembrosVinculados;

    @Transient
    private double distancia = -1.0;
    @Transient
    private double HC = -1.0;
    @Transient
    private final String mensajeExcepcionMT_NoEsCompartido = "El Medio de transporte utilizado no es Compartido";

    @ManyToOne
    @JoinColumn(name = "trayecto_id",referencedColumnName = "id")
    private Trayecto trayecto;

    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado = Estado.ACTIVO;


    public Tramo(MedioTransporte mTU, String description, Direccion direccionInicio, Direccion direccionFin, Miembro miembro) {
        this(miembro);
        this.medioTransporteUtilizado = mTU;
        this.descripcion = description;
        this.direccionInicio = direccionInicio;
        this.direccionFin = direccionFin;
        miembrosVinculados.add(miembro);
    }

    public Tramo(Miembro miembro) {
        this();
        miembrosVinculados.add(miembro);
    }

    public Tramo() {
        this.miembrosVinculados = new ArrayList<>();
    }

    public void agregarMiembro(Miembro miembro){
        if (!medioTransporteUtilizado.esCompartido())
            this.lanzarExepcion(mensajeExcepcionMT_NoEsCompartido);
        else {
            this.miembrosVinculados.add(miembro);
            HC = distancia * medioTransporteUtilizado.getFe().getValor()/miembrosVinculados.size();
        }
    }

    public void calcularHCTramo() {
        HC = this.getDistancia() * medioTransporteUtilizado.getFe().getValor()/miembrosVinculados.size();
    }

    public Double getHC(){
        calcularHCTramo();
        return HC;
    }

    public void calcularDistancia() {
        distancia = medioTransporteUtilizado.calcularDistancia(this.direccionInicio, this.direccionFin);
    }

    public Double getDistancia(){
        if (distancia == -1.0)
            calcularDistancia();

        return distancia;

        /*
        calcularDistancia();
        return distancia;
         */
    }

    protected void lanzarExepcion(String mensaje) {throw new Excepcion(mensaje);}

    public boolean esCompartido() {
        return medioTransporteUtilizado.esCompartido();
    }


    public TramoDTO convertirADTO() {
        return new TramoDTO(this);
    }

    public void quitarMiembro(Miembro miembroEliminado) {
        this.miembrosVinculados.remove(miembroEliminado);
    }


    public class TramoDTO {
        @Getter @Setter
        int id;
        @Getter @Setter
        MedioTransporte medioTransporteUtilizado;
        @Getter @Setter
        String descripcion;
        @Getter @Setter
        Direccion.DireccionDTO direccionInicio;
        @Getter @Setter
        Direccion.DireccionDTO direccionFin;
        @Getter @Setter
        List<Miembro> miembrosVinculados;

        public TramoDTO(Tramo tramo) {
            this.medioTransporteUtilizado = tramo.medioTransporteUtilizado;
            this.descripcion = tramo.descripcion;
            this.direccionInicio = tramo.direccionInicio.convertirADTO();
            this.direccionFin = tramo.direccionFin.convertirADTO();
            this.miembrosVinculados = tramo.getMiembrosVinculados();
            this.id = tramo.getId();
        }
    }
}