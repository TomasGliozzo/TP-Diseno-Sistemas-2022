package domain.controllers.Trayecto;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Entidades.Miembro.Miembro;

import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.MedioTransporte.MedioTransporte;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.MedioTransporte.Recorrido.Tramo;
import domain.modelo.entities.MedioTransporte.Recorrido.Trayecto;

import domain.modelo.entities.MedioTransporte.ServicioContratado.TipoTransporteContratado;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Localidad;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Municipio;
import domain.modelo.entities.Servicies.GeoDDS.Entities.Provincia;
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

import java.util.*;
import java.util.stream.Collectors;


public class TramoController {
    private RepositorioMiembro repositorioMiembro;
    private Repositorio<Trayecto> trayectoRepositorio;
    private Repositorio<Tramo> tramoRepositorio;
    private Repositorio<MedioTransporte> medioTransporteRepositorio;

    private String mensajeSucces;
    private String mensajeDanger;


    public ModelAndView mostrarTodos(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        trayectoRepositorio = FactoryRepositorio.get(Trayecto.class);

        Integer id_miembro = Integer.valueOf(request.params("id"));
        Integer id_trayecto = Integer.valueOf(request.params("id_trayecto"));
        String url = "/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/tramos/eliminar/";

        List<Tramo.TramoDTO> tramos = trayectoRepositorio.buscar(id_trayecto).getTramos().stream()
                .filter(t -> t.getEstado()== Estado.ACTIVO).map(Tramo::convertirADTO)
                .collect(Collectors.toList());

        parametros.put("miembro", id_miembro);
        parametros.put("tramos", tramos);
        parametros.put("eliminar", true);
        parametros.put("urlEliminar", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/trayectos/tramos/tramos.hbs");
    }


    public ModelAndView crear(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        medioTransporteRepositorio = FactoryRepositorio.get(MedioTransporte.class);
        repositorioMiembro = FactoryRepositorioMiembro.get();

        int id_miembro = Integer.parseInt(request.params("id"));
        int id_trayecto = Integer.parseInt(request.params("id_trayecto"));

        Miembro miembro = repositorioMiembro.buscar(id_miembro);
        List<MedioTransporte> mediosTransportes = medioTransporteRepositorio.buscarTodos();
        List<Miembro> miembros = repositorioMiembro.buscarTodos()
                .stream().filter(m -> m.getId()!=id_miembro).collect(Collectors.toList());

        String url = "/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/tramos/crear";
        String urlTransporte = "/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/tramos/crear/transporte";

        parametros.put("miembro", id_miembro);
        parametros.put("miembros", miembros);
        parametros.put("compartido", request.session().attribute("compartido"));
        parametros.put("transportes", mediosTransportes);
        parametros.put("transporte", request.session().attribute("transporte"));
        parametros.put("url", url);
        parametros.put("urlTransporte", urlTransporte);

        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/trayectos/tramos/tramo.hbs");
    }


    public Response obtenerTipoVehiculo(Request request, Response response) {
        medioTransporteRepositorio = FactoryRepositorio.get(MedioTransporte.class);

        int id_transporte = Integer.parseInt(request.params("id_transporte"));
        MedioTransporte transporte = medioTransporteRepositorio.buscar(id_transporte);

        if(transporte.esCompartido())
            request.session().attribute("compartido", "Si");

        request.session().attribute("transporte", transporte);

        return response;
    }


    public Response guardarVehiculo(Request request, Response response) {
        mensajeDanger = "No se ha podido crear el tramo";

        int id_miembro = Integer.parseInt(request.params("id"));
        int id_trayecto = Integer.parseInt(request.params("id_trayecto"));

        repositorioMiembro = FactoryRepositorioMiembro.get();
        medioTransporteRepositorio = FactoryRepositorio.get(MedioTransporte.class);
        trayectoRepositorio = FactoryRepositorio.get(Trayecto.class);

        try {
            Miembro miembro = repositorioMiembro.buscar(id_miembro);
            Tramo tramo = new Tramo(miembro);

            asignarAtributos(tramo, request);
            request.session().attribute("tramo", tramo);

            response.redirect("/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/tramos/crear/direcciones");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            request.session().removeAttribute("compartido");
            request.session().removeAttribute("transporte");

            response.redirect("/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/tramos/crear");
        }
        finally {
            return response;
        }
    }


    private void asignarAtributos(Tramo tramo, Request request) {
        String descripcion = request.queryParams("descripcion");
        if( !descripcion.isEmpty() ){
            tramo.setDescripcion(descripcion);
        }
        else {
            mensajeDanger = "No ha ingresado una descripcion";
            throw new RuntimeException();
        }

        String idTransporte = request.queryParams("transporte");
        MedioTransporte medioTransporte = medioTransporteRepositorio.buscar(Integer.parseInt(idTransporte));
        if(!idTransporte.isEmpty() && medioTransporte!=null) {
            tramo.setMedioTransporteUtilizado(medioTransporte);
        }
        else {
            mensajeDanger = "Transporte invalido";
            throw new RuntimeException();
        }

        if(request.session().attribute("compartido") != null) {
            List<Integer> miembros = Arrays.stream(request.queryParams("lista_miembros")
                    .split(",")).map(Integer::valueOf).collect(Collectors.toList());

            miembros.forEach(m -> tramo.agregarMiembro(repositorioMiembro.buscar(m)));
        }
        // !!!
    }

    /*
    public Response agregarMiembro(Request request, Response response) {
        String mensajeSucces = "Se ha agregado al miembro exitosamente";
        String mensajeDanger = "No se ha podido agregar al miembro";

        repositorioMiembro = FactoryRepositorioMiembro.get();

        Integer id_miembro = Integer.valueOf(request.params("id"));
        Integer id_trayecto = Integer.valueOf(request.params("id_trayecto"));
        Integer id_tramo = Integer.valueOf(request.params("id_tramo"));

        String idNuevoMiembro = request.queryParams("nuevo-miembro");

        try {
            Tramo tramo = tramoRepositorio.buscar(id_tramo);

            if(!tramo.esCompartido()) {
                mensajeDanger = "El tramo no es compartido";
                throw new RuntimeException();
            }

            if(idNuevoMiembro.isEmpty() || repositorioMiembro.buscar(Integer.parseInt(idNuevoMiembro)) == null) {
                mensajeDanger = "El miembro no existe";
                throw new RuntimeException();
            }

            tramo.agregarMiembro(repositorioMiembro.buscar(Integer.parseInt(idNuevoMiembro)));
            tramoRepositorio.modificar(tramo);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/tramos");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/tramos/editar");
        }
        finally {
            return response;
        }
    }

    public Response eliminarMiembro(Request request, Response response) {
        Integer id_miembro = Integer.valueOf(request.params("id"));
        Integer id_tramo = Integer.valueOf(request.params("id_tramo"));
        String idEliminado = request.queryParams("eliminar");

        repositorioMiembro = FactoryRepositorioMiembro.get();

        String mensajeSucces = "Se ha eliminado al miembro del tramo";
        String mensajeDanger = "No se ha podido eliminar al miembro del tramo";

        if(idEliminado != null) {
            Miembro miembroEliminado = repositorioMiembro.buscar(Integer.parseInt(idEliminado));

            if(miembroEliminado != null) {
                Tramo tramo = tramoRepositorio.buscar(id_tramo);
                tramo.quitarMiembro(miembroEliminado);
                tramoRepositorio.modificar(tramo);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                response.redirect("/miembro/" + id_miembro + "/trayectos/" + idEliminado + "/tramos/" + id_tramo);
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        response.redirect("/miembro/" + id_miembro + "/trayectos/" + idEliminado + "/tramos" + id_tramo);
        return response;
    }
    */


    public Response eliminar(Request request, Response response) {
        //Integer id_miembro = Integer.valueOf(request.params("id"));
        //Integer id_trayecto = Integer.valueOf(request.params("id_trayecto"));
        String id_tramo = request.params("id_tramo");

        String mensajeSucces = "Se ha podido eliminar el tramo";
        String mensajeDanger = "No se ha podido eliminar el tramo";

        tramoRepositorio = FactoryRepositorio.get(Tramo.class);

        if(id_tramo != null) {
            Tramo tramo = tramoRepositorio.buscar(Integer.parseInt(id_tramo));

            if(tramo != null) {
                tramo.setEstado(Estado.INACTIVO);
                tramoRepositorio.modificar(tramo);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                //response.redirect("/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/tramos");
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        //response.redirect("/miembro/" + id_miembro + "/trayectos/" + id_trayecto + "/tramos");
        return response;
    }


    public Response guardarTramo(Request request, Response response) {
        mensajeSucces = "Se ha podido crear el tramo";
        mensajeDanger = "No se ha podido crear el tramo";

        trayectoRepositorio = FactoryRepositorio.get(Trayecto.class);
        tramoRepositorio = FactoryRepositorio.get(Tramo.class);

        int idMiembro = Integer.parseInt(request.params("id"));
        int idTrayecto = Integer.parseInt(request.params("id_trayecto"));

        Trayecto trayecto = trayectoRepositorio.buscar(idTrayecto);
        Miembro miembro = repositorioMiembro.buscar(idMiembro);

        try {
            Tramo tramo = request.session().attribute("tramo");

            asignarAtributosDireccion(tramo, "inicio", request);
            asignarAtributosDireccion(tramo, "fin", request);

            trayecto.agregarTramo(tramo);
            tramoRepositorio.agregar(tramo);

            if(tramo.esCompartido()) {
                tramo.getMiembrosVinculados().stream().filter(m -> !m.equals(miembro))
                        .forEach(m -> {
                                    Trayecto t = m.nuevoTrayectoCompartido(trayecto);
                                    t.agregarTramoCompartido(tramo);
                                    repositorioMiembro.modificar(miembro);
                                }
                        );
            }

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/miembro/" + idMiembro + "/trayectos/" + idTrayecto + "/tramos");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/miembro/" + idMiembro + "/trayectos/" + idTrayecto + "/tramos");
        }
        finally {
            request.session().removeAttribute("provincia_inicio");
            request.session().removeAttribute("municipio_inicio");
            request.session().removeAttribute("localidad_inicio");
            request.session().removeAttribute("calle_inicio");
            request.session().removeAttribute("numero_inicio");

            request.session().removeAttribute("provincia_fin");
            request.session().removeAttribute("municipio_fin");
            request.session().removeAttribute("localidad_fin");

            request.session().removeAttribute("tramo");

            request.session().removeAttribute("provincias");
            request.session().removeAttribute("municipios");
            request.session().removeAttribute("localidades");

            request.session().removeAttribute("compartido");
            request.session().removeAttribute("transporte");

            return response;
        }
    }

    private void asignarAtributosDireccion(Tramo tramo, String prefijo,Request request) {
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

        if(prefijo.equals("inicio"))
            tramo.setDireccionInicio(direccion);
        else
            tramo.setDireccionFin(direccion);
    }
}
