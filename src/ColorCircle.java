import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class ColorCircle extends JPanel {

	private static final long serialVersionUID = 1L;
	private Color color;
	private String number;
	private int x, y, diameter;
	private Font font;
	
	public ColorCircle(int diameter, int x, int y, int number, Color color) {
		this.diameter = diameter;
		this.x = x;
		this.y = y;
		this.setSize(diameter, diameter);
		this.setLocation(x - 20, y); //No idea why I needed -20, should just be X.
		this.number = "" + number;
		this.color = color;
		this.setOpaque(false);
		this.font = new Font("SansSerif", Font.PLAIN, 40);
	}

	public void draw(Graphics2D g2d) {
		super.paintComponent((Graphics) g2d);
		g2d.setColor(color);
		g2d.fillOval(x, y, diameter, diameter);

		g2d.setColor(new Color(24, 24, 24));
		g2d.drawOval(x, y, diameter, diameter);

		g2d.setFont(font);
		g2d.setColor(new Color(24, 24, 24));
		FontMetrics fontMetrics = g2d.getFontMetrics(font);
		g2d.drawString(number, x + diameter / 2 - ((fontMetrics.stringWidth(number)) / 2),
				y + diameter / 2 + ((fontMetrics.getHeight()) / 4));

	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setNumber(int number) {
		this.number = "" + number;
	}

	public void updatePosition(int x, int y) {
		this.x = x;
		this.y = y;
		this.setLocation(x, y);
	}
}
