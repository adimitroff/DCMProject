package net.cb.dcm.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionValidator {

	static public Pattern fileNamePattern = Pattern.compile( ValidationPatterns.FILE_NAME_EXPRESSION.getRegularExpr() );
	
	static public boolean isStringValid( String str, ValidationPatterns regularExpr ){
		Matcher matcher;
		switch( regularExpr ){
			case FILE_NAME_EXPRESSION : matcher = fileNamePattern.matcher( str ); break;
			default : return false;
		}
		return matcher.matches();
	}
}
