package net.cb.dcm.enums;

public enum DevProcedureScheduleType {
	ONCE (1),
	DAILY (2);
	
	private int id;

	DevProcedureScheduleType(int id) { this.id = id; }

    public int getValue() { return id; }
}
