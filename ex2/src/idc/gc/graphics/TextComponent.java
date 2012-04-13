package idc.gc.graphics;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class TextComponent extends JComponent {

	public TextComponent(Bus bus) {
		setPreferredSize(new Dimension(100, 20));
	}

	String displayText = "";
	double x, y;

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawString(displayText, 125, 14);
		g.drawString(String.format("Location: %5.2f, %5.2f", x,y), 10, 14);
	}

	public void showSize(int size) {
		if (size >= 0) {
			displayText = "Group size: " + size;
		} else {
			displayText = "";
		}
		repaint();
	}

	public void showSize(double x, double y) {
		this.x=Math.max(0, Math.min(x, 100));
		this.y=Math.max(0, Math.min(y, 100));

	}
}
