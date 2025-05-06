package Persistencia;

import Factory.Entidades.EntidadesFactory;
import Factory.MedioTransporte.MedioTransporteFactory;
import Factory.MedioTransporte.TipoTramo;
import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Organizacion.*;
import domain.modelo.entities.Entidades.Usuario.Permiso;
import domain.modelo.entities.Entidades.Usuario.Rol;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.entities.HuellaCarbono.HC;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.Mediciones.Consumo.TipoConsumo;
import domain.modelo.entities.Mediciones.MedicionCompuesta.CategoriaLogistica;
import domain.modelo.entities.Mediciones.MedicionCompuesta.MedioTransporteLogistica;
import domain.modelo.entities.Mediciones.TipoActividad.Alcance;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;
import domain.modelo.entities.Mediciones.Unidad;
import domain.modelo.entities.MedioTransporte.MedioTransporte;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Trayecto;
import domain.modelo.entities.MedioTransporte.ServicioContratado.ServicioContratado;
import domain.modelo.entities.MedioTransporte.ServicioContratado.TipoTransporteContratado;
import domain.modelo.entities.MedioTransporte.TransporteEcologico.TransporteEcologico;
import domain.modelo.entities.MedioTransporte.TransportePublico.Parada;
import domain.modelo.entities.MedioTransporte.TransportePublico.TransportePublico;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.TipoDeCombustible;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.TipoDeVehiculo;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.VehiculoParticular;
import domain.modelo.entities.SectorTerritorial.AgenteSectorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialMunicipal;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialProvincial;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.RepositorioMiembro;
import domain.modelo.repositories.RepositorioOrganizacion;
import domain.modelo.repositories.RepositorioUsuarios;
import domain.modelo.repositories.factories.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PersistenciaTest {
    @Test
    @DisplayName("Persistencia Roles")
    public void roles() {
        Repositorio<Rol> rolRepositorio = FactoryRepositorio.get(Rol.class);

        Rol miembro = new Rol();
        miembro.setNombre("MIEMBRO");
        miembro.agregarPermiso(Permiso.VER_MIEMBRO);
        miembro.agregarPermiso(Permiso.EDITAR_MIEMBRO);

        miembro.agregarPermiso(Permiso.CREAR_SOLICITUD);
        miembro.agregarPermiso(Permiso.VER_SOLICITUD);
        miembro.agregarPermiso(Permiso.EDITAR_SOLICITUD);
        miembro.agregarPermiso(Permiso.ELIMIAR_SOLICITUD);

        miembro.agregarPermiso(Permiso.CREAR_TRAYECTO);
        miembro.agregarPermiso(Permiso.VER_TRAYECTO);
        miembro.agregarPermiso(Permiso.EDITAR_TRAYECTO);
        miembro.agregarPermiso(Permiso.ELIMIAR_TRAYECTO);

        Rol organizacion = new Rol();
        organizacion.setNombre("ORGANIZACION");
        organizacion.agregarPermiso(Permiso.VER_ORGANIZACION);
        organizacion.agregarPermiso(Permiso.EDITAR_ORGANIZACION);

        organizacion.agregarPermiso(Permiso.VER_SOLICITUD);
        organizacion.agregarPermiso(Permiso.ELIMIAR_SOLICITUD);

        organizacion.agregarPermiso(Permiso.CREAR_CONTACTO);
        organizacion.agregarPermiso(Permiso.VER_CONTACTO);
        organizacion.agregarPermiso(Permiso.EDITAR_CONTACTO);
        organizacion.agregarPermiso(Permiso.ELIMIAR_CONTACTO);

        organizacion.agregarPermiso(Permiso.CREAR_SECTOR);
        organizacion.agregarPermiso(Permiso.VER_SECTOR);
        organizacion.agregarPermiso(Permiso.EDITAR_SECTOR);
        organizacion.agregarPermiso(Permiso.ELIMIAR_SECTOR);

        organizacion.agregarPermiso(Permiso.CARGA_MEDICIONES);

        Rol administrador = new Rol();
        administrador.setNombre("ADMINISTRADOR");
        administrador.agregarPermiso(Permiso.CREAR_TRANSPORTE);
        administrador.agregarPermiso(Permiso.VER_TRANSPORTE);
        administrador.agregarPermiso(Permiso.EDITAR_TRANSPORTE);
        administrador.agregarPermiso(Permiso.ELIMIAR_TRANSPORTE);

        administrador.agregarPermiso(Permiso.CREAR_FE);
        administrador.agregarPermiso(Permiso.VER_FE);
        administrador.agregarPermiso(Permiso.EDITAR_FE);
        administrador.agregarPermiso(Permiso.ELIMIAR_FE);

        administrador.agregarPermiso(Permiso.CREAR_ORGANIZACION);
        administrador.agregarPermiso(Permiso.VER_ORGANIZACION);
        administrador.agregarPermiso(Permiso.EDITAR_ORGANIZACION);
        administrador.agregarPermiso(Permiso.ELIMIAR_ORGANIZACION);

        administrador.agregarPermiso(Permiso.CREAR_MIEMBRO);
        administrador.agregarPermiso(Permiso.VER_MIEMBRO);
        administrador.agregarPermiso(Permiso.EDITAR_MIEMBRO);
        administrador.agregarPermiso(Permiso.ELIMIAR_MIEMBRO);

        administrador.agregarPermiso(Permiso.CREAR_SECTOR_TERRITORIAL);
        administrador.agregarPermiso(Permiso.VER_SECTOR_TERRITORIAL);
        administrador.agregarPermiso(Permiso.EDITAR_SECTOR_TERRITORIAL);
        administrador.agregarPermiso(Permiso.ELIMINAR_SECTOR_TERRITORIAL);

        administrador.agregarPermiso(Permiso.CREAR_AGENTE_SECTORIAL);
        administrador.agregarPermiso(Permiso.VER_AGENTE_SECTORIAL);
        administrador.agregarPermiso(Permiso.EDITAR_AGENTE_SECTORIAL);
        administrador.agregarPermiso(Permiso.ELIMINAR_AGENTE_SECTORIAL);

        // AGENTE SECTORIAL
        Rol agenteSectorial = new Rol();
        agenteSectorial.setNombre("AGENTE_SECTORIAL");

        agenteSectorial.agregarPermiso(Permiso.VER_AGENTE_SECTORIAL);
        agenteSectorial.agregarPermiso(Permiso.EDITAR_AGENTE_SECTORIAL);

        agenteSectorial.agregarPermiso(Permiso.VER_SECTOR_TERRITORIAL);

        agenteSectorial.agregarPermiso(Permiso.VER_REPORTES);


        rolRepositorio.agregar(miembro);
        rolRepositorio.agregar(organizacion);
        rolRepositorio.agregar(administrador);
        rolRepositorio.agregar(agenteSectorial);
    }

    @Test
    @DisplayName("Persistencia FE")
    public void fe() {
        Repositorio<FE> feRepositorio = FactoryRepositorio.get(FE.class);

        FE feEcologico = new FE();
        feEcologico.setUnidad(Unidad.lts);
        feEcologico.setDescripcion("FE transporte ecologico");
        feEcologico.setNombre("Ecologico");
        feEcologico.setValor(0.0);
        feEcologico.setEstado(Estado.ACTIVO);

        feRepositorio.agregar(feEcologico);
    }

    @Test
    @DisplayName("Persistencia Organizaciones")
    public void organizaciones() {
        RepositorioOrganizacion repositorioOrganizacion = FactoryRepositorioOrganizacion.get();
        RepositorioRol repositorioRol = FactoryRepositorioRol.get();
        //Repositorio<ClasificacionOrganizacion> clasificacionOrganizacionRepositorio = FactoryRepositorio.get(ClasificacionOrganizacion.class);

        Organizacion organizacionMostaza = EntidadesFactory.crearOrganizacionMostaza();
        Organizacion organizacionBurgerKing = EntidadesFactory.crearOrganizacionBurgerKing();
        Organizacion organizacionMcDonalds = EntidadesFactory.crearOrganizacionMcDonalds();

        Rol organizacion = repositorioRol.buscarRolPorNombre("ORGANIZACION");
        organizacionMostaza.getUsuario().setRol(organizacion);
        organizacionBurgerKing.getUsuario().setRol(organizacion);
        organizacionMcDonalds.getUsuario().setRol(organizacion);

        organizacionMostaza.getUsuario().setContrasenia("123456");
        organizacionBurgerKing.getUsuario().setContrasenia("123456");
        organizacionMcDonalds.getUsuario().setContrasenia("123456");

        ClasificacionOrganizacion clasificacionOrganizacion = new ClasificacionOrganizacion("Empresa Sector Secundario","Empresa comida rapida");

        organizacionMostaza.setClasificacionOrganizacion(clasificacionOrganizacion);
        organizacionBurgerKing.setClasificacionOrganizacion(clasificacionOrganizacion);
        organizacionMcDonalds.setClasificacionOrganizacion(clasificacionOrganizacion);

        organizacionMostaza.agregarContacto(new Contacto("Pablo Lopez","pablolopez@gmail.com","49082277"));
        organizacionBurgerKing.agregarContacto(new Contacto("Pablo Lopez","pablolopez@gmail.com","49082277"));
        organizacionMcDonalds.agregarContacto(new Contacto("Pablo Lopez","pablolopez@gmail.com","49082277"));

        organizacionMostaza.agregarContacto(new Contacto("Sofia Benitez", "sofi.beni@gmail.com","1109001654"));
        organizacionBurgerKing.agregarContacto(new Contacto("Sofia Benitez", "sofi.beni@gmail.com","1109001654"));
        organizacionMcDonalds.agregarContacto(new Contacto("Sofia Benitez", "sofi.beni@gmail.com","1109001654"));

        organizacionMostaza.agregarContacto(new Contacto("Jose Lopez","joselopez@gmail.com","49082277"));
        organizacionBurgerKing.agregarContacto(new Contacto("Jose Lopez","joselopez@gmail.com","49082277"));
        organizacionMcDonalds.agregarContacto(new Contacto("Jose Lopez","joselopez@gmail.com","49082277"));

        organizacionMostaza.agregarContacto(new Contacto("Maria Benitez", "maria.beni@gmail.com","1109001654"));
        organizacionBurgerKing.agregarContacto(new Contacto("Maria Benitez", "maria.beni@gmail.com","1109001654"));
        organizacionMcDonalds.agregarContacto(new Contacto("Maria Benitez", "maria.beni@gmail.com","1109001654"));

        organizacionMostaza.agregarHC(new HC(Periodicidad.ANUAL,"2020",3460.9));
        organizacionBurgerKing.agregarHC(new HC(Periodicidad.ANUAL,"2020",3460.9));
        organizacionMcDonalds.agregarHC(new HC(Periodicidad.ANUAL,"2020",3460.9));

        organizacionMostaza.agregarHC(new HC(Periodicidad.MENSUAL,"07/2020",500.0));
        organizacionBurgerKing.agregarHC(new HC(Periodicidad.MENSUAL,"07/2020",500.0));
        organizacionMcDonalds.agregarHC(new HC(Periodicidad.MENSUAL,"07/2020",500.0));

        organizacionMostaza.agregarHC(new HC(Periodicidad.ANUAL,"2019",50.0));
        organizacionBurgerKing.agregarHC(new HC(Periodicidad.ANUAL,"2019",50.0));
        organizacionMcDonalds.agregarHC(new HC(Periodicidad.ANUAL,"2019",50.0));

        organizacionMostaza.agregarHC(new HC(Periodicidad.MENSUAL,"07/2019",100.0));
        organizacionBurgerKing.agregarHC(new HC(Periodicidad.MENSUAL,"07/2019",100.0));
        organizacionMcDonalds.agregarHC(new HC(Periodicidad.MENSUAL,"07/2019",100.0));

        organizacionMostaza.agregarHC(new HC(Periodicidad.ANUAL,"2022",3460.9));
        organizacionBurgerKing.agregarHC(new HC(Periodicidad.ANUAL,"2022",3460.9));
        organizacionMcDonalds.agregarHC(new HC(Periodicidad.ANUAL,"2022",3460.9));

        organizacionMostaza.agregarHC(new HC(Periodicidad.MENSUAL,"07/2022",500.0));
        organizacionBurgerKing.agregarHC(new HC(Periodicidad.MENSUAL,"07/2022",500.0));
        organizacionMcDonalds.agregarHC(new HC(Periodicidad.MENSUAL,"07/2022",500.0));

        repositorioOrganizacion.agregar(organizacionMostaza);
        repositorioOrganizacion.agregar(organizacionBurgerKing);
        repositorioOrganizacion.agregar(organizacionMcDonalds);
    }

    @Test
    @DisplayName("Persistencia Miembros")
    public void miembros() {
        Miembro jose = EntidadesFactory.crearMiembroJose();
        Miembro mariela = EntidadesFactory.crearMiembroMariela();
        Miembro maria = EntidadesFactory.crearMiembroMaria();

        RepositorioRol repositorioRol = FactoryRepositorioRol.get();
        RepositorioMiembro repositorioMiembro = FactoryRepositorioMiembro.get();
        RepositorioUsuarios repositorioUsuarios = FactoryRepositorioUsuarios.get();
        Repositorio<MedioTransporte> repoMedioTransporte = FactoryRepositorio.get(MedioTransporte.class);

        Rol miembro = repositorioRol.buscarRolPorNombre("MIEMBRO");
        jose.getUsuario().setRol(miembro);
        maria.getUsuario().setRol(miembro);
        mariela.getUsuario().setRol(miembro);

        jose.getUsuario().setContrasenia("123456");
        maria.getUsuario().setContrasenia("123456");
        mariela.getUsuario().setContrasenia("123456");

        FE feEcologico = FactoryRepositorio.get(FE.class).buscar(5);


        TransporteEcologico ecologico = new TransporteEcologico();
        ecologico.setNombre("Long");
        ecologico.setDescripcion("Skate largo");
        ecologico.setFe(feEcologico);

        VehiculoParticular vehiculoParticular = new VehiculoParticular();
        vehiculoParticular.setFe(feEcologico);
        vehiculoParticular.setEstado(Estado.ACTIVO);
        vehiculoParticular.setDescripcion("auto poco contaminante");
        vehiculoParticular.setNombre("Fiat Duna");
        vehiculoParticular.setTipoDeVehiculo(TipoDeVehiculo.AUTO);
        vehiculoParticular.setTipoDeCombustible(TipoDeCombustible.NAFTA);

        repoMedioTransporte.agregar(ecologico);
        repoMedioTransporte.agregar(vehiculoParticular);

        repositorioMiembro.agregar(jose);
        repositorioMiembro.agregar(maria);
        repositorioMiembro.agregar(mariela);

        Rol rolAdmin = repositorioRol.buscarRolPorNombre("ADMINISTRADOR");
        Usuario admin = new Usuario();
        admin.setNombre("administrador");
        admin.setContrasenia("123456");
        admin.setRol(rolAdmin);

        repositorioUsuarios.agregar(admin);
    }

    @Test
    @DisplayName("Persistencia Tipos Actividades")
    public void tiposActividades() {
        Repositorio<TipoActividad> tipoActividadRepositorio = FactoryRepositorio.get(TipoActividad.class);
        FE feGenerico = new FE(1.0, Unidad.lts);
        feGenerico.setNombre("FE Generico");
        feGenerico.setDescripcion("FE utilizado para realizar pruebas y simplificar calculos");

        TipoActividad ta1 = new TipoActividad("Combustion Fija",
                new TipoConsumo("Gas Natural", Unidad.m3), Alcance.DIRECTO);
        ta1.setFE(feGenerico);

        TipoActividad ta2 = new TipoActividad("Combustion Fija",
                new TipoConsumo("Diesel", Unidad.lts), Alcance.DIRECTO);
        ta2.setFE(feGenerico);

        TipoActividad ta3 = new TipoActividad("Combustion Fija",
                new TipoConsumo("Kerosene", Unidad.lts), Alcance.DIRECTO);
        ta3.setFE(feGenerico);

        TipoActividad ta4 = new TipoActividad("LOGISTICA_PRODUCTOS_RESIDUOS",
                new TipoConsumo("LOGISTICA_PRODUCTOS_RESIDUOS",Unidad.lts, CategoriaLogistica.MATERIA_PRIMA,
                        MedioTransporteLogistica.UTILITARIO_LIVIANO,2.0), Alcance.DIRECTO);
        ta4.setFE(feGenerico);

        TipoActividad ta5 = new TipoActividad("LOGISTICA_PRODUCTOS_RESIDUOS",
                new TipoConsumo("LOGISTICA_PRODUCTOS_RESIDUOS", Unidad.lts, CategoriaLogistica.MATERIA_PRIMA,
                        MedioTransporteLogistica.CAMION_DE_CARGA, 1.0),
                Alcance.DIRECTO);
        ta5.setFE(feGenerico);

        tipoActividadRepositorio.agregar(ta1);
        tipoActividadRepositorio.agregar(ta2);
        tipoActividadRepositorio.agregar(ta3);
        tipoActividadRepositorio.agregar(ta4);
        tipoActividadRepositorio.agregar(ta5);
    }

    @Test
    @DisplayName("Persistencia Transportes")
    public void transportes() {
        Repositorio<TransporteEcologico> transporteEcologicoRepositorio = FactoryRepositorio.get(TransporteEcologico.class);
        Repositorio<FE> feRepositorio = FactoryRepositorio.get(FE.class);
        // Ojo ACA
        FE ecologica = feRepositorio.buscar(5);

        // Transportes Ecologicos
        TransporteEcologico pie = new TransporteEcologico("Pie",
                "traccion a sangre", ecologica);
        TransporteEcologico bicicleta = new TransporteEcologico("Bicicleta",
                "bicicleta para desplazarse por la ciudad", ecologica);
        TransporteEcologico patin = new TransporteEcologico("Patin",
                "vehiculo a sangre", ecologica);

        transporteEcologicoRepositorio.agregar(pie);
        transporteEcologicoRepositorio.agregar(bicicleta);
        transporteEcologicoRepositorio.agregar(patin);
    }

    @Test
    @DisplayName("Persistencia Tipo Transporte Contratado")
    public void tipoTransporteContratado() {
        Repositorio<FE> feRepositorio = FactoryRepositorio.get(FE.class);
        FE feTransporteContratado1 = new FE();
        feTransporteContratado1.setUnidad(Unidad.lts);
        feTransporteContratado1.setDescripcion("FE transporte pesado");
        feTransporteContratado1.setNombre("Vehiculo Pesado");
        feTransporteContratado1.setValor(1.0);
        feTransporteContratado1.setEstado(Estado.ACTIVO);

        FE feTransporteContratado2 = new FE();
        feTransporteContratado2.setUnidad(Unidad.lts);
        feTransporteContratado2.setDescripcion("FE transporte liviano");
        feTransporteContratado2.setNombre("Vehiculo Liviano");
        feTransporteContratado2.setValor(1.0);
        feTransporteContratado2.setEstado(Estado.ACTIVO);

        feRepositorio.agregar(feTransporteContratado1);
        feRepositorio.agregar(feTransporteContratado2);

        // Tipo Transporte Contratado
        TipoTransporteContratado tipo1 = new TipoTransporteContratado("Taxi", "Usa autos particulares");
        TipoTransporteContratado tipo2 = new TipoTransporteContratado("MotoTaxi", "Moto contratada como taxi");
        TipoTransporteContratado tipo3 = new TipoTransporteContratado("Flete", "Camion contratado");

        Repositorio<TipoTransporteContratado> tipoTransporteContratadoRepositorio = FactoryRepositorio.get(TipoTransporteContratado.class);
        tipoTransporteContratadoRepositorio.agregar(tipo1);
        tipoTransporteContratadoRepositorio.agregar(tipo2);
        tipoTransporteContratadoRepositorio.agregar(tipo3);

        Repositorio<ServicioContratado> servicioContratadoRepositorio = FactoryRepositorio.get(ServicioContratado.class);

        ServicioContratado servicioContratado = new ServicioContratado();
        servicioContratado.setTipo(tipo1);
        servicioContratado.setFe(feTransporteContratado2);
        servicioContratado.setNombre("Uber");
        servicioContratado.setDescripcion("Servicio de remisiria a domicilio");

        servicioContratadoRepositorio.agregar(servicioContratado);
    }

    @Test
    @DisplayName("Persistencia Transporte Particular")
    public void transporteParticular() {
        Repositorio<FE> feRepositorio = FactoryRepositorio.get(FE.class);

        FE feAutoLujo = new FE();
        feAutoLujo.setUnidad(Unidad.lts);
        feAutoLujo.setNombre("FE Auto de Lujo");
        feAutoLujo.setDescripcion("Vehiculo de lujo que consume mucho");
        feAutoLujo.setValor(1.0);
        feAutoLujo.setEstado(Estado.ACTIVO);

        FE feAutoComun = new FE();
        feAutoComun.setUnidad(Unidad.lts);
        feAutoComun.setDescripcion("FE de auto que consume lo normal");
        feAutoComun.setNombre("Vehiculo noraml");
        feAutoComun.setValor(1.0);
        feAutoComun.setEstado(Estado.ACTIVO);

        feRepositorio.agregar(feAutoLujo);
        feRepositorio.agregar(feAutoComun);


        Repositorio<VehiculoParticular> vehiculoParticularRepositorio = FactoryRepositorio.get(VehiculoParticular.class);

        VehiculoParticular vehiculoParticular = new VehiculoParticular();
        vehiculoParticular.setFe(feAutoComun);
        vehiculoParticular.setNombre("Fiat Duna");
        vehiculoParticular.setDescripcion("Auto de la marca fiat del 2000");
        vehiculoParticular.setTipoDeCombustible(TipoDeCombustible.GASOIL);
        vehiculoParticular.setTipoDeVehiculo(TipoDeVehiculo.AUTO);

        vehiculoParticularRepositorio.agregar(vehiculoParticular);
    }

    @Test
    @DisplayName("Persistencia Sectores Territoriales")
    public void sectorTerritorial(){
        Repositorio<AgenteSectorial> repoAgenteSectorial = FactoryRepositorio.get(AgenteSectorial.class);
        RepositorioRol repositorioRol = FactoryRepositorioRol.get();
        RepositorioOrganizacion repoOrganizacion = FactoryRepositorioOrganizacion.get();

        Organizacion mostaza = repoOrganizacion.buscarOrganizacionPorNombre("Mostaza S.A");
        Organizacion burger = repoOrganizacion.buscarOrganizacionPorNombre("BurgerKing S.A.");
        Organizacion mcdonalds = repoOrganizacion.buscarOrganizacionPorNombre("Mc Donalds S.A.");

        Rol agente = repositorioRol.buscarRolPorNombre("AGENTE_SECTORIAL");

        SectorTerritorialMunicipal municipal1 = new SectorTerritorialMunicipal("Municipio de Ezeiza");
        SectorTerritorialMunicipal municipal2 = new SectorTerritorialMunicipal("Municipio de Avellaneda");
        SectorTerritorialProvincial provincial1 = new SectorTerritorialProvincial("Provincia de BSAS");

        municipal1.setSectorTerritorialProvincial(provincial1);
        municipal2.setSectorTerritorialProvincial(provincial1);

        mostaza.setSectorTerritorialMunicipal(municipal1);
        burger.setSectorTerritorialMunicipal(municipal2);
        mcdonalds.setSectorTerritorialMunicipal(municipal2);

        Repositorio<HC> repoHCs = FactoryRepositorio.get(HC.class);
        List<HC> hcs = repoHCs.buscarTodos();
        hcs.forEach(hc -> hc.setSectorTerritorial(hc.getOrganizacion().getSectorTerritorialMunicipal()));

        Direccion direccionEzeiza = new Direccion("Au. Tte. General Pablo Riccheri","33",3619,"Ezeiza");
        direccionEzeiza.setProvincia(168);
        direccionEzeiza.setMunicipio(368);
        municipal1.setDireccion(direccionEzeiza);

        Direccion direccionAvellaneda = new Direccion("Cnel. Adrogué","950",3316,"Avellaneda");
        direccionAvellaneda.setProvincia(168);
        direccionAvellaneda.setMunicipio(335);
        municipal2.setDireccion(direccionAvellaneda);

        Direccion direccionPBA = new Direccion("Moreno","1025",3279,"Adolfo Alsina");
        direccionPBA.setProvincia(168);
        direccionPBA.setMunicipio(330);
        provincial1.setDireccion(direccionPBA);
        //AGENTE M1

        Usuario usuarioASM1 = new Usuario("fgoldan","123456");
        usuarioASM1.setRol(agente);
        usuarioASM1.setContrasenia("123456");

        AgenteSectorial aSM1 = new AgenteSectorial(municipal1);
        aSM1.setNombreApellido("Fernando Goldan");
        aSM1.setUsuario(usuarioASM1);

        //AGENTE M2

        Usuario usuarioASM2 = new Usuario("jmoreno","123456");
        usuarioASM2.setRol(agente);
        usuarioASM2.setContrasenia("123456");

        AgenteSectorial aSM2 = new AgenteSectorial(municipal2);
        aSM2.setNombreApellido("Julieta Moreno");
        aSM2.setUsuario(usuarioASM2);

        //AGENTE P1
        Usuario usuarioASP1 = new Usuario("aigartua","123456");
        usuarioASP1.setRol(agente);
        usuarioASP1.setContrasenia("123456");

        AgenteSectorial aSP1 = new AgenteSectorial(provincial1);
        aSP1.setNombreApellido("Alexis Igartua");
        aSP1.setUsuario(usuarioASP1);

        repoAgenteSectorial.agregar(aSM1);
        repoAgenteSectorial.agregar(aSM2);
        repoAgenteSectorial.agregar(aSP1);
    }

/*

    @Test
    @DisplayName("Persistencia Trayectos")
    public void trayectos() {
        RepositorioMiembro repositorioMiembro = FactoryRepositorioMiembro.get();
        Miembro jose = repositorioMiembro.buscar(40);

        //ORGANIZACION
        RepositorioOrganizacion repositorioOrganizacion = FactoryRepositorioOrganizacion.get();
        Organizacion mostaza = repositorioOrganizacion.buscar(6);
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

 */

}
