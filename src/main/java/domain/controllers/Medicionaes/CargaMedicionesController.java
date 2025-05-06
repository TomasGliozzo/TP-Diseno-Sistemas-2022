package domain.controllers.Medicionaes;

import Config.Config;
import spark.ModelAndView;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class CargaMedicionesController {

    public ModelAndView mostrar(Request request, Response response) {
        Map<String, Object> parametros = new HashMap<>();

        Integer id_organizacion = Integer.valueOf(request.params("id"));
        parametros.put("organizacion", id_organizacion);
        this.agregarAlerta(request, parametros);

        return new ModelAndView(parametros, "/carga_mediciones/carga_mediciones.hbs");
    }

    public Response guardar(Request request, Response response) {
        String mensajeSucces = "Se ha cargado el archivo exitosamente";
        String mensajeDanger = "No se ha podido cargar el archivo";

        Integer id_organizacion = Integer.valueOf(request.params("id"));

        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
        try (InputStream is = request.raw().getPart("archivo-mediciones").getInputStream()) {

            File uploadDir = new File(Config.RUTA_EXPORTACION_SPARK);
            Path tempFile = Files.createTempFile(uploadDir.toPath(), "", ".xlsx");

            Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);

            CargarMedicionesThread cargarMedicionesThread = new CargarMedicionesThread();
            cargarMedicionesThread.setIdOrganizacion(id_organizacion);
            cargarMedicionesThread.setFileName(tempFile.getFileName().toString());
            cargarMedicionesThread.start();

            request.session().attribute("succes",true);
            request.session().attribute("mensaje", mensajeSucces);
        }
        catch (RuntimeException e) {
            request.session().attribute("danger",true);
            request.session().attribute("mensaje", mensajeDanger);
        }
        finally {
            response.redirect("/organizacion/" + request.params("id") + "/mediciones");
            return response;
        }
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
}
