package domain.modelo.entities.Entidades.Organizacion;

import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "clasificacion_organizacion")
@Getter @Setter
public class ClasificacionOrganizacion {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;

    @Column(name = "tipo_clasificacion")
    private String tipoClasificacion;

    @Column(name = "descripcion")
    private String descripcion;

    @OneToMany(mappedBy = "clasificacionOrganizacion",fetch = FetchType.LAZY,cascade = CascadeType.PERSIST)
    private List<Organizacion> organizaciones;

    public ClasificacionOrganizacion(String tipoClasificacion, String descripcion) {
        this.tipoClasificacion = tipoClasificacion;
        this.descripcion = descripcion;
    }

    public ClasificacionOrganizacion() {}

    public Double ReporteHCTotalOrganizaciones(Periodicidad periodicidad, String periodoDeImputacion) {
        return organizaciones.stream().filter(o -> o.getClasificacionOrganizacion().equals(this)).
                mapToDouble(o -> o.calcularHCTotal(periodicidad, periodoDeImputacion)).sum();
    }

    public void setOrganizaciones(List<Organizacion> organizaciones) {
        this.organizaciones = organizaciones;
        organizaciones.forEach(o -> o.setClasificacionOrganizacion(this));
    }
    public void setOrganizacion(Organizacion organizacion){
        this.organizaciones.add(organizacion);
        organizacion.setClasificacionOrganizacion(this);
    }
}
