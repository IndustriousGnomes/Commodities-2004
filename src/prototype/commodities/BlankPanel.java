package prototype.commodities;

// This is a temporary class used to hold panel space in layouts.
import java.awt.*;
import javax.swing.*;

public class BlankPanel extends JPanel {
    String msg = "";
    public BlankPanel (Dimension pSize, Dimension mSize) {
        setBackground(new Color((int)(Math.random() * 256*256*256)));
        setPreferredSize(pSize);
        setMinimumSize(mSize);
    }

    public BlankPanel (Dimension mSize) {
        setBackground(new Color((int)(Math.random() * 256*256*256)));
        setPreferredSize(mSize);
        setMinimumSize(mSize);
    }

    public BlankPanel (Color c, Dimension mSize) {
        setBackground(c);
        setPreferredSize(mSize);
        setMinimumSize(mSize);
    }

    public BlankPanel (Color c, Dimension pSize, Dimension mSize) {
        setBackground(c);
        setPreferredSize(pSize);
        setMinimumSize(mSize);
    }

    public BlankPanel (Dimension pSize, Dimension mSize, String msg) {
        setBackground(new Color((int)(Math.random() * 256*256*256)));
        setPreferredSize(pSize);
        setMinimumSize(mSize);
        this.msg = msg;
    }

    public BlankPanel (Dimension mSize, String msg) {
        setBackground(new Color((int)(Math.random() * 256*256*256)));
        setPreferredSize(mSize);
        setMinimumSize(mSize);
        this.msg = msg;
    }

    public BlankPanel (Color c, Dimension mSize, String msg) {
        setBackground(c);
        setPreferredSize(mSize);
        setMinimumSize(mSize);
        this.msg = msg;
    }

    public BlankPanel (Color c, Dimension pSize, Dimension mSize, String msg) {
        setBackground(c);
        setPreferredSize(pSize);
        setMinimumSize(mSize);
        this.msg = msg;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);
        Dimension sz = getSize();
        g.drawLine(0,0,sz.width,sz.height);
        g.drawLine(sz.width,0,0,sz.height);
        g.drawString(msg, 0, sz.height-10);
    }
}