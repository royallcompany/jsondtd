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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings ( { "unchecked", "rawtypes", "serial" })
public class ConditionEvaluatorTest {

	private static final String KEY_1 = "key1";
	private static final String KEY_2 = "key2";
	private static final String TYPE = JSONValidator.KEY_TYPE;
	private static final String REQUIRED = JSONValidator.KEY_REQ;
	private static final String FIELDS = JSONValidator.KEY_FIELDS;
	private static final String DEFINITION = JSONValidator.KEY_FIELD_DEFINITION;
	private static final String STRING = "string";
	private static final String STRUCT = "struct";
	private static final String ANY = "any";
	private static final int TRUE = 1;
	private static final int FALSE = 0;
	private static final int ERROR = -1;
	public static JSONValidator jv;

	@BeforeClass
	public static void before() {
		jv = new JSONValidator();
	}

	@Test
	public void testConditionEvaluator() {

		// Test Equals
		runConditionTest( "string", "eq^&key2", "string", TRUE, "" );
		runConditionTest( "incorrect", "eq^&key2", "string", FALSE, "" );
		runConditionTest( 1, "eq^&key2", 1, TRUE, "" );
		runConditionTest( 2, "eq^&key2", 1, FALSE, "" );
		runConditionTest( true, "eq^&key2", true, TRUE, "" );
		runConditionTest( false, "eq^&key2", true, FALSE, "" );
		runConditionTest( 1, "eq^&key2", "1", FALSE, "" );
		runConditionTest( "1", "eq^&key2", 1, FALSE, "" );
		runConditionTest( "StRiNg", "eq^&key2", "STRING", FALSE, "" );

		// Test Not Equals
		runConditionTest( "string", "neq^&key2", "string", FALSE, "" );
		runConditionTest( "incorrect", "neq^&key2", "string", TRUE, "" );
		runConditionTest( 1, "neq^&key2", 1, FALSE, "" );
		runConditionTest( 2, "neq^&key2", 1, TRUE, "" );
		runConditionTest( true, "neq^&key2", true, FALSE, "" );
		runConditionTest( false, "neq^&key2", true, TRUE, "" );
		runConditionTest( "false", "neq^&key2", true, TRUE, "" );
		runConditionTest( 1, "neq^&key2", "1", TRUE, "" );
		runConditionTest( "1", "neq^&key2", 1, TRUE, "" );
		runConditionTest( "StRiNg", "neq^&key2", "STRING", TRUE, "" );
		runConditionTest( "StRiNg", "neq^&key2", new Date(), ERROR, "Dynamic condition 'key2' contains a java.util.Date Expected String, Number, or Boolean." );

		// Test Equals Ignore Case
		runConditionTest( "string", "eqi^&key2", "string", TRUE, "" );
		runConditionTest( "STRING", "eqi^&key2", "StRiNg", TRUE, "" );

		// Test Not Equals Ignore Case
		runConditionTest( "string", "neqi^&key2", "string", FALSE, "" );
		runConditionTest( "StRiNg", "neqi^&key2", "STRING", FALSE, "" );

		// Test Greater Than
		runConditionTest( 2, "gt^&key2", 2, FALSE, "" );
		runConditionTest( 1, "gt^&key2", 2, FALSE, "" );
		runConditionTest( 2, "gt^&key2", 1, TRUE, "" );

		// Test Greater Than Equals
		runConditionTest( 2, "gte^&key2", 2, TRUE, "" );
		runConditionTest( 1, "gte^&key2", 2, FALSE, "" );
		runConditionTest( 2, "gte^&key2", 1, TRUE, "" );

		// Test Less Than
		runConditionTest( 2, "lt^&key2", 2, FALSE, "" );
		runConditionTest( 1, "lt^&key2", 2, TRUE, "" );
		runConditionTest( 2, "lt^&key2", 1, FALSE, "" );

		// Test Less Than Equals
		runConditionTest( 2, "lte^&key2", 2, TRUE, "" );
		runConditionTest( 1, "lte^&key2", 2, TRUE, "" );
		runConditionTest( 2, "lte^&key2", 1, FALSE, "" );
		runConditionTest( 1, "lte^&key2", new Date(), ERROR, "Dynamic condition must be a Number when using comparisons 'gt', 'gte', 'lt', and 'lte'" );

		runConditionTest( 1, "e&k", 2, ERROR, "Unrecognized comparison type: e from dynamic Condition 'k'" );
		runConditionTest( "string", "&", "another string", FALSE, "" );
		runConditionTest( "string", "^&", "another string", FALSE, "" );

		runConditionTest( "NaN", "gt^&key2", 2, FALSE, "" );

		runConditionTest( 2, "lte^&key2", null, ERROR, "Dynamic condition must be a Number when using comparisons 'gt', 'gte', 'lt', and 'lte'" );

	}

	/**
	 * Parameterized JSON and prototype creation for Condition compare and
	 * compareEquals tests.
	 * 
	 * @param _valueToExamine The value from the JSON
	 * @param _conditionStatement The condition statement being placed in the
	 *          Prototype
	 * @param _conditionValue The value that the valueToExamine will be compared
	 *          to.
	 * @param _expected This is an int for what the expectation of the test is. -1
	 *          = exception, 0 = false, 1 = true.
	 * @param _exceptionMessage If you put -1 as the expected parameter then you
	 *          need to give the method the message from the exception that will
	 *          be thrown.
	 */
	private void runConditionTest( final Object _valueToExamine, final String _conditionStatement, final Object _conditionValue, int _expected, String _exceptionMessage ) {

		// Create the JSON Object.
		Object json = new HashMap() {
			{
				// Leave an empty hash map.
				put( KEY_1, new HashMap() );
				// ValueToExamine
				put( KEY_2, _valueToExamine );
			}
		};
		// Create the Prototype.
		Map prototype = new HashMap() {
			{
				put( TYPE, STRUCT );
				put( REQUIRED, true );
				put( FIELDS, new ArrayList() {
					{
						add( new HashMap() {
							{
								put( KEY_1, new HashMap() {
									{
										put( DEFINITION, new HashMap() {
											{
												put( TYPE, STRUCT );
												put( FIELDS, new ArrayList() {
													{
														add( new HashMap() {
															{
																put( "key11", new HashMap() {
																	{
																		put( REQUIRED, new HashMap() {
																			{
																				// StandardObject
																				put( _conditionStatement, _conditionValue );
																			}
																		} );
																		put( DEFINITION, new HashMap() {
																			{
																				put( TYPE, ANY );
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
									}
								} );
							}
						} );
					}
				} );
			}
		};

		validateResult( _expected, _exceptionMessage, json, prototype );
	}

	/**
	 * Test OR with explicit "required" hashmap containing an "or" map.
	 */
	@Test
	public void testConditionEvaluator0() {

		// Create the JSON Object.
		Object json = new HashMap() {
			{
				put( "key2", "value2" );
				put( "key3", "value3" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {
			{
				put( TYPE, ANY );
				put( REQUIRED, new HashMap() {
					{
						put( "or", new HashMap() {
							{
								put( "eq&key2", "value2" );
								put( "eq&key3", "value3" );
							}
						} );
					}
				} );
			}
		} );

		validateResult( TRUE, "", json, prototype );
	}

	/**
	 * Test OR with implicit "required" hashmap containing statements.
	 */
	@Test
	public void testConditionEvaluator1() {

		// Create the JSON Object.
		Object json = new HashMap() {
			{
				put( "key2", "value2" );
				put( "key3", "value3" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {
			{
				put( TYPE, ANY );
				put( REQUIRED, new HashMap() {
					{
						put( "eq&key2", "value2" );
						put( "eq&key3", "value3" );
					}
				} );
			}
		} );

		validateResult( TRUE, "", json, prototype );
	}

	/**
	 * Test explicit "required" hashmap with "or" hashmap. This test should fail
	 * due to lack of key3.
	 */
	@Test
	public void testConditionEvaluator2() {

		// Create the JSON Object.
		Object json = new HashMap() {
			{
				put( "key2", "value2" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {
			{
				put( TYPE, ANY );
				put( REQUIRED, new HashMap() {
					{
						put( "or", new HashMap() {
							{
								put( "eq&key2", "value2" );
								put( "eq&key3", "value3" );
							}
						} );
					}
				} );
			}
		} );

		validateResult( TRUE, "", json, prototype );
	}

	/**
	 * Test explicit "required" hashmap with "or" hashmap. This test should fail
	 * due to lack of key2.
	 */
	@Test
	public void testConditionEvaluator3() {

		// Create the JSON Object.
		Object json = new HashMap() {
			{
				put( "key3", "value3" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {
			{
				put( TYPE, ANY );
				put( REQUIRED, new HashMap() {
					{
						put( "eq&key2", "value2" );
						put( "eq&key3", "value3" );
					}
				} );
			}
		} );

		validateResult( TRUE, "", json, prototype );
	}

	/**
	 * Test explicit "required" hashmap with "or" hashmap. This test should fail
	 * due to lack of key2 and key3.
	 */
	@Test
	public void testConditionEvaluator4() {

		// Create the JSON Object.
		Object json = new HashMap() {
			{
				put( "key4", "value4" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {
			{
				put( TYPE, ANY );
				put( REQUIRED, new HashMap() {
					{
						put( "or", new HashMap() {
							{
								put( "eq&key2", "value2" );
								put( "eq&key3", "value3" );
							}
						} );
					}
				} );
			}
		} );

		validateResult( FALSE, "", json, prototype );
	}

	/**
	 * Test explicit "required" hashmap with "and" hashmap.
	 */
	@Test
	public void testConditionEvaluator6() {

		// Create the JSON Object.
		Object json = new HashMap() {
			{
				put( "key2", "value2" );
				put( "key3", "value3" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {
			{
				put( TYPE, ANY );
				put( REQUIRED, new HashMap() {
					{
						put( "AND", new HashMap() {
							{
								put( "eq&key2", "value2" );
								put( "eq&key3", "value3" );
							}
						} );
					}
				} );
			}
		} );

		validateResult( TRUE, "", json, prototype );
	}

	/**
	 * Test explicit "required" hashmap with "and" hashmap. This test should fail
	 * due to lack of key3.
	 */
	@Test
	public void testConditionEvaluator7() {

		// Create the JSON Object.
		Object json = new HashMap() {
			{
				put( "key2", "value2" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {
			{
				put( TYPE, ANY );
				put( REQUIRED, new HashMap() {
					{
						put( "AND", new HashMap() {
							{
								put( "eq&key2", "value2" );
								put( "eq&key3", "value3" );
							}
						} );
					}
				} );
			}
		} );

		validateResult( FALSE, "", json, prototype );
	}

	/**
	 * Test explicit "required" hashmap with "and" hashmap. This test should fail
	 * due to lack of key2 & key3.
	 */
	@Test
	public void testConditionEvaluator8() {

		// Create the JSON Object.
		Object json = new HashMap() {
			{
				put( "key4", "value4" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {
			{
				put( TYPE, ANY );
				put( REQUIRED, new HashMap() {
					{
						put( "AND", new HashMap() {
							{
								put( "eq&key2", "value2" );
								put( "eq&key3", "value3" );
							}
						} );
					}
				} );
			}
		} );

		validateResult( FALSE, "", json, prototype );
	}

	/**
	 * Test null key in the required map.
	 */
	@Test
	public void testConditionEvaluator9() {

		// Create the JSON Object.
		Object json = new HashMap() {
			{
				put( "key2", "value2" );
				put( "key3", "value3" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {
			{
				put( TYPE, STRING );
				put( REQUIRED, new HashMap() {
					{
						put( null, "value2" );
					}
				} );
			}
		} );

		validateResult( ERROR, "Keys for Struct must be a String in prototype", json, prototype );
	}

	/**
	 * Test having a map inside of the required map that is not an "and" or an
	 * "or".
	 */
	@Test
	public void testConditionEvaluator10() {

		// Create the JSON Object.
		Object json = new HashMap() {
			{
				put( "key2", "value2" );
				put( "key3", "value3" );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {
			{
				put( TYPE, STRING );
				put( REQUIRED, new HashMap() {
					{
						put( "not and or or", new HashMap() {
							{
								put( "eq&key2", "value2" );
							}
						} );
					}
				} );
			}
		} );

		validateResult( ERROR, "Conditional type 'not and or or contains a struct. Only an 'AND' or an 'OR' can contain a struct of dynamic conditions. Expect String, Number, or boolean.", json, prototype );
	}

	/**
	 * Have a required field that requests a key too many levels up.
	 */
	@Test
	public void testConditionEvaluator11() {

		// Create the JSON Object.
		Object json = new HashMap() {
			{
				put( "key2", new HashMap() {
					{
						put( "junk", "more junk" );
					}
				} );
			}
		};
		// Create the Prototype.
		Map prototype = createKey( "key1", new HashMap() {
			{
				put( TYPE, STRING );
				put( REQUIRED, new HashMap() {
					{
						put( "or", new HashMap() {
							{
								put( "eq^^^^^&key2", "value2" );
							}
						} );
					}
				} );
			}
		} );

		validateResult( ERROR, "Dynamic conditions for key2 went beyond root", json, prototype );
	}

	/**
	 * Validates the test results. This method is backwards, if you put in true it
	 * will assert false. This is only useful for how this class is testing the
	 * required marker. If we expect that the validation will be true because the
	 * conditions result in true then the test case should actually be false
	 * because the thing that's required won't be there.
	 * 
	 * @param _expected Integer representing if the expected test outcome is
	 *          True(1), False(2) or Error(-1).
	 * @param _exceptionMessage If the test is going to throw an error then the
	 *          validator needs to know the error.
	 * @param _json The JSON being validated.
	 * @param _prototype The "schema" that the prototype is being validated
	 *          against.
	 */
	private void validateResult( int _expected, String _exceptionMessage, Object _json, Map _prototype ) {

		if ( _expected == ERROR ) {
			try {
				jv.validate( _json, _prototype );
				fail( "Test was expected to error out and did not." );
			} catch ( Exception e ) {
				assertEquals( _exceptionMessage, e.getMessage() );
			}
		} else if ( _expected == TRUE ) {
			try {
				assertFalse( jv.validate( _json, _prototype ) );
			} catch ( Exception e ) {
				fail( "An unexpected Exception was thrown during the test. Message: " + e.getMessage() );
			}
		} else if ( _expected == FALSE ) {
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
	 * Takes in a name of a key and then creates the extra hashmaps and arrays
	 * that it needs to be placed into. This is useful if you are trying to create
	 * a single prototype with just one key value.
	 * 
	 * @param _key The name of the key you are creating this map with. For
	 *          instance key1
	 * @param _value The map containing the fully formed value for the key value
	 *          pair.
	 * @return A map with the key value pair properly placed into the prototype
	 *         structure.
	 */
	private Map createKey( final String _key, final Map _value ) {

		return new HashMap() {
			{
				put( TYPE, STRUCT );
				put( REQUIRED, true );
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
