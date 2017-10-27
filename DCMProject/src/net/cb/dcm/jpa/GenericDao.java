package net.cb.dcm.jpa;

import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GenericDao<T> {

	protected EntityManager entityManager;

    protected static final Logger moLogger = LoggerFactory.getLogger(GenericDao.class);

	public GenericDao() {
		try {
			entityManager = JpaEntityManagerFactory.getEntityManagerFactory().createEntityManager();
		} catch (NamingException | SQLException e) {
			moLogger.error("Unable to create EntityManager", e);
		}
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getPersistentClass() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

	}

	public void beginTransaction() {
		entityManager.getTransaction().begin();
	}

	public void commitTransaction() {
		entityManager.getTransaction().commit();
	}

	public void rollbackTransaction() {
		entityManager.getTransaction().rollback();
	}

	public void close() {
		entityManager.close();
	}


	public T findById(Object id) {
		return entityManager.find(getPersistentClass(), id);
	}


	public List<T> findAll() {
		TypedQuery<T> query = entityManager.createQuery("from " + getPersistentClass().getName(), getPersistentClass());
		List<T> objects = query.getResultList();
		return objects;
	}


	public void insert(T instance) {
		beginTransaction();
		entityManager.persist(instance);
		commitTransaction();
	}


	public void update(T instance) {
		beginTransaction();
		entityManager.merge(instance);
		commitTransaction();
	}


	public void delete(T instance) {
		beginTransaction();
		entityManager.remove(instance);
		commitTransaction();
	}

}
