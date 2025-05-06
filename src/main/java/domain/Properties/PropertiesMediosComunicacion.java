package domain.Properties;

import lombok.Getter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesMediosComunicacion {
    private static PropertiesMediosComunicacion propertiesTwilio = null;
    private String nombreProperties = "Mensajeria.properties";
    @Getter private String ACCOUNT_SID;
    @Getter private String AUTH_TOKEN;
    @Getter private String nuestroTelefono;

    private PropertiesMediosComunicacion() {
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

    public static PropertiesMediosComunicacion getInstance() {
        if(propertiesTwilio == null) {
            return new PropertiesMediosComunicacion();
        }
        return propertiesTwilio;
    }

    private void cargarValores(Properties properties) {
        ACCOUNT_SID = properties.getProperty("ACCOUNT_SID");
        AUTH_TOKEN = properties.getProperty("AUTH_TOKEN");
        nuestroTelefono = properties.getProperty("TELEFONO");
    }
}
