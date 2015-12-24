package jfxtras.labs.repeatagenda.internal.scene.control.skin.repeatagenda.base24hour.controller;


import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.EXDate;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.VComponent;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.rrule.RRule;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.rrule.byxxx.ByDay;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.rrule.byxxx.ByDay.ByDayPair;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.rrule.byxxx.Rule;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.rrule.freq.Frequency;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.rrule.freq.Frequency.FrequencyType;
import jfxtras.labs.repeatagenda.scene.control.repeatagenda.icalendar.rrule.freq.Weekly;

/**
 * Makes pane for selection some repeatable rules
 * @author David Bal
 *
 * @param <T> type of recurrence instance (such as Agenda.Appointment)
 */
public class RepeatableController<T>
{

final private static int EXCEPTION_CHOICE_LIMIT = 50;
final private static int INITIAL_COUNT = 10;
final private static int INITIAL_INTERVAL = 1;
    
private VComponent<T> vComponent;
private LocalDateTime dateTimeStartInstanceNew;

@FXML private ResourceBundle resources; // ResourceBundle that was given to the FXMLLoader

@FXML private CheckBox repeatableCheckBox;
@FXML private GridPane repeatableGridPane;
@FXML private ComboBox<Frequency.FrequencyType> frequencyComboBox;
@FXML private Spinner<Integer> intervalSpinner;
@FXML private Label frequencyLabel;
@FXML private Label eventLabel;
@FXML private Label weeklyLabel;
@FXML private HBox weeklyHBox;
@FXML private CheckBox sundayCheckBox;
@FXML private CheckBox mondayCheckBox;
@FXML private CheckBox tuesdayCheckBox;
@FXML private CheckBox wednesdayCheckBox;
@FXML private CheckBox thursdayCheckBox;
@FXML private CheckBox fridayCheckBox;
@FXML private CheckBox saturdayCheckBox;
private final ObservableList<DayOfWeek> dayOfWeekList = FXCollections.observableArrayList();
private final Map<BooleanProperty, DayOfWeek> checkBoxDayOfWeekMap = new HashMap<>();
private Map<DayOfWeek, BooleanProperty> checkBoxDayOfWeekMap2;
@FXML private VBox monthlyVBox;
@FXML private Label monthlyLabel;
private ToggleGroup monthlyGroup;
@FXML private RadioButton dayOfMonthRadioButton;
@FXML private RadioButton dayOfWeekRadioButton;
@FXML private DatePicker startDatePicker;
@FXML private RadioButton endNeverRadioButton;
@FXML private RadioButton endAfterRadioButton;
@FXML private RadioButton endOnRadioButton;
@FXML private Spinner<Integer> endAfterEventsSpinner;
@FXML private DatePicker endOnDatePicker;
private ToggleGroup endGroup;
@FXML private Label repeatSummaryLabel;

@FXML ComboBox<Temporal> exceptionComboBox;
@FXML Button addExceptionButton;
@FXML ListView<Temporal> exceptionsListView; // EXDATE date/times to be skipped (deleted events)
@FXML Button removeExceptionButton;


// DAY OF WEEK CHECKBOX LISTENER
private final ChangeListener<? super Boolean> dayOfWeekCheckBoxListener = (obs, oldSel, newSel) -> 
{
    System.out.println("trigger day of week:");
    DayOfWeek dayOfWeek = checkBoxDayOfWeekMap.get(obs);
    ByDay rule = (ByDay) vComponent
            .getRRule()
            .getFrequency()
            .getByRuleByType(Rule.ByRules.BYDAY);
    if (newSel)
    {
        rule.addDayOfWeek(dayOfWeek);
        dayOfWeekList.add(dayOfWeek);
    } else
    {
        rule.removeDayOfWeek(dayOfWeek);
        dayOfWeekList.remove(dayOfWeek);
    }
//    refreshSummary();
//    System.out.println("make exception " + obs);
//    makeExceptionDates();
};

// Listener for dayOfWeekRadioButton when frequency if monthly
private ChangeListener<? super Boolean> dayOfWeekButtonListener = (obs2, oldSel2, newSel2) -> 
{
    if (newSel2)
    {
        LocalDateTime start = dateTimeStartInstanceNew.with(TemporalAdjusters.firstDayOfMonth());
        int ordinalWeekNumber = 0;
        while (! dateTimeStartInstanceNew.isBefore(start))
        {
            ordinalWeekNumber++;
            start = start.plusWeeks(1);
        }
        DayOfWeek dayOfWeek = dateTimeStartInstanceNew.getDayOfWeek();
        Rule byDayRuleMonthly = new ByDay(new ByDayPair(dayOfWeek, ordinalWeekNumber));
        vComponent.getRRule().getFrequency().addByRule(byDayRuleMonthly);
    } else
    { // remove rule to reset to default behavior of repeat by day of month
        Rule r = vComponent.getRRule().getFrequency().getByRuleByType(Rule.ByRules.BYDAY);
        vComponent.getRRule().getFrequency().getByRules().remove(r);
    }
    refreshSummary();
    // makeExceptionDates();
};


//MAKE EXCEPTION DATES LISTENER
final private InvalidationListener makeExceptionDatesAndSummaryListener = (obs) -> 
{
   System.out.println("exceptions6:" + obs);
   refreshSummary();
   makeExceptionDates();
};
private void refreshSummary()
{
// System.out.println("refresh summary:");
 String summaryString = vComponent.getRRule().summary(vComponent.getDateTimeStart());
 repeatSummaryLabel.setText(summaryString);
}
private final ChangeListener<? super Boolean> neverListener = (obs, oldValue, newValue) ->
{
    if (newValue)
    {
        refreshSummary();
        System.out.println("make exception1 " + obs);
        makeExceptionDates();
    }
};

// FREQUENCY CHANGE LISTENER
private final ChangeListener<? super FrequencyType> frequencyListener = (obs, oldSel, newSel) -> 
{
    // Change Frequency if different.  Copy Interval, null ExDate
    if (vComponent.getRRule().getFrequency().frequencyType() != newSel)
    {
        Frequency newFrequency = newSel.newInstance();
        Integer interval = vComponent.getRRule().getFrequency().getInterval();
        if (interval > 1) newFrequency.setInterval(interval);
        vComponent.getRRule().setFrequency(newFrequency);
        vComponent.setExDate(null);
    }

    // Setup monthlyVBox and weeklyHBox setting visibility
    switch (newSel)
    {
    case DAILY:
    case YEARLY:
        break;
    case MONTHLY:
        Rule r = vComponent.getRRule().getFrequency().getByRuleByType(Rule.ByRules.BYDAY);
        vComponent.getRRule().getFrequency().getByRules().remove(r);
        dayOfMonthRadioButton.selectedProperty().set(true);
        dayOfWeekRadioButton.selectedProperty().set(false);
        break;
    case WEEKLY:
        System.out.println("dayOfWeekList0a " + dayOfWeekList);
        Rule r2 = vComponent.getRRule().getFrequency().getByRuleByType(Rule.ByRules.BYDAY);
        vComponent.getRRule().getFrequency().getByRules().remove(r2);
        if (dayOfWeekList.isEmpty())
        {
            DayOfWeek dayOfWeek = LocalDate.from(vComponent.getDateTimeStart()).getDayOfWeek();
            vComponent.getRRule().getFrequency().addByRule(new ByDay(dayOfWeek)); // add days already clicked
            checkBoxDayOfWeekMap2.get(dayOfWeek).set(true);
//            checkBoxDayOfWeekMap.entrySet().stream()
//                    .filter(c -> c.getValue().equals(dayOfWeek))
//                    .findFirst()
//                    .get()
//                    .getKey()
//                    .set(true);
        } else
        {
            vComponent.getRRule().getFrequency().addByRule(new ByDay(dayOfWeekList)); // add days already clicked            
        }
        System.out.println("dayOfWeekList0b " + dayOfWeekList);
        break;
    case SECONDLY:
    case MINUTELY:
    case HOURLY:
        throw new IllegalArgumentException("Frequency " + newSel + " not implemented");
    default:
        break;
    }
    
    // visibility of monthlyVBox & weeklyHBox
    setFrequencyVisibility(newSel);
    
    if (intervalSpinner.getValue() == 1) {
        frequencyLabel.setText(frequencyComboBox.valueProperty().get().toStringSingular());
    } else {
        frequencyLabel.setText(frequencyComboBox.valueProperty().get().toStringPlural());
    }
    
    // Make summary and exceptions
    refreshSummary();
    System.out.println("make exception2" + obs + " " + oldSel);
    makeExceptionDates();
};

private void setFrequencyVisibility(FrequencyType f)
{
    // Setup monthlyVBox and weeklyHBox setting visibility
    switch (f)
    {
    case DAILY:
    case YEARLY:
    {
        monthlyVBox.setVisible(false);
        monthlyLabel.setVisible(false);
        weeklyHBox.setVisible(false);
        weeklyLabel.setVisible(false);
        break;
    }
    case MONTHLY:
        monthlyVBox.setVisible(true);
        monthlyLabel.setVisible(true);
        weeklyHBox.setVisible(false);
        weeklyLabel.setVisible(false);
        break;
    case WEEKLY:
    {
        monthlyVBox.setVisible(false);
        monthlyLabel.setVisible(false);
        weeklyHBox.setVisible(true);
        weeklyLabel.setVisible(true);
        break;
    }
    case SECONDLY:
    case MINUTELY:
    case HOURLY:
        throw new IllegalArgumentException("Frequency " + f + " not implemented");
    default:
        break;
    }
}

// listen for changes to start date/time (type may change requiring new exception date choices)
private final ChangeListener<? super Temporal> dateTimeStartToExceptionChangeListener = (obs, oldValue, newValue) ->
{
    System.out.println("make exception3 " + obs);

    makeExceptionDates();
    // update existing exceptions
    if (! exceptionsListView.getItems().isEmpty())
    {
        List<Temporal> newItems = null;
        if (newValue.getClass().equals(LocalDate.class))
        {
            newItems = exceptionsListView.getItems()
                    .stream()
                    .map(d -> LocalDate.from(d))
                    .collect(Collectors.toList());
        } else if (newValue.getClass().equals(LocalDateTime.class))
        {
            LocalTime time = LocalDateTime.from(newValue).toLocalTime();
            newItems = exceptionsListView.getItems()
                    .stream()
                    .map(d -> LocalDate.from(d).atTime(time))
                    .collect(Collectors.toList());            
        }
        exceptionsListView.setItems(FXCollections.observableArrayList(newItems));
    }
};

// INITIALIZATION - runs when FXML is initialized
@FXML public void initialize()
{
    // REPEATABLE CHECKBOX
    repeatableCheckBox.selectedProperty().addListener((observable, oldSelection, newSelection) ->
    {
        if (newSelection)
        {
            removeExceptionListeners();
            if (vComponent.getRRule() == null)
            {
                // setup new default RRule
                RRule rRule = new RRule()
                        .withFrequency(new Weekly()
                        .withByRules(new ByDay(dateTimeStartInstanceNew.getDayOfWeek()))); // default RRule
                vComponent.setRRule(rRule);
                setInitialValues(vComponent);
                System.out.println("dayOfWeekList1" + dayOfWeekList);
            }
            System.out.println("dayOfWeekList2" + dayOfWeekList);
            vComponent.dateTimeStartProperty().addListener(dateTimeStartToExceptionChangeListener);
            repeatableGridPane.setDisable(false);
            startDatePicker.setDisable(false);
            addExceptionListeners();
            System.out.println("dayOfWeekList3" + dayOfWeekList);
        } else
        {
            vComponent.dateTimeStartProperty().removeListener(dateTimeStartToExceptionChangeListener);
            vComponent.setRRule(null);
            repeatableGridPane.setDisable(true);
            startDatePicker.setDisable(true);
        }
    });
    
    // DAY OF WEEK CHECK BOX LISTENERS (FOR WEEKLY)
    checkBoxDayOfWeekMap.put(sundayCheckBox.selectedProperty(), DayOfWeek.SUNDAY);
    checkBoxDayOfWeekMap.put(mondayCheckBox.selectedProperty(), DayOfWeek.MONDAY);
    checkBoxDayOfWeekMap.put(tuesdayCheckBox.selectedProperty(), DayOfWeek.TUESDAY);
    checkBoxDayOfWeekMap.put(wednesdayCheckBox.selectedProperty(), DayOfWeek.WEDNESDAY);
    checkBoxDayOfWeekMap.put(thursdayCheckBox.selectedProperty(), DayOfWeek.THURSDAY);
    checkBoxDayOfWeekMap.put(fridayCheckBox.selectedProperty(), DayOfWeek.FRIDAY);
    checkBoxDayOfWeekMap.put(saturdayCheckBox.selectedProperty(), DayOfWeek.SATURDAY);
    checkBoxDayOfWeekMap2 = checkBoxDayOfWeekMap.entrySet().stream().collect(Collectors.toMap(e -> e.getValue(), e -> e.getKey()));
    checkBoxDayOfWeekMap.entrySet().stream().forEach(entry -> entry.getKey().addListener(dayOfWeekCheckBoxListener));

    // DAY OF WEEK RAIO BUTTON LISTENER (FOR MONTHLY)
    dayOfWeekRadioButton.selectedProperty().addListener(dayOfWeekButtonListener);

    // Setup frequencyComboBox items
    frequencyComboBox.setItems(FXCollections.observableArrayList(FrequencyType.implementedValues()));
//    frequencyComboBox.getSelectionModel().select(FrequencyType.WEEKLY); // default selection
    frequencyComboBox.setConverter(Frequency.FrequencyType.stringConverter);

    // INTERVAL SPINNER
    intervalSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, INITIAL_INTERVAL));
    intervalSpinner.valueProperty().addListener((observable, oldValue, newValue) ->
    {
        if (newValue == 1) {
            frequencyLabel.setText(frequencyComboBox.valueProperty().get().toStringSingular());
        } else {
            frequencyLabel.setText(frequencyComboBox.valueProperty().get().toStringPlural());
        }
        vComponent.getRRule().getFrequency().setInterval(newValue);
        System.out.println("interval:" + newValue);
        refreshSummary();
         makeExceptionDates();
    });
    
    // Make frequencySpinner and only accept numbers (needs below two listeners)
    intervalSpinner.setEditable(true);
    intervalSpinner.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, (event)  ->
    {
        if (event.getCode() == KeyCode.ENTER) {
            String s = intervalSpinner.getEditor().textProperty().get();
            boolean isNumber = s.matches("[0-9]+");
            if (! isNumber) {
                String lastValue = intervalSpinner.getValue().toString();
                intervalSpinner.getEditor().textProperty().set(lastValue);
                notNumberAlert("123");
            }
        }
    });
    intervalSpinner.focusedProperty().addListener((obs, wasFocused, isNowFocused) ->
    {
        if (! isNowFocused) {
            int value;
            String s = intervalSpinner.getEditor().textProperty().get();
            boolean isNumber = s.matches("[0-9]+");
            if (isNumber)
            {
                value = Integer.parseInt(s);
                vComponent.getRRule().getFrequency().setInterval(value);
                refreshSummary();
                makeExceptionDates();
            } else {
                String lastValue = intervalSpinner.getValue().toString();
                intervalSpinner.getEditor().textProperty().set(lastValue);
                notNumberAlert("123");
            }
        }
    });
    
    // END AFTER LISTENERS
    endAfterEventsSpinner.valueProperty().addListener((observable, oldSelection, newSelection) ->
    {
        if (endAfterRadioButton.isSelected())
        {
              vComponent.getRRule().setCount(newSelection);
              refreshSummary();
               makeExceptionDates();
        }
        if (newSelection == 1) {
            eventLabel.setText(resources.getString("event"));
        } else {
            eventLabel.setText(resources.getString("events"));
        }
    });
    
    endAfterRadioButton.selectedProperty().addListener((observable, oldSelection, newSelection) ->
    {
        if (newSelection)
        {
            endAfterEventsSpinner.setDisable(false);
            eventLabel.setDisable(false);
            vComponent.getRRule().setCount(endAfterEventsSpinner.getValue());
            refreshSummary();
            System.out.println("make exception4 " + observable);
            makeExceptionDates();
        } else
        {
            vComponent.getRRule().setCount(0);
            endAfterEventsSpinner.setValueFactory(null);
            endAfterEventsSpinner.setDisable(true);
            eventLabel.setDisable(true);
        }
    });

    // Make endAfterEventsSpinner and only accept numbers in text field (needs below two listeners)
    endAfterEventsSpinner.setEditable(true);
    endAfterEventsSpinner.getEditor().addEventHandler(KeyEvent.KEY_PRESSED, (event)  ->
    {
        if (event.getCode() == KeyCode.ENTER) {
            String s = endAfterEventsSpinner.getEditor().textProperty().get();
            boolean isNumber = s.matches("[0-9]+");
            if (! isNumber) {
                String lastValue = endAfterEventsSpinner.getValue().toString();
                endAfterEventsSpinner.getEditor().textProperty().set(lastValue);
                notNumberAlert("123");
            }
        }
    });
    endAfterEventsSpinner.focusedProperty().addListener((obs, wasFocused, isNowFocused) ->
    {
        if (! isNowFocused) {
            int value;
            String s = endAfterEventsSpinner.getEditor().textProperty().get();
            boolean isNumber = s.matches("[0-9]+");
            if (isNumber) {
                value = Integer.parseInt(s);
                vComponent.getRRule().setCount(value);
            } else {
                String lastValue = endAfterEventsSpinner.getValue().toString();
                endAfterEventsSpinner.getEditor().textProperty().set(lastValue);
                notNumberAlert("123");
            }
        }
    });
    
    // END ON LISTENERS
    endOnDatePicker.valueProperty().addListener((observable, oldSelection, newSelection) ->
    {
        vComponent.getRRule().setUntil(newSelection.atTime(LocalTime.of(23, 59, 59))); // end at end of day
        refreshSummary();
        System.out.println("make exception5 " + observable);
        makeExceptionDates();
    });
    endOnRadioButton.selectedProperty().addListener((observable, oldSelection, newSelection) ->
    {
        if (newSelection)
        {
            if (vComponent.getRRule().getUntil() == null)
            { // new selection - use date/time one month in the future as default
                LocalDateTime defaultEndOnDateTime = (dateTimeStartInstanceNew.equals(vComponent.getDateTimeStart())) ?
                        VComponent.localDateTimeFromTemporal(vComponent.getDateTimeStart()).plus(1, ChronoUnit.MONTHS)
                      : dateTimeStartInstanceNew;
                vComponent.getRRule().setUntil(defaultEndOnDateTime);
            }
            endOnDatePicker.setValue(LocalDate.from(vComponent.getRRule().getUntil()));
            endOnDatePicker.setDisable(false);
            endOnDatePicker.show();
        } else {
            vComponent.getRRule().setUntil(null);
            endOnDatePicker.setDisable(true);
        }
    });
    
    // Day of week tooltips
    sundayCheckBox.setTooltip(new Tooltip(resources.getString("sunday")));
    mondayCheckBox.setTooltip(new Tooltip(resources.getString("monday")));
    tuesdayCheckBox.setTooltip(new Tooltip(resources.getString("tuesday")));
    wednesdayCheckBox.setTooltip(new Tooltip(resources.getString("wednesday")));
    thursdayCheckBox.setTooltip(new Tooltip(resources.getString("thursday")));
    fridayCheckBox.setTooltip(new Tooltip(resources.getString("friday")));
    saturdayCheckBox.setTooltip(new Tooltip(resources.getString("saturday")));

    // Monthly ToggleGroup
    monthlyGroup = new ToggleGroup();
    dayOfMonthRadioButton.setToggleGroup(monthlyGroup);
    dayOfWeekRadioButton.setToggleGroup(monthlyGroup);
    
    // End criteria ToggleGroup
    endGroup = new ToggleGroup();
    endNeverRadioButton.setToggleGroup(endGroup);
    endAfterRadioButton.setToggleGroup(endGroup);
    endOnRadioButton.setToggleGroup(endGroup);
}

/**
 * Add data that was unavailable at initialization time
 * 
 * @param rrule
 * @param dateTimeRange : date range for current agenda skin
 */
    public void setupData(
            VComponent<T> vComponent
          , LocalDateTime dateTimeStartInstanceNew)
    {
        System.out.println("setup previous rrule" + vComponent);
        this.vComponent = vComponent;
        this.dateTimeStartInstanceNew = dateTimeStartInstanceNew;

        // EXCEPTIONS
        // Note: exceptionComboBox string converter must be setup done after the controller's initialization 
        // because the resource bundle isn't instantiated earlier.
        final DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern(resources.getString("date.format.agenda.exception"));
        final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(resources.getString("date.format.agenda.exception.dateonly"));
        exceptionComboBox.setConverter(new StringConverter<Temporal>()
        { // setup string converter
            @Override public String toString(Temporal d)
            {
                DateTimeFormatter myFormatter;
                if ((d instanceof LocalDateTime))
                {
                    myFormatter = formatterDateTime;
                } else if ((d instanceof LocalDate))
                {
                    myFormatter = formatterDate;
                } else throw new DateTimeException("DTSTART and DTEND must have same Temporal type("
                        + d.getClass().getSimpleName() + ", " + d.getClass().getSimpleName() +")");
                return myFormatter.format(d);
            }
            @Override public Temporal fromString(String string) { throw new RuntimeException("not required for non editable ComboBox"); }
        });
        exceptionComboBox.valueProperty().addListener(obs -> addExceptionButton.setDisable(false)); // turn on add button when exception date is selected in combobox
                
        exceptionsListView.getSelectionModel().selectedItemProperty().addListener(obs ->
        {
          removeExceptionButton.setDisable(false); // turn on add button when exception date is selected in combobox
        });
        
        // Format Temporal in exceptionsListView to LocalDate or LocalDateTime
        final Callback<ListView<Temporal>, ListCell<Temporal>> temporalCellFactory = new Callback<ListView<Temporal>,  ListCell<Temporal>>()
        {
            @Override 
            public ListCell<Temporal> call(ListView<Temporal> list)
            {
                return new ListCell<Temporal>()
                {
                    @Override public void updateItem(Temporal temporal, boolean empty)
                    {
                        super.updateItem(temporal, empty);
                        if (temporal == null || empty)
                        {
                            setText(null);
                            setStyle("");
                        } else
                        {
                            // Format date.
                            DateTimeFormatter myFormatter;
                            if ((temporal instanceof LocalDateTime))
                            {
                                myFormatter = formatterDateTime;
                            } else if ((temporal instanceof LocalDate))
                            {
                                myFormatter = formatterDate;
                            } else throw new DateTimeException("Invalid Temporal type("
                                    + temporal.getClass().getSimpleName() + ", " + temporal.getClass().getSimpleName() +")");
                            setText(myFormatter.format(temporal));
                        }
                    }
                };
            }
        };
        exceptionsListView.setCellFactory(temporalCellFactory);
                
        // SETUP CONTROLLER'S INITIAL DATA FROM RRULE
        boolean isRepeatable = (vComponent.getRRule() != null);
        repeatableCheckBox.selectedProperty().set(isRepeatable);
        if (isRepeatable) 
        {
            System.out.println("setup previous rrule" + vComponent);
            setInitialValues(vComponent);
            if (vComponent.getExDate() != null)
            {
                List<Temporal> collect = vComponent
                        .getExDate()
                        .getTemporals()
                        .stream()
                        .collect(Collectors.toList());
                exceptionsListView.getItems().addAll(collect);
          }
        }

        // LISTENERS TO BE ADDED AFTER INITIALIZATION
        addExceptionListeners(); // Listeners to update exception dates
        frequencyComboBox.valueProperty().addListener(frequencyListener);
//        dayOfWeekList.addListener(makeExceptionDatesAndSummaryListener);

    }
    
    private void addExceptionListeners()
    {
//        intervalSpinner.valueProperty().addListener(makeExceptionDatesAndSummaryListener);
        endNeverRadioButton.selectedProperty().addListener(neverListener);
//        endGroup.selectedToggleProperty().addListener(makeExceptionDatesAndSummaryListener);
//        endAfterEventsSpinner.valueProperty().addListener(makeExceptionDatesAndSummaryListener);
//        endOnDatePicker.valueProperty().addListener(makeExceptionDatesAndSummaryListener);
        dayOfWeekList.addListener(makeExceptionDatesAndSummaryListener);
    }
    
    private void removeExceptionListeners()
    {
//        intervalSpinner.valueProperty().removeListener(makeExceptionDatesAndSummaryListener);
        endNeverRadioButton.selectedProperty().removeListener(neverListener);
//        endGroup.selectedToggleProperty().removeListener(makeExceptionDatesAndSummaryListener);
//        endAfterEventsSpinner.valueProperty().removeListener(makeExceptionDatesAndSummaryListener);
//        endOnDatePicker.valueProperty().removeListener(makeExceptionDatesAndSummaryListener);
        dayOfWeekList.removeListener(makeExceptionDatesAndSummaryListener);
    }
    
    /* Set controls to values in rRule */
    private void setInitialValues(VComponent<T> vComponent)
    {
        int initialInterval = (vComponent.getRRule().getFrequency().getInterval() > 0) ?
                vComponent.getRRule().getFrequency().getInterval() : INITIAL_INTERVAL;
        intervalSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, initialInterval));
        FrequencyType frequencyType = vComponent.getRRule().getFrequency().frequencyType();
        frequencyComboBox.setValue(frequencyType); // will trigger frequencyListener
        switch(frequencyType)
        {
        case MONTHLY:
            ByDay rule = (ByDay) vComponent.getRRule().getFrequency().getByRuleByType(Rule.ByRules.BYDAY);
            if (rule == null)
            {
                dayOfMonthRadioButton.selectedProperty().set(true);
            } else
            {
                dayOfWeekRadioButton.selectedProperty().set(true);
            }
            break;
        case WEEKLY:
            setDayOfWeek(vComponent.getRRule());
            break;
        default:
            break;
        }
        setFrequencyVisibility(frequencyType);
        
        // ExDates
        if (vComponent.getExDate() != null)
        {
            List<Temporal> collect = vComponent
                    .getExDate()
                    .getTemporals()
                    .stream()
                    .collect(Collectors.toList());
            exceptionsListView.getItems().addAll(collect);
        }
        
        int initialCount = (vComponent.getRRule().getCount() > 0) ? vComponent.getRRule().getCount() : INITIAL_COUNT;
        endAfterEventsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, initialCount));
        if (vComponent.getRRule().getCount() > 0)
        {
            endAfterRadioButton.selectedProperty().set(true);
        } else if (vComponent.getRRule().getUntil() != null)
        {
            endOnRadioButton.selectedProperty().set(true);
        } else
        {
            endNeverRadioButton.selectedProperty().set(true);
        }
        
        startDatePicker.setValue(LocalDate.from(vComponent.getDateTimeStart()));
    }

    /** Set day of week properties if FREQ=WEEKLY and has BYDAY rule 
     * This method is called only during setup */
    private void setDayOfWeek(RRule rRule)
    {
        // Set day of week properties
        if (rRule.getFrequency().frequencyType() == FrequencyType.WEEKLY)
        {
            Rule rule = rRule.getFrequency().getByRuleByType(Rule.ByRules.BYDAY);
            ((ByDay) rule).getDayofWeekWithoutOrdinalList()
                    .stream()
                    .forEach(d -> 
                    {
                        switch(d)
                        {
                        case FRIDAY:
                            fridayCheckBox.selectedProperty().set(true);
                            break;
                        case MONDAY:
                            mondayCheckBox.selectedProperty().set(true);
                            break;
                        case SATURDAY:
                            saturdayCheckBox.selectedProperty().set(true);
                            break;
                        case SUNDAY:
                            sundayCheckBox.selectedProperty().set(true);
                            break;
                        case THURSDAY:
                            thursdayCheckBox.selectedProperty().set(true);
                            break;
                        case TUESDAY:
                            tuesdayCheckBox.selectedProperty().set(true);
                            break;
                        case WEDNESDAY:
                            wednesdayCheckBox.selectedProperty().set(true);
                            break;
                        default:
                            break;
                        }
                    });
        }
    }
    
    
//    /**
//     * Default settings for a new RRule - weekly, repeats on day of week in dateTime
//     */
//    private RRule setDefaults(RRule rRule, LocalDateTime dateTime)
//    {
//        rRule.setFrequency(new Weekly());
//        monthlyVBox.setVisible(false);
//        monthlyLabel.setVisible(false);
//        return rRule;
//    }
    
    /** Make list of start date/times for exceptionComboBox */
    private void makeExceptionDates()
    {
        System.out.println("make exception date list");
        
        final LocalDateTime dateTimeStart = VComponent.localDateTimeFromTemporal(vComponent.getDateTimeStart());
        Stream<LocalDateTime> stream1 = vComponent
                .getRRule()
                .stream(dateTimeStart);
        Stream<LocalDateTime> stream2 = (vComponent.getExDate() == null) ? stream1
                : vComponent.getExDate().stream(stream1, dateTimeStart); // remove exceptions
        Class<? extends Temporal> clazz = vComponent.getDateTimeStart().getClass();
        List<Temporal> exceptionDates = null;
        if (clazz.equals(LocalDate.class))
        {
            exceptionDates = stream2
                    .limit(EXCEPTION_CHOICE_LIMIT)
                    .map(d -> d.toLocalDate())
                    .collect(Collectors.toList());
        } else if (clazz.equals(LocalDateTime.class))
        {
            exceptionDates = stream2
                    .limit(EXCEPTION_CHOICE_LIMIT)
                    .collect(Collectors.toList());
        } else throw new DateTimeException("Invalid Temporal class: " +
                clazz.getSimpleName() + " Only LocalDate and LocalDateTime accepted.");

        exceptionComboBox.getItems().clear();
        exceptionComboBox.getItems().addAll(exceptionDates);
    }
    
    @FXML private void handleAddException()
    {
        Temporal d = exceptionComboBox.getValue();
        exceptionsListView.getItems().add(d);
        if (vComponent.getExDate() == null) vComponent.setExDate(new EXDate());
        vComponent.getExDate().getTemporals().add(d);
        makeExceptionDates();
        Collections.sort(exceptionsListView.getItems(),VComponent.DATE_OR_DATETIME_TEMPORAL_COMPARATOR); // Maintain sorted list
        if (exceptionComboBox.getValue() == null) addExceptionButton.setDisable(true);
    }

    @FXML private void handleRemoveException()
    {
        Temporal d = exceptionsListView.getSelectionModel().getSelectedItem();
        vComponent.getExDate().getTemporals().remove(d);
        makeExceptionDates();
        exceptionsListView.getItems().remove(d);
        if (exceptionsListView.getSelectionModel().getSelectedItem() == null) removeExceptionButton.setDisable(true);
    }
    
    
    // Displays an alert notifying user number input is not valid
    private static void notNumberAlert(String validFormat)
    {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Invalid Number");
        alert.setHeaderText("Please enter valid numbers.");
        alert.setContentText("Accepted format: " + validFormat);
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk);
        alert.showAndWait();
    }
    
}
