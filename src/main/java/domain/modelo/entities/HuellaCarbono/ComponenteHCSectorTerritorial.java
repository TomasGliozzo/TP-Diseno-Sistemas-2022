package domain.modelo.entities.HuellaCarbono;

import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialMunicipal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComponenteHCSectorTerritorial {
    private Double valor;
    private Organizacion organizacion;
    private SectorTerritorialMunicipal sectorTerritorialMunicipal;

    public ComponenteHCSectorTerritorial(Organizacion organizacion,Double valor) {
        this.valor = valor;
        this.organizacion = organizacion;
    }

    public ComponenteHCSectorTerritorial(SectorTerritorialMunicipal sectorTerritorialMunicipal, Double valor) {
        this.valor = valor;
        this.sectorTerritorialMunicipal = sectorTerritorialMunicipal;
    }
}
