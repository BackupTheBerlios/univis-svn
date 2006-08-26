package unikn.dbis.univis.pivot.view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * @author Christian Gruen
 */
public class ScrollBarPanel extends JPanel {
    public static final Color DARK = new Color(64, 64, 64);
    public static final Color LIGHT = new Color(255, 255, 255);
    public static final Color BUTTON = new Color(208, 208, 208);
    public static final Color BACK = new Color(225, 225, 225);

    public static final int MAXSTEP = 15;
    // interaction variables
    private final static int[] STEPS = {-MAXSTEP, -14, -11, -8, -6, -4, -3,
            -2, -1, -1, 0, 0, 1, 1, 2, 3, 4, 6, 8, 11, 14, MAXSTEP};
    private int step = STEPS.length / 2;
    private boolean running;
    // width of scroll bar
    public static final int BARSIZE = 16;
    // public position & size indicators
    public int pos, height;
    // view reference
    private JComponent comp;
    // dimensions & positions
    private int ww, hh;
    // coordinates
    private int barPos, barSize, barOffset, dragPos;
    // interaction variables
    private boolean button, up, down, sliding, moving;

    public ScrollBarPanel(JComponent comp) {
        this.comp = comp;
        UniLayout.setWidth(this, BARSIZE);
        Mouse mouse = new Mouse();
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
        ww = BARSIZE;
    }

    public void paintComponent(Graphics g) {
        hh = getHeight();

        // paint scrollbar background
        g.setColor(BUTTON);
        g.drawLine(0, 0, 0, hh);
        g.setColor(BACK);
        g.fillRect(1, 0, ww - 1, hh);

        // draw scroll up button
        int x = 0, y = 0;
        drawButton(g, button && up, x, y, ww, ww);

        if (button && up) {
            x++;
            y++;
        }
        g.setColor(DARK);
        g.drawLine(x + 7, y + 5, x + 7, y + 5);
        g.drawLine(x + 6, y + 6, x + 8, y + 6);
        g.drawLine(x + 6, y + 7, x + 8, y + 7);
        g.drawLine(x + 5, y + 8, x + 9, y + 8);
        g.drawLine(x + 5, y + 9, x + 9, y + 9);

        // draw scroll down button
        x = 0;
        y = Math.max(BARSIZE, hh - ww);
        drawButton(g, button && down, x, y, ww, ww);

        if (button && down) {
            x++;
            y++;
        }
        g.setColor(DARK);
        g.drawLine(x + 7, y + 9, x + 7, y + 9);
        g.drawLine(x + 6, y + 8, x + 8, y + 8);
        g.drawLine(x + 6, y + 7, x + 8, y + 7);
        g.drawLine(x + 5, y + 6, x + 9, y + 6);
        g.drawLine(x + 5, y + 5, x + 9, y + 5);
        if (button && down) {
            x--;
            y--;
        }

        // calculate bar size
        int barH = hh - ww * 2;
        float factor = (barH - barOffset) / (float) height;
        int size = (int) (hh * factor);
        // define minimum size for scrollbar mover
        barOffset = size < 11 ? 11 - size : 0;
        size += barOffset;
        barPos = (int) Math.max(0, Math.min(pos * factor, barH - barSize - 1));
        barSize = Math.min(size, barH);

        // draw scroll slider
        drawButton(g, false, x, ww + barPos, ww, barSize);
    }

    void drawButton(Graphics g, boolean active, int x, int y, int w, int h) {
        Color col1 = active ? DARK : LIGHT;
        Color col2 = active ? LIGHT : DARK;

        g.setColor(BUTTON);
        g.fillRect(x, y, w, h);
        g.setColor(col1);
        g.drawLine(x + 1, y + 1, x + w - 2, y + 1);
        g.drawLine(x + 1, y + 1, x + 1, y + h - 2);
        g.setColor(col2);
        g.drawLine(x + w - 1, y + 2, x + w - 1, y + h - 1);
        g.drawLine(x + 2, y + h - 1, x + w - 1, y + h - 1);
    }

    class Mouse extends MouseInputAdapter {
        public void mousePressed(final MouseEvent e) {
            checkPosition(e.getPoint());
            // start dragging
            if (sliding || running) return;

            new Thread() {
                public void run() {
                    // scroll up/down/move slider
                    running = moving;
                    while (running) {
                        if (moving)
                            step = Math.max(0, Math.min(STEPS.length - 1,
                                    step + (down ? 1 : -1)));
                        else
                            step += step < STEPS.length / 2 ? 1 : -1;
                        int offset = STEPS[step];

                        if (!button) {
                            offset = offset * hh / MAXSTEP / 4;
                        }
                        pos = Math.max(0, Math.min(height - hh, pos + offset));
                        comp.repaint();
                        try {
                            Thread.sleep(25);
                        }
                        catch (Exception ex) {
                        }
                        running = step != STEPS.length / 2;
                    }
                }
            }.start();
        }

        public void mouseReleased(MouseEvent e) {
            up = false;
            down = false;
            moving = false;
            sliding = false;
            comp.repaint();
        }

        public void mouseDragged(MouseEvent e) {
            // no dragging...
            if (!sliding) return;

            pos = (e.getY() + dragPos) * height / (hh - ww * 2);
            pos = Math.max(0, Math.min(height - hh, pos));
            comp.repaint();
        }

        void checkPosition(Point p) {
            sliding = p.y > ww + barPos && p.y < ww + barPos + barSize;
            moving = !sliding;
            up = p.y < ww + barPos;
            down = p.y > ww + barPos + barSize;
            button = p.y < ww || p.y > hh - ww;
            if (sliding) dragPos = barPos - p.y;
        }
    }
}
