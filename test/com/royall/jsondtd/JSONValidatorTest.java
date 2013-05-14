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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class JSONValidatorTest {

	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	@Test
	public void test() throws Exception {

		final TestObject testObject = new TestObject("correct value in Test Object");

		Object json = new HashMap() {
			{
				put("key1", "value1");
				put("key2", "value2");
				put("key3", new ArrayList() {
					{
						add("item1");
						add("item2");
					}
				});
				put("key4", new HashMap() {
					{
						put("key6", "value5");
					}
				});
				put("key10", new TestObject("id"));
				put("key11", new java.util.Date());
				put("key12", testObject);
				put("key14", "good@email.com");
				put("key15", 10);
			}
		};

		Map prototype = new HashMap() {
			{
				put("type", "struct");
				put("req", true);
				put("fields", new ArrayList() {
					{
						add(new HashMap() {
							{
								put("key1", new HashMap() {
									{
										put("type", "simple");
										put("req", true);
									}
								});
								put("key2", new HashMap() {
									{
										put("type", "string");
										put("req", false);
									}
								});
								put("key3", new HashMap() {
									{
										put("type", "array");
										put("req", true);
										put("min", 1);
										put("children", new HashMap() {
											{
												put("type", "simple");
											}
										});
									}
								});
								put("key5", new HashMap() {
									{
										put("type", "string");
										put("req", false);
										put("def", "default string");
									}
								});
								put("key4", new HashMap() {
									{
										put("type", "struct");
										put("req", false);
										put("fields", new ArrayList() {
											{
												add(new HashMap() {
													{
														put("key6", new HashMap() {
															{
																put("type", "any");
																put("req", new HashMap() {
																	{
																		put("eq^&key2", "value2");
																	}
																});
															}
														});
													}
												});
											}
										});
									}
								});
								put("key10", new HashMap() {
									{
										put("class", "Date,com.royall.jsondtd.TestObject");
										put("req", true);
									}
								});
								put("key11", new HashMap() {
									{
										put("type", "Date");
										put("req", true);
										put(JSONValidator.KEY_DATE_MUST_BE_BEFORE, new java.util.Date());
									}
								});
								put("key12", new HashMap() {
									{
										put("class", "com.royall.jsondtd.TestObject");
										put(JSONValidator.KEY_NORMALIZE, "com.royall.jsondtd.TestNormalizer");
										put(JSONValidator.KEY_DENORMALIZE, "com.royall.jsondtd.TestNormalizer");
										put("type", "struct");
										put("fields", new HashMap() {
											{
												put("testKey", new HashMap() {
													{
														put("type", "string");
														put("req", true);
													}
												});
											}
										});
									}
								});
								put("key13", new HashMap() {
									{
										put("defitem", "now");
									}
								});
								put("key14", new HashMap() {
									{
										put("type", "email");
									}
								});
								put("key15", new HashMap() {
									{
										put("type", "number");
										put("min", 9);
										put("max", 19);
									}
								});
							}
						});
					}
				});
			}
		};

		JSONFormatter formatter = new JSONFormatter();

		System.out.println("JSON: " + formatter.parseObject(json));

		System.out.println("Prototype: " + formatter.parseObject(prototype));

		JSONValidator jv = new JSONValidator();
		jv.addCustomType("email", new HashMap() {
			{
				put("type", "string");
				put("regex", "^([\\w-.]+)@(([([0-9]{1,3}.){3}[0-9]{1,3}])|(([\\w-]+.)+)([a-zA-Z]{2,4}))$");
			}
		});
		boolean result;
		try {
			result = jv.validate(json, prototype);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		System.out.println(result);
		System.out.println(jv.getFailMessage());

		System.out.println();
		System.out.println("Result: " + formatter.parseObject(jv.getReturnJson()));
		assertTrue(result);

		System.out.println();
		if (jv.getReturnJson() instanceof Map && ((Map) jv.getReturnJson()).get("key12") instanceof TestObject) {
			System.out.println(((TestObject) ((Map) jv.getReturnJson()).get("key12")).id);
		}

	}

}
