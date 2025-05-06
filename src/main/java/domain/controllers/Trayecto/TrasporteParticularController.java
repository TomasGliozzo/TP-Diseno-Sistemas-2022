package domain.controllers.Trayecto;

import domain.modelo.entities.Entidades.Estado;
import domain.modelo.entities.Mediciones.TipoActividad.FE;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.TipoDeCombustible;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.TipoDeVehiculo;
import domain.modelo.entities.MedioTransporte.VehiculoParticular.VehiculoParticular;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.factories.FactoryRepositorio;
import helpers.AlertaHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TrasporteParticularController {
    private Repositorio<VehiculoParticular> vehiculoParticularRepositorio;
    private Repositorio<FE> feRepositorio;

    private String mensajeSucces;
    private String mensajeDanger;


    public ModelAndView mostrarTransportesParticular(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        vehiculoParticularRepositorio = FactoryRepositorio.get(VehiculoParticular.class);

        List<VehiculoParticular> vehiculosParticulares = vehiculoParticularRepositorio.buscarTodos()
                .stream()
                .filter(t -> t.getEstado()== Estado.ACTIVO)
                .collect(Collectors.toList());
        String url = "/transportes/particulares/eliminar/";

        parametros.put("admin", true);
        parametros.put("particulares", vehiculosParticulares);
        parametros.put("eliminar", true);
        parametros.put("urlEliminar", url);
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/medios_transporte/particulares.hbs");
    }


    public ModelAndView crearTransporteParticular(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        feRepositorio = FactoryRepositorio.get(FE.class);

        String url = "/transportes/particulares/crear";

        parametros.put("admin", true);
        parametros.put("url", url);
        parametros.put("fes", feRepositorio.buscarTodos());
        parametros.put("tiposDeCombustible", Arrays.stream(TipoDeCombustible.values()).collect(Collectors.toList()));
        parametros.put("tiposDeVehiculos", Arrays.stream(TipoDeVehiculo.values()).collect(Collectors.toList()));
        AlertaHelper.agregarAlerta(request, parametros);

        return new ModelAndView(parametros,"/medios_transporte/particular.hbs");
    }


    public ModelAndView editarTransporteParticular(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        vehiculoParticularRepositorio = FactoryRepositorio.get(VehiculoParticular.class);
        feRepositorio = FactoryRepositorio.get(FE.class);

        Integer id_transporte = Integer.valueOf(request.params("id_transporte"));
        VehiculoParticular transporte = vehiculoParticularRepositorio.buscar(id_transporte);
        String url = "/transportes/particulares/" + id_transporte + "/editar";

        parametros.put("admin", true);
        parametros.put("particular", transporte);
        parametros.put("fes", feRepositorio.buscarTodos());
        parametros.put("tiposDeCombustible", Arrays.stream(TipoDeCombustible.values()).collect(Collectors.toList()));
        parametros.put("tiposDeVehiculos", Arrays.stream(TipoDeVehiculo.values()).collect(Collectors.toList()));
        parametros.put("url", url);

        return new ModelAndView(parametros, "/medios_transporte/particular.hbs");
    }


    public Response modificarTransporteParticular(Request request, Response response) {
        mensajeSucces = "Se ha modificado el transpote exitosamente";
        mensajeDanger = "No se ha podido modificar el transporte";

        vehiculoParticularRepositorio = FactoryRepositorio.get(VehiculoParticular.class);
        feRepositorio = FactoryRepositorio.get(FE.class);

        try {
            int id_transporte = Integer.parseInt(request.params("id_transporte"));
            VehiculoParticular transporte = vehiculoParticularRepositorio.buscar(id_transporte);

            asignarAtributosTransporteParticular(transporte, request);
            vehiculoParticularRepositorio.modificar(transporte);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {
            response.redirect("/transportes/particulares");
            return response;
        }
    }


    public Response guardarTransporteParticular(Request request, Response response) {
        mensajeSucces = "Se ha creado el transporte xitosamente";
        mensajeDanger = "No se ha creado el transporte ecologico";

        vehiculoParticularRepositorio = FactoryRepositorio.get(VehiculoParticular.class);
        feRepositorio = FactoryRepositorio.get(FE.class);

        try {
            VehiculoParticular transporte = new VehiculoParticular();
            asignarAtributosTransporteParticular(transporte, request);

            vehiculoParticularRepositorio.agregar(transporte);

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);

            response.redirect("/transportes/particulares");
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);

            response.redirect("/transportes/particulares/crear");
        }
        finally {
            return response;
        }
    }


    private void asignarAtributosTransporteParticular(VehiculoParticular transporte, Request request) {
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

        if(request.queryParams("tipo_vehiculo") != null) {
            transporte.setTipoDeVehiculo(TipoDeVehiculo.valueOf(request.queryParams("tipo_vehiculo")));
        }
        else {
            mensajeDanger = "Tipo de vehiculo invalido";
            throw new RuntimeException();
        }

        if(request.queryParams("tipo_combustible") != null){
            transporte.setTipoDeCombustible(TipoDeCombustible.valueOf(request.queryParams("tipo_combustible")));
        }
        else {
            mensajeDanger = "Tipo combustible invalido";
            throw new RuntimeException();
        }

        String idFE = request.queryParams("id_fe");
        FE fe = feRepositorio.buscar(Integer.parseInt(idFE));
        if(idFE != null && fe!=null) {
            transporte.setFe(fe);
        }
        else {
            mensajeDanger = "FE invalida";
            throw new RuntimeException();
        }
    }


    public Response eliminarTransporteParticular(Request request, Response response) {
        String id_transporte = request.params("id_transporte");

        mensajeSucces = "Se ha podido eliminar el transporte";
        mensajeDanger = "No se ha podido eliminar el transporte";

        vehiculoParticularRepositorio = FactoryRepositorio.get(VehiculoParticular.class);

        if(id_transporte != null) {
            VehiculoParticular transporte = vehiculoParticularRepositorio.buscar(Integer.parseInt(id_transporte));

            if(transporte != null) {
                transporte.setEstado(Estado.INACTIVO);
                vehiculoParticularRepositorio.modificar(transporte);

                request.session().attribute("succes",true);
                request.session().attribute("mensaje", mensajeSucces);

                //response.redirect("/transportes/particulares");
                return response;
            }
        }

        request.session().attribute("danger",true);
        request.session().attribute("mensaje", mensajeDanger);

        //response.redirect("/transportes/particulares");
        return response;
    }
}
