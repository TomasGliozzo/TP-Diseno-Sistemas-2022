package domain.modelo.entities.Servicies.GeoDDS;

import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.modelo.entities.Servicies.GeoDDS.Entities.*;
import domain.modelo.entities.Servicies.GeoDDS.adapters.APIDistanciaAdapter;
import domain.modelo.entities.Servicies.GeoDDS.adapters.ServicioGeoDDSRetrofitAdapter;

import java.io.IOException;
import java.util.List;

public class ServicioDistancia {
    private static ServicioDistancia instancia = null;
    private APIDistanciaAdapter adapter;


    private ServicioDistancia() {
        adapter = new ServicioGeoDDSRetrofitAdapter();
    }

    public void setAdapter(APIDistanciaAdapter adapter) {
        this.adapter = adapter;
    }

    public static ServicioDistancia instancia(){
        if(instancia== null){
            instancia = new ServicioDistancia();
        }
        return instancia;
    }

    public List<Pais> listaPaises() throws IOException {
        return this.adapter.listaPaises();
    }

    public List<Provincia> listaProvincias(int paisId) throws IOException {
        return this.adapter.listaProvincias(paisId);
    }

    public List<Provincia> listaProvincias() {
        try {
            return this.adapter.listaProvincias(9);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Municipio> listaMunicipios(int provinciaId) {
        try {
            return this.adapter.listaMunicipios(provinciaId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Localidad> listaLocalidades(int municipioId) {
        try {
            return adapter.listaLocalidades(municipioId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Distancia distancia(Direccion direccionO, Direccion direccionD) {
        try {
            return adapter.distancia(direccionO, direccionD);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String getNombreMunicipio(int provincia, int municipio) {
        try {
            return listaMunicipios(provincia).stream()
                    .filter(m -> m.id==municipio)
                    .findFirst().get().nombre;
        }
        catch(Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public String getNombreLocalidad(int municipio, int localidad) {
        try {
            return listaLocalidades(municipio).stream()
                    .filter(l -> l.id==localidad)
                    .findFirst().get().nombre;
        }
        catch(Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public String getNombreProvincia(int provincia) {
        try {
            return listaProvincias(9).stream()
                    .filter(p -> p.id==provincia)
                    .findFirst().get().nombre;
        }
        catch(Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    public Provincia getProvincia(int provincia) {
        try {
            return listaProvincias(9).stream()
                    .filter(p -> p.id==provincia)
                    .findFirst().get();
        }
        catch(Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    public Municipio getMunicipio(int provincia, int municipio) {
        try {
            return listaMunicipios(provincia).stream()
                    .filter(m -> m.id==municipio)
                    .findFirst().get();
        }
        catch(Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public Localidad getLocalidad(int municipio, int localidad) {
        try {
            return listaLocalidades(municipio).stream()
                    .filter(l -> l.id==localidad)
                    .findFirst().get();
        }
        catch(Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    public String getDetalleDireccion(Direccion direccion) {
        return getNombreProvincia(direccion.getProvincia()) + ", "
                + getNombreLocalidad(direccion.getMunicipio(), direccion.getLocalidad()) + " - "
                + getNombreMunicipio(direccion.getProvincia(), direccion.getMunicipio()) + " " +
                direccion.getNombreCalle() + " " + direccion.getNumeroCalle();
    }
}
