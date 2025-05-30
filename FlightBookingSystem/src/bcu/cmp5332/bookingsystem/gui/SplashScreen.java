package bcu.cmp5332.bookingsystem.gui;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    private int duration;

    public SplashScreen(int duration) {
        this.duration = duration;
    }

    // A simple method to show a splash screen with a given image and duration
    public void showSplash() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);

        // Could load an image here, e.g., "logo.png"
        // ImageIcon splashIcon = new ImageIcon(getClass().getResource("/images/logo.png"));
        // JLabel splashLabel = new JLabel(splashIcon);
        // content.add(splashLabel, BorderLayout.CENTER);

        JLabel textLabel = new JLabel("Flight Booking System - Loading...", SwingConstants.CENTER);
        textLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        textLabel.setForeground(new Color(50, 150, 200)); // A nice blue color
        content.add(textLabel, BorderLayout.CENTER); // Using text for simplicity

        // Add a border to the panel
        content.setBorder(BorderFactory.createLineBorder(new Color(50, 150, 200), 5));

        // Get the screen size
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 400;
        int height = 250;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, width, height);

        setContentPane(content);
        setVisible(true);

        try {
            Thread.sleep(duration); // Keep the splash screen visible for 'duration' milliseconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setVisible(false); // Hide the splash screen
        dispose();          // Release resources
    }
}