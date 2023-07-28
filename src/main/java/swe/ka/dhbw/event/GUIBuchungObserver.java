package swe.ka.dhbw.event;

import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.GUIController;
import swe.ka.dhbw.model.Chipkarte;
import swe.ka.dhbw.ui.components.BookingCreateComponent;
import swe.ka.dhbw.ui.components.BookingListComponent;
import swe.ka.dhbw.ui.components.BookingOverviewComponent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GUIBuchungObserver implements IGUIEventListener {

    @Override
    @SuppressWarnings("unchecked")
    public void processGUIEvent(final GUIEvent guiEvent) {
        // BookingOverviewComponent
        if (guiEvent.getCmd() == BookingOverviewComponent.Commands.PREVIOUS_WEEK) {
            GUIController.getInstance().handleWindowBookingAppointmentOverviewPreviousWeek((LocalDate) guiEvent.getData());
        } else if (guiEvent.getCmd() == BookingOverviewComponent.Commands.NEXT_WEEK) {
            GUIController.getInstance().handleWindowBookingAppointmentOverviewNextWeek((LocalDate) guiEvent.getData());
        } else if (guiEvent.getCmd() == BookingOverviewComponent.Commands.BOOKING_SELECTED) {
            GUIController.getInstance().handleWindowBookingBookingSelected(((IDepictable) guiEvent.getData()).getElementID());
        }

        // BookingListComponent
        else if (guiEvent.getCmd() == BookingListComponent.Commands.BOOKING_SELECTED) {
            GUIController.getInstance().handleWindowBookingBookingSelected(((IDepictable) guiEvent.getData()).getElementID());
        }

        // BookingCreateComponent
        else if (guiEvent.getCmd() == BookingCreateComponent.Commands.ADD_GUEST) {
            // TODO: real set parameter
            GUIController.getInstance().openDialogGuestSelector(guiEvent, Set.of());
        } else if (guiEvent.getCmd() == BookingCreateComponent.Commands.ADD_SERVICE) {
            GUIController.getInstance().openDialogServiceSelector(guiEvent);
        } else if (guiEvent.getCmd() == BookingCreateComponent.Commands.ADD_EQUIPMENT) {
            GUIController.getInstance().openDialogEquipmentSelector(guiEvent);
        } else if (guiEvent.getCmd() == BookingCreateComponent.Commands.SELECT_START_DATE ||
                guiEvent.getCmd() == BookingCreateComponent.Commands.SELECT_END_DATE) {
            final var date = ((Optional<LocalDateTime>) guiEvent.getData())
                    .map(LocalDateTime::toLocalDate);

            GUIController.getInstance().openDialogDatePicker(date, guiEvent);
        } else if (guiEvent.getCmd() == BookingCreateComponent.Commands.SELECT_CHIPCARD) {
            final var payload = (BookingCreateComponent.SelectChipkartePayload) guiEvent.getData();
            GUIController.getInstance().bookingCreateSelectChipkarte((List<Chipkarte>) payload.availableChipkarten(),
                    (List<Chipkarte>) payload.selectedChipkarten(), (Chipkarte) payload.selectedChipkarte());
        } else if (guiEvent.getCmd() == BookingCreateComponent.Commands.DELETE_CHIPCARD) {
            final var payload = (BookingCreateComponent.DeleteChipkartePayload) guiEvent.getData();
            GUIController.getInstance().bookingRemoveChipkarte((List<Chipkarte>) payload.availableChipkarten(),
                    (List<Chipkarte>) payload.selectedChipkarten(), (Chipkarte) payload.deletedChipkarte());
        } else if (guiEvent.getCmd() == BookingCreateComponent.Commands.RESET) {
            GUIController.getInstance().handleWindowBookingCreateBookingCancel();
        } else if (guiEvent.getCmd() == BookingCreateComponent.Commands.CREATE_BOOKING) {
            GUIController.getInstance().handleWindowBookingCreateBookingCreate((BookingCreateComponent.BookingCreatePayload) guiEvent.getData());
        } else if (guiEvent.getCmd() == BookingCreateComponent.Commands.SELECT_PITCH_INTERACTIVELY) {
            GUIController.getInstance().openDialogPitchSelector(guiEvent);
        }


        // other events
        else {
            LogObserver.logGUIEvent(guiEvent);
        }
    }
}
