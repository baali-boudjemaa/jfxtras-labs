package jfxtras.labs.icalendaragenda.internal.scene.control.skin.agenda.base24hour;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jfxtras.labs.icalendarfx.components.VComponentDisplayable;
import jfxtras.labs.icalendarfx.properties.component.descriptive.Summary;
import jfxtras.scene.control.LocalDateTextField;
import jfxtras.scene.control.LocalDateTimeTextField;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.Agenda.AppointmentGroup;
import jfxtras.scene.control.agenda.TemporalUtilities;

/** Makes new TabPane for editing a {@link VComponentDisplayable} component
 * 
 * @author David Bal
 */
public abstract class DescriptiveVBox<T extends VComponentDisplayable<?>> extends VBox
{
    @FXML private ResourceBundle resources; // ResourceBundle that was given to the FXMLLoader
    public ResourceBundle getResources() { return resources; }

    @FXML GridPane timeGridPane;
    LocalDateTimeTextField startDateTimeTextField = new LocalDateTimeTextField(); // start of recurrence
    LocalDateTextField startDateTextField = new LocalDateTextField(); // start of recurrence when wholeDayCheckBox is selected
    
    @FXML Label endLabel;
    
    @FXML private CheckBox wholeDayCheckBox;
    @FXML private TextField summaryTextField; // SUMMARY
    @FXML TextArea descriptionTextArea; // DESCRIPTION
    @FXML protected TextField locationTextField; // LOCATION
    @FXML private TextField groupTextField; // CATEGORIES
    @FXML private AppointmentGroupGridPane appointmentGroupGridPane;
    @FXML private Button saveComponentButton;
    @FXML private Button cancelComponentButton;
    @FXML private Button saveRepeatButton;
    @FXML private Button cancelRepeatButton;
    @FXML private Button deleteComponentButton;
    @FXML private Tab appointmentTab;
    @FXML private Tab repeatableTab;
    
    public DescriptiveVBox( )
    {
        super();
        loadFxml(DescriptiveVBox.class.getResource("view/EditDescriptive.fxml"), this);
        appointmentGroupGridPane.getStylesheets().addAll(getStylesheets());
        startDateTimeTextField.setId("startDateTimeTextField");
        startDateTextField.setId("startDateTextField");
    }
    
    ObjectProperty<Boolean> isFinished = new SimpleObjectProperty<>(false);
    
    @FXML
    void handleSave()
    {
        // primary functionality added in subclasses
        System.out.println("here6:");
        isFinished.set(true);
    }
    
    // Checks to see if start date has been changed, and a date shift is required, and then runs ordinary handleSave method.
    @FXML private void handleRepeatSave()
    {
        handleSave();
    }
    
    @FXML private void handleCancelButton()
    {
        isFinished.set(true);
////        vEventOriginal.copyTo(vEvent);
//        vComponent.copyComponentFrom(vEventOriginal);
//        popup.close();
    }

    @FXML private void handleDeleteButton()
    {
        isFinished.set(true);
//        vEvent.handleDelete(
//                vComponents
//              , startRecurrence
//              , appointment
//              , appointments
//              , DeleteChoiceDialog.DELETE_DIALOG_CALLBACK);
//        popup.close();
    }    
    
//    private Appointment appointment; // selected appointment
//    protected Temporal startRecurrence; // bound to startTextField, but adjusted to be DateTimeType identical to VComponent DTSTART, updated in startTextListener
//    private Temporal endRecurrence; // bound to endTextField, but adjusted to be DateTimeType identical to VComponent DTSTART, updated in endTextListener
//    protected Temporal startOriginalRecurrence;
//    private Temporal endRecurrenceOriginal;
    

    final private ChangeListener<? super LocalDate> startDateTextListener = (observable, oldValue, newValue) -> synchStartDate(oldValue, newValue);

    /** Update startDateTimeTextField when startDateTextField changes */
    void synchStartDate(LocalDate oldValue, LocalDate newValue)
    {
        startRecurrence = newValue;
        startDateTimeTextField.localDateTimeProperty().removeListener(startDateTimeTextListener);
        LocalDateTime newDateTime = startDateTimeTextField.getLocalDateTime().with(newValue);
        startDateTimeTextField.setLocalDateTime(newDateTime);
        startDateTimeTextField.localDateTimeProperty().addListener(startDateTimeTextListener);
    }
            
    final private ChangeListener<? super LocalDateTime> startDateTimeTextListener = (observable, oldValue, newValue) -> synchStartDateTime(oldValue, newValue);

    /** Update startDateTextField when startDateTimeTextField changes */
    void synchStartDateTime(LocalDateTime oldValue, LocalDateTime newValue)
    {
        startRecurrence = newValue;
        startDateTextField.localDateProperty().removeListener(startDateTextListener);
        LocalDate newDate = LocalDate.from(startDateTimeTextField.getLocalDateTime());
        startDateTextField.setLocalDate(newDate);
        startDateTextField.localDateProperty().addListener(startDateTextListener);
    }
    
    // Callback for LocalDateTimeTextField that is called when invalid date/time is entered
    protected final Callback<Throwable, Void> errorCallback = (throwable) ->
    {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Invalid Date or Time");
        alert.setContentText("Please enter valid date and time");
        alert.showAndWait();
        return null;
    };
    
    T vComponentEdited;
    T vComponentOriginalCopy;
    List<T> vComponents;
    Temporal startOriginalRecurrence;
    Temporal startRecurrence;

    public void setupData(
            Appointment appointment,
            T vComponent,
            List<T> vComponents,
            List<AppointmentGroup> appointmentGroups)
    {
        startOriginalRecurrence = appointment.getStartTemporal();
//        this.appointment = appointment;
        this.vComponents = vComponents;
        vComponentEdited = vComponent;
        
        // Disable repeat rules for events with recurrence-id
        if (vComponentEdited.getRecurrenceDates() != null)
        { // recurrence recurrences can't add repeat rules (only parent can have repeat rules)
            repeatableTab.setDisable(true);
            repeatableTab.setTooltip(new Tooltip(resources.getString("repeat.tab.unavailable")));
        }
        
        // Copy original VEvent
////        vEventOriginal = (VEventOld<Appointment,?>) VComponentFactory.newVComponent(vEvent);
//        vEventOriginal = new VEvent(vComponent);
        
        // String bindings
        if (vComponentEdited.getSummary() == null)
        {
            vComponentEdited.setSummary(Summary.parse(""));
        }
        summaryTextField.textProperty().bindBidirectional(vComponentEdited.getSummary().valueProperty());
        
//        descriptionTextArea.textProperty().bindBidirectional(vComponent.getDescription().valueProperty());
//        if (vComponent.getLocation() == null)
//        {
//            vComponent.withLocation("");
//        }
//        locationTextField.textProperty().bindBidirectional(vComponent.getLocation().valueProperty());
        
//        startDateTimeTextListener = (observable, oldSelection, newSelection) ->
//        {
//            if (wholeDayCheckBox.isSelected())
//            {
//                startRecurrence = LocalDate.from(startDateTimeTextField.getLocalDateTime());
//            } else
//            {
//                startRecurrence = vComponent.getDateTimeStart().getValue().with(startDateTimeTextField.getLocalDateTime());
//            }
//            System.out.println("startRecurrence:" + startRecurrence);
//        };

        // TIME ZONE
//        updateZone(); // initialize
//        vComponent.dateTimeStartProperty().addListener((obs) -> updateZone()); // setup listener to handle changes
        
//        // END DATE/TIME
//        Locale locale = Locale.getDefault();
//        endTextField.setLocale(locale);
//        endTextField.localDateTimeProperty().addListener(endTextlistener);
////        endTextField.setLocalDateTime(DateTimeType.localDateTimeFromTemporal(endRecurrenceOriginal));
//        endTextField.setLocalDateTime(TemporalUtilities.toLocalDateTime(endRecurrenceOriginal));
//        endTextField.setParseErrorCallback(errorCallback);
        
        // START DATE/TIME
        startDateTimeTextField.setLocale(Locale.getDefault());
        startDateTimeTextField.localDateTimeProperty().addListener(startDateTimeTextListener);
        final LocalDateTime start;
        if (startOriginalRecurrence.isSupported(ChronoUnit.NANOS))
        {
            start = TemporalUtilities.toLocalDateTime(startOriginalRecurrence);
        } else
        {
            start = LocalDate.from(startOriginalRecurrence).atTime(defaultStartTime);
        }
        startDateTimeTextField.setLocalDateTime(start);
        startDateTimeTextField.setParseErrorCallback(errorCallback);
        startDateTextField.setLocale(Locale.getDefault());
//        ChangeListener<? super LocalDate> startDateTextListener;
//        startDateTextField.localDateProperty().addListener(startDateTextListener);
        startDateTextField.setLocalDate(LocalDate.from(startOriginalRecurrence));
        startDateTextField.setParseErrorCallback(errorCallback);
        
        // WHOLE DAY
        wholeDayCheckBox.setSelected(vComponentEdited.isWholeDay());
        handleWholeDayChange(vComponentEdited, wholeDayCheckBox.isSelected()); 
        wholeDayCheckBox.selectedProperty().addListener((observable, oldSelection, newSelection) -> handleWholeDayChange(vComponent, newSelection));
        
        // APPOINTMENT GROUP
//        System.out.println("cats1:" + vComponent.getCategories().size());
        appointmentGroupGridPane.appointmentGroupSelectedProperty().addListener(
            (observable, oldSelection, newSelection) ->
            {
                Integer i = appointmentGroupGridPane.getAppointmentGroupSelected();
                String newText = appointmentGroups.get(i).getDescription();
                groupTextField.setText(newText);
//                groupNameEdited.set(true); // TODO - HANDLE APPOINTMENT GROUP I/O
            });
//        System.out.println("cats2:" + vComponent.getCategories().size());
        // store group name changes by each character typed
        groupTextField.textProperty().addListener((observable, oldSelection, newSelection) ->
        {
            int i = appointmentGroupGridPane.getAppointmentGroupSelected();
            appointmentGroups.get(i).setDescription(newSelection);
            appointmentGroupGridPane.updateToolTip(i, appointmentGroups);
            vComponentEdited.withCategories(newSelection);
//            System.out.println("cats4:" + vComponent.getCategories().size() + " " + newSelection);
            // TODO - ensure groupTextField has unique description text
//            groupNameEdited.set(true);
        });
        appointmentGroupGridPane.setupData(vComponentEdited, appointmentGroups);
//        System.out.println("cats3:" + vComponent.getCategories().size());

        // SETUP REPEATABLE CONTROLLER
//        repeatableController.setupData(vComponent, startRecurrence, popup);
        
//        // When Appointment tab is selected make sure start and end times are valid, adjust if not
//        appointmentEditTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
//        {
//            if (newValue == appointmentTab)
//            {
//                Runnable alertRunnable = validateStartRecurrence();
//                if (alertRunnable != null)
//                {
//                    Platform.runLater(alertRunnable); // display alert after tab change refresh
//                }
//            }
//        });
    }
    
    //    private LocalDateTime lastStartTextFieldValue = null; // last LocalDateTime in startTextField
    protected LocalTime defaultStartTime = LocalTime.of(10, 0); // default time
//    protected TemporalAmount lastDuration = Duration.ofHours(1); // Default to one hour duration
//    protected Temporal lastDateTimeStart;
    
    /*
     * When
     */
    void handleWholeDayChange(T vComponent, Boolean newSelection)
    {
        startDateTimeTextField.localDateTimeProperty().removeListener(startDateTimeTextListener);
        startDateTextField.localDateProperty().removeListener(startDateTextListener);
        if (newSelection)
        {
            timeGridPane.getChildren().remove(startDateTimeTextField);
            timeGridPane.add(startDateTextField, 1, 0);
        } else
        {
            timeGridPane.getChildren().remove(startDateTextField);
            timeGridPane.add(startDateTimeTextField, 1, 0);
        }
        startDateTextField.localDateProperty().addListener(startDateTextListener);
        startDateTimeTextField.localDateTimeProperty().addListener(startDateTimeTextListener);
    }
    
    /* If startRecurrence isn't valid due to a RRULE change, changes startRecurrence and
     * endRecurrence to closest valid values
     */
    // TODO - FIX THIS
    Runnable validateStartRecurrence()
    {
//        Temporal actualRecurrence = vEvent.streamRecurrences(startRecurrence).findFirst().get();
        if (! vComponentEdited.isRecurrence(startRecurrence))
        {
            Temporal recurrenceBefore = vComponentEdited.previousStreamValue(startRecurrence);
            Optional<Temporal> optionalAfter = vComponentEdited.streamRecurrences(startRecurrence).findFirst();
            Temporal newStartRecurrence = (optionalAfter.isPresent()) ? optionalAfter.get() : recurrenceBefore;
//            TemporalAmount duration = DateTimeUtilities.temporalAmountBetween(startRecurrence, endRecurrence);
//            Temporal newEndRecurrence = newStartRecurrence.plus(duration);
            Temporal startRecurrenceBeforeChange = startRecurrence;
            startDateTimeTextField.setLocalDateTime(TemporalUtilities.toLocalDateTime(newStartRecurrence));
//            endTextField.setLocalDateTime(TemporalUtilities.toLocalDateTime(newEndRecurrence));
            startOriginalRecurrence = startRecurrence;
            return () -> startRecurrenceChangedAlert(startRecurrenceBeforeChange, newStartRecurrence);
        }
        return null;
    }
    
    /* Displays an alert notifying that startInstance has changed due to changes in the Repeat tab.
     * These changes can include the day of the week is not valid or the start date has shifted.
     * The closest valid date is substituted.
    */
    // TODO - PUT COMMENTS IN RESOURCES
    protected void startRecurrenceChangedAlert(Temporal t1, Temporal t2)
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.getDialogPane().setId("startInstanceChangedAlert");
        alert.getDialogPane().lookupButton(ButtonType.OK).setId("startInstanceChangedAlertOkButton");
        alert.setHeaderText("Time not valid due to repeat rule change");
        alert.setContentText(Settings.DATE_FORMAT_AGENDA_EXCEPTION.format(t1) + " is no longer valid." + System.lineSeparator()
                    + "It has been replaced by " + Settings.DATE_FORMAT_AGENDA_EXCEPTION.format(t2));
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk);
        alert.showAndWait();
    }
    
    protected static void loadFxml(URL fxmlFile, Object rootController)
    {
        FXMLLoader loader = new FXMLLoader(fxmlFile);
        loader.setController(rootController);
        loader.setRoot(rootController);
        loader.setResources(Settings.resources);
        try {
            loader.load();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
 
