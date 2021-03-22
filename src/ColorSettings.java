import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ColorSettings extends JPanel implements ChangeListener, ActionListener, MouseWheelListener {

	private static final long serialVersionUID = 1L;
	private Color backgroundColor = new Color(50, 50, 50);
	private Color foregroundColor = new Color(249, 65, 32);
	private Color borderColor = new Color(24, 24, 24);

	private Integer index;

	private JSlider redSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 255 / 2);
	private JSlider greenSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 255 / 2);
	private JSlider blueSlider = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 255 / 2);

	private ArrayList<JSlider> sliders = new ArrayList<JSlider>();

	private JTextField redValueTextField = new JTextField();
	private JTextField greenValueTextField = new JTextField();
	private JTextField blueValueTextField = new JTextField();
	private JTextField ammountOfColors = new JTextField("2");

	private ArrayList<JTextField> textFields = new ArrayList<JTextField>();

	private JPanel sliderBox = new JPanel();

	private ColorCircle colorCircle;
	private ArrayList<Color> colors = new ArrayList<>();

	private OtherSettings otherSettings;

	private Font font = new Font("SansSerif", Font.BOLD, 15);

	private JLabel nextColor, previousColor;

	public ColorSettings(int width, int height) {
		this.setPreferredSize(new Dimension(width, height));
		this.setBorder(BorderFactory.createMatteBorder(0, 20, 20, 20, backgroundColor));
		this.setLayout(new GridLayout(2, 1));
		this.setBackground(backgroundColor);

		colors.add(new Color(255 / 2, 255 / 2, 255 / 2));
		index = 0;

		colorCircle = new ColorCircle(120, width / 2 - 60, 22, index + 1, colors.get(index));
		colorCircle.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {

				if (e.getWheelRotation() < 0)
					updateIndex(index + 1);
				else
					updateIndex(index - 1);

			}

		});
		colors.add(new Color(0, 0, 255));

		nextColor = new JLabel(">");
		previousColor = new JLabel("<");
		previousColor.setVisible(false);
		nextColor.setForeground(borderColor);
		previousColor.setForeground(borderColor);
		nextColor.setSize(25, 50);
		previousColor.setSize(25, 50);
		nextColor.setLocation(250, 57);
		previousColor.setLocation(88, 57);
		nextColor.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				nextColor.setForeground(foregroundColor);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				nextColor.setForeground(borderColor);
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				updateIndex(++index);
			}

		});
		previousColor.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				previousColor.setForeground(foregroundColor);

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				previousColor.setForeground(borderColor);

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				updateIndex(--index);
			}

		});

		Font font = new Font("SansSerif", Font.BOLD, 40);

		nextColor.setFont(font);
		previousColor.setFont(font);

		JPanel colorPanel = new JPanel(null);
		colorPanel.setOpaque(false);
		colorPanel.add(previousColor);
		colorPanel.add(colorCircle);
		colorPanel.add(nextColor);
		sliderSetup();
		this.add(colorPanel);
		this.add(sliderBox);
	}

	public void setOtherSettings(OtherSettings otherSettings) {
		this.otherSettings = otherSettings;
	}

	public void updateIndex(int index) {
		if (index < colors.size() && index >= 0)
			this.index = index;
		if (this.index > 0)
			previousColor.setVisible(true);
		else
			previousColor.setVisible(false);

		if (this.index < colors.size() - 1)
			nextColor.setVisible(true);
		else
			nextColor.setVisible(false);

		colorCircle.setNumber(this.index + 1);
		otherSettings.setIndex(this.index);
		updateSliders(colors.get(this.index), 0);
		repaint();
	}

	public void paintComponent(Graphics g) {
		colorCircle.setColor(colors.get(index));
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setStroke(new BasicStroke(4));
		colorCircle.draw(g2d);
		g2d.setStroke(new BasicStroke(2));
	}

	private void sliderSetup() {
		sliderBox.setOpaque(false);
		sliderBox.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;

		constraints.insets = new Insets(0, 6, 0, 6);
		constraints.gridwidth = 1;
		constraints.weightx = 0.5;
		constraints.gridx = 1;
		constraints.gridy = 0;

		ammountOfColors.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int ammount;
				try {
					ammount = Integer.parseInt(ammountOfColors.getText());
					if (ammount > 8)
						ammount = 8;
					else if (ammount < 2)
						ammount = 2;
				} catch (NumberFormatException nfe) {
					ammount = colors.size();
				}
				updateAmmountOfColors(ammount);

			}

		});
		ammountOfColors.setBackground(backgroundColor);
		ammountOfColors.setForeground(foregroundColor);
		ammountOfColors.setHorizontalAlignment(JLabel.CENTER);
		ammountOfColors.setFont(font);

		ammountOfColors.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, borderColor));

		sliderBox.add(ammountOfColors, constraints);

		sliders.add(redSlider);
		sliders.add(greenSlider);
		sliders.add(blueSlider);

		redSlider.setForeground(Color.RED);
		greenSlider.setForeground(Color.GREEN);
		blueSlider.setForeground(Color.BLUE);

		for (int i = 0; i < sliders.size(); i++) {
			constraints.insets = new Insets(10, 0, 0, 0);
			constraints.gridwidth = 3;
			constraints.gridx = 0;
			constraints.gridy = i + 1;
			JSlider tempSlider = sliders.get(i);
			tempSlider.setMajorTickSpacing(50);
			tempSlider.setMinorTickSpacing(5);
			tempSlider.setPaintTicks(true);
			tempSlider.addChangeListener(this);
			tempSlider.setOpaque(false);
			sliderBox.add(tempSlider, constraints);

		}
		textFields.add(redValueTextField);
		textFields.add(greenValueTextField);
		textFields.add(blueValueTextField);

		for (int i = 0; i < textFields.size(); i++) {
			constraints.insets = new Insets(0, 6, 0, 6);
			constraints.gridwidth = 1;
			constraints.weightx = 0.5;
			constraints.gridx = i;
			constraints.gridy = 4;
			JTextField tempTextField = textFields.get(i);
			tempTextField.addActionListener(this);
			tempTextField.setBackground(backgroundColor);
			tempTextField.setForeground(foregroundColor);
			tempTextField.setHorizontalAlignment(JLabel.CENTER);
			tempTextField.setFont(font);
			tempTextField.setText("127");
			sliderBox.add(tempTextField, constraints);
		}
		redValueTextField.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.RED));
		greenValueTextField.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.GREEN));
		blueValueTextField.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, Color.BLUE));

		sliderBox.addMouseWheelListener(this);
	}

	public ArrayList<Color> getColors() {
		return colors;
	}

	public void setColors(ArrayList<Color> colors) {
		this.colors = colors;
		otherSettings.setColors(colors);
		updateAmmountOfColors(colors.size());
	}

	public ArrayList<String> getColorStrings() {
		ArrayList<String> colorStrings = new ArrayList<String>();
		for (int i = 0; i < colors.size(); i++) {
			Color color = colors.get(i);
			String formattedRed = String.format("%03d", color.getRed());
			String formattedGreen = String.format("%03d", color.getGreen());
			String formattedBlue = String.format("%03d", color.getBlue());
			String colorString = formattedRed + " " + formattedGreen + " " + formattedBlue;
			colorStrings.add(colorString);
		}
		return colorStrings;
	}

	public int getIndex() {
		return index;
	}

	public String getAmmountOfColors() {
		return ammountOfColors.getText();
	}

	public void updateAmmountOfColors(int ammount) {
		if (ammount > colors.size()) {
			for (int i = colors.size(); i < ammount; i++) {
				colors.add(new Color(127, 127, 127));
			}
			updateIndex(index); // Just to make sure it all registers, including updating the arrows
		} else if (ammount < colors.size()) {
			for (int i = colors.size() - 1; i >= ammount; i--) {
				colors.remove(i);
			}
			updateIndex(colors.size() - 1);
		}
		ammountOfColors.setText(Integer.toString(ammount));
		otherSettings.sizeUpdated();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		int redValue = redSlider.getValue();
		int greenValue = greenSlider.getValue();
		int blueValue = blueSlider.getValue();

		this.redValueTextField.setText(Integer.toString(redValue));
		this.greenValueTextField.setText(Integer.toString(greenValue));
		this.blueValueTextField.setText(Integer.toString(blueValue));

		colors.set(index, new Color(redValue, greenValue, blueValue));
		otherSettings.colorUpdated();
		repaint();
	}

	public void updateSliders(Color color, int alpha) {
		redSlider.setValue(color.getRed() + alpha);
		greenSlider.setValue(color.getGreen() + alpha);
		blueSlider.setValue(color.getBlue() + alpha);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		int redValue, greenValue, blueValue;

		try {
			redValue = Integer.parseInt(this.redValueTextField.getText());
			if (redValue > 255)
				redValue = 255;
			else if (redValue < 0)
				redValue = 0;
		} catch (NumberFormatException nfe) {
			redValue = colors.get(index).getRed();
		}
		try {
			greenValue = Integer.parseInt(this.greenValueTextField.getText());
			if (greenValue > 255)
				greenValue = 255;
			else if (greenValue < 0)
				greenValue = 0;
		} catch (NumberFormatException nfe) {
			greenValue = colors.get(index).getGreen();
		}
		try {
			blueValue = Integer.parseInt(this.blueValueTextField.getText());
			if (blueValue > 255)
				blueValue = 255;
			else if (blueValue < 0)
				blueValue = 0;
		} catch (NumberFormatException nfe) {
			blueValue = colors.get(index).getBlue();
		}

		this.redValueTextField.setText(Integer.toString(redValue));
		this.greenValueTextField.setText(Integer.toString(greenValue));
		this.blueValueTextField.setText(Integer.toString(blueValue));

		updateSliders(new Color(redValue, greenValue, blueValue), 0);
		
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		updateSliders(colors.get(index), e.getWheelRotation());

	}
}
