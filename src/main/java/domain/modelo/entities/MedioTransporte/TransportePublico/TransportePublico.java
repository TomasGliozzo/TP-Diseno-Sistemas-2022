package domain.modelo.entities.MedioTransporte.TransportePublico;

import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.MedioTransporte;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("transporte_publico")
@Getter @Setter
public class TransportePublico extends MedioTransporte {
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transporte_publico")
    private TipoDeTransportePublico tipoTransporte;

    @OneToMany(mappedBy = "transportePublico", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Parada> paradas;

    @Column(name = "linea")
    private String linea;

    public TransportePublico(TipoDeTransportePublico tipoTransportePublico, String linea, FE fe) {
        this();
        this.tipoTransporte = tipoTransportePublico;
        this.linea = linea;
        this.fe = fe;
    }

    public TransportePublico() {
        this.paradas = new ArrayList<>();
    }

    public void agregarParada(Parada parada){ //REQ PERSISTENCIA
        this.paradas.add(parada);
        parada.setTransportePublico(this);
    }

    public Direccion direccionParada(Parada parada){
        return parada.getDireccion();
    }

    public Double calcularDistancia(Direccion direccionInicio,Direccion direccionFin){
        Parada paradaInicial = paradaSegunDireccion(direccionInicio);
        Parada paradaFinal = paradaSegunDireccion(direccionFin);

        return  calcularDistanciaEntreParadas(paradaInicial,paradaFinal);
    }

    public Parada paradaSegunDireccion(Direccion direccion) {
        List<Direccion> direcciones = paradas.stream().map(p -> p.getDireccion()).collect(Collectors.toList()); //Obtengo una lista de direcciones eq a cada parada
        Parada parada = paradas.get(direcciones.indexOf(direccion)); //Obtengo la parada correspondiente a la direccion ( que anteriormente era esa parada).

        return parada;
    }


    public Double calcularDistanciaEntreParadas(Parada paradaInicial, Parada paradaFinal) {
        int i = paradas.indexOf(paradaInicial);
        int j =  paradas.indexOf(paradaFinal);
        Double distanciaEntreParadas = 0.0;

        if(i>j) {
            int aux=i;
            i=j;
            j=aux;
        }


        for (; i < j; i ++){
            distanciaEntreParadas += paradas.get(i).getDistanciaDireccionSiguiente();
        }

        return distanciaEntreParadas;
   }

    @Override
    public boolean esCompartido() {
        return false;
    }
}
