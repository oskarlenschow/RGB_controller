import java.awt.AWTException;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class Communicator {
	private OutputStream[] outputStreams = new OutputStream[0];
	private ArrayList<Color> colors;
	ArrayList<Integer> uploadValues = new ArrayList<>();
	private Integer index;
	private int mode, speed;
	private Robot robot;
	private Rectangle hueRect = new Rectangle(0, 0, (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(), 1);
	private Rectangle healthRect = new Rectangle(0, 0, 100, 1);
	private BufferedImage hueImage;
	private BufferedImage healthbarImage;
	private boolean readyForUpload = false, pauseThread = false;

	public Communicator(ArrayList<Color> colors, int index) {
		this.speed = 1;
		this.index = index;
		this.colors = colors;
		try {
			robot = new Robot();
		} catch (AWTException e) {
		}
		new HealthBar();
	}

	public void updateOutputStreams(OutputStream[] outputStreams) {
		this.outputStreams = outputStreams;
		if (outputStreams.length > 0)
			readyForUpload = true;
		else
			readyForUpload = false;

		uploadAll();
	}

	public void setSpeed(int speed) {
		this.speed = speed;
		if (readyForUpload)
			uploadSpeed();
	}

	public void setMode(int mode) {
		this.mode = mode;
		if (readyForUpload)
			uploadMode();
	}

	public void setIndex(int index) {
		this.index = index;
		if (readyForUpload)
			uploadIndex();
	}

	public void sizeUpdated() {

		if (readyForUpload)
			uploadColors();
	}

	public void colorUpdated() {
		if (readyForUpload)
			uploadColor();
	}

	public void setColors(ArrayList<Color> colors) {
		this.colors = colors;
	}

	public void uploadAll() {
		uploadMode();
		uploadSpeed();
		uploadIndex();
		uploadColors();
	}

	private void uploadMode() {
		uploadValues.add(0); // Command index for arduino
		uploadValues.add(mode);
		upload();
	}

	private void uploadSpeed() {
		uploadValues.add(1); // Command index for arduino
		uploadValues.add(speed);
		pauseThread = true;
		upload();
	}

	private void uploadIndex() {
		uploadValues.add(2);
		uploadValues.add(index);
		pauseThread = true;
		upload();
	}

	private void uploadAmmount() {
		uploadValues.add(3); // Command index for arduino
		uploadValues.add(speed);
		pauseThread = true;
		upload();
	}

	private void uploadColor() {
		uploadValues.add(4); // Command index for arduino
		uploadValues.add(colors.get(index).getRed());
		uploadValues.add(colors.get(index).getGreen());
		uploadValues.add(colors.get(index).getBlue());
		pauseThread = true;
		upload();
	}

	private void uploadColors() {
		uploadValues.add(5); // Command index for arduino
		uploadValues.add(colors.size());
		for (int i = 0; i < colors.size(); i++) {
			uploadValues.add(colors.get(i).getRed());
			uploadValues.add(colors.get(i).getGreen());
			uploadValues.add(colors.get(i).getBlue());
		}
		pauseThread = true;
		upload();
	}

	private void upload() {

		for (int i = 0; i < outputStreams.length; i++) {

			if (i >= outputStreams.length)
				break;
			try {
				for (int j = 0; j < uploadValues.size(); j++) {
					outputStreams[i].write(uploadValues.get(j).intValue());
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		uploadValues = new ArrayList<>();
	}

	private class HealthBar implements Runnable {
		private HealthBar() {
			Thread thread = new Thread(this);
			thread.start();
		}

		@Override
		public void run() {
			int counter = 0;
			while (true) {
				if (mode == Screen.HEALTH && !pauseThread) {
					healthbarImage = robot.createScreenCapture(healthRect);
					int imageWidth = healthbarImage.getWidth();
					int rValue = 0, gValue = 0, bValue = 0;
					int percentage = 0;
					for (int x = 0; x < imageWidth; x++) {

						int clr = healthbarImage.getRGB(x, 0);
						rValue = (clr & 0x00ff0000) >> 16;
						gValue = (clr & 0x0000ff00) >> 8;
						bValue = clr & 0x000000ff;
						if (rValue == 0 & gValue == 0 && bValue == 0)
							percentage++;
					}
					uploadValues.add(6);
					uploadValues.add(percentage);
					upload();

				} else if (mode == Screen.HUE && !pauseThread) {
					hueImage = robot.createScreenCapture(hueRect);
					int imageWidth = hueImage.getWidth();
					int color;
					int ammount = 0;
					uploadValues.add(7);
					for (double x = 0; x < imageWidth; x += 68.8){
						ammount++;
					}
					
					uploadValues.add(ammount);
					System.out.println(ammount);
					
					for (double x = 0; x < imageWidth; x += 68.8) {

						color = hueImage.getRGB((int)x, 0);
						
						uploadValues.add((color & 0x00ff0000) >> 16);
						uploadValues.add((color & 0x0000ff00) >> 8);
						uploadValues.add(color & 0x000000ff);

					}
					upload();

				} else if (mode == Screen.GPU_TEMP && !pauseThread) {
					Process process = null;
					try {
						process = Runtime.getRuntime().exec(
								"cmd /c cd C:\\Program Files\\NVIDIA Corporation\\NVSMI && nvidia-smi --query-gpu=temperature.gpu --format=csv,noheader");
					} catch (IOException e) {
						e.printStackTrace();
					}
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line = null;
					int temperature = 0;

					try {
						while ((line = input.readLine()) != null) {
							temperature = Integer.parseInt(line);
						}
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					uploadValues.add(8);
					uploadValues.add(temperature);
					upload();

				} else if (mode == Screen.GPU_USAGE && !pauseThread) {
					Process process = null;
					try {
						process = Runtime.getRuntime().exec(
								"cmd /c cd C:\\Program Files\\NVIDIA Corporation\\NVSMI && nvidia-smi --query-gpu=utilization.gpu --format=csv,noheader");
					} catch (IOException e) {
						e.printStackTrace();
					}
					BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line = null;
					int percentage = 0;

					try {
						while ((line = input.readLine()) != null) {
							percentage = Integer.parseInt(line.split(" ", 2)[0]);
						}
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					uploadValues.add(9);
					uploadValues.add(percentage);
					upload();

				} else {
					if (counter >= 5) {
						pauseThread = false;
						counter = 0;
					} else
						counter++;
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}
	/*
	 * private void dominantColor() {
	 * 
	 * Image temp = robot.createScreenCapture(screenRect).getScaledInstance(500,
	 * 200, BufferedImage.SCALE_AREA_AVERAGING);
	 * 
	 * screenFullImage = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
	 * Graphics g = screenFullImage.getGraphics(); g.drawImage(temp, 0, 0, null);
	 * g.dispose();
	 * 
	 * int imageHeight = screenFullImage.getHeight(); int imageWidth =
	 * screenFullImage.getWidth();
	 * 
	 * int rValue = 0, gValue = 0, bValue = 0; for (int y = 0; y < imageHeight; y +=
	 * imageHeight) { for (int x = 0; x < imageWidth; x += imageWidth) {
	 * 
	 * int clr = screenFullImage.getRGB(x, y);
	 * 
	 * rValue = (clr & 0x00ff0000) >> 16; gValue = (clr & 0x0000ff00) >> 8; bValue =
	 * clr & 0x000000ff; } } upload(new Color(rValue, gValue, bValue)); try {
	 * Thread.sleep(100); } catch (InterruptedException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } }
	 */

}
