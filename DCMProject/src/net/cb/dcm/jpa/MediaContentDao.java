package net.cb.dcm.jpa;

import javax.persistence.Query;

import net.cb.dcm.jpa.entities.MediaContent;

public class MediaContentDao extends GenericDao<MediaContent> {

	public void removeRelations(MediaContent mediaContent) {
		this.beginTransaction();
		
		Query query =  entityManager.createQuery("DELETE FROM LoopMediaContentRelation WHERE mediaContentId = :mediaContentId");
		query.setParameter("mediaContentId", mediaContent.getId()).executeUpdate();
		
//		query =  entityManager.createQuery("DELETE FROM MediaContentTagRelation WHERE mediaContentId = :mediaContentId");
//		query.setParameter("mediaContentId", mediaContent.getId()).executeUpdate();
		
		query =  entityManager.createQuery("DELETE FROM PlaylistMediaContentRelation WHERE mediaContentId = :mediaContentId");
		query.setParameter("mediaContentId", mediaContent.getId()).executeUpdate();
		
		this.commitTransaction();
	}
}
