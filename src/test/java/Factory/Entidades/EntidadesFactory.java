package Factory.Entidades;

import Factory.MedioTransporte.MedioTransporteFactory;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Miembro.TipoDeDocumento;
import domain.modelo.entities.Entidades.Organizacion.ClasificacionOrganizacion;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Organizacion.Sector;
import domain.modelo.entities.Entidades.Organizacion.TipoOrganizacion;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import domain.modelo.entities.MedioTransporte.Recorrido.Direccion;
import lombok.Getter;
import lombok.Setter;

public class EntidadesFactory {
    @Setter @Getter
    private static ClasificacionOrganizacion clasificacionOrganizacion = new ClasificacionOrganizacion("Empresa Sector Secundario","Empresa comida rapida");

    public static Organizacion crearOrganizacionMostaza(){
        Organizacion mostazaCentro;
        String razonSocial = "Mostaza S.A";
        TipoOrganizacion tipoOrganizacion = TipoOrganizacion.EMPRESA;
        Direccion ubicacionGeograficaMostaza = new Direccion("Ezeiza", "600", 3615, "CABA");
        ubicacionGeograficaMostaza.setProvincia(168);
        ubicacionGeograficaMostaza.setMunicipio(368);
        Usuario usuario = new Usuario("dueniomostazacentro", "mostazalamejor");

        mostazaCentro = new Organizacion(razonSocial, tipoOrganizacion, ubicacionGeograficaMostaza, clasificacionOrganizacion, usuario);

        Sector sector1 = new Sector("Tesoreria");
        Sector sector2 = new Sector("Contaduria");
        mostazaCentro.agregarSector(sector1);
        mostazaCentro.agregarSector(sector2);

        return mostazaCentro;
    }

    public static  Organizacion crearOrganizacionBurgerKing(){
        Organizacion burgerKing;
        Direccion ubicacionBurger = new Direccion("Colon","750",3324,"Avellaneda");
        ubicacionBurger.setMunicipio(335);
        ubicacionBurger.setProvincia(168);
        Usuario usuario = new Usuario("duenioburger","aguanteLaBigMac007");

        burgerKing = new Organizacion("BurgerKing S.A.",TipoOrganizacion.EMPRESA,ubicacionBurger,clasificacionOrganizacion,usuario);

        Sector sector1 = new Sector("RRHH");
        Sector sector2 = new Sector("Marketing");
        burgerKing.agregarSector(sector1);
        burgerKing.agregarSector(sector2);

        return burgerKing;
    }

    public static  Organizacion crearOrganizacionMcDonalds(){
        Organizacion mcDonalds;
        Direccion ubicacionMcDonalds = new Direccion("Av. Adolfo Alsina","700",3324,"Avellaneda");
        ubicacionMcDonalds.setMunicipio(335);
        ubicacionMcDonalds.setProvincia(168);
        Usuario usuario = new Usuario("dueniomcdonalds","aguanteLaBigMac007");

        mcDonalds = new Organizacion("Mc Donalds S.A.",TipoOrganizacion.EMPRESA, ubicacionMcDonalds, clasificacionOrganizacion, usuario);

        Sector sector1 = new Sector("RRHH");
        Sector sector2 = new Sector("Marketing");
        mcDonalds.agregarSector(sector1);
        mcDonalds.agregarSector(sector2);

        return mcDonalds;
    }

    public static Miembro crearMiembroJose(){
        Usuario usuarioMiembro = new Usuario("jmartinez", "jneregh23");

        return new Miembro("Jose", "Martinez", TipoDeDocumento.DNI, "40.293.111", usuarioMiembro);
    };

    public static Miembro crearMiembroMariela(){
        Usuario usuarioMiembro2 = new Usuario("mfernandez", "tyj3dfh3");
        return new Miembro("Mariela", "Fernandez", TipoDeDocumento.DNI, "43.843.701", usuarioMiembro2);
    }

    public static Miembro crearMiembroMaria(){
        Usuario usuarioMiembro3 = new Usuario("mgagliardi","@ma@gag@2000");
        return new Miembro("Maria","Gagliardi",TipoDeDocumento.LIBRETA_DE_ENROLAMIENTO,"5.209.562",usuarioMiembro3);
    }

}
