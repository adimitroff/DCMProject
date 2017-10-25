package net.cb.dcm.enums;

public enum MediaObjectType {
	MPEG (1),
	JPEG (2),
	OTHER (3);
	
	private int id;

    private MediaObjectType(int id) { this.id = id; }

    public int getValue() { return id; }
}
