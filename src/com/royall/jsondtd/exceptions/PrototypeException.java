/* 
 *  Copyright (C) 2013 Royall & Company
*
*  JSON DTD is free software: you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  Free Software Foundation,version 3.
*  
 *  JSON DTD is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*  
 *  You should have received a copy of the GNU General Public License
*  along with JSON DTD.  If not, see http://www.gnu.org/licenses/
*  
 *  Additional permission under GNU GPL version 3 section 7
*  
 *  If you modify this Program, or any covered work, by linking or combining 
 *  it with any of the JARS listed in the README.txt (or a modified version of 
 *  (that library), containing parts covered by the terms of that JAR, the 
 *  licensors of this Program grant you additional permission to convey the 
 *  resulting work. 
 *  
*/
package com.royall.jsondtd.exceptions;

public class PrototypeException extends JSONValidatorException {
	private static final long serialVersionUID = 1L;

	private String message = null;

	private Exception e = null;

	public PrototypeException(String _message) {
		super(_message);
		message = _message;
	}

	public PrototypeException(Exception _e) {
		super(_e);
		e = _e;
	}

	public PrototypeException(String _message, Exception _e) {
		super(_message, _e);
		message = _message;
		e = _e;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public Exception getException() {
		return e;
	}
}
