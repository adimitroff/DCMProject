package net.cb.dcm.jpa;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public class JpaEntityManagerFactory {

	public final static String PROPERTIES_FILE_NAME = "META-INF/jdbc.properties";

	public final static String persistenceUnitName = "dcm-persistence";
	private static EntityManagerFactory loEmf = null;

	public static synchronized EntityManagerFactory getEntityManagerFactory() throws NamingException, SQLException {
		if (loEmf == null) {
			Map<String, Object> properties = new HashMap<String, Object>();
			Properties loProperties = new Properties();
			try {

				InputStream loInputStream = JpaEntityManagerFactory.class.getClassLoader()
						.getResourceAsStream(PROPERTIES_FILE_NAME);
				loProperties.load(loInputStream);
				loInputStream.close();

				String lsDriver = loProperties.getProperty("jdbc.driver");
				if (lsDriver != null) {
					Class.forName(lsDriver);
				}
				String lsUrl = loProperties.getProperty("jdbc.url");
				String lsUsername = loProperties.getProperty("jdbc.username");
				String lsPassword = loProperties.getProperty("jdbc.password");

				properties.put(PersistenceUnitProperties.JDBC_DRIVER, lsDriver);
				properties.put(PersistenceUnitProperties.JDBC_URL, lsUrl);
				properties.put(PersistenceUnitProperties.JDBC_USER, lsUsername);
				properties.put(PersistenceUnitProperties.JDBC_PASSWORD, lsPassword);
			} catch (Exception e) {
				// TODO - where to raise the error?
				e.printStackTrace();
				properties.put(PersistenceUnitProperties.JDBC_DRIVER, "com.mysql.jdbc.Driver");
				properties.put(PersistenceUnitProperties.JDBC_URL, "jdbc:mysql://localhost/sql11202950");
				properties.put(PersistenceUnitProperties.JDBC_USER, "sql11202950");
				properties.put(PersistenceUnitProperties.JDBC_PASSWORD, "lQvKn9PbDQ");
			}

			loEmf = Persistence.createEntityManagerFactory(persistenceUnitName, properties);

		}
		return loEmf;
	}

}
