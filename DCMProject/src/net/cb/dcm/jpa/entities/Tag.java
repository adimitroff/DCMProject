package net.cb.dcm.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.PreRemove;
import javax.persistence.Table;

import net.cb.dcm.jpa.TagDao;

@Entity
@Table(name = "tags")
@NamedQuery(name = "AllTags", query = "select t from Tag t")
public class Tag {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
	
    @Column(unique=true, length=30)
    private String name;
 
    @Column(length=250)
    private String description;
    
	private boolean system;
	
//	@OneToMany(mappedBy="tag")
//	private List<DeviceTagRelation> devices;

    public long getId() {
        return id;
    }

    public void setId(long newId) {
        this.id = newId;
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

	public boolean isSystem() {
		return system;
	}

	public void setSystem(boolean system) {
		this.system = system;
	}
	
	@PreRemove
	private void beforeRemove() {
		new TagDao().removeRelations(this);
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
		Tag other = (Tag) obj;
		if (id != other.id)
			return false;
		return true;
	}


//	public List<DeviceTagRelation> getDevices() {
//		return devices;
//	}
//
//	public void setDevices(List<DeviceTagRelation> devices) {
//		this.devices = devices;
//	}
}
