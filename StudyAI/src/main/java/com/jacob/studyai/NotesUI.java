package com.jacob.studyai;

import java.awt.BorderLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class NotesUI extends JFrame {
    private final NoteLoader loader   = new NoteLoader();
    private final TextCleaner cleaner = new TextCleaner();
    private final CohereClient ai      = new CohereClient();
    private final QAParser parser      = new QAParser();

    private final JTextArea notesArea   = new JTextArea();
    private final JButton   btnLoad     = new JButton("Load Notes");
    private final JButton   btnGenerate = new JButton("Generate Q&A");

    public NotesUI() {
        super("StudyAI — Notes");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Top control panel with Load and Generate buttons
        JPanel top = new JPanel();
        top.setBorder(new EmptyBorder(5, 5, 5, 5));
        top.add(btnLoad);
        top.add(btnGenerate);

        // Notes text area
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setFont(notesArea.getFont().deriveFont(14f));
        JScrollPane scroll = new JScrollPane(notesArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Enter or load your notes"));

        getContentPane().add(top,    BorderLayout.NORTH);
        getContentPane().add(scroll, BorderLayout.CENTER);

        // Action listeners
        btnLoad.addActionListener(e -> loadNotesFromFile());
        btnGenerate.addActionListener(e -> generateQAAsync());
    }

    private void loadNotesFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt", "md"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Path path      = chooser.getSelectedFile().toPath();
                String text    = loader.loadFromTextFile(path);
                notesArea.setText(cleaner.normalize(text));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to load notes: " + ex.getMessage(),
                    "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void generateQAAsync() {
        String raw = notesArea.getText().trim();
        if (raw.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter or load some notes first.",
                "No Notes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnLoad.setEnabled(false);
        btnGenerate.setEnabled(false);

        new SwingWorker<List<QAParser.QA>, Void>() {
            private Exception error;
            @Override
            protected List<QAParser.QA> doInBackground() {
                try {
                    String cleaned = cleaner.normalize(raw);
                    String json    = ai.generateQuestions(cleaned);
                    return parser.parse(json);
                } catch (Exception ex) {
                    this.error = ex;
                    return null;
                }
            }
            @Override
            protected void done() {
                btnLoad.setEnabled(true);
                btnGenerate.setEnabled(true);
                if (error != null) {
                    JOptionPane.showMessageDialog(NotesUI.this,
                        "Error generating Q&A:\n" + error.getMessage(),
                        "Generation Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    List<QAParser.QA> qas = get();
                    if (qas == null || qas.isEmpty()) {
                        JOptionPane.showMessageDialog(NotesUI.this,
                            "No questions parsed from the response.",
                            "No Q&A", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    new QAUI(qas).setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(NotesUI.this,
                        "Unexpected error:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NotesUI().setVisible(true);
        });
    }
}
