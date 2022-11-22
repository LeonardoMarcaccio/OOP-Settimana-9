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

    private final double scale = 0.5;
    private final JLabel display = new JLabel();
    private final JPanel panel = new JPanel();
    private final JButton up = new JButton("Up");
    private final JButton down = new JButton("Down");
    private final JButton stop = new JButton("Stop");

    public AnotherConcurrentGUI(){
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * scale), (int) (screenSize.getHeight() * scale));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);

        final Agent agent = new Agent();
        new Thread(agent).start();
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    agent.stopCounting();
                } catch (Exception ex) {
                    ex.printStackTrace();
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
                    ex.printStackTrace();
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
            try {
                AnotherConcurrentGUI.this.shut();
            } catch (Exception e) {
                e.printStackTrace();
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

