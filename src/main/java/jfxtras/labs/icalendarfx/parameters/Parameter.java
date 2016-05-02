package jfxtras.labs.icalendarfx.parameters;

import javafx.beans.property.ObjectProperty;

/**
 * Every parameter requires the following methods:
 * toContentLine - make iCalendar string
 * getValue - return parameters value
 * isEqualsTo - checks equality between two parameters
 * parse - convert string into parameter - this method is in ParameterEnum
 * 
 * @author David Bal
 * @param <T> - parameter value
 *
 */
public interface Parameter<T> extends Comparable<Parameter<T>>
{    
    /**
     * The value of the parameter.
     * 
     * For example, in the below parameter:
     * CN=John Doe
     * The value is the String "John Doe"
     * 
     * Note: the value's object must have an overridden toString method that complies
     * with iCalendar content line output.
     */
    T getValue();
    
    /** object property of parameter's value */
    ObjectProperty<T> valueProperty();
  
    /** Set the value of this parameter */  
    void setValue(T value);
    
    
    /**
     * Returns the enumerated type for the parameter as it would appear in the iCalendar content line
     * Examples:
     * VALUE
     * TZID
     * 
     * @return - the parameter type
     */
    PropertyElement parameterType();
    
    /**
     * return parameter name-value pair string separated by an "="
     * for example:
     * LANGUAGE=en-US
     */
    String toContent();
}
