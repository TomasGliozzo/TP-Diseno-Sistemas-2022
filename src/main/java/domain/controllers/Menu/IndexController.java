package domain.controllers.Menu;

import spark.ModelAndView;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;


public class IndexController {

    public ModelAndView mostrarIndexMiembro(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        Integer id_miembro = Integer.valueOf(request.params("id"));
        parametros.put("miembro", id_miembro);

        return new ModelAndView(parametros, "index.hbs");
    }

    public ModelAndView mostrarIndexOrganizacion(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        Integer id_organizacion = Integer.valueOf(request.params("id"));
        parametros.put("organizacion", id_organizacion);

        return new ModelAndView(parametros, "index.hbs");
    }

    public ModelAndView mostrarIndexAgenteSectorial(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        Integer id_agente = Integer.valueOf(request.params("id"));
        parametros.put("agente", id_agente);

        return new ModelAndView(parametros, "index.hbs");
    }
}
