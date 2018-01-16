package net.cb.dcm.jpa;

import javax.persistence.Query;

import net.cb.dcm.jpa.entities.Tag;

public class TagDao extends GenericDao<Tag> {

	public void removeRelations(Tag tag) {
		this.beginTransaction();
		
		Query query =  entityManager.createQuery("DELETE FROM DeviceGroupTagRelation WHERE tagId = :tagId");
		query.setParameter("tagId", tag.getId()).executeUpdate();
		
		query =  entityManager.createQuery("DELETE FROM DeviceTagRelation WHERE tagId = :tagId");
		query.setParameter("tagId", tag.getId()).executeUpdate();
		
		query =  entityManager.createQuery("DELETE FROM MediaContentTagRelation WHERE tagId = :tagId");
		query.setParameter("tagId", tag.getId()).executeUpdate();
		
		this.commitTransaction();
	}
}
