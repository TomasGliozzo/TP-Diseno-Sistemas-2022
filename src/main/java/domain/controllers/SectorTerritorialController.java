package domain.controllers;

import db.EntityManagerHelper;
import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Miembro.TipoDeDocumento;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.TipoOrganizacion;
import domain.modelo.entities.Entidades.Usuario.Rol;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.SectorTerritorial.AgenteSectorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialMunicipal;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialProvincial;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Localidad;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Municipio;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Provincia;
import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
import domain.modelo.entities.ValidadorContrasenia.ValidadorContrasenia;
import domain.modelo.entities.ValidadorContrasenia.ValidarContraseniaSinNombreDeUsuario;
import domain.modelo.entities.ValidadorContrasenia.ValidarLongitudContrasenia;
import domain.modelo.entities.ValidadorContrasenia.ValidarTopContraseniasDebiles;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.RepositorioAgenteSectorial;
import domain.modelo.repositories.RepositorioOrganizacion;
import domain.modelo.repositories.RepositorioUsuarios;
import domain.modelo.repositories.factories.*;

import helpers.AlertaHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.*;
import java.util.stream.Collectors;


public class SectorTerritorialController {
    private Repositorio<SectorTerritorial> sectorTerritorialRepositorio;

    private Repositorio<SectorTerritorialMunicipal> sectorTerritorialMunicipalRepositorio;
    private Repositorio<SectorTerritorialProvincial> sectorTerritorialProvincialRepositorio;
    private RepositorioAgenteSectorial agenteSectorialRepositorio;
    private RepositorioUsuarios repoUsuarios;
    private ValidadorContrasenia validadorContrasenia;
    private RepositorioRol repositorioRol;
    private RepositorioOrganizacion repositorioOrganizacion;

    private String mensajeSucces;
    private String mensajeDanger;
    public SectorTerritorialController(){
        sectorTerritorialRepositorio = FactoryRepositorio.get(SectorTerritorial.class);
        sectorTerritorialMunicipalRepositorio = FactoryRepositorio.get(SectorTerritorialMunicipal.class);
        sectorTerritorialProvincialRepositorio = FactoryRepositorio.get(SectorTerritorialProvincial.class);
        agenteSectorialRepositorio = FactoryRepositorioAgenteSectorial.get();
        repoUsuarios = FactoryRepositorioUsuarios.get();
        repositorioRol = FactoryRepositorioRol.get();
        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        validadorContrasenia = new ValidadorContrasenia();
        validadorContrasenia.agregarOpcionesValidacion(
                new ValidarTopContraseniasDebiles(),
                new ValidarLongitudContrasenia(),
                new ValidarContraseniaSinNombreDeUsuario(),
                new ValidarLongitudContrasenia()
        );
    }


    public ModelAndView mostrarTodos(Request request, Response response) {

        String urlEliminar = "/sectores-territoriales/eliminar";

        List<SectorTerritorial> sectores = sectorTerritorialRepositorio.buscarTodos().stream()
                .filter(o -> o.getEstado().equals(Estado.ACTIVO))
                .collect(Collectors.toList());

        return new ModelAndView(new HashMap(){{
            put("admin", true);
            put("sectores_territoriales",sectores);
            put("url",urlEliminar);
        }}, "sector_territorial/sectores-territoriales.hbs");
    }

    /*
    public ModelAndView editar(Request request, Response response) {
        String id_sector = request.params("id");
        String url = "/sectores-territoriales/" + id_sector + "/editar";

        SectorTerritorial sector_buscado = sectorTerritorialRepositorio.buscar(Integer.parseInt(id_sector));
        return new ModelAndView(new HashMap<String, Object>(){{
            put("admin", true);
            put("sector_territorial",sector_buscado);
            put("url",url);
        }}, "sector_territorial/sector_territorial.hbs");
    }


    public Response modificar(Request request, Response response) {

            SectorTerritorial sector = sectorTerritorialRepositorio.buscar(Integer.parseInt(request.params("id")));
            this.asignarParametros(sector, request);
            sectorTerritorialRepositorio.modificar(sector);

            response.redirect("/sectores-territoriales");

            EntityManagerHelper.closeEntityManager();
            return response;
    }

    public Response modificar1(Request request, Response response) {
            SectorTerritorial sectorAModificar = sectorTerritorialRepositorio.buscar(Integer.parseInt(request.params("id")));
            this.asignarParametros(sectorAModificar, request);
            this.sectorTerritorialRepositorio.modificar(sectorAModificar);

            response.redirect("/sectores-territoriales");
            EntityManagerHelper.closeEntityManager();
            return response;
    }

     */
    public Response eliminar(Request request, Response response) {
        String id_sector = request.queryParams("eliminar");

        String mensajeSucces = "Se ha podido eliminar el sector territorial";
        String mensajeDanger = "No se ha podido eliminar el sector territorial";

        if(!id_sector.isEmpty()) {
            SectorTerritorial sectorTerritorial = sectorTerritorialRepositorio.buscar(Integer.parseInt(id_sector));

            if(sectorTerritorial != null) {
                sectorTerritorial.setEstado(Estado.INACTIVO);
                sectorTerritorial.getAgenteSectorial().setEstado(Estado.INACTIVO);
                sectorTerritorial.getAgenteSectorial().getUsuario().setEstado(Estado.INACTIVO);

                sectorTerritorialRepositorio.modificar(sectorTerritorial);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                response.redirect("/sectores-territoriales");
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        response.redirect("/sectores-territoriales");
        EntityManagerHelper.closeEntityManager();
        return response;
    }

    public ModelAndView crear(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        String url = "/sectores-territoriales/crear/guardar";
        String urlProvincia = "/sectores-territoriales/crear/provincia";
        String urlMunicipio = "/sectores-territoriales/crear/municipio";
        String urlLocalidad = "/sectores-territoriales/crear/localidad";
        String urlCiudad = "/sectores-territoriales/crear/ciudad";

        List<Provincia> provincias = ServicioDistancia.instancia().listaProvincias();

        parametros.put("provincia_inicio", request.session().attribute("provincia_inicio"));
        parametros.put("municipio_inicio", request.session().attribute("municipio_inicio"));
        parametros.put("localidad_inicio", request.session().attribute("localidad_inicio"));
        parametros.put("ciudad_inicio",request.session().attribute("ciudad_inicio"));
        parametros.put("calle_inicio", request.session().attribute("calle_inicio"));
        parametros.put("numero_inicio", request.session().attribute("numero_inicio"));

        parametros.put("provincias", provincias);
        parametros.put("municipios", request.session().attribute("municipios"));
        parametros.put("localidades", request.session().attribute("localidades"));

        parametros.put("url", url);
        parametros.put("urlMunicipio", urlMunicipio);
        parametros.put("urlProvincia", urlProvincia);
        parametros.put("urlLocalidad", urlLocalidad);
        parametros.put("urlCiudad", urlCiudad);
        parametros.put("admin",true);

        return new ModelAndView(parametros,"sector_territorial/sector_territorial_crear.hbs");
    }

    public Response guardar(Request request, Response response) {
        mensajeSucces = "Se ha podido crear el ST";
        mensajeDanger = "No se ha podido crear el ST";
        AgenteSectorial agente = new AgenteSectorial();
        try {
            if (Objects.equals(request.queryParams("tipoSector"), "MUNICIPAL")) {
                SectorTerritorialMunicipal nuevoSector = new SectorTerritorialMunicipal();
                nuevoSector.agregarAgenteSectorial(agente);
                asignarParametros(nuevoSector, request);
                asignarAtributosDireccion(nuevoSector, "inicio", request);

                sectorTerritorialRepositorio.agregar(nuevoSector);
            //TODO reveer o sacar
            /*
                //AGREGAR ORGS REZAGADAS
                int municipio = nuevoSector.getDireccion().getMunicipio();

                List<Organizacion> organizaciones = repositorioOrganizacion.buscarTodos().
                                    stream().filter(o->o.getUbicacionGeografica().getMunicipio() == municipio).
                                    collect(Collectors.toList());

                List<Organizacion> organizacionesSinSector = organizaciones.stream().
                                    filter(o->o.getSectorTerritorialMunicipal() == null).
                                    collect(Collectors.toList());


                organizacionesSinSector.forEach(o->o.agregarST(nuevoSector));
                organizacionesSinSector.forEach(o->repositorioOrganizacion.modificar(o));

             */

                //AGREGAR ST PROVINCIAL EXISTENTE
                int provincia = nuevoSector.getDireccion().getProvincia();

                List<SectorTerritorialProvincial> provincias = sectorTerritorialProvincialRepositorio.buscarTodos();
                SectorTerritorialProvincial provinciaReq = provincias.stream().
                                                filter(s->s.getDireccion().getProvincia() == provincia).
                                                collect(Collectors.toList()).get(0);

                provinciaReq.agregarSector(nuevoSector);
                sectorTerritorialProvincialRepositorio.modificar(provinciaReq);



            } else {
                SectorTerritorialProvincial nuevoSector = new SectorTerritorialProvincial();
                nuevoSector.agregarAgenteSectorial(agente);
                asignarParametros(nuevoSector, request);
                asignarAtributosDireccion(nuevoSector, "inicio", request);

                sectorTerritorialRepositorio.agregar(nuevoSector);

                /* TODO reveer o sacar
                //AGREGO STs MUNICIPALES REZAGADOS

                int provincia = nuevoSector.getDireccion().getProvincia();

                List<SectorTerritorialMunicipal> sectores = sectorTerritorialMunicipalRepositorio.buscarTodos().
                                                                stream().filter(s->s.getDireccion().getProvincia() == provincia).
                                                                collect(Collectors.toList());
                List<SectorTerritorialMunicipal> sectoresSinProvincial = sectores.stream().
                                                                            filter(s->s.getSectorTerritorialProvincial() == null).
                                                                            collect(Collectors.toList());
                if(!sectoresSinProvincial.isEmpty()){
                        sectoresSinProvincial.forEach(s->s.agregarSTProv(nuevoSector));
                        sectoresSinProvincial.forEach(s->sectorTerritorialMunicipalRepositorio.modificar(s));
                }
                 */
            }

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);
            response.redirect("/sectores-territoriales");

            } catch (Exception e) {
                request.session().attribute("danger",true);
                request.session().attribute("mensaje", mensajeDanger);
                response.redirect("/sectores-territoriales");
            } finally {

                request.session().removeAttribute("provincia_inicio");
                request.session().removeAttribute("municipio_inicio");
                request.session().removeAttribute("localidad_inicio");

                request.session().removeAttribute("provincias");
                request.session().removeAttribute("municipios");
                request.session().removeAttribute("localidades");

                EntityManagerHelper.closeEntityManager();
                return response;
            }
    }

    private void asignarParametros(SectorTerritorial sector, Request request) {

        if(request.queryParams("descripcion") != null) {
            sector.setDescripcion(request.queryParams("descripcion"));
        }

        AgenteSectorial agente = sector.getAgenteSectorial();

        if (request.queryParams("nombreApellido") != null){
            agente.setNombreApellido(request.queryParams("nombreApellido"));
        }

        String nombreUsuario = request.queryParams("usuario");
        String contrasenia = request.queryParams("contrasenia");

        if(!nombreUsuario.isEmpty() && !contrasenia.isEmpty() && repoUsuarios.buscarUsuarioPorNombre(nombreUsuario)==null) {
            Rol rolAgente = repositorioRol.buscarRolPorNombre("AGENTE_SECTORIAL");

            Usuario usuario = new Usuario(nombreUsuario, contrasenia);
            usuario.setRol(rolAgente);
            agente.setUsuario(usuario);

            if(!validadorContrasenia.validarContrasenia(usuario)) throw new RuntimeException();

            usuario.setContrasenia(contrasenia);
        }


    }

    public ModelAndView mostrarCargaDireccion(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        String url = "/sectores-territoriales/guardarDireccion";
        String urlProvincia = "/sectores-territoriales/crear/provincia";
        String urlMunicipio = "/sectores-territoriales/crear/municipio";
        String urlLocalidad = "/sectores-territoriales/crear/localidad";
        String urlCiudad = "/sectores-territoriales/crear/ciudad";

        List<Provincia> provincias = ServicioDistancia.instancia().listaProvincias();

        parametros.put("provincia_inicio", request.session().attribute("provincia_inicio"));
        parametros.put("municipio_inicio", request.session().attribute("municipio_inicio"));
        parametros.put("localidad_inicio", request.session().attribute("localidad_inicio"));

        parametros.put("ciudad_inicio",request.session().attribute("ciudad_inicio"));
        parametros.put("calle_inicio", request.session().attribute("calle_inicio"));
        parametros.put("numero_inicio", request.session().attribute("numero_inicio"));

        parametros.put("provincias", provincias);
        parametros.put("municipios", request.session().attribute("municipios"));
        parametros.put("localidades", request.session().attribute("localidades"));

        parametros.put("url", url);
        parametros.put("urlMunicipio", urlMunicipio);
        parametros.put("urlProvincia", urlProvincia);
        parametros.put("urlLocalidad", urlLocalidad);
        parametros.put("urlCiudad", urlCiudad);

        return new ModelAndView(parametros,"/sector_territorial/sector_territorial_crear_direccion.hbs");
    }
    public Response guardarDireccion(Request request, Response response) {

        String mensajeSucces = "Se ha creado el sector territorial exitosamente";
        String mensajeDanger = "No se ha creado el sector territorial";

        try {
            String id_sector = request.params("id_sector");
            SectorTerritorial sector = sectorTerritorialRepositorio.buscar(Integer.parseInt(id_sector));

            asignarAtributosDireccion(sector,"inicio",request);
            sectorTerritorialRepositorio.modificar(sector);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {

            response.redirect("/sectores-territoriales");
            EntityManagerHelper.closeEntityManager();
            return response;
        }
    }

    public Response actualizarProvinciaInicio(Request request, Response response) {
        return this.actualizarProvincia(request, response, "inicio");
    }

    public Response actualizarMunicipioInicio(Request request, Response response) {
        return this.actualizarMunicipio(request, response, "inicio");
    }

    public Response actualizarLocalidadInicio(Request request, Response response) {
        return this.actualizarLocalidad(request, response, "inicio");
    }

    public Response actualizarCiudadInicio(Request request, Response response) {
        return this.actualizarCiudad(request, response, "inicio");
    }

    public Response actualizarProvincia(Request request, Response response, String prefijo) {
        Integer idProvincia = Integer.valueOf(request.params("id_provincia"));

        request.session().attribute("municipios", ServicioDistancia.instancia().listaMunicipios(idProvincia));
        request.session().attribute("provincia_" + prefijo,
                ServicioDistancia.instancia().getProvincia(idProvincia));

        return response;
    }

    public Response actualizarMunicipio(Request request, Response response, String prefijo) {
        Provincia provincia = request.session().attribute("provincia_" + prefijo);
        int id_municipio = Integer.parseInt(request.params("id_municipio"));

        request.session().attribute("localidades", ServicioDistancia.instancia().listaLocalidades(id_municipio));
        request.session().attribute("municipio_" + prefijo,
                ServicioDistancia.instancia().getMunicipio(provincia.getId(), id_municipio));

        return response;
    }

    public Response actualizarLocalidad(Request request, Response response, String prefijo) {
        Integer id_localidad = Integer.valueOf(request.params("id_localidad"));
        Municipio municipio = request.session().attribute("municipio_"+prefijo);

        request.session().attribute("localidad_" + prefijo,
                ServicioDistancia.instancia().getLocalidad(municipio.getId(),id_localidad));

        return response;
    }

    public Response actualizarCiudad(Request request, Response response, String prefijo) {
        String ciudad = request.params("ciudad");
        String calle = request.params("calle");
        String numero = request.params("numero");

        request.session().attribute("ciudad_" + prefijo, ciudad);
        request.session().attribute("calle_" + prefijo, calle);
        request.session().attribute("numero_" + prefijo, numero);

        return response;
    }

    private void asignarAtributosDireccion(SectorTerritorial sector, String prefijo, Request request) {

        Direccion direccion = new Direccion();

        Localidad localidad = request.session().attribute("localidad_"+prefijo);
        if( localidad != null){
            direccion.setLocalidad(localidad.getId());
        }
        else {
            mensajeDanger = "Localidad Invalida";
            throw new RuntimeException();
        }

        Municipio municipio = request.session().attribute("municipio_"+prefijo);
        if( municipio != null ){
            direccion.setMunicipio(municipio.getId());
        }
        else {
            mensajeDanger = "Municipio Invalido";
            throw new RuntimeException();
        }


        Provincia provincia = request.session().attribute("provincia_"+prefijo);
        if( provincia != null ){
            direccion.setProvincia(provincia.getId());
        }
        else {
            mensajeDanger = "Provincia Invalida";
            throw new RuntimeException();
        }

        String ciudad = request.queryParams("ciudad_" + prefijo);
        if( !ciudad.isEmpty() ){
            direccion.setCiudad(ciudad);
        }
        else {
            mensajeDanger = "No ha ingresado una ciudad";
            throw new RuntimeException();
        }

        String calle = request.queryParams("calle_" + prefijo);
        if( !calle.isEmpty() ){
            direccion.setNombreCalle(calle);
        }
        else {
            mensajeDanger = "No ha ingresado una calle";
            throw new RuntimeException();
        }


        String numero = request.queryParams("numero_"+prefijo);
        if( !numero.isEmpty() ){
            direccion.setNumeroCalle(numero);
        }
        else {
            mensajeDanger = "No ha ingresado un numero de calle";
            throw new RuntimeException();
        }

        sector.setDireccion(direccion);

    }
}
