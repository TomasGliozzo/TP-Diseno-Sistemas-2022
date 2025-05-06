package domain.modelo.entities.MedioTransporte.Recorrido;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.MedioTransporte.MedioTransporte;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "trayecto")
@Getter @Setter
public class Trayecto {
    @Id
    @GeneratedValue
    @Column(name = "id") private int id;

    @OneToMany(mappedBy = "trayecto", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Tramo> tramos;

    @Column(name="frecuencia_de_uso_semanal")
    private int frecuenciaDeUsoSemanal;

    @Transient
    private final static int  cantidadSemanasMes = 4;

    @Transient
    private final static int  cantidadSemanasAnio = 52;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_miembro",referencedColumnName = "id")
    private Miembro miembro;

    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado = Estado.ACTIVO;

    @Column(name = "nombre")
    @Getter @Setter
    private String nombre;

    @Column(name = "descripcion")
    @Getter @Setter
    private String descripcion;


    public Trayecto(int frecuenciaDeUsoSemanal, Tramo tramo) {
        this();
        this.frecuenciaDeUsoSemanal=frecuenciaDeUsoSemanal;
        this.tramos.add(tramo);
    }

    public Trayecto() {
        this.tramos = new ArrayList<>();
    }

    public void agregarTramo(Tramo tramo){ //REQ PERSISTENCIA
        this.tramos.add(tramo);
        tramo.setTrayecto(this);
    }

    public Direccion puntoDePartida() {
        return tramos.get(0).getDireccionInicio();
    }

    public Direccion puntoDeLlegada() {
        return tramos.get(tramos.size() - 1).getDireccionFin();
    }

    public Double calcularDistanciaTotal() {
        Tramo tramoInicial = tramos.get(0);
        Tramo tramoFinal = tramos.get(tramos.size()-1);

        Double distancia = this.calcularDistanciaTramosIntermedios(tramoInicial,tramoFinal);

        return distancia;
    }

    public void agregarTramoCompartido(Tramo tramoCompartido) {
        Tramo tramo = new Tramo();

        tramo.setMedioTransporteUtilizado(tramoCompartido.getMedioTransporteUtilizado());
        tramo.setDescripcion(tramoCompartido.getDescripcion());
        tramo.setDireccionInicio(tramoCompartido.getDireccionInicio());
        tramo.setDireccionFin(tramoCompartido.getDireccionFin());

        tramoCompartido.getMiembrosVinculados().forEach(tramo::agregarMiembro);
        //tramo.setMiembrosVinculados(tramoCompartido.getMiembrosVinculados());

        this.tramos.add(tramo);
        tramo.setTrayecto(this);
    }

    public Double calcularDistanciaTramosIntermedios(Tramo tramoInicial, Tramo tramoFinal) {
        int i = tramos.indexOf(tramoInicial);
        int j = tramos.indexOf(tramoFinal);
        Double distancia = 0.0;

        for(;i<=j;i++)
            distancia += tramos.get(i).getDistancia();

        return distancia;
    }

    public Double calcularHCTrayecto(Periodicidad periodicidad) {
        switch (periodicidad) {
            case MENSUAL:
                return cantidadSemanasMes * frecuenciaDeUsoSemanal * this.calcularHCTramos();

            case ANUAL:
                return cantidadSemanasAnio * frecuenciaDeUsoSemanal * this.calcularHCTramos();

            default:
                return 0.0;
        }
    }

    public double calcularHCTramos() {
        return tramos.stream().filter(t -> t.getEstado().equals(Estado.ACTIVO))
                .mapToDouble(Tramo::getHC).sum();
    }
}
