package domain.controllers.Trayecto;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.MedioTransporte.ServicioContratado.ServicioContratado;
import domain.modelo.entities.MedioTransporte.ServicioContratado.TipoTransporteContratado;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.factories.FactoryRepositorio;
import helpers.AlertaHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TransporteContratadoController {
    private Repositorio<ServicioContratado> servicioContratadoRepositorio;
    private Repositorio<TipoTransporteContratado> tipoTransporteContratadoRepositorio;
    private Repositorio<FE> feRepositorio;

    private String mensajeSucces;
    private String mensajeDanger;


    public ModelAndView mostrarTransportesContratado(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        servicioContratadoRepositorio = FactoryRepositorio.get(ServicioContratado.class);

        List<ServicioContratado> serviciosContratados = servicioContratadoRepositorio.buscarTodos()
                .stream()
                .filter(t -> t.getEstado()== Estado.ACTIVO)
                .collect(Collectors.toList());

        String url = "/transportes/contratados/eliminar/";

        parametros.put("admin", true);
        parametros.put("contratados", serviciosContratados);
        parametros.put("eliminar", true);
        parametros.put("urlEliminar", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/medios_transporte/contratados.hbs");
    }


    public ModelAndView crearTransporteContratado(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        tipoTransporteContratadoRepositorio = FactoryRepositorio.get(TipoTransporteContratado.class);
        feRepositorio = FactoryRepositorio.get(FE.class);

        String url = "/transportes/contratados/crear";

        parametros.put("admin", true);
        parametros.put("url", url);
        parametros.put("fes", feRepositorio.buscarTodos());
        parametros.put("tiposTransportesContratados", tipoTransporteContratadoRepositorio.buscarTodos());
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/medios_transporte/contratado.hbs");
    }


    public ModelAndView editarTransporteContratado(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        servicioContratadoRepositorio = FactoryRepositorio.get(ServicioContratado.class);
        tipoTransporteContratadoRepositorio = FactoryRepositorio.get(TipoTransporteContratado.class);
        feRepositorio = FactoryRepositorio.get(FE.class);

        int id_transporte = Integer.parseInt(request.params("id_transporte"));
        ServicioContratado trasporte = servicioContratadoRepositorio.buscar(id_transporte);
        String url = "/transportes/contratados/" + id_transporte + "/editar";

        parametros.put("admin", true);
        parametros.put("contratado", trasporte);
        parametros.put("fes", feRepositorio.buscarTodos());
        parametros.put("tiposTransportesContratados", tipoTransporteContratadoRepositorio.buscarTodos());
        parametros.put("url", url);

        return new ModelAndView(parametros, "/medios_transporte/contratado.hbs");
    }


    public Response modificarTransporteContratado(Request request, Response response) {
        mensajeSucces = "Se ha modificado el transpote exitosamente";
        mensajeDanger = "No se ha podido modificar el transporte";

        servicioContratadoRepositorio = FactoryRepositorio.get(ServicioContratado.class);
        tipoTransporteContratadoRepositorio = FactoryRepositorio.get(TipoTransporteContratado.class);
        feRepositorio = FactoryRepositorio.get(FE.class);

        int id_transporte = Integer.parseInt(request.params("id_transporte"));

        try {
            ServicioContratado transporte = servicioContratadoRepositorio.buscar(id_transporte);

            asignarAtributosTransporte(transporte, request);
            servicioContratadoRepositorio.modificar(transporte);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/transportes/contratados");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/transportes/contratados/" + id_transporte + "/editar");
        }
        finally {
            return response;
        }
    }


    public Response guardarTransporteContratado(Request request, Response response) {
        mensajeSucces = "Se ha creado el transporte xitosamente";
        mensajeDanger = "No se ha creado el transporte ecologico";

        servicioContratadoRepositorio = FactoryRepositorio.get(ServicioContratado.class);
        tipoTransporteContratadoRepositorio = FactoryRepositorio.get(TipoTransporteContratado.class);
        feRepositorio = FactoryRepositorio.get(FE.class);

        try {
            ServicioContratado transporte = new ServicioContratado();
            asignarAtributosTransporte(transporte, request);

            servicioContratadoRepositorio.agregar(transporte);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/transportes/contratados");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/transportes/contratados/crear");
        }
        finally {
            return response;
        }
    }


    private void asignarAtributosTransporte(ServicioContratado transporte, Request request) {
        String nombre = request.queryParams("nombre");
        if( !nombre.isEmpty()) {
            transporte.setNombre(nombre);
        }
        else {
            mensajeDanger = "No ha ingresado un nombre";
            throw new RuntimeException();
        }

        String descripcion = request.queryParams("descripcion");
        if( !descripcion.isEmpty() ){
            transporte.setDescripcion(descripcion);
        }
        else {
            mensajeDanger = "No ha ingresado una descripcion";
            throw new RuntimeException();
        }

        String idTipo = request.queryParams("id_tipo");
        TipoTransporteContratado tipo = tipoTransporteContratadoRepositorio.buscar(Integer.parseInt(idTipo));
        if(!idTipo.isEmpty() && tipo!=null) {
            transporte.setTipo(tipo);
        }
        else {
            mensajeDanger = "Tipo transporte invalido";
            throw new RuntimeException();
        }

        String idFE = request.queryParams("id_fe");
        FE fe = feRepositorio.buscar(Integer.parseInt(idFE));
        if(!idFE.isEmpty() && fe!=null) {
            transporte.setFe(fe);
        }
        else {
            mensajeDanger = "FE invalida";
            throw new RuntimeException();
        }
    }


    public Response eliminarTransporteContratado(Request request, Response response) {
        String id_transporte = request.params("id_transporte");

        servicioContratadoRepositorio = FactoryRepositorio.get(ServicioContratado.class);

        mensajeSucces = "Se ha podido eliminar el transporte";
        mensajeDanger = "No se ha podido eliminar el transporte";

        if(id_transporte != null) {
            ServicioContratado transporte = servicioContratadoRepositorio.buscar(Integer.parseInt(id_transporte));

            if(transporte != null) {
                transporte.setEstado(Estado.INACTIVO);
                servicioContratadoRepositorio.modificar(transporte);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                //response.redirect("/transportes/contratados");
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        //response.redirect("/transportes/contratados");
        return response;
    }
}
