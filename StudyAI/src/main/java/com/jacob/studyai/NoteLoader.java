package com.jacob.studyai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NoteLoader {

    /** Load plain-text notes from a file. */
    public String loadFromTextFile(Path path) throws IOException {
        return Files.readString(path);
    }


}
