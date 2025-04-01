import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class ProcrastinationPunisher {
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 100;
    private static final int SCREEN_PADDING = 20;
    private static final int VERTICAL_SPACING = 10;
    private static int roastY = SCREEN_PADDING;
    private static int complimentY = SCREEN_PADDING;
    private static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private static List<String> roasts;
    private static List<String> compliments;
    private static int roastIndex = 0;
    private static int complimentIndex = 0;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProcrastinationPunisher::loadMessages);
        new Timer(15000, e -> SwingUtilities.invokeLater(() -> displayMessage(new Random().nextBoolean()))).start();
    }
    
    private static void loadMessages() {
        roasts = loadMessagesFromFile("roasts.txt");
        compliments = loadMessagesFromFile("praise.txt");
        Collections.shuffle(roasts);
        Collections.shuffle(compliments);
    }
    
    private static List<String> loadMessagesFromFile(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            return lines.isEmpty() ? List.of("(No messages found)") : lines;
        } catch (IOException e) {
            return List.of("(Error loading messages)");
        }
    }
    
    private static void displayMessage(boolean isRoast) {
        JFrame frame = new JFrame("Procrastination Punisher");
        frame.setUndecorated(true);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLayout(new BorderLayout());
        frame.setAlwaysOnTop(true);
        
        String message = isRoast ? getNextRoast() : getNextCompliment();
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(isRoast ? Color.RED : Color.GREEN);
        
        JLabel label = new JLabel("<html><div style='text-align: center; font-size:14px; color:white;'>" + message + "</div></html>", SwingConstants.CENTER);
        JButton closeButton = new JButton(isRoast ? "Ugh, alright" : "Yay, thanks");
        closeButton.addActionListener(e -> frame.dispose());
        
        panel.add(label, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);
        frame.add(panel);
        
        int x = isRoast ? SCREEN_SIZE.width - WINDOW_WIDTH - SCREEN_PADDING : SCREEN_PADDING;
        int y = isRoast ? roastY : complimentY;
        
        frame.setLocation(x, y);
        frame.setVisible(true);
        
        if (isRoast) {
            roastY += WINDOW_HEIGHT + VERTICAL_SPACING;
            if (roastY + WINDOW_HEIGHT > SCREEN_SIZE.height) roastY = SCREEN_PADDING;
        } else {
            complimentY += WINDOW_HEIGHT + VERTICAL_SPACING;
            if (complimentY + WINDOW_HEIGHT > SCREEN_SIZE.height) complimentY = SCREEN_PADDING;
        }
    }
    
    private static String getNextRoast() {
        if (roastIndex >= roasts.size()) {
            Collections.shuffle(roasts);
            roastIndex = 0;
        }
        return roasts.get(roastIndex++);
    }
    
    private static String getNextCompliment() {
        if (complimentIndex >= compliments.size()) {
            Collections.shuffle(compliments);
            complimentIndex = 0;
        }
        return compliments.get(complimentIndex++);
    }
}
