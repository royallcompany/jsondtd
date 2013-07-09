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
package com.royall.jsondtd.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class DateUtil extends Object {
	
	public static java.util.Date parseDate( String sDate, String _datePattern ){
		sDate = (sDate != null) ? sDate.trim() : null;
		
		/* if the date is null or empty, no point in going forward */
		if ( sDate == null || sDate.length() == 0 )
			return null;
		
		ParsePosition pp;
		java.util.Date d = null;
		DateFormat df = new SimpleDateFormat(_datePattern);
		df.setLenient(false);
		
		try {
			pp = new ParsePosition(0);
			d = df.parse(sDate,pp);
			if ( pp.getIndex()!=sDate.length() ) {
				d = null;
			}
		}catch (Exception ex1) {
			return null;
		}	
		return d;
	}
  
}
