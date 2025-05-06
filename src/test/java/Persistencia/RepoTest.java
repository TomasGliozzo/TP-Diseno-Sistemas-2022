package Persistencia;

import Factory.MedioTransporte.MedioTransporteFactory;
import Factory.MedioTransporte.TipoTramo;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.Sector;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.Mediciones.Unidad;
import domain.modelo.entities.MedioTransporte.MedioTransporte;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Trayecto;
import domain.modelo.entities.MedioTransporte.TransportePublico.Parada;
import domain.modelo.entities.MedioTransporte.TransportePublico.TransportePublico;
import domain.modelo.entities.SectorTerritorial.AgenteSectorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialMunicipal;
import domain.modelo.repositories.*;
import domain.modelo.repositories.factories.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;

public class RepoTest {
    @Test
    @DisplayName("Repo miembro")
    public void repoMiembro() {
        RepositorioMiembro repositorioMiembro = FactoryRepositorioMiembro.get();
        Miembro miembro = repositorioMiembro.buscarMiembroPorUsuario(31);

        Assertions.assertEquals("Jose", miembro.getNombre());
        Assertions.assertEquals(30, miembro.getId());
    }

    @Test
    @DisplayName("Repo Usuario")
    public void repoUsuario() {
        RepositorioUsuarios repositorioUsuarios = FactoryRepositorioUsuarios.get();
        Usuario usuario = repositorioUsuarios.buscarUsuario("Hola", "123123");

        Assertions.assertEquals(40, usuario.getId());
        Assertions.assertTrue(repositorioUsuarios.existe("Hola", "123123"));
    }

    @Test
    @DisplayName("Repo Organizacion")
    public void repoOrganizacion() {
        RepositorioOrganizacion repositorioOrganizacion = FactoryRepositorioOrganizacion.get();
        Organizacion organizacion = repositorioOrganizacion.buscarOrganizacionPorUsuario(10);
        Organizacion organizacionMostaza = repositorioOrganizacion.buscar(1);

        Assertions.assertEquals(1, organizacion.getId());
        Assertions.assertEquals(1, organizacionMostaza.getId());
    }

    @Test
    @DisplayName("Persistencia trayectos")
    public void persistenciaTrayectos(){
        //MIEMBRO
        RepositorioMiembro repositorioMiembro = FactoryRepositorioMiembro.get();
        Miembro jose = repositorioMiembro.buscar(30);

        //ORGANIZACION
        RepositorioOrganizacion repositorioOrganizacion = FactoryRepositorioOrganizacion.get();
        Organizacion mostaza = repositorioOrganizacion.buscar(1);
        Sector sector = mostaza.getSectores().get(0);
        mostaza.agregarMiembro(jose, sector);

        //FE
        FE fe = new FE(5.0, Unidad.lts);

        //TRAYECTO1 y TRAMO SERVICIO CONTRATADO(INICIO) HC=44000.0 (1500 +0 + 9500) * 4 * 1
        Trayecto trayecto1 = MedioTransporteFactory.instanciarTrayectoConUnTramo(TipoTramo.TRAMO_SERVICIO_CONTRATADO);

        Tramo tramoServicioContratado = trayecto1.getTramos().get(0);
        Direccion dISC = tramoServicioContratado.getDireccionInicio();
        Direccion dFSC = tramoServicioContratado.getDireccionFin();

        MedioTransporte transporteServicioParticular = tramoServicioContratado.getMedioTransporteUtilizado();
        transporteServicioParticular.setFe(fe);

        //TRANSPORTE PUBLICO
        Tramo tramoTransportePublico = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_TRANSPORTE_PUBLICO);
        TransportePublico transportePublico = (TransportePublico) tramoTransportePublico.getMedioTransporteUtilizado();
        transportePublico.setFe(fe);

        Parada pI = MedioTransporteFactory.instanciarParada(1);
        Parada pInt = MedioTransporteFactory.instanciarParada(2);
        Parada pF = MedioTransporteFactory.instanciarParada(3);

        Direccion dIS = pI.getDireccion();
        Direccion dFS = pF.getDireccion();

        transportePublico.agregarParada(pI);
        transportePublico.agregarParada(pInt);
        transportePublico.agregarParada(pF);
        tramoTransportePublico.setDireccionInicio(dIS);
        tramoTransportePublico.setDireccionFin(dFS);

        //AGREGO TRAMOS TP1 y TP2
        Tramo tramoTransporteEcologico = MedioTransporteFactory.instanciarTramo(TipoTramo.TRAMO_TRANSPORTE_ECOLOGICO);
        trayecto1.agregarTramo(tramoTransportePublico);
        trayecto1.agregarTramo(tramoTransporteEcologico);
        trayecto1.setFrecuenciaDeUsoSemanal(1);

        //Trayecto2 y Tramo Vehiculo Particular HC=18000.0 ( 1500.0 * 4 * 3)
        Trayecto trayecto2 = MedioTransporteFactory.instanciarTrayectoConUnTramo(TipoTramo.TRAMO_VEHICULO_PARTICULAR);
        trayecto2.setFrecuenciaDeUsoSemanal(3);
        Tramo tramoVehiculoParticular = trayecto2.getTramos().get(0);
        MedioTransporte transporteVehiculoParticular = tramoVehiculoParticular.getMedioTransporteUtilizado();
        transporteVehiculoParticular.setFe(fe);


        //Repositorio<FE> repositorioFE = FactoryRepositorio.get(FE.class);
        //repositorioFE.agregar(fe);

        //EntityManagerHelper.getEntityManager().persist(trayecto1);
        //EntityManagerHelper.getEntityManager().persist(trayecto2);
        //EntityManagerHelper.getEntityManager().persist(tramoServicioContratado);
        //EntityManagerHelper.getEntityManager().merge(jose);
        //EntityManagerHelper.getEntityManager().merge(mostaza);

        Repositorio<Trayecto> repositorioTrayecto = FactoryRepositorio.get(Trayecto.class);
        repositorioTrayecto.agregar(trayecto1);
        repositorioTrayecto.agregar(trayecto2);
    }

    @Test
    @DisplayName("HC test")
    public void hcTest() {
        RepositorioOrganizacion repositorioOrganizacion = FactoryRepositorioOrganizacion.get();
        Organizacion organizacionMostaza = repositorioOrganizacion.buscar(1);

        System.out.println(organizacionMostaza.calcularHCTotal(Periodicidad.MENSUAL, "07/2022"));

        /*
        RepositorioMiembro repositorioMiembro = FactoryRepositorioMiembro.get();
        Miembro jose = repositorioMiembro.buscar(30);

        System.out.println(jose.calcularHC(Periodicidad.MENSUAL, "07/2022"));
        System.out.println(jose.getTrayectos().size());
        */
    }

    @Test
    @DisplayName("Repo Usuario Organizacion existente")
    public void repoUsuarioOrganizacion() {
        RepositorioOrganizacion repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        Organizacion organizacion = repositorioOrganizacion.buscarOrganizacionPorUsuario(10);
        Assertions.assertEquals(1, organizacion.getId());
    }

    @Test
    @DisplayName("Repo Usuario Organizacion no existente")
    public void repoUsuarioOrganizacionInvalido() {
        RepositorioOrganizacion repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        Organizacion organizacion = repositorioOrganizacion.buscarOrganizacionPorUsuario(11);
        Assertions.assertEquals(null, organizacion);
    }

    @Test
    @DisplayName("Repo Usuario por nombre")
    public void repoUsuarioPorNombre() {
        RepositorioUsuarios repositorioUsuarios = FactoryRepositorioUsuarios.get();

        Usuario usuario = repositorioUsuarios.buscarUsuarioPorNombre("usuario1");
        Assertions.assertEquals("usuario1", usuario.getNombre());
        Assertions.assertFalse(usuario.estaPenalizado());
    }

    @Test
    @DisplayName("Repo Sector Territorial")
    public void repoST() {
        Repositorio<SectorTerritorialMunicipal> repoSectorTerritorial = FactoryRepositorio.get(SectorTerritorialMunicipal.class);
        RepositorioAgenteSectorial repositorioAgenteSectorial = FactoryRepositorioAgenteSectorial.get();
        RepositorioOrganizacion repositorioOrganizacion = FactoryRepositorioOrganizacion.get();


        SectorTerritorialMunicipal sector= repoSectorTerritorial.buscar(138);
        Assertions.assertEquals("Municipio de San Rafael 2", sector.getDescripcion());


        Assertions.assertEquals(new ArrayList<>(),sector.getOrganizaciones());


        int municipio = sector.getDireccion().getMunicipio();

        List<Organizacion> organizaciones = repositorioOrganizacion.buscarTodos().
                stream().filter(o->o.getUbicacionGeografica().getMunicipio() == municipio).
                collect(Collectors.toList());

        List<Organizacion> organizacionesSinSector = organizaciones.stream().
                filter(o->o.getSectorTerritorialMunicipal() == null).
                collect(Collectors.toList());

        Assertions.assertEquals(new ArrayList<>(),organizacionesSinSector);
    }
}
