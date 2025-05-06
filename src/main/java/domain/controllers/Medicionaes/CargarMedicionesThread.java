package domain.controllers.Medicionaes;

import Config.Config;
import domain.modelo.entities.CargaDeMediciones.RegistrarMediciones;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Mediciones.Medicion;
import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;
import domain.modelo.repositories.Repositorio;
import domain.modelo.repositories.factories.FactoryRepositorio;
import domain.modelo.repositories.factories.FactoryRepositorioOrganizacion;
import lombok.Setter;


@Setter
public class CargarMedicionesThread extends Thread {
    private Repositorio<Medicion> medicionRepositorio;
    private Repositorio<TipoActividad> tipoActividadRepositorio;

    private Integer idOrganizacion;
    private RegistrarMediciones registrarMediciones;
    private String fileName;

    @Override
    public void run() {
        medicionRepositorio = FactoryRepositorio.get(Medicion.class);
        tipoActividadRepositorio = FactoryRepositorio.get(TipoActividad.class);

        Organizacion organizacion = FactoryRepositorioOrganizacion.get().buscar(idOrganizacion);

        registrarMediciones = new RegistrarMediciones(Config.RUTA_STATIC_SPARK + fileName,
                organizacion, tipoActividadRepositorio.buscarTodos());
        registrarMediciones.cargarMediciones();

        organizacion.getMediciones().forEach(m -> medicionRepositorio.agregar(m));
    }

}
