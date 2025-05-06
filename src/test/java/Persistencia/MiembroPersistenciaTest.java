package Persistencia;

import Factory.Entidades.EntidadesFactory;
import domain.modelo.entities.Entidades.Miembro.Miembro;
import domain.modelo.entities.Entidades.Miembro.SolicitudMiembro;
import domain.modelo.entities.Entidades.Miembro.TipoDeDocumento;
import domain.modelo.entities.Entidades.Organizacion.Organizacion;
import domain.modelo.entities.Entidades.Usuario.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;

public class MiembroPersistenciaTest {
    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static void main(String[] args) {
        emf = Persistence.createEntityManagerFactory("db");
        em = emf.createEntityManager();

        // Miembro
        Miembro miembroJose = EntidadesFactory.crearMiembroJose();

        // Agrego algunas Organizaciones
        Organizacion organizacionMostaza = EntidadesFactory.crearOrganizacionMostaza();
        Organizacion organizacionBurgerKing = EntidadesFactory.crearOrganizacionBurgerKing();
        Organizacion organizacionMcDonalds = EntidadesFactory.crearOrganizacionMcDonalds();

        organizacionMostaza.agregarMiembro(miembroJose, organizacionMostaza.getSectores().get(0));
        organizacionBurgerKing.agregarMiembro(miembroJose, organizacionBurgerKing.getSectores().get(0));

        // Solicitudes
        miembroJose.solicitarAdhesion(organizacionMcDonalds.getSectores().get(0), organizacionMcDonalds);

        // Trayectos
        /*
        private List<Trayecto> trayectos;
        */

        em.getTransaction().begin();

        em.persist(miembroJose);
        em.persist(EntidadesFactory.crearMiembroMariela());
        imprimir();

        em.getTransaction().commit();

        em.close();
        emf.close();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Persistencia Miembro")
    private static void imprimir() {
        Miembro miembro = (Miembro) em.createQuery("FROM Miembro WHERE id = 1").getSingleResult();
        Organizacion organizacion = (Organizacion) em.createQuery("FROM Organizacion WHERE razon_social LIKE :razonSocialOrganizacion").
                setParameter("razonSocialOrganizacion", "Mc Donalds S.A.").getSingleResult();

        // Atributos Miembro
        Assertions.assertEquals("Jose", miembro.getNombre());
        Assertions.assertEquals("Martinez", miembro.getApellido());
        Assertions.assertEquals(TipoDeDocumento.DNI, miembro.getTipoDeDocumento());
        Assertions.assertEquals("40.293.111", miembro.getNroDocumento());

        // Atributos Usuario
        Usuario usuario = miembro.getUsuario();
        Assertions.assertEquals("jmartinez", usuario.getNombre());
        Assertions.assertEquals("jneregh23", usuario.getContrasenia());

        // Organizaciones
        Assertions.assertEquals(2, miembro.getOrganizaciones().size());
        Assertions.assertEquals("Mostaza S.A", miembro.getOrganizaciones().get(0).getRazonSocial());
        Assertions.assertEquals("BurgerKing S.A.", miembro.getOrganizaciones().get(1).getRazonSocial());

        // Solicitudes
        SolicitudMiembro solicitud = miembro.getSolicitudes().get(0);

        Assertions.assertEquals(1, miembro.getSolicitudes().size());
        Assertions.assertEquals(1, organizacion.getSolicitudesMiembros().size());

        Assertions.assertEquals(miembro, solicitud.getMiembro());
        Assertions.assertEquals(organizacion, solicitud.getOrganizacion());

        // Todos los miembros que hay en la BD
        List<Miembro> miembros = (List<Miembro>) em.createQuery("FROM Miembro").getResultList();
        Assertions.assertEquals(2, miembros.size());
    }
}
