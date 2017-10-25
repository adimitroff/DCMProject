package net.cb.dcm.enums;

public enum DeviceType {
	MONITOR (1),
	MOBILE (2),
	OTHER (3);
	
	private int id;

    DeviceType(int id) { this.id = id; }

    public int getValue() { return id; }
}
