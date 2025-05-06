package domain.controllers.Calculadora;

import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.HuellaCarbono.HC;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
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
import java.util.Map;


public class CalculadoraController {
    RepositorioMiembro repositorioMiembro;
    RepositorioOrganizacion repositorioOrganizacion;

    String mensajeDanger;


    public ModelAndView mostrarCalculadoraOrganizacion(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        int id_organizacion = Integer.parseInt(request.params("id"));
        Organizacion organizacion = repositorioOrganizacion.buscar(id_organizacion);

        parametros.put("organizacion", id_organizacion);
        parametros.put("periodos", Periodicidad.values());
        AlertaHelper.agregarAlerta(request, parametros);
        this.agregarHC(request, parametros);

        return new ModelAndView(parametros,"/calculadora/calculadora.hbs");
    }

    public ModelAndView mostrarCalculadoraMiembro(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        repositorioMiembro = FactoryRepositorioMiembro.get();

        Integer id_miembro = Integer.valueOf(request.params("id"));
        Miembro miembro = repositorioMiembro.buscar(Integer.parseInt(request.params("id")));

        parametros.put("miembro", id_miembro);
        parametros.put("periodos", Periodicidad.values());
        AlertaHelper.agregarAlerta(request, parametros);
        this.agregarHC(request, parametros);

        return new ModelAndView(parametros,"/calculadora/calculadora.hbs");
    }

    public Response calcularHCOrganizacion(Request request, Response response) {
        mensajeDanger = "Periodicidad invalida";

        repositorioOrganizacion = FactoryRepositorioOrganizacion.get();

        int id_organizacion = Integer.parseInt(request.params("id"));
        Organizacion organizacion = repositorioOrganizacion.buscar(id_organizacion);

        try {
            Periodicidad periodicidad = Periodicidad.valueOf(request.queryParams("periodicidad"));
            String periodoImputacion = request.queryParams("periodo_imputacion");

            validarAtributos(periodicidad, periodoImputacion);

            request.session().attribute("hc", organizacion.hc(periodicidad,periodoImputacion));
            repositorioOrganizacion.modificar(organizacion);
        }
        catch (Exception e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {
            response.redirect("/organizacion/" + id_organizacion + "/calculadora");
            return response;
        }
    }

    public void validarAtributos(Periodicidad periodicidad, String periodoImputacion) {
        if(periodicidad==null || periodoImputacion.isEmpty()) {
            mensajeDanger = "Complete todos los atributos";
            throw new RuntimeException();
        }

        if(periodicidad.equals(Periodicidad.MENSUAL) && !periodoImputacion.contains("/") ||
                periodicidad.equals(Periodicidad.MENSUAL) && periodoImputacion.length()!=7) {
            mensajeDanger = "Periodicidad invalida";
            throw new RuntimeException();
        }

        if(periodicidad.equals(Periodicidad.ANUAL) && periodoImputacion.length()!=4) {
            mensajeDanger = "Periodicidad invalida";
            throw new RuntimeException();
        }
    }

    public Response calcularHCMiembro(Request request, Response response) {
        mensajeDanger = "Periodicidad invalida";

        repositorioMiembro = FactoryRepositorioMiembro.get();

        int id_miembro = Integer.parseInt(request.params("id"));
        Miembro miembro = repositorioMiembro.buscar(Integer.parseInt(request.params("id")));

        try{
            Periodicidad periodicidad = Periodicidad.valueOf(request.queryParams("periodicidad"));
            String periodoImputacion = request.queryParams("periodo_imputacion");

            validarAtributos(periodicidad, periodoImputacion);

            System.out.println("XXXX " +  miembro.hc(periodicidad,periodoImputacion).getValor());

            request.session().attribute("hc", miembro.hc(periodicidad,periodoImputacion));
            repositorioMiembro.modificar(miembro);
        }
        catch (Exception e){
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {
            response.redirect("/miembro/" + id_miembro + "/calculadora");
            return response;
        }
    }

    private void agregarHC(Request request, Map<String, Object> parametros) {
        if(request.session().attribute("hc") != null) {
            parametros.put("hc", request.session().attribute("hc"));
            request.session().removeAttribute("hc");
        }
    }
}
