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
	@JoinColumn(name = "PLAYLIST_ID", nullable = false)
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
	
	@Column(name = "DAY_OF_WEEK")
	private int dayOfWeek;
	
	@Column(name = "DAY_OF_MONTH")
	private int dayOfMoth;
	
	@Column
	private int month;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Playlist getPlaylist() {
		return playlist;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}

	public PlaylistScheduleType getType() {
		return type;
	}

	public void setType(PlaylistScheduleType type) {
		this.type = type;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public int getDayOfMoth() {
		return dayOfMoth;
	}

	public void setDayOfMoth(int dayOfMoth) {
		this.dayOfMoth = dayOfMoth;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}
	
	
   
}
