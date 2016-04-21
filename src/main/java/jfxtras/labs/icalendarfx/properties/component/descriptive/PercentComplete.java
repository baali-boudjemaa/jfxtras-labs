package jfxtras.labs.icalendarfx.properties.component.descriptive;

import jfxtras.labs.icalendarfx.components.DaylightSavingTime;
import jfxtras.labs.icalendarfx.components.StandardTime;
import jfxtras.labs.icalendarfx.components.VEventNewInt;
import jfxtras.labs.icalendarfx.components.VFreeBusy;
import jfxtras.labs.icalendarfx.components.VJournalInt;
import jfxtras.labs.icalendarfx.components.VTodoInt;
import jfxtras.labs.icalendarfx.properties.PropertyBaseAltText;

/**
 * COMMENT
 * RFC 5545 iCalendar 3.8.1.4. page 83
 * 
 * This property specifies non-processing information intended
 * to provide a comment to the calendar user
 * 
 * Example:
 * COMMENT:The meeting really needs to include both ourselves
 *  and the customer. We can't hold this meeting without them.
 *  As a matter of fact\, the venue for the meeting ought to be at
 *  their site. - - John
 *  
 * @author David Bal
 * 
 * The property can be specified in following components:
 * @see VEventNewInt
 * @see VTodoInt
 * @see VJournalInt
 * @see VFreeBusy
 * @see StandardTime
 * @see DaylightSavingTime
 */
public class PercentComplete extends PropertyBaseAltText<String, PercentComplete>
{
    public PercentComplete(CharSequence contentLine)
    {
        super(contentLine);
    }
    
    public PercentComplete(PercentComplete source)
    {
        super(source);
    }
}
