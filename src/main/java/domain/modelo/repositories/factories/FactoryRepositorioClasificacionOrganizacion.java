package domain.modelo.repositories.factories;

import Config.Config;
import domain.modelo.entities.Entidades.Organizacion.ClasificacionOrganizacion;
import domain.modelo.repositories.daos.DAO;
import domain.modelo.repositories.daos.DAOHibernate;
import domain.modelo.repositories.RepositorioClasificacionOrganizacion;

public class FactoryRepositorioClasificacionOrganizacion {
    private static RepositorioClasificacionOrganizacion repo;

    static {
        repo = null;
    }

    public static RepositorioClasificacionOrganizacion get(){
        if(repo == null){
            if(Config.useDataBase){
                DAO<ClasificacionOrganizacion> dao = new DAOHibernate<>(ClasificacionOrganizacion.class);
                repo = new RepositorioClasificacionOrganizacion(dao);
            }
        }
        return repo;
    }
}
