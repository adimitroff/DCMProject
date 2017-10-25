package net.cb.dcm.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

//@Entity
//@Table(name="medcont_tag_rel")
public class MediaContentTagRelation {
	
	
//	@Id
//	@GeneratedValue
	private long id;
	
//	@Column(name="media_content_id")
	private long mediaContentId;
	
//	@Column(name="tag_id")
	private long tagId;
	
//	@ManyToOne
//	@PrimaryKeyJoinColumn(name="MEDIACONTENTID", referencedColumnName="ID")
	/* if this JPA model doesn't create a table for the "PROJ_EMP" entity,
	 * please comment out the @PrimaryKeyJoinColumn, and use the ff:
	 *  @JoinColumn(name = "deviceId", updatable = false, insertable = false)
	 * or @JoinColumn(name = "deviceId", updatable = false, insertable = false, referencedColumnName = "id")
	*/
	private MediaContent mediaContent;
	
//	@ManyToOne
//	@PrimaryKeyJoinColumn(name="TAGID", referencedColumnName="ID")
	/* if this JPA model doesn't create a table for the "PROJ_DeviceTagRelation" entity,
	 * please comment out the @PrimaryKeyJoinColumn, and use the ff:
	 *  @JoinColumn(name = "tagId", updatable = false, insertable = false)
	 * or @JoinColumn(name = "tagId", updatable = false, insertable = false, referencedColumnName = "id")
	*/
	private Tag tag;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTagId() {
		return tagId;
	}

	public void setTagId(long tagId) {
		this.tagId = tagId;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public long getMediaContentId() {
		return mediaContentId;
	}

	public void setMediaContentId(long mediaContentId) {
		this.mediaContentId = mediaContentId;
	}

	public MediaContent getMediaContent() {
		return mediaContent;
	}

	public void setMediaContent(MediaContent mediaContent) {
		this.mediaContent = mediaContent;
	}
}
