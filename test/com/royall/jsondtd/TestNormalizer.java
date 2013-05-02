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
package com.royall.jsondtd;

import java.util.HashMap;
import java.util.Map;

public class TestNormalizer implements Normalizer {

	@Override
	public Object normalize(Object _objectToNormalize) {
		final TestObject o = ((TestObject) _objectToNormalize);
		@SuppressWarnings("serial")
		Map<String, Object> map = new HashMap<String, Object>() {{put("testKey", o.id);}};
		return map;
	}

	@Override
	public Object denormalize(Object _objectToDenormalize) {
		@SuppressWarnings("unchecked")
		TestObject o = new TestObject("" + ((Map<String, Object>)_objectToDenormalize).get("testKey"));
		return o;
	}

}
