package EntidadesTests;

import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Miembro.SolicitudMiembro;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.Sector;
import Factory.Entidades.EntidadesFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MiembroTest {
    //Miembro
    private Miembro jose = EntidadesFactory.crearMiembroJose();
    // Organizacion
    private Organizacion mostazaCentro = EntidadesFactory.crearOrganizacionMostaza();
    private Sector rrhh = new Sector("RRHH");

    @BeforeEach
    public void init() {
        mostazaCentro.agregarSector(rrhh);

    }

    @Test
    @DisplayName("Un miembro puede generar una solicitud de adhesion a una organizacion")
    public void unMiembroPuedeGenerarUnaSolicitudDeAdhesion() {
        SolicitudMiembro soliJose = jose.solicitarAdhesion(rrhh, mostazaCentro);

        // La organizacion contiene la solicitus
        Assertions.assertTrue(mostazaCentro.getSolicitudesMiembros().contains(soliJose));

        // La solicitud debe contener al miembro y al sector
        Assertions.assertEquals(soliJose.getSector(), rrhh);
        Assertions.assertEquals(soliJose.getMiembro(), jose);
    }

    @Test
    @DisplayName("Un miembro sabe a que sector de una organizacion pertence")
    public void unMiembroSabeAQueSectorDeUnaOrganizacionPertence() {
        jose.agregarOrganizaciones(mostazaCentro);
        rrhh.agregarMiembro(jose);

        Assertions.assertEquals(rrhh, jose.sectorPorOrganizacion(mostazaCentro));
    }
}