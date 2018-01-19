package net.cb.dcm.jpa.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;

import net.cb.dcm.enums.MediaObjectType;
import net.cb.dcm.jpa.MediaContentDao;


@Entity
@Table(name="media_content")
public class MediaContent {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(length=100, nullable = false)
	private String name;
	
	@Column(length=250)
	private String description;
	
	@Column(name="MEDIA_TYPE", nullable = false)
	private MediaObjectType mediaType;
	
	private long duration;
	

	@Column(length=260)
	private String filePath;
	
	@Column(length=260)
	private String thumbnail;
	
	@Column(length=260, nullable = true)
	private String thumbnaiLarge;
	
	@Column(length=260, nullable = true)
	private String thumbnailMedium;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST})
	@JoinTable(name = "media_cont_tag_rel", 
	joinColumns = @JoinColumn(name = "MEDIA_CONTENT_ID"), 
	inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
	private List<Tag> tags;

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

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	@PreRemove
	private void beforeRemove() {
		new MediaContentDao().removeRelations(this);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MediaContent other = (MediaContent) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getThumbnaiLarge() {
		return thumbnaiLarge;
	}

	public void setThumbnaiLarge(String thumbnaiLarge) {
		this.thumbnaiLarge = thumbnaiLarge;
	}

	public String getThumbnailMedium() {
		return thumbnailMedium;
	}

	public void setThumbnailMedium(String thumbnailMedium) {
		this.thumbnailMedium = thumbnailMedium;
	}
	
}
