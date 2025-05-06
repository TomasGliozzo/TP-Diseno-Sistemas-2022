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
import java.util.stream.Collectors;


@Entity(name = "sector_territorial_provincial")
@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
@Setter  @Getter
public class SectorTerritorialProvincial extends SectorTerritorial {

    @OneToMany(mappedBy = "sectorTerritorialProvincial", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<SectorTerritorialMunicipal> sectoresTerritoriales;

    public SectorTerritorialProvincial(String descripcion) {
        super();
        this.sectoresTerritoriales = new ArrayList<>();
        this.setDescripcion(descripcion);
    }

    public SectorTerritorialProvincial() {}

    public Double nuevaHC(Periodicidad periodicidad, String periodo) {
        Double valorHC = sectoresTerritoriales.stream().mapToDouble(s->s.calcularHC(periodicidad,periodo)).sum();

        HC hc = new HC(periodicidad, periodo, valorHC);
        this.agregarHC(hc);

        return valorHC;
    }

    @Override
    public List<ComponenteHCSectorTerritorial> calcularComposicionHC(Periodicidad periodicidad, String periodo) {
        List<ComponenteHCSectorTerritorial>  componentesHCSectorTerritorial =  sectoresTerritoriales.stream().flatMap(st -> st.calcularComposicionHC(periodicidad, periodo).stream()).collect(Collectors.toList());

        return componentesHCSectorTerritorial;
    }

    public void agregarSector(SectorTerritorialMunicipal sector){
        sector.setSectorTerritorialProvincial(this);
        sectoresTerritoriales.add(sector);
    }

}
