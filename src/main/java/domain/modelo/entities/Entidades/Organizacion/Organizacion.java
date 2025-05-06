package domain.modelo.entities.Entidades.Organizacion;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Miembro.EstadoSolicitud;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Miembro.SolicitudMiembro;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.entities.HuellaCarbono.HC;
import domain.modelo.entities.Mediciones.Medicion;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialMunicipal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Entity
@Table(name = "organizacion")
@Getter @Setter
public class Organizacion {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "razon_social")
    private String razonSocial;

    @Enumerated(value = EnumType.STRING)
    private TipoOrganizacion tipoOrganizacion;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_direccion", referencedColumnName = "id")
    private Direccion ubicacionGeografica;

    @OneToMany(mappedBy = "organizacion", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Sector> sectores;

    @OneToMany(mappedBy = "organizacion", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<SolicitudMiembro> solicitudesMiembros;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_clasificacion_organizacion",referencedColumnName = "id")
    private ClasificacionOrganizacion clasificacionOrganizacion;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    private Usuario usuario;

    @OneToMany(mappedBy = "organizacion", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Medicion> mediciones;

    @OneToMany(mappedBy = "organizacion", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Contacto> contactos;
    
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_sector_territorial", referencedColumnName = "id")
    private SectorTerritorialMunicipal sectorTerritorialMunicipal;

    @OneToMany (fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_organizacion",referencedColumnName = "id")
    private List<HC> historialHC;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado = Estado.ACTIVO;


    public Organizacion(String razonSocial, TipoOrganizacion tipoOrganizacion, Direccion ubicacionGeografica,
                        ClasificacionOrganizacion clasificacionOrganizacion, Usuario usuario) {
        this();
        this.razonSocial = razonSocial;
        this.tipoOrganizacion = tipoOrganizacion;
        this.ubicacionGeografica = ubicacionGeografica;
        this.clasificacionOrganizacion = clasificacionOrganizacion;
        this.usuario = usuario;
    }

    public Organizacion() {
        this.sectores = new ArrayList<>();
        this.solicitudesMiembros = new ArrayList<>();
        this.mediciones = new ArrayList<>();
        this.contactos = new ArrayList<>();
        this.historialHC = new ArrayList<>();
    }

    public void agregarSector(Sector sector) {
        this.sectores.add(sector);
        sector.setOrganizacion(this);
    }

    public void agregarSolicitudDeMiembro(SolicitudMiembro solicitudMiembro) {
        this.solicitudesMiembros.add(solicitudMiembro);
        solicitudMiembro.setOrganizacion(this); //REQ PERSISTENCIA
    }

    public void vincularMiembro(SolicitudMiembro solicitudMiembro) {
        agregarMiembro(solicitudMiembro.getMiembro(), solicitudMiembro.getSector());
        //solicitudesMiembros.remove(solicitudMiembro);
        solicitudMiembro.setEstadoSolicitud(EstadoSolicitud.APROBADA);
    }

    public void agregarMiembro(Miembro miembro, Sector sector) {
        sector.agregarMiembro(miembro);
        miembro.agregarOrganizaciones(this);
    }

    public void rechazarMiembro(SolicitudMiembro solicitudMiembro) {
        solicitudMiembro.setEstadoSolicitud(EstadoSolicitud.RECHAZADA);
        //solicitudesMiembros.remove(solicitudMiembro);
    }

    public Sector sectorMiembro(Miembro miembro) {
        Sector sectorDelMiembro = sectores
                .stream()
                .filter(sector -> sector.getMiembros().contains(miembro))
                .findFirst()
                .get();
        return sectorDelMiembro;
    }

    public List<Miembro> miembrosOrganizacion() {
        return sectores
                .stream()
                .flatMap(sector -> sector.getMiembros().stream())
                .collect(Collectors.toList());
    }

    public void cargarMediciones(List<Medicion> mediciones) {
        this.mediciones.addAll(mediciones);
        mediciones.forEach(m -> m.setOrganizacion(this));
    }

    public void agregarContacto(Contacto contacto) {
        this.contactos.add(contacto);
        contacto.setOrganizacion(this); //REQ PERSISTENCIA
    }

    public HC hc(Periodicidad periodicidad, String periodoDeImputacion) {
        Optional<HC> huellaCarbono = historialHC.stream()
                .filter(h -> h.esDe(periodicidad, periodoDeImputacion)).findFirst();
        HC hc;

        if(!huellaCarbono.isPresent()) {
            Double hcTotalMiembros = this.calcularHCMiembros(periodicidad, periodoDeImputacion);
            Double hcTotalMediciones = this.calcularHCMediciones(periodicidad, periodoDeImputacion);
            hc = new HC(periodicidad, periodoDeImputacion, hcTotalMediciones + hcTotalMiembros);

            historialHC.add(hc);
        }
        else {
            hc = huellaCarbono.get();
        }

        return hc;
    }

    public Double calcularHCTotal(Periodicidad periodicidad, String periodoDeImputacion){
        return hc(periodicidad, periodoDeImputacion).getValor();
    }

    public Double calcularHCMiembros(Periodicidad periodicidad, String periodoDeImputacion) {
        return this.miembrosOrganizacion().stream().mapToDouble(m -> m.calcularHC(periodicidad, periodoDeImputacion)).sum();
    }

    public Double calcularHCMediciones(Periodicidad periodicidad, String periodoImputacion) {
        List<Medicion> medicionesAfectadas = new ArrayList<>();
        medicionesAfectadas = mediciones.stream().filter(m -> m.fueTomadaEn(periodicidad, periodoImputacion)).collect(Collectors.toList());
        return medicionesAfectadas.stream().mapToDouble(m -> m.calcularHC()).sum();
    }

    public Double impactoPorMiembro (Periodicidad periodicidad, String periodo, Miembro miembro) {
        Double HCMiembro = miembro.calcularHC(periodicidad, periodo); //n%
        Double HCOrganizacion = this.calcularHCTotal(periodicidad, periodo); //100%
        return (HCMiembro * 100) / HCOrganizacion;
    }

    public Double indicadorHCSector(Sector sector, Periodicidad periodicidad, String periodo) {
        Double HCSector = sector.calcularHCSector(periodicidad,periodo);
        return HCSector / sector.getMiembros().size();
    }

    public void agregarHC(HC hc){
        historialHC.add(hc);
        hc.setOrganizacion(this);
    }

    public Sector getSector(String nombre) {
        Sector sector = sectores.stream().filter(s -> s.getNombre().equals(nombre)).findFirst().get();
        return sector;
    }

    public Sector getSector(int id) {
        Sector sector = sectores.stream().filter(s -> s.getId()==id).findFirst().get();
        return sector;
    }

    public void agregarST(SectorTerritorialMunicipal sector){
        this.setSectorTerritorialMunicipal(sector);
        sector.agregarOrganizacion(this);
    }
}
