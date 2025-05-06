package domain.modelo.entities.HuellaCarbono;

import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ComponenteHCOrganizacion {

    private TipoActividad tipoActividad;
    private Double valor;

    public ComponenteHCOrganizacion(TipoActividad tipoActividad, Double valor) {
        this.tipoActividad = tipoActividad;
        this.valor = valor;
    }

    public void sumarValor(Double valorNuevo){
        valor += valorNuevo;
    }
}
