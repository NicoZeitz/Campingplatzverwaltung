package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.control.Payload;
import swe.ka.dhbw.model.Ausruestung;
import swe.ka.dhbw.model.Chipkarte;
import swe.ka.dhbw.model.Gast;
import swe.ka.dhbw.model.GebuchteLeistung;
import swe.ka.dhbw.ui.GUIComponent;
import swe.ka.dhbw.ui.components.BookingCreateComponent;
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
            }
        }

        // BookingCreateComponent
        else if (guiEvent.getCmd() instanceof BookingCreateComponent.Commands command) {
            switch (command) {
                // guests
                case BUTTON_PRESSED_ADD_GUEST -> {
                    final var excludedGuests = new HashSet<>((List<Gast>) ((Payload.GuestList) guiEvent.getData()).guests());

                    controller.openDialogGuestSelector(
                            (GUIComponent) guiEvent.getSource(),
                            BookingCreateComponent.Commands.ADD_ASSOCIATED_GUEST,
                            excludedGuests
                    );
                }
                case RADIO_BUTTON_PRESSED_SELECT_RESPONSIBLE_GUEST -> {
                    final var payload = (BookingCreateComponent.ResponsibleGuestSelectedPayload) guiEvent.getData();
                    controller.handleWindowBookingCreateResponsibleGuestSelected(payload);
                }
                case BUTTON_PRESSED_DELETE_GUEST -> {
                    final var payload = (BookingCreateComponent.GuestDeletePayload) guiEvent.getData();
                    controller.handleWindowBookingCreateGuestDeleted(payload);
                }
                // services
                case BUTTON_PRESSED_ADD_SERVICE -> controller.openDialogServiceSelector(
                        (GUIComponent) guiEvent.getSource(),
                        BookingCreateComponent.Commands.ADD_BOOKED_SERVICE
                );
                case BUTTON_PRESSED_EDIT_SERVICE -> {
                    final var payload = (BookingCreateComponent.ServiceEditPayload) guiEvent.getData();
                    controller.openDialogEditService(
                            (GUIComponent) guiEvent.getSource(),
                            BookingCreateComponent.Commands.SET_BOOKED_SERVICES,
                            (List<GebuchteLeistung>) payload.selectedServices(),
                            (GebuchteLeistung) payload.serviceToEdit()
                    );
                }
                case BUTTON_PRESSED_DELETE_SERVICE -> {
                    final var payload = (BookingCreateComponent.ServiceDeletePayload) guiEvent.getData();
                    controller.handleWindowBookingCreateDeleteService(payload);
                }
                // equipment
                case BUTTON_PRESSED_ADD_EQUIPMENT -> controller.openDialogEquipmentSelector(
                        (GUIComponent) guiEvent.getSource(),
                        BookingCreateComponent.Commands.ADD_RENTED_EQUIPMENT
                );
                case BUTTON_PRESSED_INCREMENT_EQUIPMENT_COUNT -> {
                    final var payload = (BookingCreateComponent.EquipmentEditPayload) guiEvent.getData();
                    controller.handleWindowBookingCreateEditEquipment(
                            (List<Ausruestung>) payload.rentedEquipment(),
                            (Ausruestung) payload.equipment(),
                            +1
                    );
                }
                case BUTTON_PRESSED_DECREMENT_EQUIPMENT_COUNT -> {
                    final var payload = (BookingCreateComponent.EquipmentEditPayload) guiEvent.getData();
                    controller.handleWindowBookingCreateEditEquipment(
                            (List<Ausruestung>) payload.rentedEquipment(),
                            (Ausruestung) payload.equipment(),
                            -1
                    );
                }
                case BUTTON_PRESSED_DELETE_EQUIPMENT -> {
                    final var payload = (BookingCreateComponent.EquipmentDeletePayload) guiEvent.getData();
                    controller.handleWindowBookingCreateDeleteEquipment(
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
                            BookingCreateComponent.Commands.SET_START_DATE,
                            date
                    );
                }
                case BUTTON_PRESSED_SELECT_END_DATE -> {
                    final var date = ((Optional<LocalDateTime>) guiEvent.getData())
                            .map(LocalDateTime::toLocalDate);

                    controller.openDialogDatePicker(
                            (GUIComponent) guiEvent.getSource(),
                            BookingCreateComponent.Commands.SET_END_DATE,
                            date
                    );
                }
                // pitch
                case BUTTON_PRESSED_SELECT_PITCH -> controller.openDialogPitchSelector(
                        (GUIComponent) guiEvent.getSource(),
                        BookingCreateComponent.Commands.SET_PITCH
                );
                // chip card
                case BUTTON_PRESSED_SELECT_CHIPCARD -> controller.bookingCreateSelectChipkarte((Chipkarte) guiEvent.getData());
                case BUTTON_PRESSED_DELETE_CHIPCARD -> {
                    final var payload = (BookingCreateComponent.ChipcardDeletePayload) guiEvent.getData();
                    controller.bookingRemoveChipkarte(
                            (List<Chipkarte>) payload.selectedChipCards(),
                            (Chipkarte) payload.chipCardToDelete()
                    );
                }
                // create
                case BUTTON_PRESSED_CREATE_BOOKING -> controller.handleWindowBookingCreateBookingCreate(
                        (BookingCreateComponent.BookingCreatePayload) guiEvent.getData()
                );
                case BUTTON_PRESSED_CANCEL -> controller.handleWindowBookingCreateBookingCancel();
            }
        }
    }
}
