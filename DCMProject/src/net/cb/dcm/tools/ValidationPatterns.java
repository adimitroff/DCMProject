package net.cb.dcm.tools;

public enum ValidationPatterns {

	FILE_NAME_EXPRESSION( "^[a-zA-Z[0-9]][a-zA-Z[0-9][_]]*\\.[a-zA-Z[0-9]]+" );
	
	private final String regularExpr;
	
	ValidationPatterns( String expr ){
		this.regularExpr = expr;
	}
	
	public String getRegularExpr(){ return regularExpr; }
	
}
