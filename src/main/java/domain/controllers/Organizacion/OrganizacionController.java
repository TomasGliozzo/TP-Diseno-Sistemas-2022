package domain.controllers.Organizacion;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Organizacion.ClasificacionOrganizacion;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.TipoOrganizacion;
import domain.modelo.entities.Entidades.Usuario.Rol;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialMunicipal;
import domain.modelo.entities.SectorTerritorial.SectorTerritorialProvincial;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Provincia;
import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
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
import spark.Spark;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class OrganizacionController {
    private RepositorioOrganizacion repositorioOrganizacion;
    private Repositorio<ClasificacionOrganizacion> clasificacionOrganizacionRepositorio;
    private RepositorioUsuarios usuarioRepositorio;
    private RepositorioRol repositorioRol;
    private ValidadorContrasenia validadorContrasenia;

    private Repositorio<SectorTerritorialMunicipal> sectorTerritorialMunicipalRepositorio;

    private String mensajeSucces;
    private String mensajeDanger;


    public OrganizacionController() {
        validadorContrasenia = new ValidadorContrasenia();
        validadorContrasenia.agregarOpcionesValidacion(
                new ValidarTopContraseniasDebiles(),
                new ValidarLongitudContrasenia(),
                new ValidarContraseniaSinNombreDeUsuario(),
                new ValidarLongitudContrasenia()
        );
        sectorTerritorialMunicipalRepositorio = FactoryRepositorio.get(SectorTerritorialMunicipal.class);
    }

    public ModelAndView mostrarTodos(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        String url = "/organizaciones/eliminar/";

        List<Organizacion> organizaciones = repositorioOrganizacion.buscarTodos().stream()
                .filter(o -> o.getEstado()== Estado.ACTIVO)
                .collect(Collectors.toList());

        parametros.put("admin", true);
        parametros.put("organizaciones", organizaciones);
        parametros.put("eliminar", true);
        parametros.put("urlEliminar", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/organizaciones/organizaciones.hbs");
    }

    public ModelAndView crear(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        clasificacionOrganizacionRepositorio = FactoryRepositorio.get(ClasificacionOrganizacion.class);

        String url = "/organizaciones/crear";
        String urlLocalidad = "/organizaciones/crear/localidad";
        String urlProvincia = "/organizaciones/crear/provincia";
        String urlMunicipio = "/organizaciones/crear/municipio";

        List<Provincia> provincias = ServicioDistancia.instancia().listaProvincias();

        parametros.put("admin", true);
        parametros.put("crear", true);

        parametros.put("provincia", request.session().attribute("provincia"));
        parametros.put("municipio", request.session().attribute("municipio"));
        parametros.put("localidad", request.session().attribute("localidad"));

        parametros.put("provincias", provincias);
        parametros.put("municipios", request.session().attribute("municipios"));
        parametros.put("localidades", request.session().attribute("localidades"));

        parametros.put("tipos", Arrays.stream(TipoOrganizacion.values()).collect(Collectors.toList()));
        parametros.put("clasificaciones", clasificacionOrganizacionRepositorio.buscarTodos());

        parametros.put("url", url);
        parametros.put("urlLocalidad", urlLocalidad);
        parametros.put("urlMunicipio", urlMunicipio);
        parametros.put("urlProvincia", urlProvincia);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/organizaciones/organizacion.hbs");
    }

    public ModelAndView editar(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();
        clasificacionOrganizacionRepositorio = FactoryRepositorio.get(ClasificacionOrganizacion.class);

        int id_organizacion = Integer.parseInt(request.params("id"));
        Organizacion organizacion = repositorioOrganizacion.buscar(id_organizacion);
        Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);

        String url;
        String urlCambio;

        if(usuario.getId() == organizacion.getUsuario().getId()) {
            url = "/organizacion/" + id_organizacion + "/editar";
            urlCambio = "/organizacion/" + id_organizacion + "/editar/cambiar_contrasenia";
            parametros.put("organizacion", organizacion.getId());
        }
        else {
            url = "/organizaciones/" + id_organizacion + "/editar";
            urlCambio = "/organizaciones/" + id_organizacion + "/editar/cambiar_contrasenia";
            parametros.put("admin", true);
        }

        parametros.put("modificar", true);
        parametros.put("organizacion_editar", organizacion);
        parametros.put("tipos", Arrays.stream(TipoOrganizacion.values()).collect(Collectors.toList()));
        parametros.put("clasificaciones", clasificacionOrganizacionRepositorio.buscarTodos());
        parametros.put("url", url);
        parametros.put("urlCambio", urlCambio);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros, "/organizaciones/organizacion.hbs");
    }

    public Response actualizarProvincia(Request request, Response response) {
        int idProvincia = Integer.parseInt(request.params("id_provincia"));

        request.session().attribute("municipios", ServicioDistancia.instancia().listaMunicipios(idProvincia));
        request.session().attribute("provincia", ServicioDistancia.instancia().getProvincia(idProvincia));

        return response;
    }

    public Response actualizarMunicipio(Request request, Response response) {
        int id_municipio = Integer.parseInt(request.params("id_municipio"));
        int id_provincia = Integer.parseInt(request.params("id_provincia"));

        request.session().attribute("localidades", ServicioDistancia.instancia().listaLocalidades(id_municipio));
        request.session().attribute("municipio", ServicioDistancia.instancia().getMunicipio(id_provincia, id_municipio));

        return response;
    }

    public Response modificar(Request request, Response response) {
        mensajeSucces = "Se ha modificado la organizacion exitosamente";
        mensajeDanger = "No se ha modificado la organizacion";

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();
        repositorioRol = FactoryRepositorioRol.get();
        clasificacionOrganizacionRepositorio = FactoryRepositorio.get(ClasificacionOrganizacion.class);

        try {
            int id_organizacion = Integer.parseInt(request.params("id"));
            Organizacion organizacion = repositorioOrganizacion.buscar(id_organizacion);

            asignarAtributosPerfil(organizacion, request);

            repositorioOrganizacion.modificar(organizacion);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {
            int id_organizacion = Integer.parseInt(request.params("id"));
            Organizacion organizacion = repositorioOrganizacion.buscar(id_organizacion);
            Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);

            if(usuario.getId() == organizacion.getUsuario().getId())
                response.redirect("/organizacion/" + id_organizacion + "/editar");
            else
                response.redirect("/organizaciones");

            return response;
        }
    }

    public Response guardar(Request request, Response response) {
        mensajeSucces = "Se ha creado la organizacion exitosamente";
        mensajeDanger = "No se ha creado organizacion";

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();
        repositorioRol = FactoryRepositorioRol.get();
        usuarioRepositorio = FactoryRepositorioUsuarios.get();
        clasificacionOrganizacionRepositorio = FactoryRepositorio.get(ClasificacionOrganizacion.class);

        try {
            Organizacion organizacion = new Organizacion();
            asignarAtributos(organizacion, request);
            repositorioOrganizacion.agregar(organizacion);

            //BUSCAR ST MUNICIPAL TODO falla si no hay ST pero lo crea igual

            int municipio = organizacion.getUbicacionGeografica().getMunicipio();

            List<SectorTerritorialMunicipal> municipios = sectorTerritorialMunicipalRepositorio.buscarTodos();
            SectorTerritorialMunicipal municipioReq = municipios.stream().
                    filter(s->s.getDireccion().getMunicipio() == municipio).
                    collect(Collectors.toList()).get(0);

            municipioReq.agregarOrganizacion(organizacion);
            sectorTerritorialMunicipalRepositorio.modificar(municipioReq);


            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/organizaciones");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/organizaciones/crear");
        }
        finally {
            request.session().removeAttribute("provincia");
            request.session().removeAttribute("municipio");
            request.session().removeAttribute("localidad");
            request.session().removeAttribute("municipios");
            request.session().removeAttribute("localidades");

            return response;
        }
    }

    public void asignarAtributosPerfil(Organizacion organizacion, Request request) {
        String razonSocial = request.queryParams("razon-social");
        if( !razonSocial.isEmpty() ){
            organizacion.setRazonSocial(razonSocial);
        }
        else {
            mensajeDanger = "No se ha ingresado razon social";
            throw new RuntimeException();
        }

        String tipoOrganizacion = request.queryParams("tipo-organizacion");
        if( !tipoOrganizacion.isEmpty() ){
            organizacion.setTipoOrganizacion(TipoOrganizacion.valueOf(tipoOrganizacion));
        }
        else {
            mensajeDanger = "Tipo de organizacion Invalida";
            throw new RuntimeException();
        }

        String clasificacion = request.queryParams("clasificacion");
        ClasificacionOrganizacion clasificacionOrganizacion = clasificacionOrganizacionRepositorio
                .buscar(Integer.parseInt(clasificacion));
        if( !clasificacion.isEmpty() && clasificacionOrganizacion != null) {
            organizacion.setClasificacionOrganizacion(clasificacionOrganizacion);
        }
        else {
            mensajeDanger = "Clasificacion de Organizacion invalida";
            throw new RuntimeException();
        }
    }

    private void asignarAtributos(Organizacion organizacion, Request request) {
        this.asignarAtributosPerfil(organizacion, request);

        String nombreUsuario = request.queryParams("usuario");
        String contrasenia = request.queryParams("contrasenia");
        if(!nombreUsuario.isEmpty() && !contrasenia.isEmpty() &&
                usuarioRepositorio.buscarUsuarioPorNombre(nombreUsuario)==null) {
            Rol rol = repositorioRol.buscarRolPorNombre("ORGANIZACION");

            Usuario usuario = new Usuario(nombreUsuario, contrasenia);
            usuario.setRol(rol);
            organizacion.setUsuario(usuario);

            if(!validadorContrasenia.validarContrasenia(usuario)) {
                mensajeDanger = "Contrasenia debil";
                throw new RuntimeException();
            }

            usuario.setContrasenia(contrasenia);
        }
        else {
            throw new RuntimeException();
        }


        Direccion direccion = new Direccion();
        String localidad = request.queryParams("localidad");
        if( !localidad.isEmpty() ){
            direccion.setLocalidad(Integer.parseInt(localidad));
        }
        else {
            mensajeDanger = "No se ha ingresado Localidad";
            throw new RuntimeException();
        }

        String municipio = request.queryParams("municipio");
        if( !municipio.isEmpty() ){
            direccion.setMunicipio(Integer.parseInt(municipio));
        }
        else {
            mensajeDanger = "No se ha ingresado municipio";
            throw new RuntimeException();
        }

        String provincia = request.queryParams("provincia");
        if( !provincia.isEmpty() ){
            direccion.setProvincia(Integer.parseInt(provincia));
        }
        else {
            mensajeDanger = "No se ha ingresado provincia";
            throw new RuntimeException();
        }

        String calle = request.queryParams("calle");
        if( !calle.isEmpty() ){
            direccion.setNombreCalle(calle);
        }
        else {
            mensajeDanger = "No se ha ingresado calle";
            throw new RuntimeException();
        }

        String numero = request.queryParams("numero");
        if( !numero.isEmpty() ){
            direccion.setNumeroCalle(numero);
        }
        else {
            mensajeDanger = "No se ha ingresado numero";
            throw new RuntimeException();
        }

        organizacion.setUbicacionGeografica(direccion);
    }

    public Response eliminar(Request request, Response response) {
        String id_organizacion = request.params("id_organizacion");

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        mensajeSucces = "Se ha podido eliminar la organizacion";
        mensajeDanger = "No se ha podido eliminar la organizacion";

        if(!id_organizacion.isEmpty()) {
            Organizacion organizacion = repositorioOrganizacion.buscar(Integer.parseInt(id_organizacion));

            if(organizacion != null) {
                organizacion.setEstado(Estado.INACTIVO);
                organizacion.getUsuario().setEstado(Estado.INACTIVO);
                repositorioOrganizacion.modificar(organizacion);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        return response;
    }


    public ModelAndView cambiarContrasenia(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        int id_organizacion = Integer.parseInt(request.params("id"));
        Organizacion organizacion = repositorioOrganizacion.buscar(id_organizacion);
        Usuario usuario = UsuarioLogueadoHelper.usuarioLogueado(request);
        String urlCambio;
        String urlVolver;

        if(usuario.getId() == organizacion.getUsuario().getId()) {
            urlVolver = "/organizacion/" + id_organizacion + "/editar";
            urlCambio = "/organizacion/" + id_organizacion + "/editar/cambiar_contrasenia";
            parametros.put("organizacion", organizacion.getId());
        }
        else {
            urlVolver = "/organizaciones/" + id_organizacion + "/editar";
            urlCambio = "/organizaciones/" + id_organizacion + "/editar/cambiar_contrasenia";
            parametros.put("admin", true);
        }

        parametros.put("urlCambio", urlCambio);
        parametros.put("urlVolver", urlVolver);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/login-register/cambio_contrasenia.hbs");
    }

    public Response modificarContrasenia(Request request, Response response) {
        mensajeSucces = "Se ha modificado la contraseña";
        mensajeDanger = "No se ha modificado la contraseña";

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();
        usuarioRepositorio = FactoryRepositorioUsuarios.get();

        int id_organizacion = Integer.parseInt(request.params("id"));
        Organizacion organizacion = repositorioOrganizacion.buscar(id_organizacion);

        try {
            Usuario usuario =  usuarioRepositorio.buscar(organizacion.getUsuario().getId());
            Usuario usuarioAux = new Usuario();

            String contraseniaVieja = request.queryParams("contrasenia-vieja");
            String contraseniaNueva = request.queryParams("contrasenia-nueva");

            usuarioAux.setContrasenia(contraseniaVieja);

            if(usuario!=null && !contraseniaNueva.isEmpty() && !contraseniaVieja.isEmpty() &&
                    usuario.getContrasenia().equals(usuarioAux.getContrasenia())) {
                Usuario usuarioCCN = new Usuario(usuario.getNombre(), contraseniaNueva);

                if(!validadorContrasenia.validarContrasenia(usuarioCCN)) {
                    mensajeDanger = "Contrasenia debil";
                    throw new RuntimeException();
                }

                usuario.setContrasenia(contraseniaNueva);
                usuarioRepositorio.modificar(usuario);
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

            if(usuario.getId() == organizacion.getUsuario().getId())
                response.redirect("/organizacion/" + id_organizacion + "/editar");
            else
                response.redirect("/organizaciones");

            return response;
        }
    }


    public ModelAndView reporteOrganizacion(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        int id_organizacion = Integer.parseInt(request.params("id"));
        String urlReporte = "/api/reportes/organizacion/" + id_organizacion;

        parametros.put("organizacion", id_organizacion);
        parametros.put("urlReporte", urlReporte);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros, "organizaciones/reporte/reporte.hbs");
    }


    public ModelAndView reporteCompuestoOrganizacion(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        int id_organizacion = Integer.parseInt(request.params("id"));
        String urlReporte = "/api/reportes/organizacion/" + id_organizacion + "/";

        parametros.put("organizacion", id_organizacion);
        parametros.put("urlReporte", urlReporte);
        parametros.put("anio", "2020");
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros, "organizaciones/reporte/reporte_compuesto.hbs");
    }
}
