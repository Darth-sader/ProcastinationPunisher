import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import javax.swing.*;

public class FinalProcrastinationPunisher {
    private static List<String> productiveApps = new ArrayList<>();
    private static List<String> roasts = new ArrayList<>();
    private static List<String> praises = new ArrayList<>();
    
    private static boolean isCurrentlyRoasting = false;
    private static boolean isCurrentlyPraising = false;
    private static final String APP_FILE = "app_classification.txt"; 
    private static final List<String> SYSTEM_PROCESSES = List.of(
            "explorer.exe", "svchost.exe", "runtimebroker.exe", "taskhostw.exe", 
            "searchhost.exe", "startmenuexperiencehost.exe", "widgets.exe", "dllhost.exe", 
            "conhost.exe", "powershell.exe", "dataexchangehost.exe", "smartscreen.exe",
            "lpint.exe", "lpbint.exe", "lockapp.exe", "sentinelui.exe", "rtkauduservice64.exe",
            "securityhealthsystray.exe", "shellhost.exe", "shellexperiencehost.exe", 
            "phoneexperiencehost.exe", "widgetservice.exe", "monotificationux.exe",
            "msedgewebview2.exe", "analyticssrv.exe", "concentr.exe", "receiver.exe", 
            "selfserviceplugin.exe", "wfcrun32.exe", "redirector.exe", "adobecollabsync.exe"
    );

    private static int roastIndex = 0;
    private static int complimentIndex = 0;
    private static int complimentY = 20;
    private static int roastY = 20;
    private static final int SPACING = 10;
    private static final int MAX_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height - 120;
    private static final int MAX_WIDTH = 600; // Max allowed width

    private static Timer praiseTimer;
    private static Timer roastTimer;

    public static void main(String[] args) {
        loadRoastsAndPraises();
        setupInitialUI();
    }

    private static void setupInitialUI() {
        JFrame frame = new JFrame("Procrastination Punisher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 200);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(45, 45, 45));

        JButton startSessionButton = new JButton("Start Session");
        startSessionButton.setFont(new Font("Arial", Font.BOLD, 20));
        startSessionButton.setForeground(Color.WHITE);
        startSessionButton.setBackground(new Color(70, 130, 180));
        startSessionButton.setFocusPainted(false);
        startSessionButton.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        startSessionButton.addActionListener(e -> setupProductiveAppsUI(frame));

        frame.add(startSessionButton, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void setupProductiveAppsUI(JFrame parentFrame) {
        JFrame frame = new JFrame("Set Productive Apps/Websites");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(450, 350);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(45, 45, 45));

        JTextArea productiveAppsArea = new JTextArea();
        productiveAppsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        productiveAppsArea.setBackground(new Color(60, 60, 60));
        productiveAppsArea.setForeground(Color.WHITE);
        productiveAppsArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JScrollPane scrollPane = new JScrollPane(productiveAppsArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton addButton = new JButton("Add App/Web");
        JButton removeButton = new JButton("Remove App/Web");
        JButton startGrindButton = new JButton("Locked in. Let's start the grind!");

        startGrindButton.setBackground(new Color(173, 216, 230)); // Light blue
        startGrindButton.setForeground(Color.BLACK); // Black text for better contrast
        startGrindButton.setFocusPainted(false); // Removes focus outline
        startGrindButton.setFont(new Font("Arial", Font.BOLD, 14)); 

        addButton.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "Enter app link or executable path:");
            if (input != null && !input.trim().isEmpty()) {
                productiveApps.add(input.trim());
                productiveAppsArea.setText(String.join("\n", productiveApps));
            }
        });

        removeButton .addActionListener(e -> {
            String input = JOptionPane.showInputDialog(frame, "Enter app link or executable path to remove:");
            if (input != null && productiveApps.remove(input.trim())) {
                productiveAppsArea.setText(String.join("\n", productiveApps));
            } else {
                JOptionPane.showMessageDialog(frame, "App not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        startGrindButton.addActionListener(e -> {
            if (!productiveApps.isEmpty()) {
                parentFrame.dispose();
                frame.dispose();
                startMonitoring();
                startPraiseAndRoastTimers();
            } else {
                JOptionPane.showMessageDialog(frame, "Please add at least one productive app!", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(45, 45, 45));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(startGrindButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.pack(); 
        frame.setSize(new Dimension(frame.getWidth(), Math.max(frame.getHeight(), 350))); 
        frame.setResizable(true);  
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
    }

    private static void startPraiseAndRoastTimers() {
        praiseTimer = new Timer();
        roastTimer = new Timer();

        praiseTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isCurrentlyPraising) {
                    showPopup("✅ Good Job!", getRandomMessage(praises), new Color(50, 205, 50));
                }
            }
        }, 0, 900000); // Every 15 minutes

        roastTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isCurrentlyRoasting) {
                    showPopup("🔥 ROASTED!", getRandomMessage(roasts), Color.RED);
                }
            }
        }, 0, 900000); // Every 15 minutes
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
        } else if (!hasUnproductive && hasProductive && !isCurrentlyPraising) {
            isCurrentlyPraising = true;
            isCurrentlyRoasting = false;
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
            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setUndecorated(true);
    
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(color);
    
            JLabel label = new JLabel("<html><div style='text-align:center; font-size:16px; color:white; padding:10px;'>" + message + "</div></html>");
            label.setHorizontalAlignment(SwingConstants.CENTER);
    
            JButton closeButton = new JButton(title.equals("🔥 ROASTED!") ? "Ugh, alright" : "Yay, thanks");
            closeButton.addActionListener(e -> frame.dispose());
    
            panel.add(label, BorderLayout.CENTER);
            panel.add(closeButton, BorderLayout.SOUTH);
    
            frame.add(panel);
            frame.pack();
    
            // Adjust width dynamically but limit to MAX_WIDTH
            FontMetrics metrics = label.getFontMetrics(label.getFont());
            int textWidth = metrics.stringWidth(message) + 40;
            int adjustedWidth = Math.min(textWidth, MAX_WIDTH);
            int adjustedHeight = frame.getPreferredSize().height;
    
            frame.setSize(adjustedWidth, adjustedHeight);
    
            // Positioning logic
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (title.equals("🔥 ROASTED!")) {
                frame.setLocation(screenSize.width - adjustedWidth - 20, roastY);
                roastY += adjustedHeight + SPACING;
                if (roastY > MAX_HEIGHT) roastY = 20;
            } else {
                frame.setLocation(20, complimentY);
                complimentY += adjustedHeight + SPACING;
                if (complimentY > MAX_HEIGHT) complimentY = 20;
            }
    
            frame.setAlwaysOnTop(true);
            frame.setVisible(true);
        });
    }

    private static String getRandomMessage(List<String> messages) {
        if (messages.isEmpty()) return "Stay productive!";
        return messages.get(new Random().nextInt(messages.size()));
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
}