package net.cb.dcm.jpa;

import java.sql.SQLException;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import net.cb.dcm.jpa.entities.Device;

public class DeviceDAO {
	public static Device findDeviceByIp(String ip) throws NamingException, SQLException {
		EntityManagerFactory loEmf = JpaEntityManagerFactory.getEntityManagerFactory();
		EntityManager loEm = loEmf.createEntityManager();
		TypedQuery<Device> query = loEm.createQuery("SELECT d FROM Device d WHERE d.ip = :ip", Device.class);
		return query.setParameter("ip", ip).getSingleResult();
//		return null;
	}
}
