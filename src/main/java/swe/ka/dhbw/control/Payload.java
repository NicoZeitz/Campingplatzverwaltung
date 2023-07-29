package swe.ka.dhbw.control;

import de.dhbwka.swe.utils.model.IDepictable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// TODO: move inner classes somewhere else and delete
public final class Payload {
    public record GuestList(List<? extends IDepictable> guests, Optional<? extends IDepictable> responsibleGuest) {
    }

    public record ServiceCreation(
            IDepictable serviceType,
            Optional<LocalDate> startDate,
            Optional<LocalDate> endDate
    ) {
    }

    private Payload() {
    }
}
