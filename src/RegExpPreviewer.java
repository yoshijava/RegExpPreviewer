import java.util.regex.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.concurrent.*;

public class RegExpPreviewer extends JFrame implements Runnable {
    JLabel statusLabel = new JLabel("Status: Healthy.");
    JTextField regexpField = new JTextField();
    JTextArea contentToMatch = new JTextArea();
    JTextArea resultArea = new JTextArea();

    public RegExpPreviewer() {
        super("RegExp Previewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = getContentPane();
        // container.setLayout(new BorderLayout());
        container.add( initInputComponent(), BorderLayout.SOUTH);
        container.add( initTextArea(), BorderLayout.CENTER);
        container.add( statusLabel, BorderLayout.NORTH);
        resultArea.setSize(100, 10);
        container.add( resultArea, BorderLayout.EAST);
        regexpField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                SwingUtilities.invokeLater(RegExpPreviewer.this);
            }
        });
        setSize(500,400);
        setLocation(100,100);
        setVisible(true);
    }

    private JTextArea initTextArea() {
        contentToMatch.setText("Paste your text here");
        return contentToMatch;
    }

    public synchronized void run() {
        String regexp = regexpField.getText();
        String content = contentToMatch.getText();
        Vector<String> result = new Vector<String>();
        try {
            Pattern p = Pattern.compile(regexp);
            Matcher m = p.matcher(content);
            while(m.find()) {
                String group = m.group();
                result.add(group);
            }
            String resultToShow = "";
            for(String r : result) {
                resultToShow += (r + "\n");
            }
            System.out.println(resultToShow);
            resultArea.setText(resultToShow);

            statusLabel.setText("Status: Healthy.");
        }
        catch(java.util.regex.PatternSyntaxException e) {
            statusLabel.setText("Pattern Syntax Exception.");
        }
        catch(OutOfMemoryError error) {
            statusLabel.setText("Wrong regexp caused OutOfMemoryError. Please fix your regexp.");
        }
    }

    private JPanel initInputComponent() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(new JLabel(" Input RegExp "), BorderLayout.WEST);
        inputPanel.add(regexpField, BorderLayout.CENTER);
        return inputPanel;
    }

    public static void main(String... args) {
        new RegExpPreviewer();
    }
}