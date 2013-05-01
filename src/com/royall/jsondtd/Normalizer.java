package com.royall.jsondtd;

public interface Normalizer {

	/**
	 * Used to treat any Object as a an Object that can be handled by the JSONValidator Map, List, Date, String, etc.
	 * 
	 * @param _objectToNormalize
	 * @return
	 */
	public Object normalize(Object _objectToNormalize);
	
	/** 
	 * Used to return a normalized Object to it's original form. 
	 * 
	 * The normalized Object may have been modified by the JSONValidator by setting defaults.
	 * 
	 * @param _objectToDenormalize
	 * @return
	 */
	public Object denormalize(Object _objectToDenormalize);
}
