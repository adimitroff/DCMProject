package net.cb.dcm.jpa.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import net.cb.dcm.enums.PlaylistScheduleType;

/**
 * Entity implementation class for Entity: PlaylistSchedule
 *
 */
@Entity
@Table(name = "playlist_schedule")
public class PlaylistSchedule implements Serializable {

	
	private static final long serialVersionUID = 1L;

	public PlaylistSchedule() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "PLAYLIST_ID", referencedColumnName = "ID", nullable = false)
	private Playlist playlist;
	
	@Column
	private PlaylistScheduleType type;
	
	@Column(name = "START_TIME")
	@Temporal(TemporalType.TIME)
	private Date startTime;
	
	@Column(name= "END_TIME")
	@Temporal(TemporalType.TIME)
	private Date endTime;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date date;
	
	@Column(name = "DAY_OF_WEEK", nullable = false)
	private int dayOfWeek;
	
	@Column
	private int dayOfMoth;
	
	@Column
	private int month;
	
	
   
}
