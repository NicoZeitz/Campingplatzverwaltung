package swe.ka.dhbw.util;

import java.io.File;
import java.nio.file.Path;

public interface Archiver {
    File compressToArchive(final File[] files, final Path destination, final String filename);

    void extractArchive(final File archive, final Path destination);
}
