package com.jacob.studyai;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {
    private final NoteLoader loader;
    private final TextCleaner cleaner;
    private final CohereClient ai;
    private final QAParser parser;
    private String lastNotes = "";
    private List<QAParser.QA> lastQA = null;

    public ConsoleMenu(NoteLoader loader, TextCleaner cleaner,
                       CohereClient ai, QAParser parser) {
        this.loader = loader;
        this.cleaner = cleaner;
        this.ai = ai;
        this.parser = parser;
    }

    public void start() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== StudyAI Menu ===");
            System.out.println("1) Load notes from text file");
            System.out.println("2) Load notes from image (OCR)");
            System.out.println("3) Generate study Q&A");
            System.out.println("4) View last Q&A");
            System.out.println("5) Exit");
            System.out.print("Choose> ");
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        System.out.print("Path to .txt: ");
                        lastNotes = loader.loadFromTextFile(Path.of(sc.nextLine().trim()));
                        System.out.println("Loaded text notes.");
                        break;
                    case "3":
                        if (lastNotes == null || lastNotes.isBlank()) {
                            System.out.println("No notes loaded. Please load notes first.");
                            break;
                        }
                        String cleaned = cleaner.normalize(lastNotes);
                        String resp = ai.generateQuestions(cleaned);
                        lastQA = parser.parse(resp);
                        System.out.println("Generated Q&A:");
                        lastQA.forEach(q -> System.out.println(q));
                        break;
                    case "4":
                        if (lastQA == null) {
                            System.out.println("No Q&A yet. Generate first.");
                        } else {
                            lastQA.forEach(q -> System.out.println(q));
                        }
                        break;
                    case "5":
                        System.out.println("Goodbye!");
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception ex) {
                System.err.println("Error: " + ex.getMessage());
            }
        }
    }
}
