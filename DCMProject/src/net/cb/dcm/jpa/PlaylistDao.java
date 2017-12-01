package net.cb.dcm.jpa;

import java.util.Calendar;
import java.util.List;

import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import net.cb.dcm.enums.PlaylistScheduleType;
import net.cb.dcm.jpa.entities.Playlist;

public class PlaylistDao extends GenericDao<Playlist> {

	public PlaylistDao(DeviceDAO deviceDao) {
		super(deviceDao);
	}

	public Playlist findDefaultPlaylist() {
		TypedQuery<Playlist> query = entityManager.createQuery("SELECT p FROM Playlist p WHERE p.def = true",
				Playlist.class);
		List<Playlist> playlists = query.getResultList();
		if (playlists.size() > 0) {
			return playlists.get(0);
		} else {
			return null;
		}
	}

	public List<Playlist> findDailyPlayists() {
		TypedQuery<Playlist> query = entityManager.createQuery(
				"SELECT p FROM Playlist p JOIN p.schedules ps "
						+ " WHERE p.active AND ((ps.type = :typeDay AND ps.date = :date) "
						+ " OR (ps.type = :typeWeekly AND ps.dayOfWeek = :dayOfWeek ) "
						+ " OR (ps.type = :typeMonthly AND ps.dayOfMoth = :dayOfMoth AND ps.month = :month) "
						+ " OR (ps.type = :typeLastDayOfMonth AND :isLastDayOfMonth) "
						+ " OR (ps.type = :typeYearly AND ps.dayOfMoth = :dayOfMoth AND ps.month = :month) ) ",
				Playlist.class);
		Calendar cal = Calendar.getInstance();
		int currentMonth = cal.get(Calendar.MONTH);
		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.DAY_OF_MONTH, 1);
		boolean isLastDayOfMonth = false;
		isLastDayOfMonth = currentMonth != cal2.get(Calendar.MONTH);
		query.setParameter("typeDay", PlaylistScheduleType.SINGLE_DAY);
		query.setParameter("date", cal, TemporalType.DATE);
		query.setParameter("typeWeekly", PlaylistScheduleType.WEEKLY);
		query.setParameter("dayOfWeek", cal.get(Calendar.DAY_OF_WEEK));
		query.setParameter("typeMonthly", PlaylistScheduleType.MONTHLY);
		query.setParameter("dayOfMoth", cal.get(Calendar.DAY_OF_MONTH));
		query.setParameter("month", currentMonth);
		query.setParameter("typeLastDayOfMonth", PlaylistScheduleType.LAST_DAY_OF_MONTH);
		query.setParameter("isLastDayOfMonth", isLastDayOfMonth);
		query.setParameter("typeYearly", PlaylistScheduleType.YEARLY);
		List<Playlist> playlists = query.getResultList();
		return playlists;
	}
}
