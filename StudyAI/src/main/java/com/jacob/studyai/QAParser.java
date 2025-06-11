package com.jacob.studyai;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses AI output of the form:
 *
 * 1. Question? - Answer.
 */
public class QAParser {
    public static class QA {
        public final String question;
        public final String answer;
        public QA(String q, String a) {
            this.question = q;
            this.answer   = a;
        }
        @Override
        public String toString() {
            return "Q: " + question + "\nA: " + answer + "\n";
        }
    }

    /**
     * Extracts lines starting with "n. " and splits at the first " - ".
     */
    public List<QA> parse(String aiResponse) {
        List<QA> list = new ArrayList<>();
        String[] lines = aiResponse.split("\\r?\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.matches("^\\d+\\..*")) {
                // strip "1. "
                String content = line.replaceFirst("^\\d+\\.\\s*", "");
                // split on the first " - "
                String[] parts = content.split("\\s-\\s", 2);
                if (parts.length == 2) {
                    list.add(new QA(parts[0].trim(), parts[1].trim()));
                }
            }
        }
        return list;
    }
}
