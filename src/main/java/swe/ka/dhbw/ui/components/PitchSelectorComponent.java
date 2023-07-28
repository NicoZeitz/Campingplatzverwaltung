package swe.ka.dhbw.ui.components;

import de.dhbwka.swe.utils.event.EventCommand;
import de.dhbwka.swe.utils.event.GUIEvent;
import de.dhbwka.swe.utils.event.UpdateEvent;
import de.dhbwka.swe.utils.model.IDepictable;
import swe.ka.dhbw.control.ReadonlyConfiguration;
import swe.ka.dhbw.ui.GUIComponent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.*;

public class PitchSelectorComponent extends GUIComponent {
    // Commands
    public enum Commands implements EventCommand {
        // outgoing gui events
        PITCH_SELECTED("PitchSelectorComponent::PITCH_SELECTED", IDepictable.class),
        // incoming update events
        UPDATE_PITCHES("PitchSelectorComponent::UPDATE_PITCHES", List.class);
        
        public final Class<?> payloadType;
        public final String cmdText;

        Commands(final String cmdText) {
            this(cmdText, Void.class);
        }

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

    // Components
    private JPanel mapComponent;

    // Data
    private List<Pitch> pitches = new ArrayList<>();
    private Dimension dimension = new Dimension(0, 0);
    private BufferedImage mapImage;

    public PitchSelectorComponent(final ReadonlyConfiguration config) {
        super("PitchSelectorComponent", config);
        try {
            this.mapImage = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/Campsite.png")));
        } catch (IOException e) {
            this.mapImage = new BufferedImage(350, 350, BufferedImage.TYPE_INT_RGB);
            final var g = this.mapImage.createGraphics();
            g.setColor(this.config.getBackgroundColor());
            g.drawRect(0, 0, 350, 350);
            g.dispose();
        }
        this.initUI();
    }

    public int getImageHeight() {
        return (int) this.dimension.getHeight();
    }

    public int getImageWidth() {
        return (int) this.dimension.getWidth();
    }

    public void setSizeWithWidth(final int width) {
        this.dimension = new Dimension(width, (int) Math.round(width / (double) this.mapImage.getWidth() * this.mapImage.getHeight()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processUpdateEvent(final UpdateEvent updateEvent) {
        if (updateEvent.getCmd() == Commands.UPDATE_PITCHES) {
            this.pitches = (List<Pitch>) updateEvent.getData();
            this.buildUIPitch();
        }
    }

    private void buildUIPitch() {
        this.mapComponent.removeAll();
        final var availableWidth = this.getImageWidth();
        final var availableHeight = this.getImageHeight();

        for (var pitch : this.pitches) {
            var pitchButton = new JButton();
            pitchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            pitchButton.setBorder(BorderFactory.createLineBorder(this.config.getAccentColor(), 3));
            pitchButton.setFont(this.config.getFont());
            pitchButton.setForeground(this.config.getTextColor());
            pitchButton.setBackground(this.config.getBackgroundColor());
            final var image = pitch.resizedImage();
            if (image.isPresent()) {
                pitchButton.setIcon(new ImageIcon(image.get()));
            }
            pitchButton.setToolTipText(pitch.pitch().getVisibleText());

            final var length = 50;
            final var x = (int) Math.round((pitch.x + 90d) / 180d * availableWidth * 0.8 + availableWidth * 0.1 - length / 2d);
            final var y = (int) Math.round((pitch.y + 90d) / 180d * availableHeight * 0.8 + availableHeight * 0.1 - length / 2d);

            pitchButton.setBounds(x, y, length, length);
            pitchButton.addActionListener(e -> this.fireGUIEvent(new GUIEvent(PitchSelectorComponent.this, Commands.PITCH_SELECTED, pitch.pitch)));
            this.mapComponent.add(pitchButton);
        }
    }

    private void initUI() {
        this.mapComponent = new ImagePanel(this.mapImage);
        this.mapComponent.setLayout(null);
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent evt) {
                PitchSelectorComponent.this.dimension = evt.getComponent().getSize();
                buildUIPitch();
            }
        });

        this.setLayout(new BorderLayout());
        this.add(this.mapComponent, BorderLayout.CENTER);
    }

    public record Pitch(double x, double y, Optional<Image> image, IDepictable pitch) {
        private static Map<Pitch, Optional<Image>> cache = new HashMap<>();

        public Optional<Image> resizedImage() {
            return Pitch.cache.computeIfAbsent(this, p ->
                    p.image.map(i -> i.getScaledInstance(50, 50, Image.SCALE_SMOOTH))
            );
        }
    }

    private class ImagePanel extends JPanel {
        private BufferedImage image;

        public ImagePanel(final BufferedImage image) {
            this.image = image;
        }

        @Override
        protected void paintComponent(final Graphics g) {
            super.paintComponent(g);
            g.drawImage(this.image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
