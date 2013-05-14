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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.royall.jsondtd.exceptions.PrototypeException;

public class ConditionEvaluator {

	private enum ConditionType {
		OR, AND
	}

	public ConditionEvaluator() {
	}

	public boolean evaluate(Map<?, ?> _standardMap, JSONBlock _json) throws PrototypeException {
		return evaluate(_standardMap, _json, ConditionType.OR);
	}

	public boolean evaluate(Map<?, ?> _standardMap, JSONBlock _json, ConditionType _cType) throws PrototypeException {

		boolean startCondition;
		if (_cType == ConditionType.OR)
			startCondition = false;
		else
			startCondition = true;

		boolean condition = startCondition;

		/*
		 * Loop over conditions If ConditionType.OR evaluate until we fine a true If ConditionType.AND evaluate until we fine a false
		 */
		Set<?> standardKeys = _standardMap.keySet();
		Iterator<?> standardIT = standardKeys.iterator();
		while (condition == startCondition && standardIT.hasNext()) {

			Object standardKey = standardIT.next();
			if (!(standardKey instanceof String))
				throw new PrototypeException("Keys for Struct must be a String in prototype");
			String standardField = (String) standardKey;

			// Object to set req = true if matched
			Object standardObject = _standardMap.get(standardKey);

			if (standardObject instanceof Map) {
				if (standardField.equalsIgnoreCase("and")) {
					condition = evaluate((Map<?, ?>) standardObject, _json, ConditionType.AND);
				} else if (standardField.equalsIgnoreCase("or")) {
					condition = evaluate((Map<?, ?>) standardObject, _json, ConditionType.OR);
				} else {
					throw new PrototypeException("Conditional type '" + standardField + " contains a struct. Only an 'AND' or an 'OR' can contain a struct of dynamic conditions. Expect String, Number, or boolean.");
				}
			} else {

				// Get appropriate level for object to be found ^^^&fieldname would look three levels above current map
				int upLevelCount = 0;

				/*
				 * Parsing of the jCondition... should be of the form <condition>(^ x levels up to go)&field 
				 * For example: lt^&key1 would mean that my dynamic condition will be a less than comparison to the parent struct's key1 value.
				 */
				int ampIndex;
				String comparisonType = "eqi";
				if ((ampIndex = standardField.indexOf("\u0026")) >= 0) {
					String jCondition = standardField.substring(0, ampIndex);
					if (!jCondition.isEmpty()) {
						int carrotIndex = jCondition.indexOf("^");
						if (carrotIndex < 0)
							comparisonType = jCondition;
						else if (carrotIndex > 0) {
							comparisonType = jCondition.substring(0, carrotIndex);

							// Traverse up to specified location
							String jPath = jCondition.substring(carrotIndex);
							for (int i = 0; i < jPath.length(); i++) {
								if (jPath.charAt(i) == '^')
									upLevelCount++;
							}
						}
					}
					standardField = standardField.substring(ampIndex + 1);
				}
				JSONBlock levelToExamin = _json;
				for (int i = 0; i < upLevelCount; i++) {
					levelToExamin = levelToExamin.getParent();
					if (levelToExamin == null)
						throw new PrototypeException("Dynamic conditions for " + standardField + " went beyond root");
				}

				// Check if block exists
				if (!(levelToExamin.getBlock() instanceof Map))
					throw new PrototypeException("Conditions for " + standardField + " can only examine a Map. Found: " + levelToExamin.getBlock().getClass().getName());

				Map<?, ?> blockToExamin = (Map<?, ?>) levelToExamin.getBlock();

				if (standardObject == null) {
					// Not expecting Object to be there...
					condition = !blockToExamin.containsKey(standardField);
				} else {
					Object valueToExamin = blockToExamin.get(standardField);
					if (comparisonType.equalsIgnoreCase("eqi")) {
						condition = compareEquals(standardField, standardObject, valueToExamin, true);
					} else if (comparisonType.equalsIgnoreCase("neqi")) {
						condition = !compareEquals(standardField, standardObject, valueToExamin, true);
					} else if (comparisonType.equals("eq")) {
						condition = compareEquals(standardField, standardObject, valueToExamin, false);
					} else if (comparisonType.equalsIgnoreCase("neqi")) {
						condition = !compareEquals(standardField, standardObject, valueToExamin, false);
					} else if (comparisonType.equalsIgnoreCase("gt")) {
						condition = compare(standardObject, valueToExamin, true, false);
					} else if (comparisonType.equalsIgnoreCase("gte")) {
						condition = compare(standardObject, valueToExamin, true, true);
					} else if (comparisonType.equalsIgnoreCase("lt")) {
						condition = compare(standardObject, valueToExamin, false, false);
					} else if (comparisonType.equalsIgnoreCase("lte")) {
						condition = compare(standardObject, valueToExamin, false, true);
					} else {
						throw new PrototypeException("Unrecognized comparison type: " + comparisonType + " from dynamic Condition '" + standardField + "'");
					}
				}
			}
		} // While loop
		return condition;
	}

	private boolean compare(Object _standardObject, Object _valueToExamin, boolean _standardIsMin, boolean _allowEqual) throws PrototypeException {
		if (!(_standardObject instanceof Number))
			throw new PrototypeException("Dynamic condition must be a Number when using comparisons 'gt', 'gte', 'lt', and 'lte'");
		boolean condition = false;

		double standard = ((Number) _standardObject).doubleValue();
		if (_valueToExamin instanceof Number) {
			double doubleToExamin = ((Number) _valueToExamin).doubleValue();
			if (_standardIsMin) {
				if (_allowEqual)
					condition = doubleToExamin >= standard;
				else
					condition = doubleToExamin > standard;
			} else {
				if (_allowEqual)
					condition = doubleToExamin <= standard;
				else
					condition = doubleToExamin < standard;
			}
		}

		return condition;
	}

	private boolean compareEquals(String _standardField, Object _standardObject, Object _valueToExamin, boolean _ignoreCase) throws PrototypeException {
		boolean condition = false;

		if (_standardObject instanceof String && _valueToExamin instanceof String) {
			if (_ignoreCase)
				condition = ((String) _standardObject).equalsIgnoreCase((String) _valueToExamin);
			else
				condition = ((String) _standardObject).equals((String) _valueToExamin);
		} else if (_standardObject instanceof Number && _valueToExamin instanceof Number) {
			condition = ((Number) _standardObject).doubleValue() == ((Number) _valueToExamin).doubleValue();
		} else if (_standardObject instanceof Boolean && _valueToExamin instanceof Boolean) {
			condition = ((boolean) (Boolean) _standardObject) == ((boolean) (Boolean) _valueToExamin);
		} else if (!(_standardObject instanceof String) && !(_standardObject instanceof Number) && !(_standardObject instanceof Boolean)) {
			throw new PrototypeException("Dynamic condition '" + _standardField + "' contains a " + _standardObject.getClass().getName() + " Expected String, Number, or Boolean.");
		}
		return condition;
	}

}
