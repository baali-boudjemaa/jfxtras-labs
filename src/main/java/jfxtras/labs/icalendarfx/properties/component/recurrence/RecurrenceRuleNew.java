package jfxtras.labs.icalendarfx.properties.component.recurrence;

import jfxtras.labs.icalendarfx.components.DaylightSavingTime;
import jfxtras.labs.icalendarfx.components.StandardTime;
import jfxtras.labs.icalendarfx.components.VEventNew;
import jfxtras.labs.icalendarfx.components.VJournal;
import jfxtras.labs.icalendarfx.components.VTodo;
import jfxtras.labs.icalendarfx.properties.PropertyBase;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.RecurrenceRule3;

/**
 * RRULE
 * Recurrence Rule
 * RFC 5545 iCalendar 3.8.5.3, page 122.
 * 
 * This property defines a rule or repeating pattern for
 * recurring events, to-dos, journal entries, or time zone definitions.
 * 
 * Produces a stream of start date/times after applying all modification rules.
 * 
 * @author David Bal
 * @see VEventNew
 * @see VTodo
 * @see VJournal
 * @see DaylightSavingTime
 * @see StandardTime
 */
public class RecurrenceRuleNew extends PropertyBase<RecurrenceRule3, RecurrenceRuleNew>
{
//    public RecurrenceRule(CharSequence contentLine)
//    {
//        super(contentLine);
//    }

    public RecurrenceRuleNew(RecurrenceRule3 value)
    {
        super(value);
    }
    
    public RecurrenceRuleNew()
    {
        super();
    }

    public RecurrenceRuleNew(RecurrenceRuleNew source)
    {
        super(source);
    }

    public static RecurrenceRuleNew parse(String propertyContent)
    {
        RecurrenceRuleNew property = new RecurrenceRuleNew();
        property.parseContent(propertyContent);
        return property;
    }
}