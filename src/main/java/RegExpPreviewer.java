import java.util.regex.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.concurrent.*;
import javax.swing.text.*;

public class RegExpPreviewer extends JFrame implements Runnable {
    JLabel statusLabel;
    JTextField regexpField;
    JTextPane contentToMatch;
    JPanel inputPanel;
    Font font18 = new Font(Font.SANS_SERIF, Font.PLAIN, 18);
    Font font16 = new Font(Font.SANS_SERIF, Font.PLAIN, 16);

    DefaultHighlighter.DefaultHighlightPainter highlightPainter;

    public RegExpPreviewer() {
        super("RegExp Previewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initMembers();
        registerListener();

        Container container = getContentPane();
        container.add( inputPanel, BorderLayout.SOUTH);
        container.add( new JScrollPane(contentToMatch), BorderLayout.CENTER);
        setSize(800,600);
        setLocation(100,100);
        setVisible(true);
    }

    private void initMembers() {
        statusLabel = new JLabel("Status: Healthy.");
        statusLabel.setFont( font16 );
        regexpField = new JTextField();
        contentToMatch = new JTextPane();
        highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
        contentToMatch.setText("Paste your text here");
        contentToMatch.setFont(font18);
        inputPanel = initInputPanel();
    }

    private void registerListener() {
        contentToMatch.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                SwingUtilities.invokeLater(RegExpPreviewer.this);
            }
        });
        regexpField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                SwingUtilities.invokeLater(RegExpPreviewer.this);
            }
        });
        this.addWindowListener( new WindowAdapter() {
            public void windowOpened( WindowEvent e ) {
                regexpField.requestFocus();
            }
        }); 
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
                    contentToMatch.getHighlighter().addHighlight(index, index + group.length(), highlightPainter);
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

    private JPanel initInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        JLabel inputLabel = new JLabel(" Input RegExp ");
        inputLabel.setFont( font18 );
        inputPanel.add( inputLabel, BorderLayout.WEST);
        regexpField.setFont( font18 );
        inputPanel.add(regexpField, BorderLayout.CENTER);
        return inputPanel;
    }

    public static void main(String... args) {
        new RegExpPreviewer();
    }
}