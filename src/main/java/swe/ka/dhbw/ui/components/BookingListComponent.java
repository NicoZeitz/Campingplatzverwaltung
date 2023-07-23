package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.IGUIEventListener;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.gui.SimpleTableComponent;
import de.dhbwka.swe.utils.model.Attribute;
import de.dhbwka.swe.utils.model.IDepictable;
import de.dhbwka.swe.utils.model.ImageElement;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.*;

public class BookingListComponent extends GUIComponent implements IGUIEventListener, TableCellRenderer {
    public enum Commands implements EventCommand {
        BOOKING_SELECTED("BookingListComponent::BOOKING_SELECTED", IDepictable.class);

        public final Class<?> payloadType;
        public final String cmdText;

        Commands(final String cmdText, final Class<?> payloadType) {
            this.cmdText = cmdText;
            this.payloadType = payloadType;
        }

        @Override
        public String getCmdText() {
            return this.cmdText;
        }

        @Override
        public Class<?> getPayloadType() {
            return this.payloadType;
        }
    }

    private static final String BOOKING_SIMPLE_TABLE_COMPONENT_ID = "BookingListComponent::BOOKING_SIMPLE_TABLE_COMPONENT_ID";
    private List<? extends IDepictable> bookings;
    private Map<ImageElement, ImageIcon> cachedImages = new HashMap<>();

    public BookingListComponent(final ReadonlyConfiguration config, final List<? extends IDepictable> bookings) {
        super("BookingListComponent", config);
        this.bookings = bookings;
        this.initUI();
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table,
                                                   final Object value,
                                                   final boolean isSelected,
                                                   final boolean hasFocus,
                                                   final int row,
                                                   final int column) {
        if (value instanceof Collection<?> collection && collection.size() > 0 && collection.stream()
                .findFirst()
                .get() instanceof ImageElement) {
            final var cell = new JPanel();
            cell.setLayout(new FlowLayout());
            cell.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            cell.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            cell.setFont(table.getFont());
            cell.setOpaque(true);
            for (final var image : collection) {
                final var img = this.cachedImages.computeIfAbsent((ImageElement) image,
                        i -> new ImageIcon(i.getBaseImage()
                                .getScaledInstance(table.getRowHeight(), table.getRowHeight(), Image.SCALE_DEFAULT)));
                cell.add(new JLabel(img));
            }
            return cell;
        }

        return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    @Override
    public void processGUIEvent(final GUIEvent ge) {
        if (ge.getCmd() == SimpleTableComponent.Commands.ROW_SELECTED) {
            this.fireGUIEvent(new GUIEvent(
                    this,
                    Commands.BOOKING_SELECTED,
                    ge.getData()
            ));
        }
    }

    @Override
    public void processUpdateEvent(final UpdateEvent ue) {

    }

    private void initUI() {
        this.setLayout(new GridLayout(1, 1));
        this.setBackground(this.config.getBackgroundColor());
        this.setOpaque(true);
        this.initUIBookings();
    }

    @SuppressWarnings("unchecked")
    private void initUIBookings() {
        final var columnNames = Arrays.stream(this.bookings.get(0).getAttributeArray())
                .map(Attribute::getName)
                .toList()
                .toArray(new String[0]);

        final var table = SimpleTableComponent
                .builder(BOOKING_SIMPLE_TABLE_COMPONENT_ID)
                .columnNames(columnNames)
                .cellRenderer(this, List.class)
                .data((List<IDepictable>) this.bookings)
                .build();
        super.colorizeTable(table);
        table.addObserver(this);

        this.add(table, BorderLayout.CENTER);
    }
}
