package net.cb.dcm.jpa;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import net.cb.dcm.jpa.entities.MediaContent;

public class MediaContentDao extends GenericDao<MediaContent> {

	public MediaContentDao() {
		super();
	}
	
	public MediaContentDao(GenericDao<?> genericDao) {
		super(genericDao);
	}
	
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
	
	public MediaContent findLastAdded() {
		TypedQuery<MediaContent> query = entityManager.createQuery(
				"SELECT m FROM MediaContent m ORDER BY m.id DESC", MediaContent.class);
		List<MediaContent> foundMedia = query.setMaxResults(1).getResultList();
		if (foundMedia.size() == 1) {
			return foundMedia.get(0);
		} else {
			return null;
		}
	}
}
