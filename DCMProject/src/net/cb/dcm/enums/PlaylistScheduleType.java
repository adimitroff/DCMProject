package net.cb.dcm.enums;

public enum PlaylistScheduleType {

	SINGLE_DAY (1),
	WEEKLY (2),
	MONTHLY (3),
	LAST_DAY_OF_MONTH (4),
	YEARLY (5),
	DAILY (6);
	
	private int id;

	PlaylistScheduleType(int id) { this.id = id; }

    public int getValue() { return id; }
}
