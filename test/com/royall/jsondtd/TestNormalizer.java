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
