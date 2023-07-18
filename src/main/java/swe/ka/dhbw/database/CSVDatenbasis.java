package swe.ka.dhbw.database;

import de.dhbwka.swe.utils.model.ICSVPersistable;
import de.dhbwka.swe.utils.util.CSVReader;
import de.dhbwka.swe.utils.util.CSVWriter;
import de.dhbwka.swe.utils.util.FileEncoding;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class CSVDatenbasis implements Datenbasis<ICSVPersistable> {
    private final Path directory;
    private final Map<String, CSVWriter> writers = new HashMap<>();
    private final Map<String, CSVReader> readers = new HashMap<>();

    public CSVDatenbasis(final Path directory) throws IOException {
        this.directory = directory;
        this.init();
    }

    @Override
    public void create(final Class<?> c, ICSVPersistable data) throws IOException {
        if (!this.isInitialized(c)) {
            this.write(c, List.of(data), Optional.empty());
            return;
        }

        final var entities = this.read(c);
        entities.add(data);
        this.write(c, entities, Optional.empty());
    }

    @Override
    public void delete(final Class<?> c, final ICSVPersistable data) throws IOException {
        if (!this.isInitialized(c)) {
            return;
        }

        final var entities = this.read(c);

        if (entities.size() == 0) {
            return;
        }

        final var header = entities.get(0).getCSVHeader();
        entities.remove(data);
        this.write(c, entities, Optional.of(header));
    }

    @Override
    public List<ICSVPersistable> read(final Class<?> c) throws IOException {
        if (!this.isInitialized(c)) {
            return new ArrayList<>();
        }

        final var reader = this.getReader(c);

        final var header = reader.readHeader(CSVReader.DEFAULT_DELIMITER,
                String.valueOf(CSVReader.DEFAULT_COMMENT),
                FileEncoding.UTF_8.getName());
        return reader.readData(
                header.length,
                CSVReader.DEFAULT_DELIMITER,
                CSVReader.DEFAULT_COMMENT,
                FileEncoding.UTF_8.getName()
        ).stream().map(data -> new ICSVPersistable() {
            @Override
            public String[] getCSVData() {
                return data;
            }

            @Override
            public String[] getCSVHeader() {
                return header;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public void update(final Class<?> c, final ICSVPersistable data) throws IOException {
        if (!this.isInitialized(c)) {
            return;
        }

        final var entities = this.read(c);
        if (entities.size() == 0) {
            return;
        }

        final var index = entities.indexOf(data);
        if (index == -1) {
            return;
        }

        entities.set(index, data);
        this.write(c, entities, Optional.empty());
    }

    @Override
    public void upsert(final Class<?> c, final ICSVPersistable data) throws IOException {
        if (!this.isInitialized(c)) {
            this.write(c, List.of(data), Optional.empty());
            return;
        }

        if (this.read(c).indexOf(data) == -1) {
            this.create(c, data);
            return;
        }

        this.update(c, data);
    }

    public void init() throws IOException {
        Files.createDirectories(directory);
    }

    private CSVReader getReader(final Class<?> c) throws IOException {
        if (this.readers.containsKey(c.getName())) {
            return this.readers.get(c.getName());
        }

        final var csvFile = this.directory.resolve(c.getSimpleName() + ".csv");
        if (!Files.exists(csvFile)) {
            Files.createFile(csvFile);
        }
        final var reader = new CSVReader(csvFile.toString());
        this.readers.put(c.getName(), reader);
        return reader;
    }

    private CSVWriter getWriter(final Class<?> c) {
        if (this.writers.containsKey(c.getName())) {
            return this.writers.get(c.getName());
        }

        final var writer = new CSVWriter(this.directory.resolve(c.getSimpleName() + ".csv").toString(), true);
        this.writers.put(c.getName(), writer);
        return writer;
    }

    private boolean isInitialized(final Class<?> c) throws IOException {
        final var filePath = this.directory.resolve(c.getSimpleName() + ".csv");
        if (!Files.exists(filePath)) {
            return false;
        }

        return Files.size(filePath) > 0;
    }

    private void write(final Class<?> c,
                       final List<ICSVPersistable> entities,
                       final Optional<String[]> header) throws IOException {
        final var writer = this.getWriter(c);
        final var lines = entities.stream()
                .map(ICSVPersistable::getCSVData)
                .map(l -> Arrays.stream(l).map(Objects::toString).toArray())
                .collect(Collectors.toList());

        writer.writeDataToFile(
                lines,
                header.orElse(entities.get(0).getCSVHeader()),
                CSVWriter.DEFAULT_DELIMITER,
                CSVWriter.DEFAULT_COMMENT,
                FileEncoding.UTF_8.getName()
        );
    }
}
