package jfxtras.labs.icalendaragenda.reviser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import javafx.collections.ObservableList;
import jfxtras.labs.icalendaragenda.ICalendarStaticComponents;
import jfxtras.labs.icalendaragenda.scene.control.agenda.editors.ChangeDialogOption;
import jfxtras.labs.icalendaragenda.scene.control.agenda.editors.revisor2.ReviserVEvent;
import jfxtras.labs.icalendaragenda.scene.control.agenda.editors.revisor2.SimpleRevisorFactory;
import jfxtras.labs.icalendarfx.VCalendar;
import jfxtras.labs.icalendarfx.components.VEvent;
import jfxtras.labs.icalendarfx.components.VPrimary;
import jfxtras.labs.icalendarfx.properties.component.change.DateTimeStamp;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.FrequencyType;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.RecurrenceRule2;
import jfxtras.labs.icalendarfx.properties.component.recurrence.rrule.byxxx.ByDay;
import jfxtras.labs.icalendarfx.properties.component.relationship.UniqueIdentifier;

/**
 * Tests editing and deleting components.  Uses a stub for the dialog callback to designate
 * the scope of the change - ONE, ALL or THIS_AND_FUTURE
 * 
 * @author David Bal
 *
 */
public class ReviseComponentTest
{
    @Test
    public void canEditAll()
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getDaily1();
        vComponents.add(vComponentOriginal);
        VEvent vComponentEdited = new VEvent(vComponentOriginal);

        vComponentEdited.setSummary("Edited summary");
        Temporal startOriginalRecurrence = LocalDateTime.of(2016, 5, 16, 10, 0);
        Temporal startRecurrence = LocalDateTime.of(2016, 5, 16, 9, 0);
        Temporal endRecurrence = LocalDateTime.of(2016, 5, 16, 10, 30);

        List<VCalendar> itipMessages = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentEdited))
                .withDialogCallback((m) -> ChangeDialogOption.ALL)
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal)
                .revise();
        assertEquals(1, itipMessages.size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));

        assertEquals(1, vComponents.size());
        VEvent myComponent = vComponents.get(0);
        assertEquals(LocalDateTime.of(2015, 11, 9, 9, 0), myComponent.getDateTimeStart().getValue());        
        assertEquals(LocalDateTime.of(2015, 11, 9, 10, 30), myComponent.getDateTimeEnd().getValue());        
        assertEquals("Edited summary", myComponent.getSummary().getValue());
    }
    
    @Test
    public void canEditWeeklyAll() // shift day of weekly
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getWeekly3();
        vComponents.add(vComponentOriginal);
        VEvent vComponentEdited = new VEvent(vComponentOriginal);

        Temporal startOriginalRecurrence = LocalDateTime.of(2016, 5, 16, 10, 0);
        Temporal startRecurrence = LocalDateTime.of(2016, 5, 17, 9, 0);
        Temporal endRecurrence = LocalDateTime.of(2016, 5, 17, 10, 30);

        List<VCalendar> itipMessages = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentEdited))
                .withDialogCallback((m) -> ChangeDialogOption.ALL)
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal)
                .revise();
        assertEquals(1, itipMessages.size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));
        
        assertEquals(1, vComponents.size());
        VEvent myComponent = vComponents.get(0);
        VEvent expectedVComponent = ICalendarStaticComponents.getWeekly3();
        expectedVComponent.setDateTimeStart(LocalDateTime.of(2015, 11, 10, 9, 0));
        expectedVComponent.setDateTimeEnd(LocalDateTime.of(2015, 11, 10, 10, 30));
        expectedVComponent.setRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.WEEKLY)
                        .withByRules(new ByDay(DayOfWeek.TUESDAY)));
        expectedVComponent.setSequence(1);
        assertEquals(expectedVComponent, myComponent);
    }
    
    @Test
    public void canEditMonthlyAll2() // shift day of weekly with ordinal
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getMonthly7();
        vComponents.add(vComponentOriginal);
        VEvent vComponentEdited = new VEvent(vComponentOriginal);

        Temporal startOriginalRecurrence = LocalDateTime.of(2016, 5, 16, 10, 0);
        Temporal startRecurrence = LocalDateTime.of(2016, 5, 17, 9, 0);
        Temporal endRecurrence = LocalDateTime.of(2016, 5, 17, 10, 30);

        List<VCalendar> itipMessages = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentEdited))
                .withDialogCallback((m) -> ChangeDialogOption.ALL)
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal)
                .revise();
        assertEquals(1, itipMessages.size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));

        assertEquals(1, vComponents.size());
        VEvent myComponent = vComponents.get(0);
        VEvent expectedVComponent = ICalendarStaticComponents.getWeekly3();
        expectedVComponent.setDateTimeStart(LocalDateTime.of(2015, 11, 17, 9, 0));
        expectedVComponent.setDateTimeEnd(LocalDateTime.of(2015, 11, 17, 10, 30));
        expectedVComponent.setRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.MONTHLY)
                        .withByRules(new ByDay(new ByDay.ByDayPair(DayOfWeek.TUESDAY, 3))));
        expectedVComponent.setSequence(1);
        assertEquals(expectedVComponent, myComponent);
    }       
    
    @Test
    public void canEditOne()
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getDaily1();
        vComponents.add(vComponentOriginal);
        VEvent vComponentEdited = new VEvent(vComponentOriginal);

        vComponentEdited.setSummary("Edited summary");
        Temporal startOriginalRecurrence = LocalDateTime.of(2016, 5, 16, 10, 0);
        Temporal startRecurrence = LocalDateTime.of(2016, 5, 16, 9, 0);
        Temporal endRecurrence = LocalDateTime.of(2016, 5, 16, 10, 30);

        ReviserVEvent reviser = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentEdited))
                .withDialogCallback((m) -> ChangeDialogOption.ONE)
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal);
        List<VCalendar> itipMessages = reviser.revise();
        assertEquals(1, itipMessages.size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));

        assertEquals(2, vComponents.size());
        Collections.sort(vComponents, VPrimary.VPRIMARY_COMPARATOR);
        VEvent myComponentRepeats = vComponents.get(0);
        
        assertEquals(vComponentOriginal, myComponentRepeats);
        VEvent myComponentIndividual = vComponents.get(1);
        assertEquals(LocalDateTime.of(2016, 5, 16, 9, 0), myComponentIndividual.getDateTimeStart().getValue());        
        assertEquals(LocalDateTime.of(2016, 5, 16, 10, 30), myComponentIndividual.getDateTimeEnd().getValue());        
        assertEquals(LocalDateTime.of(2015, 11, 9, 10, 0), myComponentRepeats.getDateTimeStart().getValue());        
        assertEquals(LocalDateTime.of(2015, 11, 9, 11, 0), myComponentRepeats.getDateTimeEnd().getValue()); 
        assertEquals("Edited summary", myComponentIndividual.getSummary().getValue());
                
        // Check child components
        assertEquals(Arrays.asList(myComponentIndividual), myComponentRepeats.recurrenceChildren());
        assertEquals(Collections.emptyList(), myComponentIndividual.recurrenceChildren());

        // 2nd edit - edit component with RecurrenceID (individual)
        VEvent vComponentEditedIndividual = new VEvent(myComponentIndividual);
        VEvent vComponentIndividualCopy = new VEvent(myComponentIndividual);
        
        vComponentEditedIndividual.setSummary("new summary");
        Temporal startOriginalRecurrence2 = LocalDateTime.of(2016, 5, 16, 9, 0);
        Temporal startRecurrence2 = LocalDateTime.of(2016, 5, 16, 12, 0);
        Temporal endRecurrence2 = LocalDateTime.of(2016, 5, 16, 13, 0);

        ReviserVEvent reviser2 = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentIndividualCopy))
                .withDialogCallback((m) -> null) // no dialog required
                .withEndRecurrence(endRecurrence2)
                .withStartOriginalRecurrence(startOriginalRecurrence2)
                .withStartRecurrence(startRecurrence2)
                .withVComponentEdited(vComponentEditedIndividual)
                .withVComponentOriginal(vComponentIndividualCopy);
        itipMessages = reviser2.revise();
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));
        assertEquals(2, vComponents.size());
        
        // Check child components
        VEvent myComponentIndividual2 = vComponents.get(1);
        assertEquals(Arrays.asList(myComponentIndividual2), myComponentRepeats.recurrenceChildren());
        assertEquals("new summary", myComponentIndividual2.getSummary().getValue());
        assertEquals(Collections.emptyList(), myComponentIndividual2.recurrenceChildren());
    }
    
    @Test
    public void canEditCancel()
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getDaily1();
        vComponents.add(vComponentOriginal);
        VEvent vComponentEdited = new VEvent(vComponentOriginal);
        
        vComponentEdited.setSummary("Edited summary");
        Temporal startOriginalRecurrence = LocalDateTime.of(2016, 5, 16, 10, 0);
        Temporal startRecurrence = LocalDateTime.of(2016, 5, 16, 9, 0);
        Temporal endRecurrence = LocalDateTime.of(2016, 5, 16, 10, 30);

        ReviserVEvent reviser = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentEdited))
                .withDialogCallback((m) -> ChangeDialogOption.CANCEL)
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal);
        List<VCalendar> itipMessages = reviser.revise();
        
        assertEquals(0, itipMessages.size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));

        assertEquals(1, vComponents.size());
        VEvent myComponent = vComponents.get(0);
        assertEquals(LocalDateTime.of(2015, 11, 9, 10, 0), myComponent.getDateTimeStart().getValue());        
        assertEquals(LocalDateTime.of(2015, 11, 9, 11, 0), myComponent.getDateTimeEnd().getValue());        
        assertEquals("Daily1 Summary", myComponent.getSummary().getValue());        
    }
    
    @Test // change date and time
    public void canEditThisAndFuture()
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getDaily1();
        vComponents.add(vComponentOriginal);
        VEvent vComponentEdited = new VEvent(vComponentOriginal);

        vComponentEdited.setSummary("Edited summary");
        Temporal startOriginalRecurrence = LocalDateTime.of(2016, 5, 16, 10, 0);
        Temporal startRecurrence = LocalDateTime.of(2016, 5, 16, 9, 0);
        Temporal endRecurrence = LocalDateTime.of(2016, 5, 16, 10, 30);

        ReviserVEvent reviser = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentEdited))
                .withDialogCallback((m) -> ChangeDialogOption.THIS_AND_FUTURE)
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal);
        List<VCalendar> itipMessages = reviser.revise();
        
        assertEquals(1, itipMessages.size());
        assertEquals(2, itipMessages.get(0).getVEvents().size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));
        
        assertEquals(2, vComponents.size());
        Collections.sort(vComponents, VPrimary.VPRIMARY_COMPARATOR);
        VEvent myComponentFuture = vComponents.get(1);
        VEvent myComponentOriginal = vComponents.get(0);
        assertEquals(LocalDateTime.of(2016, 5, 16, 9, 0), myComponentFuture.getDateTimeStart().getValue());        
        assertEquals(LocalDateTime.of(2016, 5, 16, 10, 30), myComponentFuture.getDateTimeEnd().getValue());        
        assertEquals("Edited summary", myComponentFuture.getSummary().getValue());
        
        assertEquals(LocalDateTime.of(2015, 11, 9, 10, 0), myComponentOriginal.getDateTimeStart().getValue());        
        assertEquals(LocalDateTime.of(2015, 11, 9, 11, 0), myComponentOriginal.getDateTimeEnd().getValue()); 
        Temporal until = ZonedDateTime.of(LocalDateTime.of(2016, 5, 15, 10, 0), ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z"));
        RecurrenceRule2 expectedRRule = ICalendarStaticComponents.getDaily1().getRecurrenceRule().getValue().withUntil(until);
        assertEquals(expectedRRule, myComponentOriginal.getRecurrenceRule().getValue());
    }
    
    @Test // change INTERVAL
    public void canEditThisAndFuture2()
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getDaily1();
        vComponents.add(vComponentOriginal);
        VEvent vComponentEdited = new VEvent(vComponentOriginal);
        
        vComponentEdited.setSummary("Edited summary");
        vComponentEdited.getRecurrenceRule().getValue().setInterval(2);
        Temporal startOriginalRecurrence = LocalDateTime.of(2016, 5, 16, 10, 0);
        Temporal startRecurrence = LocalDateTime.of(2016, 5, 16, 9, 0);
        Temporal endRecurrence = LocalDateTime.of(2016, 5, 16, 10, 30);

        ReviserVEvent reviser = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentEdited))
                .withDialogCallback((m) -> ChangeDialogOption.THIS_AND_FUTURE)
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal);
        List<VCalendar> itipMessages = reviser.revise();
        
        assertEquals(1, itipMessages.size());
        assertEquals(2, itipMessages.get(0).getVEvents().size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));
        
        assertEquals(2, vComponents.size());
        Collections.sort(vComponents, VPrimary.VPRIMARY_COMPARATOR);
        VEvent myComponentFuture = vComponents.get(1);
        VEvent myComponentOriginal = vComponents.get(0);
        assertEquals(LocalDateTime.of(2016, 5, 16, 9, 0), myComponentFuture.getDateTimeStart().getValue());        
        assertEquals(LocalDateTime.of(2016, 5, 16, 10, 30), myComponentFuture.getDateTimeEnd().getValue());        
        assertEquals("Edited summary", myComponentFuture.getSummary().getValue());
        
        assertEquals(LocalDateTime.of(2015, 11, 9, 10, 0), myComponentOriginal.getDateTimeStart().getValue());        
        assertEquals(LocalDateTime.of(2015, 11, 9, 11, 0), myComponentOriginal.getDateTimeEnd().getValue()); 
        Temporal until = ZonedDateTime.of(LocalDateTime.of(2016, 5, 15, 10, 0), ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z"));
        RecurrenceRule2 expectedRRule = ICalendarStaticComponents.getDaily1().getRecurrenceRule().getValue().withUntil(until);
        assertEquals(expectedRRule, myComponentOriginal.getRecurrenceRule().getValue());
    }
    
    
    @Test
    public void canChangeToWholeDayAll()
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getDaily1();
        vComponents.add(vComponentOriginal);
        VEvent vComponentEdited = new VEvent(vComponentOriginal);

        vComponentEdited.setSummary("Edited summary");
        Temporal startOriginalRecurrence = LocalDateTime.of(2016, 5, 16, 10, 0);
        Temporal startRecurrence = LocalDate.of(2016, 5, 15); // shifts back 1 day
        Temporal endRecurrence = LocalDate.of(2016, 5, 16);

        ReviserVEvent reviser = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentEdited))
                .withDialogCallback((m) -> ChangeDialogOption.ALL)
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal);
        List<VCalendar> itipMessages = reviser.revise();
        
        assertEquals(1, itipMessages.size());
        assertEquals(1, itipMessages.get(0).getVEvents().size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));

        assertEquals(1, vComponents.size());
        VEvent myComponent = vComponents.get(0);
        assertEquals(LocalDate.of(2015, 11, 8), myComponent.getDateTimeStart().getValue());        
        assertEquals(LocalDate.of(2015, 11, 9), myComponent.getDateTimeEnd().getValue());        
        assertEquals("Edited summary", myComponent.getSummary().getValue());
    }
    
    @Test
    public void canChangOneWholeDayToTimeBased()
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getDaily1();
        vComponents.add(vComponentOriginal);
        VEvent vComponentEdited = new VEvent(vComponentOriginal);
        vComponentEdited.setSummary("Edited summary");

        Temporal startOriginalRecurrence = LocalDateTime.of(2016, 5, 16, 10, 0);
        Temporal startRecurrence = LocalDate.of(2016, 5, 16);
        Temporal endRecurrence = LocalDate.of(2016, 5, 17);

        ReviserVEvent reviser = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentOriginal))
                .withDialogCallback((m) -> ChangeDialogOption.ONE)
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal);
        List<VCalendar> itipMessages = reviser.revise();
        
        assertEquals(1, itipMessages.size());
        assertEquals(1, itipMessages.get(0).getVEvents().size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));

        assertEquals(2, vComponents.size());
        Collections.sort(vComponents, VPrimary.VPRIMARY_COMPARATOR);
        VEvent repeatComponent = vComponents.get(0);
        VEvent individualComponent = vComponents.get(1);
        assertEquals(LocalDate.of(2016, 5, 16), individualComponent.getDateTimeStart().getValue());        
        assertEquals(LocalDate.of(2016, 5, 17), individualComponent.getDateTimeEnd().getValue());        
        assertEquals(LocalDateTime.of(2016, 5, 16, 10, 0), individualComponent.getRecurrenceId().getValue());        
        assertEquals("Edited summary", individualComponent.getSummary().getValue());
        assertEquals(repeatComponent, vComponentOriginal);
        assertNull(individualComponent.getRecurrenceRule());
    }
    
    @Test
    public void canChangeWholeDayToTimeBasedThisAndFuture()
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getWholeDayDaily1();
        VEvent vComponentEdited = new VEvent(vComponentOriginal);
        vComponents.add(vComponentEdited);
        vComponentEdited.setSummary("Edited summary");

        Temporal startOriginalRecurrence = LocalDate.of(2016, 5, 16);
        Temporal startRecurrence = LocalDateTime.of(2016, 5, 16, 9, 0);
        Temporal endRecurrence = LocalDateTime.of(2016, 5, 16, 10, 0);

        ReviserVEvent reviser = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentOriginal))
                .withDialogCallback((m) -> ChangeDialogOption.THIS_AND_FUTURE)
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal);
        List<VCalendar> itipMessages = reviser.revise();
        
        assertEquals(1, itipMessages.size());
        assertEquals(2, itipMessages.get(0).getVEvents().size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));

        assertEquals(2, vComponents.size());
        Collections.sort(vComponents, VPrimary.VPRIMARY_COMPARATOR);
        VEvent newVComponentOriginal = vComponents.get(0);
        VEvent newVComponentFuture = vComponents.get(1);

        VEvent expectedVComponentOriginal = ICalendarStaticComponents.getWholeDayDaily1();
        RecurrenceRule2 rrule = expectedVComponentOriginal.getRecurrenceRule().getValue();
        rrule.setUntil(LocalDate.of(2016, 5, 15));
        
        assertEquals(expectedVComponentOriginal, newVComponentOriginal);

        VEvent expectedVComponentFuture = ICalendarStaticComponents.getWholeDayDaily1();
        expectedVComponentFuture.setDateTimeStart(LocalDateTime.of(2016, 5, 16, 9, 0));
        expectedVComponentFuture.setDateTimeEnd(LocalDateTime.of(2016, 5, 16, 10, 0));
        expectedVComponentFuture.setUniqueIdentifier(new UniqueIdentifier(newVComponentFuture.getUniqueIdentifier()));
        expectedVComponentFuture.setDateTimeStamp(new DateTimeStamp(newVComponentFuture.getDateTimeStamp()));
        expectedVComponentFuture.setSummary("Edited summary");
        expectedVComponentFuture.withRelatedTo(vComponentOriginal.getUniqueIdentifier().getValue());
        expectedVComponentFuture.setSequence(1);
        
        assertEquals(expectedVComponentFuture, newVComponentFuture);
    }
    
    @Test
    public void canAddRRuleToAll()
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getIndividualZoned();
        VEvent vComponentEdited = new VEvent(vComponentOriginal);
        vComponents.add(vComponentEdited);
        
        vComponentEdited.setSummary("Edited summary");
        vComponentEdited.setRecurrenceRule(new RecurrenceRule2()
                        .withFrequency(FrequencyType.WEEKLY)
                        .withByRules(new ByDay(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)));

        Temporal startOriginalRecurrence = ZonedDateTime.of(LocalDateTime.of(2015, 11, 11, 10, 0), ZoneId.of("Europe/London"));
        Temporal startRecurrence = ZonedDateTime.of(LocalDateTime.of(2015, 11, 13, 9, 0), ZoneId.of("Europe/London"));
        Temporal endRecurrence = ZonedDateTime.of(LocalDateTime.of(2015, 11, 13, 10, 0), ZoneId.of("Europe/London"));

        ReviserVEvent reviser = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentEdited))
                .withDialogCallback((m) -> ChangeDialogOption.ALL)
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal);
        List<VCalendar> itipMessages = reviser.revise();
        
        assertEquals(1, itipMessages.size());
        assertEquals(1, itipMessages.get(0).getVEvents().size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));

        assertEquals(1, vComponents.size());
        VEvent myComponent = vComponents.get(0);
        assertEquals(ZonedDateTime.of(LocalDateTime.of(2015, 11, 13, 9, 0), ZoneId.of("Europe/London")), myComponent.getDateTimeStart().getValue());        
        assertEquals(ZonedDateTime.of(LocalDateTime.of(2015, 11, 13, 10, 0), ZoneId.of("Europe/London")), myComponent.getDateTimeEnd().getValue());    
        assertEquals("Edited summary", myComponent.getSummary().getValue());
    }
    
    @Test
    public void canEditIndividual()
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getIndividual1();
        VEvent vComponentEdited = new VEvent(vComponentOriginal);
        vComponents.add(vComponentEdited);

        vComponentEdited.setSummary("Edited summary");
        Temporal startOriginalRecurrence = LocalDateTime.of(2016, 5, 16, 10, 30);
        Temporal startRecurrence = LocalDateTime.of(2016, 5, 16, 11, 30);
        Temporal endRecurrence = LocalDateTime.of(2016, 5, 16, 12, 30);
        
        ReviserVEvent reviser = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentEdited))
                .withDialogCallback((m) -> null) // no dialog for edit individual
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal);
        List<VCalendar> itipMessages = reviser.revise();
        
        assertEquals(1, itipMessages.size());
        assertEquals(1, itipMessages.get(0).getVEvents().size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));

        assertEquals(1, vComponents.size());
        VEvent myComponent = vComponents.get(0);
        assertEquals(LocalDateTime.of(2016, 5, 16, 11, 30), myComponent.getDateTimeStart().getValue());        
        assertEquals(Duration.ofMinutes(60), myComponent.getDuration().getValue());    
        assertEquals("Edited summary", myComponent.getSummary().getValue());
    }
    
    @Test
    public void canEditIndividual2() // with other components present
    {
        VCalendar mainVCalendar = new VCalendar();
        final ObservableList<VEvent> vComponents = mainVCalendar.getVEvents();
        
        VEvent vComponentOriginal = ICalendarStaticComponents.getIndividual1();
        VEvent vComponentEdited = new VEvent(vComponentOriginal);
        vComponents.add(vComponentEdited);
        vComponents.add(ICalendarStaticComponents.getDaily1());

        vComponentEdited.setSummary("Edited summary");
        Temporal startOriginalRecurrence = LocalDateTime.of(2016, 5, 16, 10, 30);
        Temporal startRecurrence = LocalDateTime.of(2016, 5, 16, 11, 30);
        Temporal endRecurrence = LocalDateTime.of(2016, 5, 16, 12, 30);
        
        ReviserVEvent reviser = ((ReviserVEvent) SimpleRevisorFactory.newReviser(vComponentEdited))
                .withDialogCallback((m) -> null) // no dialog for edit individual
                .withEndRecurrence(endRecurrence)
                .withStartOriginalRecurrence(startOriginalRecurrence)
                .withStartRecurrence(startRecurrence)
                .withVComponentEdited(vComponentEdited)
                .withVComponentOriginal(vComponentOriginal);
        List<VCalendar> itipMessages = reviser.revise();
        
        assertEquals(1, itipMessages.size());
        assertEquals(1, itipMessages.get(0).getVEvents().size());
        itipMessages.forEach(inputVCalendar -> mainVCalendar.processITIPMessage(inputVCalendar));

        assertEquals(2, vComponents.size());
        VEvent myComponent = vComponents.get(1);
        assertEquals(LocalDateTime.of(2016, 5, 16, 11, 30), myComponent.getDateTimeStart().getValue());        
        assertEquals(Duration.ofMinutes(60), myComponent.getDuration().getValue());    
        assertEquals("Edited summary", myComponent.getSummary().getValue());
    }
}