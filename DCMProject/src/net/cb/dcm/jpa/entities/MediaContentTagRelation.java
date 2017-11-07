package net.cb.dcm.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="media_cont_tag_rel")
public class MediaContentTagRelation {
	
	@Id
	@GeneratedValue
	private long id;
	
	@Column(name="MEDIA_CONTENT_ID")
	private long mediaContentId;
	
	@Column(name="TAG_ID")
	private long tagId;
	
	@ManyToOne
	@JoinColumn(name = "MEDIA_CONTENT_ID", referencedColumnName = "ID")
	private MediaContent mediaContent;
	
	@ManyToOne
	@JoinColumn(name = "TAG_ID", referencedColumnName = "ID")
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
