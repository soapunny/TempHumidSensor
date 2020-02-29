package frame.board;

import java.awt.Graphics;

import javax.swing.JComponent;

import frame.server.ServerFrame;

public class TemperatureBoard extends JComponent{
	private ServerFrame parent;
	private int sensor1Temp;
	private int sensor2Temp;
	private int sensor3Temp;
	private int sensor4Temp;
	
	public TemperatureBoard(ServerFrame frame) {
		super();
		parent = frame;
	}
	@Override
	public void paint(Graphics g) {
		
	}
	
}
