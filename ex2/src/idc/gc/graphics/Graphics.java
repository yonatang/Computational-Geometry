package idc.gc.graphics;

import idc.gc.dt.Circle;
import idc.gc.dt.Point;

import java.awt.BorderLayout;
import java.util.Set;

import javax.swing.JFrame;

public class Graphics implements Bus {

	private Set<Point> points;
	private Set<Circle> circles;
	private String title;

	private TextComponent sizeMessagePanel;

	public void showSize(int size) {
		sizeMessagePanel.showSize(size);
	}

	public Graphics(Set<Point> points, Set<Circle> circles, String title) {
		this.points = points;
		this.circles = circles;
		this.title = title;
	}

	public void show() {
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(410, 410);
		BorderLayout bl = new BorderLayout();
		frame.setLayout(bl);
		sizeMessagePanel = new TextComponent(this);
		frame.add(new CanvasComponent(this, points, circles), BorderLayout.CENTER);
		frame.add(sizeMessagePanel, BorderLayout.SOUTH);
		frame.setVisible(true);
	}
}
