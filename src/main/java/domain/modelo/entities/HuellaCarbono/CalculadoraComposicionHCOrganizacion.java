package domain.modelo.entities.HuellaCarbono;

import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.Mediciones.Medicion;
import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CalculadoraComposicionHCOrganizacion {

    public List<ComponenteHCOrganizacion> calcularComposicionHC(Organizacion organizacion, Periodicidad periodicidad, String periodoImputacion){
        List<Medicion> mediciones = this.obtenerMedicionesOrganizacion(organizacion,periodicidad,periodoImputacion);

        Set<TipoActividad> tiposActividad = this.obtenerTiposDeActividadDeMedicionesOrganizacion(mediciones);

        List<ComponenteHCOrganizacion> componentesHC = this.generarComponentesHCMediciones(tiposActividad);

        this.cargarHCMedicionesEnComponentes(mediciones,componentesHC);

        // AGREGAR TIPO DE ACTIVADAD MIEMBROS
        //this.cargarHCMiembrosEnComponente(organizacion,periodicidad,periodoImputacion,componentesHC);

        return componentesHC;
    }

    private void cargarHCMiembrosEnComponente(Organizacion organizacion, Periodicidad periodicidad, String periodoImputacion, List<ComponenteHCOrganizacion> componentesHC) {
        componentesHC.add(new ComponenteHCOrganizacion(null,organizacion.calcularHCMiembros(periodicidad,periodoImputacion)));
    }

    private void cargarHCMedicionesEnComponentes(List<Medicion> mediciones, List<ComponenteHCOrganizacion> componentesHC) {

        for(Medicion medicion : mediciones) {
            TipoActividad tipoActividad = medicion.getTipoActividad();
            //de cada medicion obtener tipoActividad

            ComponenteHCOrganizacion componente = componentesHC.stream()
                    .filter(c -> c.getTipoActividad().equals(tipoActividad))
                    .findFirst().get();

            componente.sumarValor(medicion.calcularHC());
        }
    }

    private List<ComponenteHCOrganizacion> generarComponentesHCMediciones(Set<TipoActividad> tiposActividad) {
        List<ComponenteHCOrganizacion> componentesHC = new ArrayList<>();

        for (TipoActividad tipoActividad : tiposActividad) {
            ComponenteHCOrganizacion componente = new ComponenteHCOrganizacion(tipoActividad, 0.0);
            componentesHC.add(componente);
        }
        return componentesHC;
    }

    private Set<TipoActividad> obtenerTiposDeActividadDeMedicionesOrganizacion(List<Medicion> mediciones) {
        return mediciones.stream().map(Medicion::getTipoActividad).collect(Collectors.toSet());
    }

    private List<Medicion> obtenerMedicionesOrganizacion(Organizacion organizacion, Periodicidad periodicidad, String periodoImputacion) {
        return organizacion.getMediciones().stream()
                .filter(m -> m.fueTomadaEn(periodicidad, periodoImputacion))
                .collect(Collectors.toList());
    }

}
