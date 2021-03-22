import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class TopBar extends JPanel implements MouseListener{
	
	private static final long serialVersionUID = 1L;
	private Color backgroundColor = new Color(70, 70, 70);
	private Color foregroundColor = new Color(249, 65, 32);


	private JLabel colorTitle = new JLabel("COLORS");
	private JLabel settingsTitle = new JLabel("SETTINGS");
	private JLabel close = new JLabel("×");
	
	private Screen screen;
	private Image img;

	public TopBar(int width, int height, Screen screen) {
		this.screen = screen;
		ImageIcon img = new ImageIcon(getClass().getResource("images/gray.jpeg"));
		this.img = img.getImage();
		
		
		Font textFont = new Font("SansSerif", Font.BOLD, 15);
		Font iconFont = new Font("SansSerif", Font.PLAIN, 40);
		close.setFont(iconFont);
		this.setPreferredSize(new Dimension(width, height));
		this.setBackground(backgroundColor);
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 0, 0, 20);
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		close.setForeground(Color.WHITE);
		this.add(close, c);
		close.addMouseListener(this);
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LAST_LINE_START;
		
		c.insets = new Insets(0, 20, 0, 0);
		colorTitle.setForeground(foregroundColor);
		colorTitle.setOpaque(true);
		colorTitle.setBackground(new Color(50, 50, 50));
		colorTitle.setBorder(new EmptyBorder(5, 15, 5, 15));
		colorTitle.setFont(textFont);
		this.add(colorTitle, c);
		
		c.gridx = 1;
		settingsTitle.setForeground(foregroundColor);
		settingsTitle.setOpaque(true);
		settingsTitle.setBackground(new Color(50, 50, 50));
		settingsTitle.setBorder(new EmptyBorder(5, 15, 5, 15));
		settingsTitle.setFont(textFont);
		this.add(settingsTitle, c);
		repaint();

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(img, 0, -480, null);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		close.setForeground(Color.black);
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		close.setForeground(Color.WHITE);
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		screen.saveTofile();
		System.exit(0);
	}

}
