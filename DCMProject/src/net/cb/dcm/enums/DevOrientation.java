package net.cb.dcm.enums;

public enum DevOrientation {
	LANDSCAPE (1),
	PORTRAIT (2);
	
	private int id;

	DevOrientation(int id) { this.id = id; }

    public int getValue() { return id; }
}
