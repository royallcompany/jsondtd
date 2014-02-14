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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import com.royall.jsondtd.util.DateUtil;

@SuppressWarnings( { "unchecked", "rawtypes", "serial" } )
public class JSONValidatorExtendedTest {

	private static final String TYPE = JSONValidator.KEY_TYPE;

	private static final String CLASS = JSONValidator.KEY_CLASS;

	private static final String NORMALIZE = JSONValidator.KEY_NORMALIZE;

	private static final String DENORMALIZE = JSONValidator.KEY_DENORMALIZE;

	private static final String DEFAULT = JSONValidator.KEY_DEFAULT;

	private static final String DEFAULT_ITEM = JSONValidator.KEY_DEFAULT_ITEM;

	private static final String REQUIRED = JSONValidator.KEY_REQ;

	private static final String ENUM = JSONValidator.KEY_ENUM;

	private static final String ERROR_ON_EMPTY = JSONValidator.KEY_STRING_ERR_ON_EMPTY;

	private static final String REGEX = JSONValidator.KEY_REGEX;

	private static final String FIELDS = JSONValidator.KEY_FIELDS;

	private static final String AFTER = JSONValidator.KEY_DATE_MUST_BE_AFTER;

	private static final String BEFORE = JSONValidator.KEY_DATE_MUST_BE_BEFORE;

	private static final String MINIMUM_NUMBER = JSONValidator.KEY_NUM_MIN;

	private static final String MAXIMUM_NUMBER = JSONValidator.KEY_NUM_MAX;

	private static final String ERROR_ON = JSONValidator.KEY_ERR_ON;

	private static final String CHILDREN = JSONValidator.KEY_CHILDREN;

	private static final String ARRAY_MINIMUM = JSONValidator.KEY_ARRAY_MIN;

	private static final String ARRAY_MAXIMUM = JSONValidator.KEY_ARRAY_MAX;

	private static final String WILDCARD = JSONValidator.KEY_WILDCARD_FIELD;

	private static final String DEFINITION = JSONValidator.KEY_FIELD_DEFINITION;

	private static final String ERROR_ON_UNSPECIFIED_KEYS = ValidationOptions.ErrorOnUnspecifiedKeys;

	private static final String DATE_PATTERN = ValidationOptions.DatePattern;

	private static final String STRING = "string";

	private static final String NUMBER = "number";

	private static final String BOOLEAN = "boolean";

	private static final String SIMPLE = "simple";

	private static final String STRUCT = "struct";

	private static final String ARRAY = "array";

	private static final String LIST = "list";

	private static final String DATE = "date";

	private static final String NULL = "null";

	private static final String ANY = "any";

	private static SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

	private JSONValidator jv;

	private static final int TRUE = 1;

	private static final int FALSE = 0;

	private static final int ERROR = -1;

	@Before
	public void before() {
		jv = new JSONValidator();
	}

	@Test
	public void testMinNumbers() {

		// Test one key in prototype where the key is a number but the minNumber is
		// not a number. This should throw an exception.
		testMinOrMaxNumber( 1, "NaN", MINIMUM_NUMBER, ERROR, "min must be instance of Number." );

		// Test that the JSON value adheres to the minimum number when the numNumber
		// is a int.
		testMinOrMaxNumber( 3, 2, MINIMUM_NUMBER, TRUE, "" );

		// Test that the minNumber does not work as a string number such as "1".
		testMinOrMaxNumber( 2, "1", MINIMUM_NUMBER, ERROR, "min must be instance of Number." );

		// Test that a value in the JSON below the minNumber is not allowed.
		testMinOrMaxNumber( 1, 3, MINIMUM_NUMBER, FALSE, "" );

		// Test that a value in the JSON equal to the minNumber is allowed.
		testMinOrMaxNumber( 3, 3, MINIMUM_NUMBER, TRUE, "" );

		// Test one key in prototype where the key is a number but the maxNumber is
		// not a number. This should throw an exception.
		testMinOrMaxNumber( 1, "NaN", MAXIMUM_NUMBER, ERROR, "max must be instance of Number." );

		// Test that the JSON value adheres to the maximum number when the numNumber
		// is a int.
		testMinOrMaxNumber( 3, 4, MAXIMUM_NUMBER, TRUE, "" );

		// Test that the maxNumber does not work as a string number such as "1".
		testMinOrMaxNumber( 0, "3", MAXIMUM_NUMBER, ERROR, "max must be instance of Number." );

		// Test that a value in the JSON above the maxNumber is not allowed.
		testMinOrMaxNumber( 4, 3, MAXIMUM_NUMBER, FALSE, "" );

		// Test that a value in the JSON equal to the maxNumber is allowed.
		testMinOrMaxNumber( 3, 3, MAXIMUM_NUMBER, TRUE, "" );

		// Test where a value in the json is a string but the proto is expecting a
		// number.
		testMinOrMaxNumber( "NaN", 4, MAXIMUM_NUMBER, FALSE, "" );

	}

	public void testMinOrMaxNumber( final Object _JSONValue, final Object _protoValue, final String _minOrMax, int _expected, String _exceptionMessage ) {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", _JSONValue );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, NUMBER );
						put( _minOrMax, _protoValue );
					}
				} );
			}
		} );

		validateTestResults( _expected, _exceptionMessage, json, prototype );
	}

	/**
	 * Test both max number and min number.
	 */
	@Test
	public void testMinMax() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", 2 );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, NUMBER );
						put( MAXIMUM_NUMBER, 3 );
						put( MINIMUM_NUMBER, 1 );
					}
				} );
			}
		} );

		try {
			assertTrue( jv.validate( json, prototype ) );
		} catch ( Exception e ) {
			fail( "An unexpected Exception was thrown during the test. Message: " + e.getMessage() );
		}
	}

	@Test
	public void testBasicTypes() {

		// Test that a proto is expecting a boolean and the json has a boolean
		testBasicType( true, BOOLEAN, TRUE, "" );

		// Test that a proto is expecting a boolean and the json does not have a
		// boolean
		testBasicType( "not a boolean...", BOOLEAN, FALSE, "" );

		// Test that the proto is expecting a simple and we give it a String
		testBasicType( "test string", SIMPLE, TRUE, "" );

		// Test that the proto is expecting a simple and we give it a int
		testBasicType( 2, SIMPLE, TRUE, "" );

		// Test that the proto is expecting a simple and we give it a boolean
		testBasicType( true, SIMPLE, TRUE, "" );

		// Test that the proto is expecting a simple and we give it a map
		HashMap map = new HashMap();
		testBasicType( map, SIMPLE, FALSE, "" );

		// Test that any type handles everything such as a map in this case.
		testBasicType( map, ANY, TRUE, "" );

		// Test that the null type handles a proper null value.
		testBasicType( null, NULL, TRUE, "" );

		// Test that the null type fails correctly when the proto is expecting a
		// null but the json doesn't have a null.
		testBasicType( "not null", NULL, FALSE, "" );

		// Test that the string "null" does not validate the null condition.
		testBasicType( "null", NULL, FALSE, "" );

		// Test that a proto expecting a string and a json with a string validates
		// correctly.
		testBasicType( "a word or string", STRING, TRUE, "" );

		// Test that a proto expecting a string and a json with no string validates
		// correctly.
		testBasicType( TRUE, STRING, FALSE, "" );

		// Test that a null in each type of data works correctly.
		testBasicType( null, STRING, FALSE, "" );
		testBasicType( null, DATE, FALSE, "" );
		testBasicType( null, NUMBER, FALSE, "" );
		testBasicType( null, BOOLEAN, FALSE, "" );
		testBasicType( null, SIMPLE, FALSE, "" );

		// Test that a proto was expecting a date and found a date in the json.
		testBasicType( DateUtil.parseDate( "2013-05-24", dateFormat ), DATE, TRUE, "" );

		// Test that a prototype was expecting a date but the json did not have that
		// date.
		testBasicType( "yyyy-MM-dd", DATE, FALSE, "" );

	}

	public void testBasicType( final Object _JSONValue, final String _expectedType, int _expected, String _exceptionMessage ) {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", _JSONValue );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, _expectedType );
					}
				} );
			}
		} );

		validateTestResults( _expected, _exceptionMessage, json, prototype );
	}

	@Test
	public void testClasses() {

		// Basic test for using classes as a type.
		testClass( new TreeSet(), "java.util.TreeSet", TRUE, "" );

		// Basic test for using classes as a type. The correct type is the second
		// thing in the proto list.
		testClass( new TreeSet(), "java.util.HashMap,java.util.TreeSet", TRUE, "" );

		// Basic test for using classes as a type, the type of the json is not in
		// the proto
		testClass( new TreeSet(), "s,s", FALSE, "" );

		// Basic test for using classes as a type, the type of the json is not in
		// the proto
		testClass( "key1", "java.util.HashMap", FALSE, "" );

		// Basic test for using classes as a type, the type of the json is
		// java.lang.String and the value of the json of going to actually be a
		// string.
		testClass( "key1", "java.lang.String", TRUE, "" );

		// Basic test for using classes as a type. not using the java.util preface.
		testClass( new TreeSet(), "TreeSet", TRUE, "" );

		// Basic test for using classes as a type. not using the java.util preface
		// but also the json doesn't match up.
		testClass( "not a tree set", "TreeSet", FALSE, "" );

		// Basic test for using classes as a type. Proto is expecting a String and
		// the json gives a string.
		testClass( "a string", "String", TRUE, "" );

		// Basic test for using classes as a type. Proto is null.
		testClass( "a string", null, ERROR, "'type' or 'class' is required for each level in prototype." );

		// Basic test for using classes as a type. The proto is expecting a TreeSet
		// but the json is null.
		testClass( null, "TreeSet", FALSE, "" );

		// Test for using the class tag where the tag is "null" and the json has a
		// null value in it.
		testClass( null, NULL, TRUE, "" );

	}

	public void testClass( final Object _JSONValue, final String _expectedClass, int _expected, String _exceptionMessage ) {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", _JSONValue );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( CLASS, _expectedClass );
					}
				} );
			}
		} );

		validateTestResults( _expected, _exceptionMessage, json, prototype );
	}

	@Test
	public void testArrayMinAndMaxs() {

		ArrayList<String> list = new ArrayList<String>();

		// Test one key in prototype where the key is a number but the
		// arrayMinNumber is not a number. This should throw an exception.
		testArrayMinAndMax( list, ARRAY_MINIMUM, "NaN", ERROR, "Array min must be an Integer" );

		// Test that the JSON value adheres to the minimum number when the numNumber
		// is a int.
		list.clear();
		list.add( "1" );
		list.add( "2" );
		list.add( "3" );
		testArrayMinAndMax( list, ARRAY_MINIMUM, 2, TRUE, "" );

		// Test that the arrayMinNumber does not work as a string number such as
		// "1".
		list.clear();
		list.add( "1" );
		list.add( "2" );
		testArrayMinAndMax( list, ARRAY_MINIMUM, "1", ERROR, "Array min must be an Integer" );

		// Test that a value in the JSON below the arrayMinNumber is not allowed.
		list.clear();
		list.add( "1" );
		testArrayMinAndMax( list, ARRAY_MINIMUM, 3, FALSE, "" );

		// Test that a value in the JSON equal to the arrayMinNumber is allowed.
		list.clear();
		list.add( "1" );
		list.add( "2" );
		list.add( "3" );
		testArrayMinAndMax( list, ARRAY_MINIMUM, 3, TRUE, "" );

		// Test the value of minNum being null
		list.clear();
		list.add( "1" );
		testArrayMinAndMax( list, ARRAY_MINIMUM, null, TRUE, "" );

		// Test one key in prototype where the key is a number but the
		// arrayMaxNumber is not a number. This should throw an exception.
		list.clear();
		list.add( "1" );
		testArrayMinAndMax( list, ARRAY_MAXIMUM, "NaN", ERROR, "Array max must be an Integer" );

		// Test that the JSON value adheres to the maximum number when the numNumber
		// is a int.
		list.clear();
		list.add( "1" );
		list.add( "2" );
		list.add( "3" );
		testArrayMinAndMax( list, ARRAY_MAXIMUM, 4, TRUE, "" );

		// Test that the arrayMaxNumber does not work as a string number such as
		// "1".
		list.clear();
		list.add( "1" );
		testArrayMinAndMax( list, ARRAY_MAXIMUM, "3", ERROR, "Array max must be an Integer" );

		// Test that a value in the JSON above the arrayMaxNumber is not allowed.
		list.clear();
		list.add( "1" );
		list.add( "2" );
		list.add( "3" );
		list.add( "4" );
		testArrayMinAndMax( list, ARRAY_MAXIMUM, 3, FALSE, "" );

		// Test that a value in the JSON equal to the arrayMaxNumber is allowed.
		list.clear();
		list.add( "1" );
		list.add( "2" );
		testArrayMinAndMax( list, ARRAY_MAXIMUM, 3, TRUE, "" );

		// Test the value of maxNum being null
		list.clear();
		list.add( "1" );
		testArrayMinAndMax( list, ARRAY_MAXIMUM, null, TRUE, "" );

	}

	public void testArrayMinAndMax( final Object _JSONValue, final String _minOrMax, final Object _minOrMaxValue, int _expected, String _exceptionMessage ) {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", _JSONValue );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ARRAY );
						put( _minOrMax, _minOrMaxValue );
						put( CHILDREN, new HashMap() {

							{
								put( TYPE, STRING );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( _expected, _exceptionMessage, json, prototype );
	}

	@Test
	public void testDateBeforeAndAfter() {

		// Test that a json correctly adheres to a date being before another date.
		testDateBeforeAndAfter( DateUtil.parseDate( "2013-05-24", dateFormat ), BEFORE, DateUtil.parseDate( "2013-06-24", dateFormat ), TRUE, "" );

		// Test that a json does not correctly adheres to a date being before the
		// date given in the prototype.
		testDateBeforeAndAfter( DateUtil.parseDate( "2013-05-24", dateFormat ), BEFORE, DateUtil.parseDate( "2013-04-24", dateFormat ), FALSE, "" );

		// Test that a json does not correctly adheres to a prototype where the date
		// must be after a certain date.
		testDateBeforeAndAfter( DateUtil.parseDate( "2013-03-24", dateFormat ), AFTER, DateUtil.parseDate( "2013-04-24", dateFormat ), FALSE, "" );

		// Test that a date in json correctly adheres to the prototype after a
		// certain date.
		testDateBeforeAndAfter( DateUtil.parseDate( "2013-05-24", dateFormat ), AFTER, DateUtil.parseDate( "2013-04-24", dateFormat ), TRUE, "" );

		// Proto is expecting a date and its given a int.
		testDateBeforeAndAfter( 1, AFTER, DateUtil.parseDate( "2013-04-24", dateFormat ), FALSE, "" );

		// After is in an incorrect form of an int.
		testDateBeforeAndAfter( DateUtil.parseDate( "2013-05-24", dateFormat ), AFTER, 1, ERROR, "after must be instance of java.util.Date or String." );

		// Before is in an incorrect form of an int.
		testDateBeforeAndAfter( DateUtil.parseDate( "2013-05-24", dateFormat ), BEFORE, 1, ERROR, "before must be instance of java.util.Date or String." );
	}

	public void testDateBeforeAndAfter( final Object _JSONDate, final String _beforeOrAfter, final Object _beforeOrAfterValue, int _expected, String _exceptionMessage ) {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", _JSONDate );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, DATE );
						put( _beforeOrAfter, _beforeOrAfterValue );
					}
				} );
			}
		} );

		validateTestResults( _expected, _exceptionMessage, json, prototype );
	}

	@Test
	public void testStringErrorOnEmpties() {

		// Test that a proto given a string with the error on empty tag as true that
		// it correctly validates an "" string.
		testStringErrorOnEmpty( "", true, FALSE, "" );

		// Test that a proto given a string with the error on empty tag as true that
		// it correctly validates an "  " string.
		testStringErrorOnEmpty( "  ", true, FALSE, "" );

		// Test that the string "null" does not validate the null condition.
		testStringErrorOnEmpty( "", false, TRUE, "" );

		// Test that in a proto, the type of the error on empty restriction is
		// properly checked. The value of "true" should not be picked up and it
		// should not give an error
		testStringErrorOnEmpty( "", "true", TRUE, "" );

		// Test that in a proto, the error on empty restriction properly validates
		// with a false value.
		testStringErrorOnEmpty( "", false, TRUE, "" );
	}

	public void testStringErrorOnEmpty( final Object _JSONString, final Object _emptyOrNot, int _expected, String _exceptionMessage ) {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", _JSONString );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( ERROR_ON_EMPTY, _emptyOrNot );
						put( TYPE, STRING );
					}
				} );
			}
		} );

		validateTestResults( _expected, _exceptionMessage, json, prototype );
	}

	@Test
	public void testClassNormalizations() {

		// Normal test case for classes and normalizers
		testClassNormalization( "com.royall.jsondtd.TestNormalizer", true, TRUE, "" );

		// The normalize key is null
		testClassNormalization( null, true, FALSE, "" );

		// The normalize key is not a real class.
		testClassNormalization( "Not a Class", true, ERROR, "Failed to invoke Not a Class.normalize(Object _jsonValue) due to java.lang.ClassNotFoundException: Not a Class" );

		// Denormalize is null
		testClassNormalization( "com.royall.jsondtd.TestNormalizer", null, TRUE, "" );

		// The denormalize value is not a boolean
		testClassNormalization( "com.royall.jsondtd.TestNormalizer", "not a boolean", ERROR, "Key denormalize should be a boolean." );
	}

	public void testClassNormalization( final Object _normalizeValue, final Object _denormalizeValue, int _expected, String _exceptionMessage ) {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				TestObject testObject = new TestObject( "value" );
				put( "key1", testObject );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( CLASS, "com.royall.jsondtd.TestObject" );
						put( NORMALIZE, _normalizeValue );
						put( DENORMALIZE, _denormalizeValue );
						put( TYPE, STRUCT );
						put( FIELDS, new HashMap() {

							{
								put( "testKey", new HashMap() {

									{
										put( DEFINITION, new HashMap() {

											{
												put( TYPE, STRING );
											}
										} );
									}
								} );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( _expected, _exceptionMessage, json, prototype );
	}

	@Test
	public void testRegexs() {

		// Test that the json format is correct in the prototype but the json does
		// not match.
		testRegEx( "", "[a-z]", FALSE, "" );

		// The regex in the prototype is a boolean instead of a string.
		testRegEx( "", false, TRUE, "" );

		// The regex in the prototype and the json are correct.
		testRegEx( "a", "[a-z]", TRUE, "" );

		// The regex in the prototype is null.
		testRegEx( "aa", null, TRUE, "" );

		// The regex has an improper format.
		testRegEx( "a", "++", ERROR, "Dangling meta character '+' near index 0\r\n" + "++\r\n" + "^" );

		// The JSON is null
		testRegEx( null, "++", FALSE, "" );
	}

	public void testRegEx( final Object _JSONString, final Object _regex, int _expected, String _exceptionMessage ) {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", _JSONString );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRING );
						put( REGEX, _regex );
					}
				} );
			}
		} );

		validateTestResults( _expected, _exceptionMessage, json, prototype );
	}

	/**
	 * Enum list contains a value and the json contains that value.
	 */
	@Test
	public void testEnumTrue() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "1" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRING );
						put( ENUM, new ArrayList<String>() {

							{
								add( "1" );
							}
						} );
					}
				} );
			}
		} );
		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * When checking a string the string does not match the enum list.
	 */
	@Test
	public void testEnumFalse() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "2" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRING );
						put( ENUM, new ArrayList<String>() {

							{
								add( "1" );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( FALSE, json, prototype );
	}

	/**
	 * Enum list is null when checking a string.
	 */
	@Test
	public void testEnumNull() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "2" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRING );
						put( ENUM, null );
					}
				} );
			}
		} );

		validateTestResults( ERROR, "enum list must be a list.", json, prototype );
	}

	/**
	 * Test the basic functionality of an array.
	 */
	@Test
	public void testArrayBasic() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new ArrayList<String>() {

					{
						add( "One" );
						add( "Two" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ARRAY );
						put( CHILDREN, new HashMap() {

							{
								put( TYPE, STRING );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Test the basic fail functionality of an array. The proto thinks theres going to be an array but
	 * infact its a string!
	 */
	@Test
	public void testArrayBasicFailure() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "not a list" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ARRAY );
						put( CHILDREN, new HashMap() {

							{
								put( TYPE, STRING );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( FALSE, json, prototype );
	}

	/**
	 * Test that a value in the JSON above the arrayMaxNumber is not allowed.
	 */
	@Test
	public void testArrayMinMax() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new ArrayList() {

					{
						add( "1" );
						add( "2" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, LIST );
						put( ARRAY_MAXIMUM, 3 );
						put( ARRAY_MINIMUM, 1 );
						put( CHILDREN, new HashMap() {

							{
								put( TYPE, STRING );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Test array null child in proto
	 */
	@Test
	public void testArrayNullChild() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new ArrayList() {

					{
						add( "1" );
						add( "2" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, LIST );
						put( CHILDREN, null );
					}
				} );
			}
		} );

		validateTestResults( ERROR, "'children' key required for Type List", json, prototype );
	}

	/**
	 * Test array child in proto where it is not a hashmap like it should be.
	 */
	@Test
	public void testArrayChildIsNotMap() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new ArrayList() {

					{
						add( "1" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, LIST );
						put( CHILDREN, "not a map" );
					}
				} );
			}
		} );

		validateTestResults( ERROR, "'children' key of type: list must be a Map", json, prototype );
	}

	/**
	 * Test for settings the JSONValidators options for settings dates
	 */
	@Test
	public void testDateFormatOptionBefore() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", DateUtil.parseDate( "2013-05-24", dateFormat ) );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, DATE );
						put( BEFORE, "2013-06-2" );
					}
				} );
			}
		} );

		try {
			Map<String, String> options = new HashMap<String, String>();
			options.put( DATE_PATTERN, "yyyy-MM-dd" );
			JSONValidator jv = new JSONValidator( options );
			assertTrue( jv.validate( json, prototype ) );
		} catch ( Exception e ) {
			fail( "An unexpected Exception was thrown during the test. Message: " + e.getMessage() );
		}
	}

	/**
	 * Test for settings the JSONValidators options for settings dates
	 */
	@Test
	public void testDateFormatOptionAfter() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", DateUtil.parseDate( "2013-05-24", dateFormat ) );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, DATE );
						put( AFTER, "2013-04-24" );
					}
				} );
			}
		} );

		try {
			Map<String, String> options = new HashMap<String, String>();
			options.put( DATE_PATTERN, "yyyy-MM-dd" );
			JSONValidator jv = new JSONValidator( options );
			assertTrue( jv.validate( json, prototype ) );
		} catch ( Exception e ) {
			fail( "An unexpected Exception has occured while running the test. Message: " + e.getMessage() );
		}
	}

	/**
	 * Test for settings the JSONValidators options for settings dates, correct date option format but
	 * incorrect date format in the prototype.
	 */
	@Test
	public void testDateFormatOptionBefore2() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", DateUtil.parseDate( "2013-05-24", dateFormat ) );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, DATE );
						put( BEFORE, "2013-06-228" );
					}
				} );
			}
		} );

		try {
			Map<String, String> options = new HashMap<String, String>();
			options.put( DATE_PATTERN, "yyyy-MM-dd" );
			JSONValidator jv = new JSONValidator( options );
			jv.validate( json, prototype );
			fail( "An exception was expected to be thrown but never did." );
		} catch ( Exception e ) {
			assertEquals( "Failed to parse prototype before String 2013-06-228 as Date of pattern ( yyyy-MM-dd ) ", e.getMessage() );
		}
	}

	/**
	 * Test for settings the JSONValidators options for settings dates, correct date option format but
	 * incorrect date format in the prototype.
	 */
	@Test
	public void testDateFormatOptionAfter2() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", DateUtil.parseDate( "2013-05-24", dateFormat ) );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, DATE );
						put( AFTER, "2013-04-248" );
					}
				} );
			}
		} );

		try {
			Map<String, String> options = new HashMap<String, String>();
			options.put( DATE_PATTERN, "yyyy-MM-dd" );
			JSONValidator jv = new JSONValidator( options );
			jv.validate( json, prototype );
			fail( "An exception was expected to be thrown but never did." );
		} catch ( Exception e ) {
			assertEquals( "Failed to parse prototype after String 2013-04-248 as Date of pattern ( yyyy-MM-dd ) ", e.getMessage() );
		}
	}

	/**
	 * Test a struct where the json is actually a string.
	 */
	@Test
	public void testStruct1() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "not a map" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, new HashMap() {

							{
								put( "1", new HashMap() {

									{
										put( TYPE, STRING );
									}
								} );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( FALSE, json, prototype );
	}

	/**
	 * Test a struct where the json is an empty map.
	 */
	@Test
	public void testStruct2() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new HashMap() {

					{}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, new HashMap() {

							{
								put( "key2", new HashMap() {

									{
										put( REQUIRED, true );
										put( DEFINITION, new HashMap() {

											{
												put( TYPE, STRING );
											}
										} );
									}
								} );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( FALSE, json, prototype );
	}

	/**
	 * Test a struct where the json fields are null
	 */
	@Test
	public void testStruct3() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", null );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, new HashMap() {

							{
								put( "key2", new HashMap() {

									{
										put( DEFINITION, new HashMap() {

											{
												put( TYPE, STRING );
											}
										} );
										put( REQUIRED, true );
									}
								} );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( FALSE, null, json, prototype );
	}

	/**
	 * Test struct where the proto fields is null
	 */
	@Test
	public void testStruct4() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new HashMap() {

					{
						put( "key2", "One" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map proto = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, null );
					}
				} );
			}
		} );

		validateTestResults( TRUE, json, proto );
	}

	/**
	 * Test struct where the proto fields is just a string called fields.
	 */
	@Test
	public void testStruct5() {

		Object json = new HashMap() {

			{
				put( "key1", new HashMap() {

					{
						put( "key2", "One" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, "fields" );
					}
				} );
			}
		} );

		validateTestResults( ERROR, "Fields key of type: Struct must be a List or Map", json, prototype );
	}

	/**
	 * Test structs where the proto fields is an empty map.
	 */
	@Test
	public void testStruct6() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new HashMap() {

					{
						put( "key2", "One" );
					}
				} );
			}
		};

		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, new HashMap() {} );
					}
				} );
			}
		} );

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Correct json and proto
	 */
	@Test
	public void testStruct7() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, new HashMap() {

							{
								put( "key2", new HashMap() {

									{
										put( DEFINITION, new HashMap() {

											{
												put( TYPE, STRING );
											}
										} );
									}
								} );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Test a struct with a fields whose arraylist is empty
	 */
	@Test
	public void testStruct8() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new HashMap() {

					{
						put( "key2", "One" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, new ArrayList() );
					}
				} );
			}
		} );

		validateTestResults( ERROR, "Cannot have empty fields array in prototype", json, prototype );
	}

	/**
	 * Correct Json but proto has no fields but a blank map.
	 */
	@Test
	public void testStruct9() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new HashMap() {

					{
						put( "key2", "One" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, new ArrayList() {

							{
								add( new HashMap() );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Correct json and proto has one correct hashmap in the list to validate against.
	 */
	@Test
	public void testStruct10() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new HashMap() {

					{
						put( "key2", "One" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, new ArrayList() {

							{
								add( new HashMap() {

									{
										put( "key2", new HashMap() {

											{
												put( DEFINITION, new HashMap() {

													{
														put( TYPE, STRING );
													}
												} );
												put( REQUIRED, true );
											}
										} );
									}
								} );
							}
						} );
					}
				} );

			}
		} );

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Correct json and proto that has two correct hashmaps in the arraylist.
	 */
	@Test
	public void testStruct11() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new HashMap() {

					{
						put( "key2", "One" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, new ArrayList() {

							{
								add( new HashMap() {

									{
										put( "key2", new HashMap() {

											{
												put( DEFINITION, new HashMap() {

													{
														put( TYPE, STRING );
													}
												} );
												put( REQUIRED, true );
											}
										} );
									}
								} );
								add( new HashMap() {

									{
										put( "key3", new HashMap() {

											{
												put( DEFINITION, new HashMap() {

													{
														put( TYPE, STRING );
													}
												} );
												put( REQUIRED, false );
											}
										} );
									}
								} );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * json doesn't match and proto that has two correct hashmaps in the arraylist.
	 */
	@Test
	public void testStruct12() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new HashMap() {

					{
						put( "key4", "One" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, new ArrayList() {

							{
								add( new HashMap() {

									{
										put( "key2", new HashMap() {

											{
												put( DEFINITION, new HashMap() {

													{
														put( TYPE, STRING );
													}
												} );
												put( REQUIRED, true );
											}
										} );
									}
								} );
								add( new HashMap() {

									{
										put( "key3", new HashMap() {

											{
												put( DEFINITION, new HashMap() {

													{
														put( TYPE, STRING );
													}
												} );
												put( REQUIRED, true );
											}
										} );
									}
								} );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( FALSE, json, prototype );
	}

	/**
	 * json doesn't match and proto that has one correct hashmap and one that is just an incorrect
	 * string
	 */
	@Test
	public void testStruct13() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new HashMap() {

					{
						put( "key4", "One" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRUCT );
						put( FIELDS, new ArrayList() {

							{
								add( new HashMap() {

									{
										put( "key2", new HashMap() {

											{
												put( DEFINITION, new HashMap() {

													{
														put( TYPE, STRING );
													}
												} );
												put( REQUIRED, true );
											}
										} );
									}
								} );
								add( "string something" );
							}
						} );
					}
				} );
			}
		} );

		validateTestResults( FALSE, json, prototype );
	}

	/**
	 * Test that a proto with a non existent type doesn't work.
	 */
	@Test
	public void testNonexistantType() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", 0 );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, "not a real type" );
					}
				} );
			}
		} );

		validateTestResults( ERROR, "Type 'not a real type' was not recognized", json, prototype );
	}

	/**
	 * Required is not a boolean or a map
	 */
	@Test
	public void testRequiredField1() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "value" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ANY );
					}
				} );
				put( REQUIRED, "not a boolean" );
			}
		} );
		validateTestResults( ERROR, "req can only be boolean or struct.", json, prototype );
	}

	/**
	 * Required is a boolean
	 */
	@Test
	public void testRequiredField2() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "value" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ANY );
					}
				} );
				put( REQUIRED, true );
			}
		} );

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Required is a map
	 */
	@Test
	public void testRequiredField3() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "value" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ANY );
					}
				} );
				put( REQUIRED, new HashMap() {

					{
						put( "thing", "thing" );
					}
				} );
			}
		} );

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Error On is not a boolean or a map
	 */
	@Test
	public void testErrOnField1() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "value" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ANY );
					}
				} );
				put( ERROR_ON, "not a boolean" );
			}
		} );

		validateTestResults( ERROR, "err_on can only be boolean or struct.", json, prototype );

	}

	/**
	 * Error On is a boolean
	 */
	@Test
	public void testErrOnField2() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "value" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ANY );
					}
				} );
				put( REQUIRED, true );
				put( ERROR_ON, false );
			}
		} );

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Error On is a map
	 */
	@Test
	public void testErrOnField3() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "value" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ANY );
					}
				} );
				put( ERROR_ON, new HashMap() {

					{
						put( "thing", "thing" );
					}
				} );
			}
		} );

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Test field evaluation
	 */
	@Test
	public void testFieldEvaluation1() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key12", new HashMap() );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ANY );
					}
				} );
				put( REQUIRED, true );
				put( ERROR_ON, false );
			}
		} );

		validateTestResults( FALSE, json, prototype );
	}

	/**
	 * Test field evaluation.
	 * 
	 * ErrorOn is true but the HashMap in the JSON key1 is empty so it's a false.
	 */
	@Test
	public void testFieldEvaluation2() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", new HashMap() );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ANY );
					}
				} );
				put( ERROR_ON, true );
			}
		} );

		validateTestResults( FALSE, json, prototype );
	}

	/**
	 * Test field evaluation
	 */
	@Test
	public void testFieldEvaluation3() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key12", new HashMap() );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ANY );
					}
				} );
				put( DEFAULT, "Default" );
			}
		} );

		validateTestResults( TRUE, json, prototype );

	}

	/**
	 * Test field evaluation
	 */
	@Test
	public void testFieldEvaluation4() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key12", new HashMap() );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ANY );
					}
				} );
				put( DEFAULT_ITEM, "Default Item" );
			}
		} );
		validateTestResults( ERROR, "defitem Default Item was not found. Please add to fieldItems.", json, prototype );

	}

	/**
	 * Test field evaluation
	 */
	@Test
	public void testFieldEvaluation5() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key12", new HashMap() );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ANY );
					}
				} );
				put( DEFAULT_ITEM, null );
			}
		} );

		validateTestResults( ERROR, "defitem cannot be null", json, prototype );
	}

	/**
	 * Test that the Error On Unspecified Fields works.
	 */
	@Test
	public void testFieldsEvaluation6() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", 1 );
				put( "key2", "2" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key2", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRING );
					}
				} );
			}
		} );

		try {
			Map<String, Boolean> map = new HashMap<String, Boolean>();
			map.put( ERROR_ON_UNSPECIFIED_KEYS, true );
			jv = new JSONValidator( map );
			assertFalse( jv.validate( json, prototype ) );
		} catch ( Exception e ) {
			fail( "An unexpected Exception was thrown during the test. Message: " + e.getMessage() );
		}
	}

	/**
	 * Test wildcard field when it is not a map.
	 */
	@Test
	public void testWildCard1() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key12", new HashMap() );
			}
		};
		// Create the Prototype.
		Map prototype = new HashMap() {

			{
				put( TYPE, STRUCT );
				put( "fields", new ArrayList() {

					{
						add( new HashMap() {

							{
								put( WILDCARD, "string" );
							}
						} );
					}
				} );
			}
		};

		validateTestResults( ERROR, "Fieldmap for * in prototype was not a map", json, prototype );
	}

	/**
	 * Test wildcard field when it is an empty map.
	 */
	@Test
	public void testWildCard2() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key12", new HashMap() );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( WILDCARD, new HashMap() {

			{}
		} );

		validateTestResults( ERROR, "Key definition is required for fields.", json, prototype );
	}

	/**
	 * Test wildcard field when it is a map and the json has a value it wants.
	 */
	@Test
	public void testWildCard3() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key12", new HashMap() );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( WILDCARD, new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, ANY );
					}
				} );
			}
		} );

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Test wildcard field when it is a map and the json does not have a value it wants.
	 */
	@Test
	public void testWildCard4() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", 1 );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( WILDCARD, new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( TYPE, STRING );
					}
				} );
			}
		} );

		validateTestResults( FALSE, json, prototype );
	}

	/**
	 * Test wildcard field when there is another valid key in the proto.
	 */
	@Test
	public void testWildCard5() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "1" );
				put( "key2", "2" );
			}
		};
		// Create the Prototype.
		Map prototype = new HashMap() {

			{
				put( TYPE, STRUCT );

				put( "fields", new ArrayList() {

					{
						add( new HashMap() {

							{
								put( WILDCARD, new HashMap() {

									{
										put( DEFINITION, new HashMap() {

											{
												put( TYPE, STRING );
											}
										} );
									}
								} );
								put( "key2", new HashMap() {

									{
										put( DEFINITION, new HashMap() {

											{
												put( TYPE, STRING );
											}
										} );
									}
								} );
							}
						} );
					}
				} );
			}
		};

		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Test Custom Types
	 */
	@Test
	public void testCustomType() {

		// Create the JSON Object.
		Object json = new HashMap() {

			{
				put( "key1", "good@email.com" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {

			{
				put( DEFINITION, new HashMap() {

					{
						put( "custom", "email" );
					}
				} );
			}
		} );

		jv.addCustomType( "email", new HashMap() {

			{
				put( "type", "string" );
				put( "regex", "^([\\w-.]+)@(([([0-9]{1,3}.){3}[0-9]{1,3}])|(([\\w-]+.)+)([a-zA-Z]{2,4}))$" );
			}
		} );
		validateTestResults( TRUE, json, prototype );
	}

	/**
	 * Validates the test results. This method is just useful if the test outcome isn't going to be an
	 * error because you don't have to pass the string for the error message.
	 * 
	 * @param _expected
	 *          Integer representing if the expected test outcome is True(1), False(2) or Error(-1).
	 * @param _json
	 *          The JSON being validated.
	 * @param _prototype
	 *          The "schema" that the prototype is being validated against.
	 */
	private void validateTestResults( int _expected, Object _json, Map _prototype ) {
		validateTestResults( _expected, "", _json, _prototype );
	}

	/**
	 * Validates the test results.
	 * 
	 * @param _expected
	 *          Integer representing if the expected test outcome is True(1), False(0) or Error(-1).
	 * @param _exceptionMessage
	 *          If the test is going to throw an error then the validator needs to know the error.
	 * @param _json
	 *          The JSON being validated.
	 * @param _prototype
	 *          The "schema" that the prototype is being validated against.
	 */
	private void validateTestResults( int _expected, String _exceptionMessage, Object _json, Map _prototype ) {

		if ( _expected == ERROR ) {
			try {
				jv.validate( _json, _prototype );
				fail( "An Exception was expected to be thrown but never was." );
			} catch ( Exception e ) {
				assertEquals( _exceptionMessage, e.getMessage() );
			}
		} else if ( _expected == FALSE ) {
			try {
				assertFalse( jv.validate( _json, _prototype ) );
			} catch ( Exception e ) {
				fail( "An unexpected Exception was thrown during the test. Message: " + e.getMessage() );
			}
		} else if ( _expected == TRUE ) {
			try {
				assertTrue( jv.validate( _json, _prototype ) );
			} catch ( Exception e ) {
				fail( "An unexpected Exception was thrown during the test. Message: " + e.getMessage() );
			}
		} else {
			fail( "The test values were set up incorrectly while creating the test case." );
		}
	}

	/**
	 * Takes in a name of a key and then creates the extra hashmaps and arrays that it needs to be
	 * placed into. This is useful if you are trying to create a single prototype with just one key
	 * value.
	 * 
	 * @param _key
	 *          The name of the key you are creating this map with. For instance key1
	 * @param _value
	 *          The map containing the fully formed value for the key value pair.
	 * @return A map with the key value pair properly placed into the prototype structure.
	 */
	private Map createKey( final String _key, final Map _value ) {

		return new HashMap() {

			{
				put( TYPE, STRUCT );
				put( "fields", new ArrayList() {

					{
						add( new HashMap() {

							{
								put( _key, _value );
							}
						} );
					}
				} );
			}
		};
	}
}