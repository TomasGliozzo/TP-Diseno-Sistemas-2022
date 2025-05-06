import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.HuellaCarbono.CalculadoraComposicionHCOrganizacion;
import domain.modelo.entities.HuellaCarbono.ComponenteHCOrganizacion;
import domain.modelo.entities.HuellaCarbono.ComponenteHCSectorTerritorial;
import domain.modelo.entities.HuellaCarbono.HC;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.SectorTerritorial.AgenteSectorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialMunicipal;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialProvincial;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.RepositorioAgenteSectorial;
import domain.modelo.repositories.RepositorioOrganizacion;
import domain.modelo.repositories.factories.FactoryRepositorio;
import domain.modelo.repositories.factories.FactoryRepositorioAgenteSectorial;
import domain.modelo.repositories.factories.FactoryRepositorioOrganizacion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReportesTest {

    @Test
    @DisplayName("Reporte componentes sector territorial")
    public void componentesSectorTerritorial() {
        Repositorio<SectorTerritorialProvincial> repoSTProvinciales = FactoryRepositorio.get(SectorTerritorialProvincial.class);
        RepositorioAgenteSectorial repoAS = FactoryRepositorioAgenteSectorial.get();
        AgenteSectorial agenteSeleccionado = repoAS.buscar(108);
        SectorTerritorial st = agenteSeleccionado.getSectorTerritorial();

        Periodicidad periodicidad = Periodicidad.ANUAL;
        String periodo = "2019";

        List<ComponenteHCSectorTerritorial> componentes = st.calcularComposicionHC(periodicidad, periodo);

        //aca no me aparece nada del proxy
        componentes.forEach(c -> System.out.println(c.getValor() + " - " + c.getOrganizacion().getRazonSocial()));
    }

}