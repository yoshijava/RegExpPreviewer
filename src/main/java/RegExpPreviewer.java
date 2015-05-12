import java.util.regex.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.concurrent.*;
import javax.swing.text.*;

public class RegExpPreviewer extends JFrame implements Runnable {
    JLabel statusLabel = new JLabel("Status: Healthy.");
    JTextField regexpField = new JTextField();
    JTextPane contentToMatch = new JTextPane();

    DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);

    public RegExpPreviewer() {
        super("RegExp Previewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = getContentPane();
        // container.setLayout(new BorderLayout());
        container.add( initInputComponent(), BorderLayout.SOUTH);
        container.add( new JScrollPane(initJTextPane()), BorderLayout.CENTER);
        container.add( statusLabel, BorderLayout.NORTH);
        regexpField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                SwingUtilities.invokeLater(RegExpPreviewer.this);
            }
        });
        setSize(500,400);
        setLocation(100,100);
        setVisible(true);
    }

    private JTextPane initJTextPane() {
        contentToMatch.setText("Paste your text here");
        // contentToMatch.setLineWrap(true);
        contentToMatch.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                SwingUtilities.invokeLater(RegExpPreviewer.this);
            }
        });
        return contentToMatch;
    }

    public synchronized void run() {
        String regexp = regexpField.getText();
        String content = contentToMatch.getText();
        Vector<String> result = new Vector<String>();
        contentToMatch.getHighlighter().removeAllHighlights();
        int prevIndex = 0;
        try {
            Pattern p = Pattern.compile(regexp);
            Matcher m = p.matcher(content);
            while(m.find()) {
                String group = m.group();
                // if (group.trim().equals("")) {
                //     continue;
                // }
                int index = 0;
                if( (index = content.indexOf(group, prevIndex)) != -1) {
                    contentToMatch.getHighlighter().addHighlight(index, index + group.length(), DefaultHighlighter.DefaultPainter);
                    prevIndex = index + group.length();
                }
            }
            statusLabel.setText("Status: Healthy.");
        }
        catch(BadLocationException ble) {
            // empty
            ble.printStackTrace();
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