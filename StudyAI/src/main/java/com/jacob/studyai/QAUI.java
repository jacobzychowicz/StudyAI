package com.jacob.studyai;

import java.awt.Component;
import java.awt.Font;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * Window that displays a list of Q&A pairs;
 * each answer is hidden behind its own checkbox.
 */
public class QAUI extends JFrame {
    private final List<QAParser.QA> qas;

    public QAUI(List<QAParser.QA> qas) {
        super("StudyAI — Generated Q&A");
        this.qas = qas;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10,10,10,10));

        for (int i = 0; i < qas.size(); i++) {
            addQAPair(panel, qas.get(i), i+1);
        }

        JScrollPane scroll = new JScrollPane(panel);
        getContentPane().add(scroll);
    }

    private void addQAPair(JPanel panel, QAParser.QA qa, int index) {
        // Question
        JLabel qLabel = new JLabel(index + ". " + qa.question);
        qLabel.setFont(qLabel.getFont().deriveFont(Font.BOLD, 16f));
        qLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(qLabel);

        // Checkbox
        JCheckBox cb = new JCheckBox("Show Answer");
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(cb);

        // Prepare answer text: strip any “Suggested answer:” prefix
        String answerText = qa.answer.replaceFirst("(?i)^suggested answer:s*", "");

        // Answer area (wrapped, non-editable)
        JTextArea aText = new JTextArea(answerText);
        aText.setFont(aText.getFont().deriveFont(14f));
        aText.setLineWrap(true);
        aText.setWrapStyleWord(true);
        aText.setEditable(false);
        aText.setOpaque(false);
        aText.setBorder(new EmptyBorder(0, 20, 10, 0));
        aText.setAlignmentX(Component.LEFT_ALIGNMENT);
        aText.setVisible(false);
        panel.add(aText);

        // Toggle visibility
        cb.addActionListener(e -> {
            aText.setVisible(cb.isSelected());
            panel.revalidate();
        });

        panel.add(Box.createVerticalStrut(10));
    }
}
