package HuellaDeCarbono;

import Factory.Entidades.EntidadesFactory;
import domain.modelo.entities.CargaDeMediciones.RegistrarMediciones;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.Sector;
import domain.modelo.entities.Mediciones.Consumo.Consumo;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.Mediciones.Consumo.TipoConsumo;
import domain.modelo.entities.Mediciones.Medicion;
import domain.modelo.entities.Mediciones.MedicionCompuesta.CategoriaLogistica;
import domain.modelo.entities.Mediciones.MedicionCompuesta.MedicionCompuesta;
import domain.modelo.entities.Mediciones.MedicionCompuesta.MedioTransporteLogistica;
import domain.modelo.entities.Mediciones.MedicionSimple.MedicionSimple;
import domain.modelo.entities.Mediciones.TipoActividad.Alcance;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;
import domain.modelo.entities.Mediciones.Unidad;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HCMedicionesOrganizacionTest {
    private Organizacion mostazaCentro = EntidadesFactory.crearOrganizacionMostaza();
    private Sector rrhh = new Sector("RRHH");
    private Miembro miembro1 = EntidadesFactory.crearMiembroJose();
    private RegistrarMediciones registrarMediciones;

    @BeforeEach
    public void inicializarOrganizacion() {
        mostazaCentro.agregarSector(rrhh);
        mostazaCentro.vincularMiembro(miembro1.solicitarAdhesion(rrhh,mostazaCentro));
    }

    @BeforeEach
    public void inicializarMediciones() {
        //Creacion TipoActividad
        TipoActividad tipoAtividad1 = new TipoActividad("Combustion Fija", new TipoConsumo("Gas Natural", Unidad.m3), Alcance.DIRECTO);
        TipoActividad tipoAtividad2 = new TipoActividad("Combustion Fija", new TipoConsumo("Diesel", Unidad.lts), Alcance.DIRECTO);
        TipoActividad tipoAtividad3 = new TipoActividad("Combustion Fija", new TipoConsumo("Gasoil", Unidad.lts), Alcance.DIRECTO);
        TipoActividad tipoAtividad4 = new TipoActividad("Combustion Fija", new TipoConsumo("Kerosene", Unidad.lts), Alcance.DIRECTO);

        TipoActividad tipoAtividadL1 = new TipoActividad("LOGISTICA_PRODUCTOS_RESIDUOS", new TipoConsumo("LOGISTICA_PRODUCTOS_RESIDUOS",Unidad.lts, CategoriaLogistica.MATERIA_PRIMA, MedioTransporteLogistica.UTILITARIO_LIVIANO,2.0), Alcance.DIRECTO);
        TipoActividad tipoAtividadL2 = new TipoActividad("LOGISTICA_PRODUCTOS_RESIDUOS", new TipoConsumo("LOGISTICA_PRODUCTOS_RESIDUOS",Unidad.lts, CategoriaLogistica.RESIDUOS, MedioTransporteLogistica.CAMION_DE_CARGA,5.0), Alcance.DIRECTO);

        List<TipoActividad> tipoActividades = new ArrayList<>();
        tipoActividades.add(tipoAtividad1);
        tipoActividades.add(tipoAtividad2);
        tipoActividades.add(tipoAtividad3);
        tipoActividades.add(tipoAtividad4);
        tipoActividades.add(tipoAtividadL1);
        tipoActividades.add(tipoAtividadL2);

        //seteo de FE a tipoActividades
        FE fe = new FE(1.0, Unidad.lts);
        tipoActividades.forEach(t -> t.setFE(fe));

        //Creacion Mediciones MENSUALES
        MedicionSimple ms1 = new MedicionSimple(tipoAtividad1,new Consumo(Periodicidad.MENSUAL,"07/2022"),50.0);
        MedicionSimple ms2 = new MedicionSimple(tipoAtividad2,new Consumo(Periodicidad.MENSUAL,"07/2022"),25.0);
        MedicionCompuesta mc = new MedicionCompuesta(tipoAtividadL1,new Consumo(Periodicidad.MENSUAL,"07/2022"),100.0,2000.0);

        //Creacion Mediciones ANUALES
        MedicionSimple ms3 = new MedicionSimple(tipoAtividad3,new Consumo(Periodicidad.ANUAL,"2019"),500.0);
        MedicionCompuesta mc2 = new MedicionCompuesta(tipoAtividadL2,new Consumo(Periodicidad.ANUAL,"2019"),10.0,5000.0);

        //Carga Mediciones Organizacion
        List<Medicion> mediciones = new ArrayList<>();
        Collections.addAll(mediciones,ms1,ms2,mc,ms3,mc2);
        mostazaCentro.cargarMediciones(mediciones);

        //Carga Mediciones EXCEL
        //tipoActividades.stream().filter( a -> a.getNombreActividad() == "LOGISTICA_PRODUCTOS_RESIDUOS").forEach(a-> a.setFK(1.0));
        //registrarMediciones = new RegistrarMediciones("CargaDeMediciones.xlsx", mostazaCentro, tipoActividades);
        //registrarMediciones.cargarMediciones();
    }

    //////TESTS DE //////
    @Test
    @DisplayName("Calcular HC de Mediciones en un periodo MENSUAL dado")
    public void calcularHCMedicionesMesTest() {
        Assertions.assertEquals(400075.0, mostazaCentro.calcularHCMediciones(Periodicidad.MENSUAL, "07/2022"));
    }

    @Test
    @DisplayName("Calcular HC de Mediciones en un periodo ANUAL dado")
    public void calcularHCMedicionesAnioTest() {
        Assertions.assertEquals(250500.0, mostazaCentro.calcularHCMediciones(Periodicidad.ANUAL, "2019"));
    }

}
