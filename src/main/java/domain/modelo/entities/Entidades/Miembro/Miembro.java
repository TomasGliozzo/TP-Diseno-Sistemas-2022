package domain.modelo.entities.Entidades.Miembro;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.Sector;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.entities.HuellaCarbono.HC;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Trayecto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Entity
@Table(name = "miembro")
@Getter @Setter
public class Miembro {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Enumerated(value = EnumType.STRING)
    private TipoDeDocumento tipoDeDocumento;

    @Column(name = "nroDocumento")
    private String nroDocumento;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Organizacion> organizaciones;

    @OneToMany(mappedBy = "miembro", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Trayecto> trayectos;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @OneToMany(mappedBy = "miembro", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<SolicitudMiembro> solicitudes;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado = Estado.ACTIVO;


    public Miembro(String nombre, String apellido, TipoDeDocumento tipoDeDocumento, String nroDocumento, Usuario usuario) {
        this();
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDeDocumento = tipoDeDocumento;
        this.nroDocumento = nroDocumento;
        this.usuario = usuario;
    }

    public Miembro() {
        this.organizaciones = new ArrayList<>();
        this.trayectos = new ArrayList<>();
        this.solicitudes = new ArrayList<>();
    }

    public void agregarTrayecto(Trayecto trayecto){
        this.trayectos.add(trayecto);
        trayecto.setMiembro(this);
    }

    public Trayecto nuevoTrayectoCompartido(Trayecto trayectoCompartido) {
        Trayecto trayecto = new Trayecto();

        //trayecto.setTramos(trayectoCompartido.getTramos());
        trayecto.setFrecuenciaDeUsoSemanal(trayectoCompartido.getFrecuenciaDeUsoSemanal());
        trayecto.setNombre(trayectoCompartido.getNombre());
        trayecto.setDescripcion(trayectoCompartido.getDescripcion());

        this.trayectos.add(trayecto);
        trayecto.setMiembro(this);

        return trayecto;
    }


    public void agregarOrganizaciones(Organizacion ... organizaciones) {
        Collections.addAll(this.organizaciones, organizaciones);
    }

    public void agregarTramoTrayecto(Trayecto trayecto, Tramo tramo) {
        trayecto.agregarTramo(tramo);
    }

    public SolicitudMiembro solicitarAdhesion(Sector sector, Organizacion organizacion) {
        SolicitudMiembro solicitud = new SolicitudMiembro(this, sector);
        organizacion.agregarSolicitudDeMiembro(solicitud);
        this.solicitudes.add(solicitud);
        return solicitud;
    }

    public Sector sectorPorOrganizacion(Organizacion organizacion) {
        return organizacion.sectorMiembro(this);
    }

    public Double calcularHC(Periodicidad periodicidad, String periodoImputacion) {
        return this.hc(periodicidad, periodoImputacion).getValor();
    }

    public HC hc(Periodicidad periodicidad, String periodoImputacion) {
        return new HC(periodicidad, periodoImputacion,
                trayectos.stream().filter(t -> t.getEstado().equals(Estado.ACTIVO))
                        .mapToDouble(t -> t.calcularHCTrayecto(periodicidad)).sum());
    }

    public void eliminarOrganizacion(Organizacion organizacion) {
        organizaciones.remove(organizacion);
    }
}

