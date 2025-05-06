package domain.modelo.entities.CargaDeMediciones;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Config.Config;

import domain.modelo.entities.Mediciones.Consumo.Consumo;
import domain.modelo.entities.Mediciones.Consumo.Periodicidad;
import domain.modelo.entities.Mediciones.Medicion;
import domain.modelo.entities.Mediciones.MedicionCompuesta.CategoriaLogistica;
import domain.modelo.entities.Mediciones.MedicionCompuesta.MedicionCompuesta;
import domain.modelo.entities.Mediciones.MedicionCompuesta.MedioTransporteLogistica;
import domain.modelo.entities.Mediciones.MedicionSimple.MedicionSimple;
import domain.modelo.entities.Mediciones.TipoActividad.TipoActividad;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class LeerFicherosExcel implements AdapterExcel {
    private List<TipoActividad> tiposActividades;

    public LeerFicherosExcel(List<TipoActividad> tiposActividades) {
        this.tiposActividades = tiposActividades;
    }

    @Override
    public List<Medicion> obtenerMediciones(String nombreArchivo) {
        List<Medicion> mediciones = new ArrayList();

        try (FileInputStream input = new FileInputStream(this.obtenerRutaCompleta(nombreArchivo))) {
            mediciones = obtenerMediciones(input);
        } catch (Exception e) {
            e.getMessage();
        }

        return mediciones;
    }

    @Override
    public List<Medicion> obtenerMediciones(InputStream inputStream) {
        List<Medicion> mediciones = new ArrayList();

        try {
            XSSFWorkbook worbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = worbook.getSheetAt(0);
            Medicion medicion;

            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                if (row.getCell(0).getStringCellValue().contentEquals("LOGISTICA_PRODUCTOS_RESIDUOS")) {
                    List<Row> filas = new ArrayList();
                    for (int i = 0; i < 4; i++) {
                        filas.add(row);
                        if (i < 3)
                            row = rowIterator.next();
                    }

                    medicion = this.obtenerMedicionLogistica(filas);
                } else
                    medicion = this.obtenerMedicion(row);

                mediciones.add(medicion);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return mediciones;
    }

    public String obtenerRutaCompleta(String nombreDelArchivo){
        return Config.RUTA_EXPORTACION + nombreDelArchivo;
    }

    public Medicion obtenerMedicion(Row row){
        Iterator<Cell> cellIterator = row.cellIterator();

        String nombreActividad = cellIterator.next().getStringCellValue();
        String tipoConsumo = cellIterator.next().getStringCellValue();
        String valor = cellIterator.next().getStringCellValue();
        String periodicidad = cellIterator.next().getStringCellValue();
        String periodoImputacion = cellIterator.next().getStringCellValue();

        Consumo consumo = new Consumo(Periodicidad.valueOf(periodicidad), periodoImputacion);

        TipoActividad tipoActividad = tiposActividades.stream()
                .filter(tA -> (tA.getNombreActividad().equals(nombreActividad)))
                .filter(tA -> (tA.getTipoDeConsumo().getNombre().equals(tipoConsumo)))
                .findAny().get();

        return new MedicionSimple(tipoActividad,consumo, Double.valueOf(valor));
    }

    private MedicionCompuesta obtenerMedicionLogistica(List<Row> filas) {
        Row fila1 = filas.get(0);

        String nombreActividad = fila1.getCell(0).getStringCellValue();
        String periodicidad = fila1.getCell(3).getStringCellValue();
        String periodoImputacion = fila1.getCell(4).getStringCellValue();

        String categoria = fila1.getCell(2).getStringCellValue();
        String medioTransporte = filas.get(1).getCell(2).getStringCellValue();
        Double distancia = Double.valueOf(filas.get(2).getCell(2).getStringCellValue());
        Double peso = Double.valueOf(filas.get(3).getCell(2).getStringCellValue());

        Consumo consumo = new Consumo(Periodicidad.valueOf(periodicidad), periodoImputacion);

        TipoActividad tipoActividad = tiposActividades.stream()
                .filter(tA -> (tA.getNombreActividad().equals(nombreActividad)))
                .filter(tA -> (tA.getTipoDeConsumo()).getCategoria().equals(CategoriaLogistica.valueOf(categoria)))
                .filter(tA -> (tA.getTipoDeConsumo()).getTransporte().equals(MedioTransporteLogistica.valueOf(medioTransporte)))
                .findAny().get();

        return new MedicionCompuesta(tipoActividad, consumo, distancia,peso);
    }
}


