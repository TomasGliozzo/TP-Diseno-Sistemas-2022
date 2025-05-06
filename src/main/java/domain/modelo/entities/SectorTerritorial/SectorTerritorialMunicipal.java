package domain.modelo.entities.SectorTerritorial;

import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.HuellaCarbono.ComponenteHCSectorTerritorial;
import domain.modelo.entities.HuellaCarbono.HC;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity(name = "sector_territorial_municipal")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
@Setter @Getter
public class SectorTerritorialMunicipal extends SectorTerritorial {

    @OneToMany(mappedBy = "sectorTerritorialMunicipal", fetch = FetchType.LAZY)
    private List<Organizacion> organizaciones;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_sector_territorial_provincial", referencedColumnName = "id")
    private SectorTerritorialProvincial sectorTerritorialProvincial;


    public SectorTerritorialMunicipal(String descripcion) {
        super();
        this.organizaciones = new ArrayList<>();
        this.setDescripcion(descripcion);
    }
    public SectorTerritorialMunicipal() {}

    public Double nuevaHC(Periodicidad periodicidad, String periodo) {
        Double valorHC = organizaciones.stream().mapToDouble(o -> o.calcularHCTotal(periodicidad,periodo)).sum();

        HC hc = new HC(periodicidad, periodo, valorHC);
        this.agregarHC(hc);

        return valorHC;
    }

    @Override
    public List<ComponenteHCSectorTerritorial> calcularComposicionHC(Periodicidad periodicidad, String periodo) {
        int i = 0;
        int j = organizaciones.size();
        List<ComponenteHCSectorTerritorial>  componentesHCSectorTerritorial = new ArrayList<>();

        for (;i<j;i++){
            Double valorHC = organizaciones.get(i).calcularHCTotal(periodicidad,periodo);
            ComponenteHCSectorTerritorial componente = new ComponenteHCSectorTerritorial(organizaciones.get(i),valorHC);
            componentesHCSectorTerritorial.add(componente);
        }
        return componentesHCSectorTerritorial;
    }

    public void agregarOrganizacion(Organizacion organizacion) {
        organizacion.setSectorTerritorialMunicipal(this);
        this.organizaciones.add(organizacion);
    }
    public void agregarSTProv(SectorTerritorialProvincial sector){
        sector.agregarSector(this);
    }

}
