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

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawString(displayText, 10, 10);
	}

	public void showSize(int size) {
		if (size >= 0) {
			displayText = "Group size: " + size;
		} else {
			displayText = "";
		}
		repaint();
	}
}
