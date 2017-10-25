package net.cb.dcm.odata;

import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAServiceFactory;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;

import net.cb.dcm.jpa.JpaEntityManagerFactory;

public class DcmOdataJPAServiceFactory extends ODataJPAServiceFactory {
	 
	@Override
	public ODataJPAContext initializeODataJPAContext() throws ODataJPARuntimeException {
		ODataJPAContext oDataJPAContext = getODataJPAContext();
		// oDataJPAContext.setEntityManagerFactory(JPAEntityManagerFactory
		// .getEntityManagerFactory(PUNIT_NAME));
		try {
			oDataJPAContext.setEntityManagerFactory(JpaEntityManagerFactory.getEntityManagerFactory());
		} catch (Exception e) {
			System.out.println("Error in persistent api: " + e.getMessage());
		}
		oDataJPAContext.setPersistenceUnitName(JpaEntityManagerFactory.persistenceUnitName);
		return oDataJPAContext;
	}

}
