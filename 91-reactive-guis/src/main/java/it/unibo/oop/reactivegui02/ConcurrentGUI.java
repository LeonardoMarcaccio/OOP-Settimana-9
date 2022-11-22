package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    private final double scale = 0.5;
    final JLabel display = new JLabel();
    
    public ConcurrentGUI(){
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * scale), (int) (screenSize.getHeight() * scale));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        final JPanel panel = new JPanel();
        panel.add(display);
        
        final JButton up = new JButton("Up");
        panel.add(up);
        
        final JButton down = new JButton("Down");
        panel.add(down);

        final JButton stop = new JButton("Stop");
        panel.add(stop);

        final Agent agent = new Agent();
        new Thread(agent).start();

        this.getContentPane().add(panel);
        this.setVisible(true);

        stop.addActionListener((e) -> {
            agent.stopCounting();
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
        up.addActionListener((e) -> agent.setUp());
        down.addActionListener((e) -> agent.setDown());
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
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
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
            setEnabled(false);
        }

        public void setUp() {
            this.direction = true;
        }

        public void setDown() {
            this.direction = false;
        }
    }
}
