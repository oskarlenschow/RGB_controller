import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OtherSettings extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private Color backgroundColor = new Color(50, 50, 50);
	private Color foregroundColor = new Color(249, 65, 32);

	private JSlider speedSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 50, 1);
	private JScrollPane scrollPane;
	private SerialPorts serialPorts;

	private ButtonGroup buttonGroup = new ButtonGroup();
	private JToggleButton[] buttons = new JToggleButton[Screen.constants.length];

	public OtherSettings(int width, int height, ArrayList<Color> colors, int index) {
		serialPorts = new SerialPorts(colors, index);
		serialPorts.setMode(Screen.COLOR_FADE);
		this.setBackground(backgroundColor);
		this.setPreferredSize(new Dimension(width, height));
		this.setBorder(new EmptyBorder(20, 20, 20, 20));
		this.setLayout(new GridBagLayout());

		speedSlider.setMajorTickSpacing(10);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		speedSlider.setOpaque(false);

		speedSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				serialPorts.setSpeed(speedSlider.getValue());
			}

		});
		JLabel speedLabel = new JLabel("SPEED");
		speedLabel.setHorizontalAlignment(JLabel.CENTER);
		speedLabel.setOpaque(false);
		speedLabel.setForeground(foregroundColor);

		JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 2, 5));
		buttonPanel.setBackground(backgroundColor);
		
		JPanel buttonPanel2 = new JPanel(new GridLayout(2, 4, 2, 5));
		buttonPanel2.setBackground(backgroundColor);
		
		JPanel buttonPanel3 = new JPanel(new GridLayout(2, 4, 2, 5));
		buttonPanel3.setBackground(backgroundColor);
		
		int firstRow, secondRow, thirdRow;
		
		if(Screen.constants.length > 4) {
			firstRow = 4;
			
			if(Screen.constants.length > 8) {
				secondRow = 4;
				thirdRow = Screen.constants.length - 8;
			}else {
				secondRow = Screen.constants.length - 4;
				thirdRow = 0;
			}
		}else {
			firstRow = Screen.constants.length;
			secondRow = 0;
			thirdRow = 0;
		}
		
		for (int i = 0; i < firstRow; i++) {
			JLabel label = new JLabel(Screen.constantStrings[i]);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setOpaque(false);
			label.setForeground(foregroundColor);
			buttonPanel.add(label);
		}
		for (int i = 0; i < firstRow; i++) {
			JToggleButton button = new JToggleButton();
			if (i == 0)
				button.setSelected(true);
			button.setOpaque(true);
			button.setBackground(backgroundColor);

			button.setActionCommand("" + i);
			button.addActionListener(this);
			buttons[i] = button;
			buttonGroup.add(button);
			buttonPanel.add(button);

		}
		
		for (int i = 0; i < secondRow; i++) {
			JLabel label = new JLabel(Screen.constantStrings[i + 4]);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setOpaque(false);
			label.setForeground(foregroundColor);
			buttonPanel2.add(label);
		}
		for (int i = 0; i < secondRow; i++) {
			JToggleButton button = new JToggleButton();
			if (i == 0)
				button.setSelected(true);
			button.setOpaque(true);
			button.setBackground(backgroundColor);

			button.setActionCommand("" + (i + 4));
			button.addActionListener(this);
			buttons[i + 4] = button;
			buttonGroup.add(button);
			buttonPanel2.add(button);

		}
		
		for (int i = 0; i < thirdRow; i++) {
			JLabel label = new JLabel(Screen.constantStrings[i + 8]);
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setOpaque(false);
			label.setForeground(foregroundColor);
			buttonPanel3.add(label);
		}
		for (int i = 0; i < thirdRow; i++) {
			JToggleButton button = new JToggleButton();
			if (i == 0)
				button.setSelected(true);
			button.setOpaque(true);
			button.setBackground(backgroundColor);

			button.setActionCommand("" + (i + 8));
			button.addActionListener(this);
			buttons[i + 8] = button;
			buttonGroup.add(button);
			buttonPanel3.add(button);

		}
		
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0.5;
		c.gridwidth = 4;
		c.weightx = 0.5;
		c.anchor = GridBagConstraints.NORTH;
		this.add(buttonPanel, c);
		
		c.insets = new Insets(5, 0, 0, 0);
		c.gridy = 1;
		this.add(buttonPanel2, c);
		
		c.gridy = 2;
		this.add(buttonPanel3, c);
		
		c.gridwidth = 1;
		c.gridy = 3;
		c.gridx = 3;
		this.add(speedLabel, c);
		
		c.gridwidth = 4;
		c.gridy = 4;
		c.gridx = 0;
		this.add(speedSlider, c);
		
		this.scrollPane = new JScrollPane(serialPorts);
		this.scrollPane.setBorder(null);
		
		c.gridy = 5;
		c.anchor = GridBagConstraints.SOUTH;
		c.insets = new Insets(12, 0, 0, 0);
		scrollPane.setPreferredSize(new Dimension(0, 500));
		scrollPane.setMinimumSize(new Dimension(0, 125));
		this.add(scrollPane, c);

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(new BasicStroke(4));
		g2d.setStroke(new BasicStroke(2));
	}

	public void setMode(int mode) {
		for (int i = 0; i < buttons.length; i++) {
			if (Integer.parseInt(buttons[i].getActionCommand()) == mode) {
				System.out.println(mode);
				buttons[i].setSelected(true);
			}
		}
		serialPorts.setMode(mode);
	}

	public void setSpeed(int speed) {
		this.speedSlider.setValue(speed);
		serialPorts.setSpeed(speed);
	}

	public void init() {
		serialPorts.init();
	}

	public void setIndex(int index) {
		serialPorts.setIndex(index);
	}

	public void connect(ArrayList<String> ports) {
		serialPorts.connect(ports);
	}

	public void sizeUpdated() {
		serialPorts.sizeUpdated();
	}

	public void colorUpdated() {
		serialPorts.colorUpdated();
	}

	public void setColors(ArrayList<Color> colors) {
		serialPorts.setColors(colors);
	}

	public ArrayList<String> getConnections() {
		return serialPorts.getConnections();
	}

	public int getMode() {
		return Integer.parseInt(buttonGroup.getSelection().getActionCommand());
	}

	public int getSpeed() {
		return speedSlider.getValue();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JToggleButton button = (JToggleButton) e.getSource();
		serialPorts.setMode(Integer.parseInt(button.getActionCommand()));
	}
}
