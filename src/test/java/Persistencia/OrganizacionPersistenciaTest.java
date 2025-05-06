package Persistencia;

import Factory.Entidades.EntidadesFactory;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Miembro.TipoDeDocumento;
import domain.modelo.entities.Entidades.Organizacion.*;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.entities.HuellaCarbono.HC;
import domain.modelo.entities.Mediciones.Consumo.Consumo;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.Mediciones.Consumo.TipoConsumo;
import domain.modelo.entities.Mediciones.Medicion;
import domain.modelo.entities.Mediciones.MedicionCompuesta.CategoriaLogistica;
import domain.modelo.entities.Mediciones.MedicionCompuesta.MedicionCompuesta;
import domain.modelo.entities.Mediciones.MedicionCompuesta.MedioTransporteLogistica;
import domain.modelo.entities.Mediciones.MedicionSimple.MedicionSimple;
import domain.modelo.entities.Mediciones.TipoActividad.Alcance;
import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;
import domain.modelo.entities.Mediciones.Unidad;
import domain.modelo.entities.SectorTerritorial.AgenteSectorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialMunicipal;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialProvincial;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class OrganizacionPersistenciaTest {
    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static void main(String[] args) {
        emf = Persistence.createEntityManagerFactory("db");
        em = emf.createEntityManager();

        //ORGANIZACIÓN
        Organizacion organizacionMostaza = EntidadesFactory.crearOrganizacionMostaza();

        //CLASIFICACION ORGANIZACION
        ClasificacionOrganizacion clasificacionOrganizacion = organizacionMostaza.getClasificacionOrganizacion();

        //USUARIO
        Usuario usuario = organizacionMostaza.getUsuario();

        //MIEMBRO
        Miembro miembro1 = EntidadesFactory.crearMiembroJose();
        Miembro miembro2 = EntidadesFactory.crearMiembroMariela();
        Miembro miembro3 = EntidadesFactory.crearMiembroMaria();

        //SECTOR
        Sector sector1 = organizacionMostaza.getSectores().get(0);
        Sector sector2 = organizacionMostaza.getSectores().get(1);

        //SOLICITUD DE ADHESION
        miembro1.solicitarAdhesion(sector1,organizacionMostaza);
        miembro2.solicitarAdhesion(sector1,organizacionMostaza);
        miembro3.solicitarAdhesion(sector2,organizacionMostaza);

        //CONTACTO
        Contacto contacto1 = new Contacto("Pablo Lopez","pablolopez@gmail.com","49082277");
        Contacto contacto2 = new Contacto("Sofia Benitez", "sofi.beni@gmail.com","1109001654");

        organizacionMostaza.agregarContacto(contacto1);
        organizacionMostaza.agregarContacto(contacto2);

        //TIPO ACTIVIDAD
        TipoActividad tipoActividad1 = new TipoActividad("Combustion Fija", new TipoConsumo("Gas Natural", Unidad.m3), Alcance.DIRECTO);
        TipoActividad tipoActividad2 = new TipoActividad("Combustion Fija", new TipoConsumo("Diesel", Unidad.lts), Alcance.DIRECTO);
        TipoActividad tipoActividad3 = new TipoActividad("Combustion Fija", new TipoConsumo("Kerosene", Unidad.lts), Alcance.DIRECTO);

        TipoActividad tipoAtividadL = new TipoActividad("LOGISTICA_PRODUCTOS_RESIDUOS", new TipoConsumo("LOGISTICA_PRODUCTOS_RESIDUOS",Unidad.lts, CategoriaLogistica.MATERIA_PRIMA, MedioTransporteLogistica.UTILITARIO_LIVIANO,2.0), Alcance.DIRECTO);

        //CONSUMO
        Consumo consumoM = new Consumo(Periodicidad.MENSUAL,"07/2022");
        Consumo consumoA = new Consumo(Periodicidad.ANUAL,"2019");

        //MEDICION
        Medicion medicionS1 = new MedicionSimple(tipoActividad1,consumoM,50.0);
        Medicion medicionS2 = new MedicionSimple(tipoActividad2,consumoM,150.0);
        Medicion medicionS3 = new MedicionSimple(tipoActividad3,consumoM,250.0);

        MedicionCompuesta medicionC = new MedicionCompuesta(tipoAtividadL,consumoA,10.0,5000.0);

        List<Medicion>mediciones = new ArrayList<>();
        Collections.addAll(mediciones,medicionS1,medicionS2,medicionS3,medicionC);

        organizacionMostaza.cargarMediciones(mediciones);


        //SECTOR TERRITORIAL
        SectorTerritorialMunicipal sectorTerritorialM1 = new SectorTerritorialMunicipal("Municipio de Tigre");
        sectorTerritorialM1.agregarOrganizacion(organizacionMostaza);

        SectorTerritorialProvincial sectorTerritorialP1 = new SectorTerritorialProvincial("Buenos Aires");
        sectorTerritorialP1.agregarSector(sectorTerritorialM1);

        //AGENTE SECTORIAL
        AgenteSectorial aS1 = new AgenteSectorial();
        sectorTerritorialM1.agregarAgenteSectorial(aS1);

        AgenteSectorial aS2 = new AgenteSectorial();
        sectorTerritorialP1.agregarAgenteSectorial(aS2);

        //HC
        HC hcMS = new HC(Periodicidad.MENSUAL,"07/2022",100.0);
        HC hcAS = new HC(Periodicidad.ANUAL,"2019",50.0);
        HC hcMO = new HC(Periodicidad.MENSUAL,"07/2022",500.0);
        HC hcAO = new HC(Periodicidad.ANUAL,"2020",3460.9);

        sectorTerritorialM1.agregarHC(hcAS);
        sectorTerritorialP1.agregarHC(hcMS);

        organizacionMostaza.agregarHC(hcMO);
        organizacionMostaza.agregarHC(hcAO);


        em.getTransaction().begin();

        em.persist(organizacionMostaza);
        imprimir();

        em.getTransaction().commit();

        em.close();
        emf.close();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Recuperacion de DB Organización")
    private static void imprimir() {
        /*ORGANIZACION*/
        Organizacion organizacion = (Organizacion) em.createQuery("FROM Organizacion WHERE razon_social LIKE :razonSocialOrganizacion").
                setParameter("razonSocialOrganizacion", "Mostaza S.A").getSingleResult();

        Assertions.assertEquals("Mostaza S.A", organizacion.getRazonSocial());
        Assertions.assertEquals(TipoOrganizacion.EMPRESA, organizacion.getTipoOrganizacion());


        /*MIEMBRO*/
        Miembro miembro = (Miembro) em.createQuery("FROM Miembro WHERE nombre = 'Maria'").getSingleResult();

        Assertions.assertEquals("Maria", miembro.getNombre());
        Assertions.assertEquals("Gagliardi", miembro.getApellido());
        Assertions.assertEquals(TipoDeDocumento.LIBRETA_DE_ENROLAMIENTO, miembro.getTipoDeDocumento());
        Assertions.assertEquals("5.209.562", miembro.getNroDocumento());

        /*SECTOR*/
        Sector sector = (Sector) em.createQuery("FROM Sector WHERE nombre = 'Tesoreria'").getSingleResult();

        Assertions.assertEquals("Tesoreria",sector.getNombre());
        Assertions.assertEquals(organizacion,sector.getOrganizacion());

        /*USUARIO*/
        Usuario usuarioOrg = organizacion.getUsuario();
        Usuario usuarioMiembro = miembro.getUsuario();

        Assertions.assertEquals("dueniomostazacentro",usuarioOrg.getNombre());
        Assertions.assertEquals("mostazalamejor",usuarioOrg.getContrasenia());

        Assertions.assertEquals("mgagliardi",usuarioMiembro.getNombre());
        Assertions.assertEquals("@ma@gag@2000",usuarioMiembro.getContrasenia());

        /*CONTACTO*/
        Contacto contacto = (Contacto) em.createQuery("FROM Contacto WHERE nombre = 'Pablo Lopez'").getSingleResult();
        Assertions.assertEquals("pablolopez@gmail.com",contacto.getEmail());
        Assertions.assertEquals("Pablo Lopez",contacto.getNombre());
        Assertions.assertEquals("49082277",contacto.getTelefono());

        /*SECTOR_TERRITORIAL*/
        SectorTerritorialMunicipal sectorTerritorialMunicipal = organizacion.getSectorTerritorialMunicipal();
        SectorTerritorialProvincial sectorTerritorialProvincial = sectorTerritorialMunicipal.getSectorTerritorialProvincial();

        Assertions.assertEquals("Municipio de Tigre",sectorTerritorialMunicipal.getDescripcion());
        Assertions.assertEquals(organizacion,sectorTerritorialMunicipal.getOrganizaciones().get(0));

        Assertions.assertEquals("Buenos Aires",sectorTerritorialProvincial.getDescripcion());
        Assertions.assertEquals(sectorTerritorialMunicipal,sectorTerritorialProvincial.getSectoresTerritoriales().get(0));

        //CANTIDAD

        /*ORGANIZACION*/
        List<Organizacion> organizaciones = (List<Organizacion>) em.createQuery("FROM Organizacion ").getResultList();
        Assertions.assertEquals(1, organizaciones.size());

        /*USUARIO*/
        List<Usuario> usuarios = (List<Usuario>) em.createQuery("FROM Usuario").getResultList();
        Assertions.assertEquals(4, usuarios.size());

        /*MIEMBRO*/
        List<Miembro> miembros = (List<Miembro>) em.createQuery("FROM Miembro").getResultList();
        Assertions.assertEquals(3, miembros.size());

        /*SECTOR*/
        List<Sector> sectores = (List<Sector>) em.createQuery("FROM Sector ").getResultList();
        Assertions.assertEquals(2,sectores.size());

        /*CONTACTO*/

        List<Contacto> contactos = (List<Contacto>) em.createQuery("FROM Contacto").getResultList();
        Assertions.assertEquals(2,contactos.size());

        /*MEDICION*/
        List<Medicion> mediciones = (List<Medicion>) em.createQuery("FROM Medicion").getResultList();
        Assertions.assertEquals(4,mediciones.size());


        //AGENTE_SECTORIAL
        List<AgenteSectorial> agenteSectoriales = (List<AgenteSectorial>) em.createQuery("FROM AgenteSectorial ").getResultList();
        Assertions.assertEquals(2,agenteSectoriales.size());
    }



}
