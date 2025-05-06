package domain.modelo.entities.Servicies.GeoDDS.adapters;

import domain.modelo.entities.Servicies.GeoDDS.Entities.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface GeoDDSService {
    @GET("localidades")
    Call<List<Localidad>> localidades(@Query("offset") int offset, @Query("municipioId") int municipioId);

    @GET("municipios")
    Call<List<Municipio>> municipios(@Query("offset") int offset, @Query("provinciaId") int provinciaId);

    @GET("paises")
    Call<List<Pais>> paises(@Query("offset")int offset);

    @GET("provincias")
    Call<List<Provincia>> provincias(@Query("offset")int offset, @Query("paisId")int paisId);

    @GET("distancia")
    Call <Distancia> distancia(@Query("localidadOrigenId")int lOId, @Query("calleOrigen")String cO, @Query("alturaOrigen")String aO,
                               @Query("localidadDestinoId")int lDId, @Query("calleDestino")String cD, @Query("alturaDestino")String aD);
}
