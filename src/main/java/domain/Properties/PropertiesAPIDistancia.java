package domain.Properties;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesAPIDistancia {
    private static PropertiesAPIDistancia  propertiesAPI = null;
    private String nombreProperties = "APIDistancia.properties";
    @Getter
    private String token;
    @Getter
    private String URL;

    private PropertiesAPIDistancia() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/main/resources/"+nombreProperties));
            cargarValores(properties);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PropertiesAPIDistancia getInstancia() {
        if(propertiesAPI == null)
            return new PropertiesAPIDistancia();
        return propertiesAPI;
    }

    private void cargarValores(Properties properties) {
        token = properties.getProperty("CLAVE");
        URL = properties.getProperty("URL");
    }
}
