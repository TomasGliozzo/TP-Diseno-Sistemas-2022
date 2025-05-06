package domain.modelo.entities.CargaDeMediciones;

import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Mediciones.Medicion;
import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;

import java.io.InputStream;
import java.util.List;

public class RegistrarMediciones {
    private AdapterExcel adapterExcel;
    private String nombreDelArchivo;
    private Organizacion organizacion;

    public RegistrarMediciones(String nombreDelArchivo, Organizacion organizacion, List<TipoActividad> tipoActividades) {
        this(organizacion, tipoActividades);
        this.nombreDelArchivo = nombreDelArchivo;
    }

    public RegistrarMediciones(Organizacion organizacion, List<TipoActividad> tipoActividades) {
        this.adapterExcel = new LeerFicherosExcel(tipoActividades);
        this.organizacion = organizacion;
    }

    private List<Medicion> obtenerMediciones() {
        return adapterExcel.obtenerMediciones(nombreDelArchivo);
    }

    public void cargarMediciones() {
        organizacion.cargarMediciones(this.obtenerMediciones());
    }

    public void cargarMediciones(InputStream inputStream) {
        List<Medicion> mediciones = adapterExcel.obtenerMediciones(inputStream);
        organizacion.cargarMediciones(this.obtenerMediciones());
    }
}
