package domain.modelo.entities.Servicies.GeoDDS.adapters;

import domain.modelo.entities.Servicies.GeoDDS.Entities.*;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import domain.Properties.PropertiesAPIDistancia;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServicioGeoDDSRetrofitAdapter implements APIDistanciaAdapter {
    private static int maximaCantidadRegistrosDefault = 200;
    private String urlApi;
    private Retrofit retrofit;
    private String token;
    private OkHttpClient client;

    public ServicioGeoDDSRetrofitAdapter() {
        PropertiesAPIDistancia properties = PropertiesAPIDistancia.getInstancia();
        token = properties.getToken();
        urlApi = properties.getURL();

        client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        this.retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(urlApi)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Override
    public List<Pais> listaPaises() throws IOException {
        GeoDDSService geoddsservice = this.retrofit.create(GeoDDSService.class);
        Call<List<Pais>> requestPaises = geoddsservice.paises(1);
        Response<List<Pais>> responsePaises = requestPaises.execute();
        return responsePaises.body();
    }

    @Override
    public List<Provincia> listaProvincias(int paisId) throws IOException {
        GeoDDSService geoddsservice = this.retrofit.create(GeoDDSService.class);
        Call<List<Provincia>> requestProvincias = geoddsservice.provincias(1, paisId);
        Response<List<Provincia>> responseProvincias = requestProvincias.execute();
        return responseProvincias.body();
    }

    @Override
    public List<Municipio> listaMunicipios(int provinciaId) throws IOException {
        GeoDDSService geoddsservice = this.retrofit.create(GeoDDSService.class);
        Call<List<Municipio>> requestMunicipios = geoddsservice.municipios(1, provinciaId);
        Response<List<Municipio>> responseMunicipios = requestMunicipios.execute();
        return responseMunicipios.body();

    }

    @Override
    public List<Localidad> listaLocalidades(int municipioId) throws IOException {
        GeoDDSService geoddsservice = this.retrofit.create(GeoDDSService.class);
        Call<List<Localidad>> requestLocalidades = geoddsservice.localidades(1, municipioId);
        Response<List<Localidad>> responseLocalidades = requestLocalidades.execute();
        return responseLocalidades.body();
    }

    @Override
    public Distancia distancia(Direccion direccionO, Direccion direccionD) throws IOException {
        GeoDDSService geoddsservice = this.retrofit.create(GeoDDSService.class);
        Call<Distancia> requestDistancia = geoddsservice
                .distancia(direccionO.getLocalidad(), direccionO.getNombreCalle(), direccionO.getNumeroCalle(),
                            direccionD.getLocalidad(), direccionD.getNombreCalle(), direccionD.getNumeroCalle());
        Response<Distancia> responseDistancia = requestDistancia.execute();
        return responseDistancia.body();
    }
}
