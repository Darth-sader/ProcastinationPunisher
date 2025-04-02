import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ProcrastinationPunisher1 {
    private static List<String> productiveApps = new ArrayList<>();
    private static List<String> roasts = new ArrayList<>();
    private static List<String> praises = new ArrayList<>();
    
    private static boolean isCurrentlyRoasting = false;
    private static boolean isCurrentlyPraising = false;

    public static void main(String[] args) {
        loadRoastsAndPraises();
        setupUI();
        startMonitoring();
    }

    private static void setupUI() {
        JFrame frame = new JFrame("Procrastination Punisher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 350);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(45, 45, 45));

        JLabel titleLabel = new JLabel("Procrastination Punisher", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JTextArea productiveAppsArea = new JTextArea();
        productiveAppsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        productiveAppsArea.setBackground(new Color(60, 60, 60));
        productiveAppsArea.setForeground(Color.WHITE);
        productiveAppsArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JScrollPane scrollPane = new JScrollPane(productiveAppsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton saveButton = new JButton("Save Productive Apps");
        saveButton.setFont(new Font("Arial", Font.BOLD, 14));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(new Color(70, 130, 180));
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        saveButton.addActionListener(e -> {
            productiveApps = Arrays.asList(productiveAppsArea.getText().split("\\n"));
            JOptionPane.showMessageDialog(frame, "Productive apps saved!");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(45, 45, 45));
        buttonPanel.add(saveButton);

        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static void loadRoastsAndPraises() {
        roasts = readLinesFromFile("roasts.txt");
        praises = readLinesFromFile("praise.txt");
    }

    private static List<String> readLinesFromFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Could not read " + filename);
        }
        return lines;
    }

    private static void startMonitoring() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkRunningApps();
            }
        }, 0, 5000); // Check every 5 seconds
    }

    private static void checkRunningApps() {
        List<String> runningApps = getRunningProcesses();
        boolean hasUnproductive = false;
        boolean hasProductive = false;

        for (String app : runningApps) {
            if (productiveApps.contains(app)) {
                hasProductive = true;
            } else {
                hasUnproductive = true;
            }
        }

        if (hasUnproductive && !isCurrentlyRoasting) {
            isCurrentlyRoasting = true;
            isCurrentlyPraising = false;
            showPopup("🔥 ROASTED!", getRandomMessage(roasts), Color.RED);
        } else if (!hasUnproductive && hasProductive && !isCurrentlyPraising) {
            isCurrentlyPraising = true;
            isCurrentlyRoasting = false;
            showPopup("✅ Good Job!", getRandomMessage(praises), new Color(50, 205, 50));
        }
    }

    private static List<String> getRunningProcesses() {
        List<String> processes = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("tasklist");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length > 0) {
                    processes.add(parts[0]); // Process name
                }
            }
        } catch (IOException e) {
            System.out.println("Error fetching running processes.");
        }
        return processes;
    }

    private static void showPopup(String title, String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("OptionPane.background", color);
            UIManager.put("Panel.background", color);
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private static String getRandomMessage(List<String> messages) {
        if (messages.isEmpty()) return "Stay productive!";
        return messages.get(new Random().nextInt(messages.size()));
    }
}
