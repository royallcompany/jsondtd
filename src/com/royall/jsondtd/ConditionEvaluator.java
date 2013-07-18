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

	public boolean evaluate( Map<?, ?> _standardMap, JSONBlock _json ) throws PrototypeException {
		return evaluate( _standardMap, _json, ConditionType.OR );
	}

	public boolean evaluate( Map<?, ?> _standardMap, JSONBlock _json, ConditionType _cType ) throws PrototypeException {

		boolean startCondition;
		if ( _cType == ConditionType.OR )
			startCondition = false;
		else
			startCondition = true;

		boolean condition = startCondition;

		/*
		 * Loop over conditions If ConditionType.OR evaluate until we fine a true If
		 * ConditionType.AND evaluate until we fine a false
		 */
		Set<?> standardKeys = _standardMap.keySet();
		Iterator<?> standardIT = standardKeys.iterator();
		while ( condition == startCondition && standardIT.hasNext() ) {

			Object standardKey = standardIT.next();
			if ( !( standardKey instanceof String ) )
				throw new PrototypeException( "Keys for Struct must be a String in prototype" );
			String standardField = (String) standardKey;

			// Object to set req = true if matched
			Object standardObject = _standardMap.get( standardKey );

			if ( standardObject instanceof Map ) {
				if ( standardField.length() >= 3 && standardField.substring(0, 3).equalsIgnoreCase( "and" ) ) {
					condition = evaluate( (Map<?, ?>) standardObject, _json, ConditionType.AND );
				} else if ( standardField.length() >= 2 && standardField.substring(0, 2).equalsIgnoreCase( "or" ) ) {
					condition = evaluate( (Map<?, ?>) standardObject, _json, ConditionType.OR );
				} else {
					throw new PrototypeException( "Conditional type '" + standardField + " contains a struct. Only an 'AND' or an 'OR' can contain a struct of dynamic conditions. Expect String, Number, or boolean." );
				}
			} else {

				// Get appropriate level for object to be found ^^^&fieldname would look
				// three levels above current map
				int upLevelCount = 0;

				/*
				 * Parsing of the jCondition... should be of the form <condition>(^ x
				 * levels up to go)&field For example: lt^&key1 would mean that my
				 * dynamic condition will be a less than comparison to the parent
				 * struct's key1 value.
				 */
				int ampIndex;
				String comparisonType = "eqi";
				if ( ( ampIndex = standardField.indexOf( "\u0026" ) ) >= 0 ) {
					String jCondition = standardField.substring( 0, ampIndex );
					if ( !jCondition.isEmpty() ) {
						int carrotIndex = jCondition.indexOf( "^" );
						if ( carrotIndex < 0 )
							comparisonType = jCondition;
						else if ( carrotIndex > 0 ) {
							comparisonType = jCondition.substring( 0, carrotIndex );

							// Traverse up to specified location
							String jPath = jCondition.substring( carrotIndex );
							for ( int i = 0; i < jPath.length(); i++ ) {
								if ( jPath.charAt( i ) == '^' )
									upLevelCount++;
							}
						}
					}
					standardField = standardField.substring( ampIndex + 1 );
				}
				JSONBlock levelToExamin = _json;
				for ( int i = 0; i < upLevelCount; i++ ) {
					levelToExamin = levelToExamin.getParent();
					if ( levelToExamin == null )
						throw new PrototypeException( "Dynamic conditions for " + standardField + " went beyond root" );
				}

				// Check if block exists
				if ( !( levelToExamin.getBlock() instanceof Map ) )
					throw new PrototypeException( "Conditions for " + standardField + " can only examine a Map. Found: " + levelToExamin.getBlock().getClass().getName() );

				Map<?, ?> blockToExamin = (Map<?, ?>) levelToExamin.getBlock();

				Object valueToExamin = blockToExamin.get( standardField );
				if ( comparisonType.equalsIgnoreCase( "eqi" ) ) {
					condition = compareEquals( standardField, valueToExamin, standardObject, true );
				} else if ( comparisonType.equalsIgnoreCase( "neqi" ) ) {
					condition = !compareEquals( standardField, valueToExamin, standardObject, true );
				} else if ( comparisonType.equalsIgnoreCase( "eq" ) ) {
					condition = compareEquals( standardField, valueToExamin, standardObject, false );
				} else if ( comparisonType.equalsIgnoreCase( "neq" ) ) {
					condition = !compareEquals( standardField, valueToExamin, standardObject, false );
				} else if ( comparisonType.equalsIgnoreCase( "gt" ) ) {
					condition = compare( valueToExamin, standardObject, true, false );
				} else if ( comparisonType.equalsIgnoreCase( "gte" ) ) {
					condition = compare( valueToExamin, standardObject, true, true );
				} else if ( comparisonType.equalsIgnoreCase( "lt" ) ) {
					condition = compare( valueToExamin, standardObject, false, false );
				} else if ( comparisonType.equalsIgnoreCase( "lte" ) ) {
					condition = compare( valueToExamin, standardObject, false, true );
				} else if ( comparisonType.equalsIgnoreCase( "ex" ) ) {
					condition = blockToExamin.containsKey( standardField );
				} else if ( comparisonType.equalsIgnoreCase( "nex" ) ) {
					condition = !blockToExamin.containsKey( standardField );
				} else {
					throw new PrototypeException( "Unrecognized comparison type: " + comparisonType + " from dynamic Condition '" + standardField + "'" );
				}
			}
		} // While loop
		return condition;
	}

	/**
	 * Compares to numbers for less than, less than equal, greater than, greater
	 * than equal.
	 * 
	 * If standardIsMin = true | _valueToExamine > _standardObject
	 * :
	 * Otherwise | _valueToExamine < _standardObject
	 * 
	 * @param _standardObject The value from the prototype that the json value
	 *          will be compared against.
	 * @param _valueToExamine The value from the JSON that the prototype will be
	 *          compared against.
	 * @param _standardIsMin Boolean to determine if the greater than or less than
	 *          sign is being used. If standardIsMin is true then > is being used.
	 *          If it's false then the < sign is being used.
	 * @param _allowEqual Boolean to determine if the operation is greater than
	 *          equals OR greater than.
	 * @return Boolean of how _standardObject and _valueToExamine compare.
	 * @throws PrototypeException The _standardObject was not a Number and needs
	 *           to be fixed in the prototype.
	 */
	private boolean compare( Object _valueToExamine, Object _standardObject, boolean _standardIsMin, boolean _allowEqual ) throws PrototypeException {

		//Make sure the prototype is valid
		if ( !( _standardObject instanceof Number ) )
			throw new PrototypeException( "Dynamic condition must be a Number when using comparisons 'gt', 'gte', 'lt', and 'lte'" );

		boolean condition = false;
		
		if ( _valueToExamine instanceof Number ) {
			
			double doubleStandard = ( (Number) _standardObject ).doubleValue();
			double doubleToExamine = ( (Number) _valueToExamine ).doubleValue();
			
			if ( _standardIsMin ) {
				if ( _allowEqual )
					condition = doubleToExamine >= doubleStandard;
				else
					condition = doubleToExamine > doubleStandard;
			} else {
				if ( _allowEqual )
					condition = doubleToExamine <= doubleStandard;
				else
					condition = doubleToExamine < doubleStandard;
			}
		}
		return condition;
	}

	/**
	 * Compares to objects for equality. The only types accepted are String,
	 * Boolean and Number.
	 * 
	 * @param _standardField Name of the field from the prototype.
	 * @param _standardObject Object from the prototype to test for equality.
	 * @param _valueToExamine Object in the JSON that will be tested for equality.
	 * @param _ignoreCase Whether or not the method should ignore case for String
	 *          comparisons.
	 * @return If the _standardObject and _valueToExamine were equal.
	 * @throws PrototypeException The value of _standardObject was not a String,
	 *           Boolean or Number. This needs to be fixed in the prototype.
	 */
	private boolean compareEquals( String _standardField, Object _valueToExamine, Object _standardObject, boolean _ignoreCase ) throws PrototypeException {

		boolean condition = false;

		if ( _standardObject == null ) {
			condition = _valueToExamine == null;
		} else if ( _standardObject instanceof String && _valueToExamine instanceof String ) {
			if ( _ignoreCase )
				condition = ( (String) _standardObject ).equalsIgnoreCase( (String) _valueToExamine );
			else
				condition = ( (String) _standardObject ).equals( (String) _valueToExamine );
		} else if ( _standardObject instanceof Number && _valueToExamine instanceof Number ) {
			condition = ( (Number) _standardObject ).doubleValue() == ( (Number) _valueToExamine ).doubleValue();
		} else if ( _standardObject instanceof Boolean && _valueToExamine instanceof Boolean ) {
			condition = ( (boolean) (Boolean) _standardObject ) == ( (boolean) (Boolean) _valueToExamine );
		} else if ( !( _standardObject instanceof String ) && !( _standardObject instanceof Number ) && !( _standardObject instanceof Boolean ) ) {
			// Only throw the prototype exception if something was wrong with the
			// standardObject i.e, the prototype value.
			throw new PrototypeException( "Dynamic condition '" + _standardField + "' contains a " + _standardObject.getClass().getName() + " Expected String, Number, or Boolean." );
		}
		return condition;
	}

}
