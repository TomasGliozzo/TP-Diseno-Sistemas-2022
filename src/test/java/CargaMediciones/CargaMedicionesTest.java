package CargaMediciones;

import domain.modelo.entities.CargaDeMediciones.RegistrarMediciones;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import Factory.Entidades.EntidadesFactory;
import domain.modelo.entities.Mediciones.Consumo.TipoConsumo;
import domain.modelo.entities.Mediciones.MedicionCompuesta.CategoriaLogistica;
import domain.modelo.entities.Mediciones.MedicionCompuesta.MedioTransporteLogistica;
import domain.modelo.entities.Mediciones.TipoActividad.Alcance;
import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;
import domain.modelo.entities.Mediciones.Unidad;
import domain.modelo.repositories.factories.FactoryRepositorio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class CargaMedicionesTest {
    private Organizacion mostazaCentro = EntidadesFactory.crearOrganizacionMostaza();

    private RegistrarMediciones registrarMediciones;
    TipoActividad tipoAtividad;

    @BeforeEach
    public void init() {

        TipoActividad tipoActividad1 = new TipoActividad("Combustion Fija", new TipoConsumo("Gas Natural", Unidad.m3), Alcance.DIRECTO);
        TipoActividad tipoActividad2 = new TipoActividad("Combustion Fija", new TipoConsumo("Diesel", Unidad.lts), Alcance.DIRECTO);
        tipoAtividad = new TipoActividad("Combustion Fija", new TipoConsumo("Gasoil", Unidad.lts), Alcance.DIRECTO);
        TipoActividad tipoActividad4 = new TipoActividad("Combustion Fija", new TipoConsumo("Kerosene", Unidad.lts), Alcance.DIRECTO);

        TipoActividad tipoActividad5 = new TipoActividad("LOGISTICA_PRODUCTOS_RESIDUOS",
                new TipoConsumo("LOGISTICA_PRODUCTOS_RESIDUOS", Unidad.lts, CategoriaLogistica.MATERIA_PRIMA, MedioTransporteLogistica.CAMION_DE_CARGA, 1.0),
                Alcance.DIRECTO);

        List<TipoActividad> tipoActividads = new ArrayList<>();
        tipoActividads.add(tipoActividad1);
        tipoActividads.add(tipoActividad2);
        tipoActividads.add(tipoAtividad);
        tipoActividads.add(tipoActividad4);
        tipoActividads.add(tipoActividad5);

        registrarMediciones = new RegistrarMediciones("CargaDeMediciones.xlsx", mostazaCentro, tipoActividads);
    }

    @DisplayName("Las mediciones leidas del excel se cargan en el excel")
    @Test
    public void cargarMedicionesEnOrganizacion() {
        Assertions.assertTrue(mostazaCentro.getMediciones().isEmpty());
        registrarMediciones.cargarMediciones();
        Assertions.assertEquals(5, mostazaCentro.getMediciones().size());
    }

    @DisplayName("Las mediciones leidas del excel se cargan en el excel")
    @Test
    public void cargarMedicionesEnOrganizacionConPersistencia() {
        RegistrarMediciones registrador = new RegistrarMediciones("CargaDeMediciones.xlsx", mostazaCentro,
                FactoryRepositorio.get(TipoActividad.class).buscarTodos());
        Assertions.assertTrue(mostazaCentro.getMediciones().isEmpty());
        registrador.cargarMediciones();
        Assertions.assertEquals(5, mostazaCentro.getMediciones().size());
    }
}
