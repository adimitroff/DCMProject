package net.cb.dcm.jpa;

import java.util.Calendar;
import java.util.List;

import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import net.cb.dcm.enums.PlaylistScheduleType;
import net.cb.dcm.jpa.entities.Playlist;

public class PlaylistDao extends GenericDao<Playlist> {

	public PlaylistDao() {
		super();
	}
	
	public PlaylistDao(GenericDao<?> genericDao) {
		super(genericDao);
	}

	/**
	 * Finds default and active playlist
	 * @return Default playlist or null
	 */
	public Playlist findDefaultPlaylist() {
		TypedQuery<Playlist> query = entityManager.createQuery("SELECT p FROM Playlist p WHERE p.active AND p.def",
				Playlist.class);
		List<Playlist> playlists = query.getResultList();
		if (playlists.size() > 0) {
			return playlists.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Finds all active and scheduled for today playlists including default , 
	 * ordered by priority and scheduled start time
	 * @return Lists with all founded playlists including default playlist
	 */
	public List<Playlist> findDailyPlaylists() {
		return findDailyPlaylists(Calendar.getInstance());
	}

	/**
	 * Finds all active and scheduled for the given day playlists including default , 
	 * ordered by priority and scheduled start time
	 * @return Lists with all founded playlists including default playlist
	 */
	public List<Playlist> findDailyPlaylists(Calendar calendarDay) {
		TypedQuery<Playlist> query = entityManager.createQuery(
				"SELECT p FROM Playlist p JOIN p.schedules ps "
						+ " WHERE p.active AND (p.validFrom IS NULL OR p.validFrom <= :date) "
						+ " AND ( p.validTo IS NULL OR p.validTo >= :date) "
						+ " AND (ps.def "
						+ "  OR (ps.type = :typeDaily) "
						+ "  OR (ps.type = :typeDay AND psс.date = :date) "
						+ "  OR (ps.type = :typeWeekly AND ps.dayOfWeek = :dayOfWeek ) "
						+ "  OR (ps.type = :typeMonthly AND ps.dayOfMoth = :dayOfMoth AND ps.month = :month) "
						+ "  OR (ps.type = :typeLastDayOfMonth AND :isLastDayOfMonth) "
						+ "  OR (ps.type = :typeYearly AND ps.dayOfMoth = :dayOfMoth AND ps.month = :month) ) "
						+ " ORDER BY p.priority, ps.startTime",
				Playlist.class);
		int currentMonth = calendarDay.get(Calendar.MONTH);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(calendarDay.getTime());
		// Check for last day of month
		cal2.add(Calendar.DAY_OF_MONTH, 1);
		boolean isLastDayOfMonth = (currentMonth != cal2.get(Calendar.MONTH));
		// Set query parameters
		query.setParameter("typeDaily", PlaylistScheduleType.DAILY);
		query.setParameter("typeDay", PlaylistScheduleType.SINGLE_DAY);
		query.setParameter("date", calendarDay, TemporalType.DATE);
		query.setParameter("typeWeekly", PlaylistScheduleType.WEEKLY);
		query.setParameter("dayOfWeek", calendarDay.get(Calendar.DAY_OF_WEEK));
		query.setParameter("typeMonthly", PlaylistScheduleType.MONTHLY);
		query.setParameter("dayOfMoth", calendarDay.get(Calendar.DAY_OF_MONTH));
		query.setParameter("month", currentMonth);
		query.setParameter("typeLastDayOfMonth", PlaylistScheduleType.LAST_DAY_OF_MONTH);
		query.setParameter("isLastDayOfMonth", isLastDayOfMonth);
		query.setParameter("typeYearly", PlaylistScheduleType.YEARLY);
		List<Playlist> playlists = query.getResultList();
		return playlists;
	}
}
