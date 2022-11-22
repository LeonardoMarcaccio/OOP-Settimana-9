package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final int TIMER = 10_000;
    private static final double SCALE = 0.5;
    private final JLabel display = new JLabel();
    private final JPanel panel = new JPanel();
    private final JButton up = new JButton("Up");
    private final JButton down = new JButton("Down");
    private final JButton stop = new JButton("Stop");

    /**
     * Constructor Method for my GUI. 
     */
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * SCALE), (int) (screenSize.getHeight() * SCALE));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);

        final Agent agent = new Agent();
        new Thread(agent).start();

        new Thread(new Runnable() {
            @Override
            public void run() { //NOPMD: not required
                try {
                    Thread.sleep(TIMER);
                    agent.stopCounting();
                } catch (Exception ex) { //NOPMD: suppressed because it's an exercise
                    ex.printStackTrace(); //NOPMD: suppressed because it's an exercise
                }
            }
        }).start();

        this.getContentPane().add(panel);
        this.setVisible(true);

        stop.addActionListener((e) -> {
            agent.stopCounting();
        });
        up.addActionListener((e) -> agent.setUp());
        down.addActionListener((e) -> agent.setDown());
    }

    /**
     * A Method used to disable the Jbuttons on my GUI.
     */
    public void shut() {
        this.stop.setEnabled(false);
        this.up.setEnabled(false);
        this.down.setEnabled(false);
    }

    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean direction = true;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if (this.direction) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace(); //NOPMD: suppressed because it's an exercise
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
            try {
                AnotherConcurrentGUI.this.shut();
            } catch (Exception e) { //NOPMD: suppressed because it's an exercise
                e.printStackTrace(); //NOPMD: suppressed because it's an exercise
            }
        }

        public void setUp() { 
            this.direction = true;
        }

        public void setDown() {
            this.direction = false;
        }
    }
}

