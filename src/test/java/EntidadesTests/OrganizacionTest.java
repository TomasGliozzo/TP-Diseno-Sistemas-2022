package EntidadesTests;

import domain.modelo.entities.Entidades.Miembro.EstadoSolicitud;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Miembro.SolicitudMiembro;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.Sector;
import Factory.Entidades.EntidadesFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OrganizacionTest {
    private Organizacion mostazaCentro = EntidadesFactory.crearOrganizacionMostaza();
    private Sector rrhh = new Sector("RRHH");
    private Miembro miembro1 = EntidadesFactory.crearMiembroJose();

    @BeforeEach
    public void inicializarOrganizacion() {
        mostazaCentro.agregarSector(rrhh);
    }

    @Test
    @DisplayName("Dar de alta sectores de una organizacion")
    public void darDeAltaSectoresTest() {
        mostazaCentro.agregarSector(rrhh);

        Assertions.assertTrue(mostazaCentro.getSectores().contains(rrhh));
    }
    @Test
    @DisplayName("Miembro puede enviar una solicitud a una organizacion")
    public void solicitarAdhesionAUnaOrganizacionTest() {
        SolicitudMiembro solicitudCreada = miembro1.solicitarAdhesion(rrhh, mostazaCentro);

        Assertions.assertTrue(mostazaCentro.getSolicitudesMiembros().contains(solicitudCreada));
    }
    @Test
    @DisplayName("Dado un miembro puedo saber a que sector pertenece")
    public void sectorDeMiembro(){

        SolicitudMiembro solicitudCreada = miembro1.solicitarAdhesion(rrhh, mostazaCentro);
        mostazaCentro.vincularMiembro(solicitudCreada);

        Assertions.assertEquals(mostazaCentro.sectorMiembro(miembro1), rrhh);
    }
    @Test
    @DisplayName("Aceptar vinculacion de un miembro")
    public void vincularMiembroAUnaOrganizacionTest() {
        SolicitudMiembro solicitudCreada = miembro1.solicitarAdhesion(rrhh, mostazaCentro);
        mostazaCentro.vincularMiembro(solicitudCreada);

        Assertions.assertEquals(mostazaCentro.sectorMiembro(miembro1), rrhh);
        Assertions.assertTrue(mostazaCentro.miembrosOrganizacion().contains(miembro1));
        Assertions.assertTrue(miembro1.getOrganizaciones().contains(mostazaCentro));
    }

    @Test
    @DisplayName("Rechazar solicitud de un miembro")
    public void rechazarSolicitudDeMiembroTest() {
        SolicitudMiembro solicitudCreada = miembro1.solicitarAdhesion(rrhh, mostazaCentro);
        mostazaCentro.rechazarMiembro(solicitudCreada);

        Assertions.assertEquals(solicitudCreada.getEstadoSolicitud(), EstadoSolicitud.RECHAZADA);
    }

    @Test
    @DisplayName("Obtener sector organizacion")
    public void obtenerSectorTest() {
        Sector sector = mostazaCentro.getSector("RRHH");
        Assertions.assertEquals(sector, rrhh);
    }
}