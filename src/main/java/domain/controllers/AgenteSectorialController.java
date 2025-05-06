package domain.controllers;

import db.EntityManagerHelper;
import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Miembro.TipoDeDocumento;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.Sector;
import domain.modelo.entities.Entidades.Usuario.Rol;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.entities.HuellaCarbono.HC;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.SectorTerritorial.AgenteSectorial;
import domain.modelo.entities.SectorTerritorial.SectorTerritorial;
import domain.modelo.entities.ValidadorContrasenia.ValidadorContrasenia;
import domain.modelo.entities.ValidadorContrasenia.ValidarContraseniaSinNombreDeUsuario;
import domain.modelo.entities.ValidadorContrasenia.ValidarLongitudContrasenia;
import domain.modelo.entities.ValidadorContrasenia.ValidarTopContraseniasDebiles;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.RepositorioOrganizacion;
import domain.modelo.repositories.RepositorioUsuarios;
import domain.modelo.repositories.factories.*;
import helpers.AlertaHelper;
import helpers.UsuarioLogueadoHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class AgenteSectorialController {
    private Repositorio<AgenteSectorial> agenteSectorialRepositorio;
    private Repositorio<SectorTerritorial> sectorTerritorialRepositorio;
    private RepositorioUsuarios repoUsuarios;
    private ValidadorContrasenia validadorContrasenia;
    private RepositorioRol repositorioRol;

    public AgenteSectorialController(){
        agenteSectorialRepositorio = FactoryRepositorio.get(AgenteSectorial.class);
        sectorTerritorialRepositorio = FactoryRepositorio.get(SectorTerritorial.class);
        repoUsuarios = FactoryRepositorioUsuarios.get();
        repositorioRol = FactoryRepositorioRol.get();

        validadorContrasenia = new ValidadorContrasenia();
        validadorContrasenia.agregarOpcionesValidacion(
                new ValidarTopContraseniasDebiles(),
                new ValidarLongitudContrasenia(),
                new ValidarContraseniaSinNombreDeUsuario(),
                new ValidarLongitudContrasenia()
        );
    }

    public ModelAndView mostrarTodos(Request request, Response response) {

        String urlEliminar = "/agentes-sectoriales/eliminar";

        List<AgenteSectorial> agentes = (agenteSectorialRepositorio.buscarTodos()).
                                        stream().filter(a->a.getEstado().equals(Estado.ACTIVO)).
                                        collect(Collectors.toList());

        return new ModelAndView(new HashMap<String, Object>(){{
            put("admin",true);
            put("url",urlEliminar);
            put("agentes",agentes);
        }}, "agente_sectorial/agentes-sectoriales.hbs");
    }

    public Response eliminar(Request request, Response response) {
        String id_agente = request.queryParams("eliminar");

        String mensajeSucces = "Se ha podido eliminar el agente sectorial";
        String mensajeDanger = "No se ha podido eliminar el agente sectorial";

        if(!id_agente.isEmpty()) {
            AgenteSectorial agenteSectorial = agenteSectorialRepositorio.buscar(Integer.parseInt(id_agente));

            if(agenteSectorial != null) {
                agenteSectorial.setEstado(Estado.INACTIVO);
                agenteSectorial.getUsuario().setEstado(Estado.INACTIVO);
                agenteSectorial.getSectorTerritorial().setEstado(Estado.INACTIVO);

                sectorTerritorialRepositorio.modificar(agenteSectorial);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                response.redirect("/agentes-sectoriales");
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        response.redirect("/agentes-sectoriales");
        EntityManagerHelper.closeEntityManager();
        return response;
    }

    public ModelAndView editar(Request request, Response response) {

        Map<String, Object> parametros = new HashMap<>();

        Integer id_usuario = Integer.valueOf(request.params("id"));
        AgenteSectorial agente_buscado = this.agenteSectorialRepositorio.buscar(id_usuario);

        Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);
        String url;
        String urlCambio;

        if(usuario.getId() == agente_buscado.getUsuario().getId()) {
            url = "/agentes-sectoriales/" + id_usuario + "/editar";
            urlCambio = "/agentes-sectoriales/" + id_usuario + "/editar/cambiar_contrasenia";
            parametros.put("agente", agente_buscado.getId());
        }
        else {
            url = "/agentes-sectoriales/" + id_usuario + "/editar";
            urlCambio = "/agentes-sectoriales/" + id_usuario + "/editar/cambiar_contrasenia";
            parametros.put("admin",true);
        }


        parametros.put("url",url);
        parametros.put("urlCambio",urlCambio);
        parametros.put("agente",agente_buscado);
        this.agregarAlerta(request, parametros);

        return new ModelAndView(parametros, "/agente_sectorial/agente-sectorial.hbs");
    }

    public ModelAndView editarPerfil(Request request, Response response){
        Map<String, Object> parametros = new HashMap<>();

        Integer id_usuario = Integer.valueOf(request.params("id"));
        AgenteSectorial agente_buscado = this.agenteSectorialRepositorio.buscar(id_usuario);

        Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);
        String url;
        String urlCambio;

        if(usuario.getId() == agente_buscado.getUsuario().getId()) {
            url = "/agente-sectorial/" + id_usuario + "/editar";
            urlCambio = "/agente-sectorial/" + id_usuario + "/editar/cambiar_contrasenia";
            parametros.put("agente", agente_buscado.getId());
        }
        else {
            url = "/agente-sectorial/" + id_usuario + "/editar";
            urlCambio = "/agente-sectorial/" + id_usuario + "/editar/cambiar_contrasenia";
            parametros.put("agente",true);
        }

        parametros.put("agente-editar",agente_buscado);
        parametros.put("url",url);
        parametros.put("urlCambio",urlCambio);
        this.agregarAlerta(request, parametros);

        return new ModelAndView(parametros, "/agente_sectorial/mi-perfil.hbs");
    }

    public Response modificarPerfil(Request request, Response response) {

        AgenteSectorial agenteAModificar = agenteSectorialRepositorio.buscar(Integer.parseInt(request.params("id")));

        this.asignarParametros(agenteAModificar, request);
        agenteSectorialRepositorio.modificar(agenteAModificar);

        response.redirect("/agente-sectorial/" + request.params("id") + "/editar");

        EntityManagerHelper.closeEntityManager();
        return response;
    }

    public ModelAndView cambiarContraseniaPerfil(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        Integer id_agente = Integer.valueOf(request.params("id"));
        AgenteSectorial agente = agenteSectorialRepositorio.buscar(id_agente);
        Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);
        String urlCambio;
        String urlVolver;

        if(usuario.getId() == agente.getUsuario().getId()) {
            urlVolver = "/agente-sectorial/" + id_agente + "/editar";
            urlCambio = "/agente-sectorial/" + id_agente + "/editar/cambiar_contrasenia";
            parametros.put("agente", agente.getId());
        }
        else {
            urlVolver = "/agente-sectorial/" + id_agente + "/editar";
            urlCambio = "/agente-sectorial/" + id_agente + "/editar/cambiar_contrasenia";
            parametros.put("admin", true);
        }

        parametros.put("urlCambio", urlCambio);
        parametros.put("urlVolver", urlVolver);
        this.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/login-register/cambio_contrasenia.hbs");
    }

    public Response modificarContraseniaPerfil(Request request, Response response) {
        String mensajeSucces = "Se ha modificado la contraseña";
        String mensajeDanger = "No se ha modificado la contraseña";

        int id_agente = Integer.parseInt(request.params("id"));
        AgenteSectorial agente = agenteSectorialRepositorio.buscar(id_agente);

        try{
            Usuario usuario =  repoUsuarios.buscar(agente.getUsuario().getId());
            Usuario usuarioAux = new Usuario();

            String contraseniaVieja = request.queryParams("contrasenia-vieja");
            String contraseniaNueva = request.queryParams("contrasenia-nueva");

            usuarioAux.setContrasenia(contraseniaVieja);

            if(usuario!=null && !contraseniaNueva.isEmpty() && !contraseniaVieja.isEmpty() &&
                    usuario.getContrasenia().equals(usuarioAux.getContrasenia())) {

                usuario.setContrasenia(contraseniaNueva);

                if(!validadorContrasenia.validarContrasenia(usuario))
                    throw new RuntimeException();

                repoUsuarios.modificar(usuario);
            }
            else {
                throw new RuntimeException();
            }

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {

            response.redirect("/agente-sectorial/" + id_agente + "/editar");

            EntityManagerHelper.closeEntityManager();
            return response;
        }
    }

    public Response modificar(Request request, Response response) {
        agenteSectorialRepositorio = FactoryRepositorio.get(AgenteSectorial.class);

        AgenteSectorial agenteAModificar = this.agenteSectorialRepositorio.buscar(Integer.parseInt(request.params("id")));

        this.asignarParametros(agenteAModificar, request);
        this.agenteSectorialRepositorio.modificar(agenteAModificar);

        response.redirect("/agentes-sectoriales");

        EntityManagerHelper.closeEntityManager();
        return response;

    }

    private void asignarParametros(AgenteSectorial agente, Request request) {

        if(request.queryParams("nombreApellido") != null) {
            agente.setNombreApellido(request.queryParams("nombreApellido"));
        }

        if(request.queryParams("nombreUsuario") != null) {
            agente.getUsuario().setNombre(request.queryParams("nombreUsuario"));
        }

        if (request.queryParams("sectorTerritorial") != null){
            agente.agregarSectorTerritorial(sectorTerritorialRepositorio.buscar(Integer.parseInt(request.queryParams("sectorTerritorial"))));
        }
    }

    public ModelAndView cambiarContrasenia(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        Integer id_agente = Integer.valueOf(request.params("id"));
        AgenteSectorial agente = agenteSectorialRepositorio.buscar(id_agente);
        Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);
        String urlCambio;
        String urlVolver;

        if(usuario.getId() == agente.getUsuario().getId()) {
            urlVolver = "/agentes-sectoriales/" + id_agente + "/editar";
            urlCambio = "/agentes-sectoriales/" + id_agente + "/editar/cambiar_contrasenia";
            parametros.put("agente", agente.getId());
        }
        else {
            urlVolver = "/agentes-sectoriales/" + id_agente + "/editar";
            urlCambio = "/agentes-sectoriales/" + id_agente + "/editar/cambiar_contrasenia";
            parametros.put("admin", true);
        }

        parametros.put("urlCambio", urlCambio);
        parametros.put("urlVolver", urlVolver);
        this.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/login-register/cambio_contrasenia.hbs");
    }

    public Response modificarContrasenia(Request request, Response response) {
        String mensajeSucces = "Se ha modificado la contraseña";
        String mensajeDanger = "No se ha modificado la contraseña";

        int id_agente = Integer.parseInt(request.params("id"));
        AgenteSectorial agente = agenteSectorialRepositorio.buscar(id_agente);

        try{
            Usuario usuario =  repoUsuarios.buscar(agente.getUsuario().getId());
            Usuario usuarioAux = new Usuario();

            String contraseniaVieja = request.queryParams("contrasenia-vieja");
            String contraseniaNueva = request.queryParams("contrasenia-nueva");

            usuarioAux.setContrasenia(contraseniaVieja);

            if(usuario!=null && !contraseniaNueva.isEmpty() && !contraseniaVieja.isEmpty() &&
                    usuario.getContrasenia().equals(usuarioAux.getContrasenia())) {

                usuario.setContrasenia(contraseniaNueva);

                if(!validadorContrasenia.validarContrasenia(usuario))
                    throw new RuntimeException();

                repoUsuarios.modificar(usuario);
            }
            else {
                throw new RuntimeException();
            }

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {
            Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);

            if(usuario.getId() == agente.getUsuario().getId())
                response.redirect("/agentes-sectoriales/" + id_agente + "/editar");
            else
                response.redirect("/agentes-sectoriales");

            EntityManagerHelper.closeEntityManager();
            return response;
        }
    }

/*
    public ModelAndView crear(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();
        String url = "/agentes-sectoriales/crear";

        parametros.put("url", url);
        parametros.put("admin",true);
        this.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"agente_sectorial/agente-sectorial-crear.hbs");
    }

    public Response guardar(Request request, Response response) {
        String mensajeSucces = "Registro exitoso";
        String mensajeDanger = "No se ha podido registrar al agente";

        try {
            AgenteSectorial agente = new AgenteSectorial();
            asignarAtributosAAgente(agente, request);
            agenteSectorialRepositorio.agregar(agente);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/agentes-sectoriales");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/agentes-sectoriales/crear");
        }
        finally {
            EntityManagerHelper.closeEntityManager();
            return response;
        }
    }

    private void asignarAtributosAAgente(AgenteSectorial agente, Request request) {
        String nombreApellido = request.queryParams("nombreApellido");
        if( !nombreApellido.isEmpty() ){
            agente.setNombreApellido(nombreApellido);
        }
        else {throw new RuntimeException();}

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
        else {throw new RuntimeException();}
    }

 */

    public ModelAndView mostrarSectorTerritorial(Request request, Response response){
        Map<String, Object> parametros = new HashMap<>();

        String id_buscado = request.params("id");
        AgenteSectorial agente = agenteSectorialRepositorio.buscar(Integer.parseInt(id_buscado));

        Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);

        if(usuario.getId() == agente.getUsuario().getId()) {
            parametros.put("agente", agente.getId());
        }
        else {
            parametros.put("agente",true);
        }

        parametros.put("agente-sectorial",agente);

        return new ModelAndView(parametros, "/agente_sectorial/agente-mi-sector.hbs");

    }

    private void agregarAlerta(Request request, Map<String, Object> parametros) {
        if(request.session().attribute("mensaje") != null
                && request.session().attribute("danger") != null) {
            parametros.put("mensaje", request.session().attribute("mensaje"));
            parametros.put("danger", request.session().attribute("danger"));

            request.session().removeAttribute("mensaje");
            request.session().removeAttribute("danger");
        }

        if(request.session().attribute("mensaje") != null
                && request.session().attribute("succes") != null) {
            parametros.put("mensaje", request.session().attribute("mensaje"));
            parametros.put("succes", request.session().attribute("succes"));

            request.session().removeAttribute("mensaje");
            request.session().removeAttribute("succes");
        }
    }

    public ModelAndView mostrarReportes(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        int id_agente = Integer.parseInt(request.params("id"));
        String urlReporte = "/api/reportes/agente-sectorial/" + id_agente;

        parametros.put("periodicidades", Periodicidad.values());

        Repositorio<HC> repoHC = FactoryRepositorio.get(HC.class);
        AgenteSectorial agente = agenteSectorialRepositorio.buscar(id_agente);
        SectorTerritorial stAgente = agente.getSectorTerritorial();
        List<HC> huellasCarbono = repoHC.buscarTodos();
        List<HC> hcAgente = huellasCarbono.stream().filter(hc -> hc.getSectorTerritorial().equals(stAgente)).collect(Collectors.toList());

        List<String> periodosAnuales = hcAgente.stream()
                .filter(hc -> hc.getPeriodicidad().equals(Periodicidad.ANUAL))
                .map(hc -> hc.getPeriodo())
                .distinct()
                .collect(Collectors.toList());

        List<String> periodosMensuales = hcAgente.stream()
                .filter(hc -> hc.getPeriodicidad().equals(Periodicidad.MENSUAL))
                .map(hc -> hc.getPeriodo())
                .distinct()
                //Reemplazo porque si no no llega a la ruta api/reportes/agente-sectorial/:id_agente/:periodicidad/:periodo
                .map(p -> p.replace('/','-'))
                .collect(Collectors.toList());

        parametros.put("agente", id_agente);
        parametros.put("urlReporte", urlReporte);
        parametros.put("periodosAnuales", periodosAnuales);
        parametros.put("periodosMensuales", periodosMensuales);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros, "agente_sectorial/reportes/reportes.hbs");
    }
}
