package net.cb.dcm.jpa;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public class JpaEntityManagerFactory {
	
	public final static String persistenceUnitName = "dcm-persistence";
	private static EntityManagerFactory loEmf = null;
	public static synchronized EntityManagerFactory getEntityManagerFactory() 
			throws NamingException, SQLException {
		if (loEmf == null) {
			InitialContext ctx = new InitialContext();
            DataSource loDs = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, loDs);
            loEmf = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
		}
		return loEmf;
	}
		
				
}
