import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPorts extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<SerialPort> serialPorts = new ArrayList<SerialPort>();
	private HashMap<JButton, SerialPort> portFromButton = new HashMap<JButton, SerialPort>();
	private HashMap<SerialPort, JButton> buttonFromPort = new HashMap<SerialPort, JButton>();
	private Color backgroundColor = new Color(249, 65, 32);
	private Color foregroundColor = new Color(50, 50, 50);
	private Communicator communicator;
	private Thread communicatorThread;

	public SerialPorts(ArrayList<Color> colors, int index) {
		communicator = new Communicator(colors, index);
		// communicatorThread = new Thread(communicator);
		this.setLayout(new GridLayout(0, 1));
		serialSetup();
		this.setBackground(backgroundColor);
		this.setOpaque(true);

	}

	private void serialSetup() {
		Font font = new Font("SansSerif", Font.BOLD, 15);
		SerialPort[] tempSerialPorts = SerialPort.getCommPorts();

		for (int i = 0; i < tempSerialPorts.length; i++) {
			serialPorts.add(tempSerialPorts[i]);
		}
		for (int i = 0; i < serialPorts.size(); i++) {
			JPanel serialPortContainer = new JPanel(new BorderLayout());
			if (i < serialPorts.size() - 1) {
				serialPortContainer.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 50)));
			}
			serialPortContainer.setOpaque(false);
			String descriptivePortName = serialPorts.get(i).getSystemPortName() + " - "
					+ serialPorts.get(i).getDescriptivePortName();
			descriptivePortName = descriptivePortName.substring(0, descriptivePortName.lastIndexOf(" "));
			int maxWidth = 26;
			if (descriptivePortName.length() > maxWidth) {
				int correctedMaxWidth = (Character.isLowSurrogate(descriptivePortName.charAt(maxWidth))) && maxWidth > 0
						? maxWidth - 1
						: maxWidth;
				descriptivePortName = descriptivePortName.substring(0,
						Math.min(descriptivePortName.length(), correctedMaxWidth)) + "...";
			}

			JLabel descriptor = new JLabel(descriptivePortName);
			descriptor.setFont(font);
			descriptor.setForeground(foregroundColor);
			descriptor.setBorder(new EmptyBorder(5, 5, 5, 5));
			serialPortContainer.add(descriptor, BorderLayout.WEST);

			JButton connect = new JButton(" CONNECT ");
			connect.setFont(font);
			connect.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 5, backgroundColor));
			connect.setBackground(foregroundColor);
			connect.setForeground(backgroundColor);
			connect.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JButton button = (JButton) e.getSource();
					SerialPort port = (SerialPort) portFromButton.get(button);
					if (button.getText().equals(" CONNECT ")) {
						port.setComPortParameters(115200, 8, 1, 0);
						port.openPort();
						button.setText(" DISCONNECT ");

					} else if (button.getText().equals(" DISCONNECT ")) {
						port.closePort();
						button.setText(" CONNECT ");
					}
					updateOutputStreams();
				}

			});
			serialPortContainer.add(connect, BorderLayout.EAST);

			portFromButton.put(connect, serialPorts.get(i));
			buttonFromPort.put(serialPorts.get(i), connect);
			this.add(serialPortContainer);

		}

	}

	public void connect(ArrayList<String> ports) {
		for (int i = 0; i < ports.size(); i++) {
			for (int j = 0; j < serialPorts.size(); j++) {
				SerialPort serialPort = serialPorts.get(j);
				if (serialPort.getSystemPortName().equals(ports.get(i))) {
					serialPort.setComPortParameters(115200, 8, 1, 0);
					serialPort.openPort();
					buttonFromPort.get(serialPort).setText(" DISCONNECT ");

				}
			}
		}
		updateOutputStreams();
	}

	public ArrayList<String> getConnections() {
		ArrayList<String> connections = new ArrayList<String>();
		for (int i = 0; i < serialPorts.size(); i++) {
			if (serialPorts.get(i).isOpen()) {
				connections.add(serialPorts.get(i).getSystemPortName());
			}
		}
		return connections;
	}

	public void init() {
		// communicatorThread.start();
	}
	public void setSpeed(int speed) {
		communicator.setSpeed(speed);
	}

	public void setMode(int mode) {
		communicator.setMode(mode);
	}

	public void setIndex(int index) {
		communicator.setIndex(index);
	}

	public void sizeUpdated() {
		communicator.sizeUpdated();
	}

	public void colorUpdated() {
		communicator.colorUpdated();
	}

	public void setColors(ArrayList<Color> colors) {
		communicator.setColors(colors);
	}

	private void updateOutputStreams() {
		ArrayList<SerialPort> connectedSerialPorts = new ArrayList<>();

		for (int i = 0; i < serialPorts.size(); i++) {
			if (serialPorts.get(i).isOpen()) {
				connectedSerialPorts.add(serialPorts.get(i));
			}
		}
		OutputStream[] outputStreams = new OutputStream[connectedSerialPorts.size()];
		for (int i = 0; i < connectedSerialPorts.size(); i++) {
			outputStreams[i] = connectedSerialPorts.get(i).getOutputStream();
		}
		communicator.updateOutputStreams(outputStreams);
	}
}
