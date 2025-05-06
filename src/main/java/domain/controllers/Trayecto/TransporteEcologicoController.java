package domain.controllers.Trayecto;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.MedioTransporte.TransporteEcologico.TransporteEcologico;
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


public class TransporteEcologicoController {
    private Repositorio<TransporteEcologico> transporteEcologicoRepositorio;
    private Repositorio<FE> feRepositorio;

    private String mensajeSucces;
    private String mensajeDanger;


    public ModelAndView mostrarTransportesEcologicos(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        transporteEcologicoRepositorio = FactoryRepositorio.get(TransporteEcologico.class);

        List<TransporteEcologico> transportesEcologicos = transporteEcologicoRepositorio.buscarTodos()
                                                            .stream()
                                                            .filter(t -> t.getEstado()== Estado.ACTIVO)
                                                            .collect(Collectors.toList());
        String url = "/transportes/ecologicos/eliminar/";

        parametros.put("admin", true);
        parametros.put("ecologicos", transportesEcologicos);
        parametros.put("eliminar", true);
        parametros.put("urlEliminar", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/medios_transporte/ecologicos.hbs");
    }


    public ModelAndView crearTransporteEcologico(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        feRepositorio = FactoryRepositorio.get(FE.class);

        String url = "/transportes/ecologicos/crear";

        parametros.put("admin", true);
        parametros.put("url", url);
        parametros.put("fes", feRepositorio.buscarTodos());
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/medios_transporte/ecologico.hbs");
    }


    public ModelAndView editarTransporteEcologico(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        transporteEcologicoRepositorio = FactoryRepositorio.get(TransporteEcologico.class);
        feRepositorio = FactoryRepositorio.get(FE.class);

        int id_transporte = Integer.parseInt(request.params("id_transporte"));
        TransporteEcologico trasporte = transporteEcologicoRepositorio.buscar(id_transporte);
        String url = "/transportes/ecologicos/" + id_transporte + "/editar";

        parametros.put("admin", true);
        parametros.put("ecologico", trasporte);
        parametros.put("fes", feRepositorio.buscarTodos());
        parametros.put("url", url);

        return new ModelAndView(parametros, "/medios_transporte/ecologico.hbs");
    }


    public Response modificarTransporteEcologico(Request request, Response response) {
        mensajeSucces = "Se ha modificado el transpote exitosamente";
        mensajeDanger = "No se ha podido modificar el transporte";

        transporteEcologicoRepositorio = FactoryRepositorio.get(TransporteEcologico.class);
        feRepositorio = FactoryRepositorio.get(FE.class);

        int id_transporte = Integer.parseInt(request.params("id_transporte"));

        try {
            TransporteEcologico transporte = transporteEcologicoRepositorio.buscar(id_transporte);

            asignarAtributosTransporteEcologico(transporte, request);
            transporteEcologicoRepositorio.modificar(transporte);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/transportes/ecologicos");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/transportes/ecologicos/" + id_transporte + "/editar");
        }
        finally {
            return response;
        }
    }


    public Response guardarTransporteEcologico(Request request, Response response) {
        mensajeSucces = "Se ha creado el transporte ecologico exitosamente";
        mensajeDanger = "No se ha creado el transporte ecologico";

        transporteEcologicoRepositorio = FactoryRepositorio.get(TransporteEcologico.class);
        feRepositorio = FactoryRepositorio.get(FE.class);

        try {
            TransporteEcologico transporte = new TransporteEcologico();
            asignarAtributosTransporteEcologico(transporte, request);

            transporteEcologicoRepositorio.agregar(transporte);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/transportes/ecologicos");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/transportes/ecologicos/crear");
        }
        finally {
            return response;
        }
    }


    private void asignarAtributosTransporteEcologico(TransporteEcologico transporte, Request request) {
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


    public Response eliminarTransporteEcologico(Request request, Response response) {
        String id_transporte = request.params("id_transporte");

        transporteEcologicoRepositorio = FactoryRepositorio.get(TransporteEcologico.class);

        mensajeSucces = "Se ha podido eliminar el transporte ecologico";
        mensajeDanger = "No se ha podido eliminar el transporte ecologico";

        if(id_transporte != null) {
            TransporteEcologico transporte = transporteEcologicoRepositorio.buscar(Integer.parseInt(id_transporte));

            if(transporte != null) {
                transporte.setEstado(Estado.INACTIVO);
                transporteEcologicoRepositorio.modificar(transporte);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                //response.redirect("/transportes/ecologicos");
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        //response.redirect("/transportes/ecologicos");
        return response;
    }
}
