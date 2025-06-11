package com.jacob.studyai;

public class TextCleaner {

    /** Basic normalization: trim, collapse multiple spaces, ensure sentences end properly. */
    public String normalize(String raw) {
        if (raw == null) return "";
        String s = raw.trim();
        // collapse multiple whitespace into one space
        s = s.replaceAll("\\s+", " ");
        // ensure spacing after periods
        s = s.replaceAll("\\.([^\\s])", ". $1");
        return s;
    }
}
