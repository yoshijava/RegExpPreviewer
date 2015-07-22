import java.util.regex.*;
import javafx.event.*;
import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.scene.*;
import javafx.application.*;

public class RegExpPreviewer extends Application {
    Label statusLabel;
    TextArea contentToMatch;
    Pane inputPanel;
    TextField regexpField;

    Font font18 = new Font(18);
    Font font16 = new Font(16);

    private void initMembers() {
        statusLabel = new Label("Status: Healthy.");
        statusLabel.setFont( font16 );
        contentToMatch = new TextArea();
        // highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
        contentToMatch.setText("Paste your text here");
        // contentToMatch.setFont(font18);
        inputPanel = initInputPanel();
    }

    private void registerListener() {
        // contentToMatch.addKeyListener(new KeyAdapter() {
        //     public void keyTyped(KeyEvent e) {
        //         SwingUtilities.invokeLater(RegExpPreviewer.this);
        //     }
        // });
        // regexpField.addKeyListener(new KeyAdapter() {
        //     public void keyTyped(KeyEvent e) {
        //         SwingUtilities.invokeLater(RegExpPreviewer.this);
        //     }
        // });
        // this.addWindowListener( new WindowAdapter() {
        //     public void windowOpened( WindowEvent e ) {
        //         regexpField.requestFocus();
        //     }
        // });
    }

    private Pane initInputPanel() {
        BorderPane inputPanel = new BorderPane();
        // inputPanel.setLayout(new BorderLayout());

        Label inputLabel = new Label(" Input RegExp ");
        inputLabel.setFont( font18 );
        regexpField = new TextField();
        regexpField.setFont( font18 );

        inputPanel.setLeft(inputLabel);
        inputPanel.setCenter(regexpField);
        return inputPanel;
    }

    @Override
    public void start(Stage primaryStage) {
        initMembers();
        registerListener();
        StackPane root = new StackPane();
        BorderPane bp = new BorderPane();
        bp.setBottom(inputPanel);
        bp.setCenter(contentToMatch);
        bp.setTop(statusLabel);
        root.getChildren().add(bp);

        Scene scene = new Scene(root, 800, 600);

        installEventHandler(contentToMatch);
        installEventHandler(regexpField);
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void installEventHandler(final Node keyNode) {
        // handler for enter key press / release events, other keys are
        // handled by the parent (keyboard) node handler
        final EventHandler<KeyEvent> keyEventHandler =
            new EventHandler<KeyEvent>() {
                public void handle(final KeyEvent keyEvent) {
                    Platform.runLater(new SearchRunnable());
                    // keyEvent.consume();
                }
            };

        keyNode.setOnKeyTyped(keyEventHandler);
    }

    public static void main(String... args) {
        new RegExpPreviewer().launch();
    }


    private class SearchRunnable implements Runnable {
        public synchronized void run() {
            String regexp = regexpField.getText();
            String content = contentToMatch.getText();
            // contentToMatch.getHighlighter().removeAllHighlights();
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
                        // contentToMatch.getHighlighter().addHighlight(index, index + group.length(), highlightPainter);
                        // TODO: Need to find a way to highlight text with JavaFX's TextArea
                        contentToMatch.selectRange(index, index+group.length());
                        prevIndex = index + group.length();
                    }
                }
                statusLabel.setText("Status: Healthy.");
            }
            catch(java.util.regex.PatternSyntaxException e) {
                statusLabel.setText("Pattern Syntax Exception.");
            }
            catch(OutOfMemoryError error) {
                statusLabel.setText("Wrong regexp caused OutOfMemoryError. Please fix your regexp.");
            }
        }
    }


}
