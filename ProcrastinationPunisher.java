import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class ProcrastinationPunisher {
    private List<String> roasts;
    private List<String> compliments;
    private int roastIndex = 0;
    private int complimentIndex = 0;
    
    public ProcrastinationPunisher() {
        loadMessages();
        startInactivityTimer();
    }
    
    private void loadMessages() {
        roasts = loadMessagesFromFile("roasts.txt");
        compliments = loadMessagesFromFile("praise.txt");
        System.out.println("Loaded " + roasts.size() + " roasts and " + compliments.size() + " compliments");
        Collections.shuffle(roasts);
        Collections.shuffle(compliments);
    }
    
    private List<String> loadMessagesFromFile(String filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            return lines.isEmpty() ? List.of("(No messages found)") : lines;
        } catch (IOException e) {
            System.out.println("Error loading " + filePath);
            return List.of("(Error loading messages)");
        }
    }
    
    private void startInactivityTimer() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (userIsInactive()) {
                    SwingUtilities.invokeLater(() -> displayMessage(true));
                } else if (userIsProductive()) {
                    SwingUtilities.invokeLater(() -> displayMessage(false));
                }
            }
        }, 5000, 15000); // First message in 5 sec, then every 15 sec
    }
    
    private boolean userIsInactive() {
        return true; // Placeholder
    }
    
    private boolean userIsProductive() {
        return false; // Placeholder
    }
    
    private void displayMessage(boolean isRoast) {
        String message = isRoast ? getNextRoast() : getNextCompliment();
        System.out.println("Displaying: " + message);
        
        JFrame frame = new JFrame("Procrastination Punisher");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setUndecorated(true);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(isRoast ? Color.RED : Color.GREEN);
        
        JLabel label = new JLabel("<html><div style='text-align:center; font-size:16px; color:white;'>" + message + "</div></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        
        JButton closeButton = new JButton(isRoast ? "Ugh, alright" : "Yay, thanks");
        closeButton.addActionListener(e -> frame.dispose());
        panel.add(closeButton, BorderLayout.SOUTH);
        
        frame.add(panel);
        frame.setSize(300, 100);
        
        // Positioning: Compliments (Left) | Roasts (Right)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = isRoast ? screenSize.width - 320 : 20;
        int y = new Random().nextInt(screenSize.height - 120);
        frame.setLocation(x, y);
        
        frame.setVisible(true);
    }
    
    private String getNextRoast() {
        if (roastIndex >= roasts.size()) {
            Collections.shuffle(roasts);
            roastIndex = 0;
        }
        return roasts.get(roastIndex++);
    }
    
    private String getNextCompliment() {
        if (complimentIndex >= compliments.size()) {
            Collections.shuffle(compliments);
            complimentIndex = 0;
        }
        return compliments.get(complimentIndex++);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProcrastinationPunisher::new);
    }
}