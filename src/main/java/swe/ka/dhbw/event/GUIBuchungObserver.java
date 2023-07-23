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
    public void processGUIEvent(final GUIEvent ge) {
        // BookingOverviewComponent
        if (ge.getCmd() == BookingOverviewComponent.Commands.PREVIOUS_WEEK) {
            GUIController.getInstance().handleWindowBookingAppointmentOverviewPreviousWeek((LocalDate) ge.getData());
        } else if (ge.getCmd() == BookingOverviewComponent.Commands.NEXT_WEEK) {
            GUIController.getInstance().handleWindowBookingAppointmentOverviewNextWeek((LocalDate) ge.getData());
        } else if (ge.getCmd() == BookingOverviewComponent.Commands.BOOKING_SELECTED) {
            GUIController.getInstance().handleWindowBookingBookingSelected(((IDepictable) ge.getData()).getElementID());
        }

        // BookingListComponent
        else if (ge.getCmd() == BookingListComponent.Commands.BOOKING_SELECTED) {
            GUIController.getInstance().handleWindowBookingBookingSelected(((IDepictable) ge.getData()).getElementID());
        }

        // BookingCreateComponent
        else if (ge.getCmd() == BookingCreateComponent.Commands.ADD_GUEST) {
            // TODO: real set parameter
            GUIController.getInstance().openDialogGuestSelector(ge, Set.of());
        } else if (ge.getCmd() == BookingCreateComponent.Commands.ADD_SERVICE) {
            GUIController.getInstance().openDialogServiceSelector(ge);
        } else if (ge.getCmd() == BookingCreateComponent.Commands.ADD_EQUIPMENT) {
            GUIController.getInstance().openDialogEquipmentSelector(ge);
        } else if (ge.getCmd() == BookingCreateComponent.Commands.SELECT_START_DATE ||
                ge.getCmd() == BookingCreateComponent.Commands.SELECT_END_DATE) {
            final var date = ((Optional<LocalDateTime>) ge.getData())
                    .map(LocalDateTime::toLocalDate);

            GUIController.getInstance().openDialogDatePicker(date, ge);
        } else if (ge.getCmd() == BookingCreateComponent.Commands.SELECT_CHIPCARD) {
            final var payload = (BookingCreateComponent.SelectChipkartePayload) ge.getData();
            GUIController.getInstance().bookingCreateSelectChipkarte((List<Chipkarte>) payload.availableChipkarten(),
                    (List<Chipkarte>) payload.selectedChipkarten(), (Chipkarte) payload.selectedChipkarte());
        } else if (ge.getCmd() == BookingCreateComponent.Commands.DELETE_CHIPCARD) {
            final var payload = (BookingCreateComponent.DeleteChipkartePayload) ge.getData();
            GUIController.getInstance().bookingRemoveChipkarte((List<Chipkarte>) payload.availableChipkarten(),
                    (List<Chipkarte>) payload.selectedChipkarten(), (Chipkarte) payload.deletedChipkarte());
        } else if (ge.getCmd() == BookingCreateComponent.Commands.RESET) {
            GUIController.getInstance().handleWindowBookingCreateBookingCancel();
        } else if (ge.getCmd() == BookingCreateComponent.Commands.CREATE_BOOKING) {
            // TODO:
        }


        // other events
        else {
            LogObserver.logGUIEvent(ge);
        }
    }
}
