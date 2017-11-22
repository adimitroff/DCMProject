package net.cb.dcm.enums;

public enum DevProcedureType {
	WAKE (1),
	SLEEP (2);
	
	private int id;

	DevProcedureType(int id) { this.id = id; }

    public int getValue() { return id; }
}
