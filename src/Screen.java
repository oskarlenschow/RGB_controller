import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;

public class Screen extends JFrame implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	public static final int SINGLE = 0;
	public static final int COLOR_FADE = 1;
	public static final int COLOR_FLASH = 2;
	public static final int TWIST = 3;
	public static final int LOAD = 4;
	public static final int GPU_TEMP = 5;
	public static final int GPU_USAGE = 6;
	public static final int HEALTH = 7;
	public static final int HUE = 8;
	public static final int GRADIENT = 9;
	public static final int TEMP3 = 10;
	public static final int TEMP4 = 11;
	public static final int[] constants = { SINGLE, COLOR_FADE, COLOR_FLASH, TWIST, LOAD, GPU_TEMP, GPU_USAGE, HEALTH,
			HUE, GRADIENT, TEMP3, TEMP4 };
	public static final String[] constantStrings = { "SINGLE", "FADING", "FLASHING", "!TWISTING!", "LOADING", "!TEMPERATURE!", "USAGE",
			"HEALTH", "!HUE!", "!GRADIENT!", "TEMP3", "TEMP4" };
	
	//Involve time of day, date, weather?
	private int width = 800;
	private int height = 430;

	private File initFile;

	private ColorSettings colorSettings = new ColorSettings(width / 2, height - 100);
	private OtherSettings otherSettings = new OtherSettings(width / 2, height - 100, colorSettings.getColors(),
			colorSettings.getIndex());
	private TopBar topBar = new TopBar(width, 100, this);

	private int lastX, lastY;

	public Screen() throws FileNotFoundException {
		this.setTitle("Color Setup");
		this.setPreferredSize(new Dimension(width, height));
		this.setUndecorated(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLayout(new BorderLayout());
		this.add(topBar, BorderLayout.NORTH);
		this.add(colorSettings, BorderLayout.WEST);
		this.add(otherSettings, BorderLayout.EAST);
		colorSettings.setOtherSettings(otherSettings);
		otherSettings.init();
		topBar.addMouseListener(this);
		topBar.addMouseMotionListener(this);
		pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		initFromFile();

	}

	private void initFromFile() throws FileNotFoundException {

		try {
			openFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scanner scanner = new Scanner(initFile);
		int[] settings = new int[3];
		ArrayList<Color> colors = new ArrayList<Color>();
		ArrayList<String> ports = new ArrayList<String>();

		int i = 0;
		while (scanner.hasNextLine() && i < 3) {
			settings[i] = Integer.parseInt(scanner.nextLine());
			i++;
		}
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			String firstLetter = line.substring(0, 1);
			if (firstLetter.equals("C"))
				ports.add(line);
			else {
				Scanner innerScanner = new Scanner(line);

				int rValue = innerScanner.nextInt();
				int gValue = innerScanner.nextInt();
				int bValue = innerScanner.nextInt();

				colors.add(new Color(rValue, gValue, bValue));

				innerScanner.close();
			}

		}
		scanner.close();

		colorSettings.setColors(colors);

		otherSettings.setMode(settings[0]);
		otherSettings.setSpeed(settings[1]);
		colorSettings.updateIndex(settings[2]);
		otherSettings.connect(ports);
	}

	public void openFiles() throws IOException {
		initFile = new File("init.txt");
		initFile.createNewFile();
	}

	public void saveTofile() {

		PrintWriter writer;
		try {
			writer = new PrintWriter(initFile);
			writer.println("" + otherSettings.getMode());
			writer.println("" + otherSettings.getSpeed());
			writer.println("" + colorSettings.getIndex());
			for (int i = 0; i < colorSettings.getColorStrings().size(); i++) {
				writer.println(colorSettings.getColorStrings().get(i));
			}
			for (int i = 0; i < otherSettings.getConnections().size(); i++) {
				writer.println(otherSettings.getConnections().get(i));
			}
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		/*
		 * ColorCircle hovered = (ColorCircle) e.getSource(); hovered.setHover(true);
		 * this.repaint();
		 */
	}

	@Override
	public void mouseExited(MouseEvent e) {
		/*
		 * ColorCircle hovered = (ColorCircle) e.getSource(); hovered.setHover(false);
		 * this.repaint();
		 */
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if ((TopBar) e.getSource() == topBar) {
			lastX = e.getXOnScreen();
			lastY = e.getYOnScreen();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		/*
		 * ColorCircle clicked = (ColorCircle) e.getSource();
		 * 
		 * if (clicked == upload) { //communicator.update(colorSettings.getMode(),
		 * colorSettings.getSpeed(), colorSettings.getColors()); } else if (clicked ==
		 * download) {
		 * 
		 * } else if (clicked == minimize) { setState(Frame.ICONIFIED); } else if
		 * (clicked == close) { System.exit(0); } else if (clicked == com) {
		 * communicator.serialSetup(); }
		 */
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if ((TopBar) e.getSource() == topBar) {
			int x = e.getXOnScreen();
			int y = e.getYOnScreen();
			setLocation(getLocationOnScreen().x + x - lastX, getLocationOnScreen().y + y - lastY);
			lastX = x;
			lastY = y;
		}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
