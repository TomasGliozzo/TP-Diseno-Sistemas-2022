package domain.controllers.Miembro;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Miembro.EstadoSolicitud;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Miembro.SolicitudMiembro;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.Sector;
import domain.modelo.entities.Servicies.GeoDDS.ServicioDistancia;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.RepositorioMiembro;
import domain.modelo.repositories.RepositorioOrganizacion;
import domain.modelo.repositories.factories.FactoryRepositorio;
import domain.modelo.repositories.factories.FactoryRepositorioMiembro;
import domain.modelo.repositories.factories.FactoryRepositorioOrganizacion;
import helpers.AlertaHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class SolicitudController {
    private RepositorioOrganizacion repositorioOrganizacion;
    private RepositorioMiembro repositorioMiembro;
    private Repositorio<SolicitudMiembro> repositorioSolicitud;

    private String mensajeSucces;
    private String mensajeDanger;


    public ModelAndView mostrarSolicitudesOrganizacion(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        int id_organizacion = Integer.parseInt(request.params("id"));
        Organizacion organizacion = repositorioOrganizacion.buscar(id_organizacion);
        String url = "/organizacion/" + id_organizacion + "/solicitudes";

        parametros.put("organizacion", id_organizacion);
        parametros.put("url", url);
        parametros.put("organizacion_boton", true);
        parametros.put("solicitudes", organizacion.getSolicitudesMiembros()
                                        .stream().filter(s -> s.getEstadoSolicitud()==EstadoSolicitud.EN_CURSO)
                                        .collect(Collectors.toList()));
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/solicitudes/solicitudes.hbs");
    }


    public ModelAndView mostrarSolicitudesMiembro(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioMiembro = FactoryRepositorioMiembro.get();

        int id_miembro = Integer.parseInt(request.params("id"));
        Miembro miembro = repositorioMiembro.buscar(id_miembro);
        String url = "/miembro/" + id_miembro + "/solicitudes/eliminar/";

        parametros.put("miembro", id_miembro);
        parametros.put("miembro_boton", true);
        parametros.put("eliminar", true);
        parametros.put("url", url);
        parametros.put("urlEliminar", url);
        parametros.put("solicitudes", miembro.getSolicitudes()
                .stream().filter(s -> s.getEstadoSolicitud()==EstadoSolicitud.EN_CURSO)
                .collect(Collectors.toList()));
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/solicitudes/solicitudes.hbs");
    }

    public Response confirmarSolicitud(Request request, Response response) {
        mensajeSucces = "Confirmacion de Solicitud Exitosa";
        mensajeDanger = "No se ha podido confirmar la solicitud";

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        try{
            Organizacion organizacion = repositorioOrganizacion.buscar(Integer.parseInt(request.params("id")));
            validarConfirmacionSolicitud(request, organizacion);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);
        }
        catch (RuntimeException e){
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {
            response.redirect("/organizacion/" + request.params("id") + "/solicitudes");
            return response;
        }
    }

    public void validarConfirmacionSolicitud(Request request, Organizacion organizacion) {
        repositorioSolicitud = FactoryRepositorio.get(SolicitudMiembro.class);

        String solicitudId = request.queryParams("solicitud_id");
        EstadoSolicitud accionSolicitud = EstadoSolicitud.valueOf(request.queryParams("accion_solicitud"));

        if(accionSolicitud != null && solicitudId != null) {
            SolicitudMiembro solicitudMiembro = repositorioSolicitud.buscar(Integer.parseInt(solicitudId));

            if(solicitudMiembro!=null && accionSolicitud==EstadoSolicitud.APROBADA) {
                organizacion.vincularMiembro(solicitudMiembro);
                repositorioSolicitud.modificar(solicitudMiembro);
                return;
            }

            else if(solicitudMiembro!=null && accionSolicitud==EstadoSolicitud.RECHAZADA) {
                organizacion.rechazarMiembro(solicitudMiembro);
                repositorioSolicitud.modificar(solicitudMiembro);
                return;
            }
        }

        throw new RuntimeException();
    }

    public ModelAndView crear(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        Integer id_miembro = Integer.valueOf(request.params("id"));
        String url = "/miembro/" + id_miembro + "/solicitudes/crear";
        String urlSectores = "/miembro/" + id_miembro + "/solicitudes/crear";

        List<Organizacion> organizaciones = repositorioOrganizacion.buscarTodos().stream()
                .filter(o -> o.getEstado() == Estado.ACTIVO)
                .collect(Collectors.toList());;

        parametros.put("miembro", id_miembro);
        parametros.put("url", url);
        parametros.put("urlSectores", urlSectores);
        parametros.put("organizaciones", organizaciones);
        parametros.put("organizacionSelec", request.session().attribute("organizacionSelec"));
        parametros.put("sectores", request.session().attribute("sectores"));
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/solicitudes/solicitud.hbs");
    }
    
    public Response guardar(Request request, Response response) {
        mensajeSucces = "La Solicitud se ha creado exitosamente";
        mensajeDanger = "La Solicitud no se ha podido crear";

        repositorioMiembro = FactoryRepositorioMiembro.get();

        try{
            Miembro miembro = repositorioMiembro.buscar(Integer.parseInt(request.params("id")));
            asignarParametros(miembro, request);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/miembro/" + request.params("id") + "/solicitudes");
        }
        catch (RuntimeException e){
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/miembro/" + request.params("id") + "/solicitudes/crear");
        }
        finally {
            request.session().removeAttribute("sectores");
            request.session().removeAttribute("organizacionSelec");

            return response;
        }
    }

    private void asignarParametros(Miembro miembro, Request request) {
        String idSector = request.queryParams("sector");
        String idOrganizacion = request.queryParams("organizacion");

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();
        repositorioSolicitud = FactoryRepositorio.get(SolicitudMiembro.class);

        if( !idSector.isEmpty() &&  !idOrganizacion.isEmpty()) {
            Organizacion organizacion = repositorioOrganizacion.buscar(Integer.parseInt(request.queryParams("organizacion")));
            Sector sector = organizacion.getSector(Integer.parseInt(idSector));

            if(organizacion!=null && sector!=null) {
                SolicitudMiembro solicitudMiembro = miembro.solicitarAdhesion(sector, organizacion);
                repositorioSolicitud.agregar(solicitudMiembro);
                return;
            }
        }

        throw new RuntimeException();
    }

    public Response eliminar(Request request, Response response) {
        String solicitudId = request.params("solicitud_id");

        mensajeSucces = "Se ha podido eliminar la solicitud";
        mensajeDanger = "No se ha podido eliminar la solicitud";

        repositorioSolicitud = FactoryRepositorio.get(SolicitudMiembro.class);

        if(solicitudId != null) {
            SolicitudMiembro solicitudMiembro = repositorioSolicitud.buscar(Integer.parseInt(solicitudId));
            solicitudMiembro.setEstadoSolicitud(EstadoSolicitud.ELIMINADA);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            repositorioSolicitud.modificar(solicitudMiembro);
        }
        else {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }

        //response.redirect("/miembro/" + request.params("id") + "/solicitudes");
        return response;
    }

    public Response obtenerOrganizaciones(Request request, Response response) {
        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        int id_organizacion = Integer.parseInt(request.params("id_organizacion"));
        Organizacion organizacion = repositorioOrganizacion.buscar(id_organizacion);
        List<Sector> sectores = organizacion.getSectores();

        request.session().attribute("sectores", sectores);
        request.session().attribute("organizacionSelec", organizacion);

        return response;
    }
}
