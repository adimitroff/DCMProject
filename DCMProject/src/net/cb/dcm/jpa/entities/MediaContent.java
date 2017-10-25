package net.cb.dcm.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import net.cb.dcm.enums.MediaObjectType;

//@Entity
//@Table(name="media_content")
public class MediaContent {
	
//	@Id
//	@GeneratedValue
	private long id;
	
//	@Column(length=100)
	private String name;
	
//	@Column(length=250)
	private String description;
	
//	@Column(name="media_type")
	private MediaObjectType mediaType;
	
	private long duration;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public MediaObjectType getMediaType() {
		return mediaType;
	}

	public void setMediaType(MediaObjectType mediaType) {
		this.mediaType = mediaType;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
	
}
