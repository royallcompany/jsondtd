package com.royall.jsondtd;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bson.types.ObjectId;
import org.junit.Test;


public class JSONValidatorTest {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void test() throws Exception {

		final TestObject testObject = new TestObject("correct value in Test Object");
		
		@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
		Object json = new HashMap() {{
			put("key1", "value1");
			put("key2", "value2");
			put("key3", new ArrayList() {{
				add("item1");
				add("item2");
			}});
			put("key4", new HashMap() {{
				put("key6", "value5");
			}});
			put("key10", new ObjectId());
			put("key11", new java.util.Date());
			put("key12", testObject);
		}};
		
		@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
		Map prototype = new HashMap() {{
			put("type", "struct");
			put("req", true);
			put("fields", new ArrayList() {{
				add(new HashMap() {{
					put("key1", new HashMap() {{
						put("type", "simple");
						put("req", true);
					}});
					put("key2", new HashMap() {{
						put("type", "string");
						put("req", false);
					}});
					put("key3", new HashMap() {{
						put("type", "array");
						put("req", true);
						put("min", 1);
						put("children", new HashMap() {{
							put("type", "simple");
						}});
					}});
					put("key5", new HashMap() {{
						put("type", "string");
						put("req", false);
						put("def", "default string");
					}});
					put("key4", new HashMap() {{
						put("type", "struct");
						put("req", false);
						put("fields", new ArrayList() {{
							add(new HashMap(){{
								put("key6", new HashMap() {{
									put("type", "any");
									put("req", new HashMap() {{
										put("^&key2", "value2");
									}});
								}});
							}});
						}});
					}});
					put("key10", new HashMap() {{
						put("class", "Date,org.bson.types.ObjectId");
						put("req", true);
					}});
					put("key11", new HashMap() {{
						put("type", "Date");
						put("req", true);
						put(JSONValidator.KEY_DATE_MUST_BE_BEFORE, new java.util.Date());
					}});
					put("key12", new HashMap() {{
						put("class", "com.royall.jsondtd.TestObject");
						put(JSONValidator.KEY_NORMALIZE, "com.royall.jsondtd.TestNormalizer");
						put(JSONValidator.KEY_DENORMALIZE, "com.royall.jsondtd.TestNormalizer");
						put("type", "struct");
						put("fields", new HashMap() {{
							put("testKey", new HashMap() {{
								put("type", "string");
								put("req", true);
							}});
						}});
					}});
					put("key13", new HashMap() {{
						put("defitem", "now");
					}});
				}});
			}});
		}};

		JSONFormatter formatter = new JSONFormatter();

		System.out.println("JSON: " + formatter.parseObject(json) );
		
		System.out.println("Prototype: " + formatter.parseObject(prototype) );
		
		JSONValidator jv = new JSONValidator();
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
		System.out.println("Result: " + formatter.parseObject(jv.getReturnJson()) );
		assertTrue(result);
		
		System.out.println();
		if(jv.getReturnJson() instanceof Map && ((Map)jv.getReturnJson()).get("key12") instanceof TestObject) {
			System.out.println(((TestObject)((Map)jv.getReturnJson()).get("key12")).id);
		}
		
	}

}
