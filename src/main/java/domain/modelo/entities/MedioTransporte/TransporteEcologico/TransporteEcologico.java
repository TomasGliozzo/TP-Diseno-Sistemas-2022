package domain.modelo.entities.MedioTransporte.TransporteEcologico;

import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.MedioTransporte.MedioTransporte;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("transporte_ecologico")
public class TransporteEcologico extends MedioTransporte {

    public TransporteEcologico(String name, String description, FE fe){
        setNombre(name);
        setDescripcion(description);
        this.fe = fe;
        fe.setValor(0.0);
    }

    public TransporteEcologico() {}

    @Override
    public boolean esCompartido() {
        return false;
    }

    @Override
    public Double calcularDistancia(Direccion direccionInicio, Direccion direccionFin) {
        return 0.0;
    }
}
