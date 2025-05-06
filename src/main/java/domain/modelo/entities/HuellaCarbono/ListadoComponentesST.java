package domain.modelo.entities.HuellaCarbono;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ListadoComponentesST {
    private List<String> composiciones;
    private List<Double> valores;

    public ListadoComponentesST() {
        composiciones = new ArrayList<>();
        valores = new ArrayList<>();
    }

    public void agregar(ComponenteHCSectorTerritorial componente) {
        composiciones.add(componente.getOrganizacion().getRazonSocial());
        valores.add(componente.getValor());
    }

    public void agregarComponenteOrganizacion(ComponenteHCOrganizacion componente) {
        composiciones.add(componente.getTipoActividad().getNombreActividad() + " " +
                        componente.getTipoActividad().getTipoDeConsumo().getNombre());
        valores.add(componente.getValor());
    }
}
