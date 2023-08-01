package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.model.*;
import swe.ka.dhbw.ui.GUIComponent;
import swe.ka.dhbw.ui.components.BookingChangeComponent;
import swe.ka.dhbw.ui.components.BookingListComponent;
import swe.ka.dhbw.ui.components.BookingOverviewComponent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class GUIBuchungObserver implements IGUIEventListener {

    @Override
    @SuppressWarnings({"unchecked", "SwitchStatementWithTooFewBranches"})
    public void processGUIEvent(final GUIEvent guiEvent) {
        final var controller = GUIController.getInstance();
        // BookingOverviewComponent
        if (guiEvent.getCmd() instanceof BookingOverviewComponent.Commands command) {
            switch (command) {
                case PREVIOUS_WEEK -> controller.handleWindowBookingAppointmentOverviewPreviousWeek((LocalDate) guiEvent.getData());
                case NEXT_WEEK -> controller.handleWindowBookingAppointmentOverviewNextWeek((LocalDate) guiEvent.getData());
                case BOOKING_SELECTED -> controller.handleWindowBookingBookingSelected(((IDepictable) guiEvent.getData()).getElementID());
            }
        }

        // BookingListComponent
        else if (guiEvent.getCmd() instanceof BookingListComponent.Commands command) {
            switch (command) {
                case BOOKING_SELECTED -> controller.handleWindowBookingBookingSelected(((IDepictable) guiEvent.getData()).getElementID());
                case BUTTON_PRESSED_SELECT_START_DATE -> controller.openDialogDatePicker(
                        (GUIComponent) guiEvent.getSource(),
                        BookingListComponent.Commands.SET_START_DATE,
                        (Optional<LocalDate>) guiEvent.getData(),
                        false
                );
                case BUTTON_PRESSED_SELECT_END_DATE -> controller.openDialogDatePicker(
                        (GUIComponent) guiEvent.getSource(),
                        BookingListComponent.Commands.SET_END_DATE,
                        (Optional<LocalDate>) guiEvent.getData(),
                        false
                );
                case SEARCH_INPUT_CHANGED -> {
                    final var payload = (BookingListComponent.SearchInputChangedPayload) guiEvent.getData();
                    controller.handleWindowBookingListSearchInputChanged(
                            (List<IDepictable>) payload.allBookings(),
                            payload.startDate(),
                            payload.endDate()
                    );
                }
            }
        }

        // BookingChangeComponent
        else if (guiEvent.getCmd() instanceof BookingChangeComponent.Commands command) {
            switch (command) {
                // guests
                case BUTTON_PRESSED_ADD_GUEST -> {
                    final var excludedGuests = new HashSet<>((List<Gast>) ((BookingChangeComponent.GuestListPayload) guiEvent.getData()).guests());

                    controller.openDialogGuestSelector(
                            (GUIComponent) guiEvent.getSource(),
                            BookingChangeComponent.Commands.ADD_ASSOCIATED_GUEST,
                            excludedGuests
                    );
                }
                case RADIO_BUTTON_PRESSED_SELECT_RESPONSIBLE_GUEST -> {
                    final var payload = (BookingChangeComponent.ResponsibleGuestSelectPayload) guiEvent.getData();
                    controller.handleWindowBookingChangeResponsibleGuestSelected(
                            (GUIComponent) guiEvent.getSource(),
                            (List<Gast>) payload.selectedGuests(),
                            (Gast) payload.selectedResponsibleGuest()
                    );
                }
                case BUTTON_PRESSED_DELETE_GUEST -> {
                    final var payload = (BookingChangeComponent.GuestDeletePayload) guiEvent.getData();
                    controller.handleWindowBookingChangeGuestDeleted(
                            (GUIComponent) guiEvent.getSource(),
                            (List<Gast>) payload.selectedGuests(),
                            (Gast) payload.deletedGuest(),
                            (Optional<Gast>) payload.responsibleGuest()
                    );
                }
                // services
                case BUTTON_PRESSED_ADD_SERVICE -> controller.openDialogServiceSelector(
                        (GUIComponent) guiEvent.getSource(),
                        BookingChangeComponent.Commands.ADD_BOOKED_SERVICE
                );
                case BUTTON_PRESSED_EDIT_SERVICE -> {
                    final var payload = (BookingChangeComponent.ServiceEditPayload) guiEvent.getData();
                    controller.openDialogEditService(
                            (GUIComponent) guiEvent.getSource(),
                            BookingChangeComponent.Commands.SET_BOOKED_SERVICES,
                            (List<GebuchteLeistung>) payload.selectedServices(),
                            (GebuchteLeistung) payload.serviceToEdit()
                    );
                }
                case BUTTON_PRESSED_DELETE_SERVICE -> {
                    final var payload = (BookingChangeComponent.ServiceDeletePayload) guiEvent.getData();
                    controller.handleWindowBookingChangeDeleteService(
                            (GUIComponent) guiEvent.getSource(),
                            (List<GebuchteLeistung>) payload.selectedServices(),
                            (GebuchteLeistung) payload.serviceToDelete()
                    );
                }
                // equipment
                case BUTTON_PRESSED_ADD_EQUIPMENT -> controller.openDialogEquipmentSelector(
                        (GUIComponent) guiEvent.getSource(),
                        BookingChangeComponent.Commands.ADD_RENTED_EQUIPMENT
                );
                case BUTTON_PRESSED_INCREMENT_EQUIPMENT_COUNT -> {
                    final var payload = (BookingChangeComponent.EquipmentEditPayload) guiEvent.getData();
                    controller.handleWindowBookingChangeEditEquipment(
                            (GUIComponent) guiEvent.getSource(),
                            (List<Ausruestung>) payload.rentedEquipment(),
                            (Ausruestung) payload.equipment(),
                            +1
                    );
                }
                case BUTTON_PRESSED_DECREMENT_EQUIPMENT_COUNT -> {
                    final var payload = (BookingChangeComponent.EquipmentEditPayload) guiEvent.getData();
                    controller.handleWindowBookingChangeEditEquipment(
                            (GUIComponent) guiEvent.getSource(),
                            (List<Ausruestung>) payload.rentedEquipment(),
                            (Ausruestung) payload.equipment(),
                            -1
                    );
                }
                case BUTTON_PRESSED_DELETE_EQUIPMENT -> {
                    final var payload = (BookingChangeComponent.EquipmentDeletePayload) guiEvent.getData();
                    controller.handleWindowBookingChangeDeleteEquipment(
                            (GUIComponent) guiEvent.getSource(),
                            (List<Ausruestung>) payload.rentedEquipment(),
                            (Ausruestung) payload.equipmentToDelete()
                    );
                }
                // dates
                case BUTTON_PRESSED_SELECT_START_DATE -> {
                    final var date = ((Optional<LocalDateTime>) guiEvent.getData())
                            .map(LocalDateTime::toLocalDate);

                    controller.openDialogDatePicker(
                            (GUIComponent) guiEvent.getSource(),
                            BookingChangeComponent.Commands.SET_START_DATE,
                            date,
                            true
                    );
                }
                case BUTTON_PRESSED_SELECT_END_DATE -> {
                    final var date = ((Optional<LocalDateTime>) guiEvent.getData())
                            .map(LocalDateTime::toLocalDate);

                    controller.openDialogDatePicker(
                            (GUIComponent) guiEvent.getSource(),
                            BookingChangeComponent.Commands.SET_END_DATE,
                            date,
                            true
                    );
                }
                // pitch
                case BUTTON_PRESSED_SELECT_PITCH -> controller.openDialogPitchSelector(
                        (GUIComponent) guiEvent.getSource(),
                        BookingChangeComponent.Commands.SET_PITCH
                );
                // chip card
                case BUTTON_PRESSED_SELECT_CHIPCARD -> controller.handleWindowBookingChangeSelectChipCard(
                        (GUIComponent) guiEvent.getSource(),
                        (Chipkarte) guiEvent.getData()
                );
                case BUTTON_PRESSED_DELETE_CHIPCARD -> {
                    final var payload = (BookingChangeComponent.ChipCardDeletePayload) guiEvent.getData();
                    controller.handleWindowBookingChangeDeleteChipCard(
                            (GUIComponent) guiEvent.getSource(),
                            (List<Chipkarte>) payload.selectedChipCards(),
                            (Chipkarte) payload.chipCardToDelete()
                    );
                }
                // Exit operations
                case BUTTON_PRESSED_SAVE_BOOKING -> controller.handleWindowBookingChangeSave(
                        (GUIComponent) guiEvent.getSource(),
                        (BookingChangeComponent.SavePayload) guiEvent.getData()
                );
                case BUTTON_PRESSED_DELETE_BOOKING -> controller.handleWindowBookingChangeDelete((Buchung) guiEvent.getData());
                case BUTTON_PRESSED_CANCEL -> controller.handleWindowBookingChangeCancel((GUIComponent) guiEvent.getSource(),
                        (BookingChangeComponent.Mode) guiEvent.getData());
            }
        }
    }
}
