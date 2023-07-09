package swe.ka.dhbw.database;

import java.io.IOException;
import java.util.List;

public interface Datenbasis<T> {
    void create(Class<?> c, T data) throws IOException;

    List<T> read(Class<?> c) throws IOException;

    void update(Class<?> c, T data) throws IOException;

    void delete(Class<?> c, T data) throws IOException;
}
