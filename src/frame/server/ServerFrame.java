package frame.server;

import java.awt.BorderLayout;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import frame.board.TemperatureBoard;
import frame.db.DB;

public class ServerFrame extends JFrame{
	private JPanel paintPanel;
	private JPanel tablePanel;
	private JTable table;
	private TemperatureBoard board;
	
	private DatagramSocket socket;
	private DatagramPacket packet;
	private InetAddress address;
	
	private int	myPort = 10001;
	private DB db;
	
	public ServerFrame() {
		init();
		
		createPaintPanel();
		createTablePanel();
		createServer();
		receiveStart();
		
		setVisible(true);
	}
	
	public void init() {
		setTitle("Server for Sensors Data");
		setBounds(300,200,300, 600);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void createPaintPanel() {
		paintPanel = new JPanel(new BorderLayout());
		
		board = new TemperatureBoard(this);
		paintPanel.add(board);
		paintPanel.setBounds(300,200,300, 300);
		
		this.add(paintPanel);
	}
	
	public void createTablePanel() {
		tablePanel = new JPanel(new BorderLayout());
		
		Object[] columnNames = new Object[] {"SEQ", "SensorId", "Temperature", "Humidity"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		table = new JTable(model);
		
		JScrollPane tableScroll = new JScrollPane(table);
		
		tablePanel.add(tableScroll);
		tablePanel.setSize(300, 300);
		
		this.add(tablePanel);
	}
	
	public void createServer() {
		try {
			address = InetAddress.getByName("127.0.0.1"); // ���� IP
			socket = new DatagramSocket(myPort);
			socket.setSoTimeout(500);
			socket.setReuseAddress(true);
			db = new DB();
			
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage() + "\n");
		}	
	}
	
	public void receiveStart() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				receive();
			}
		});
		
		thread.start();
	}
	
	public void receive()
	{
		Sensor sensor = new Sensor();
		
		while(true)
		{
			try {
				
				// id, seq, temp, hum
				
				byte[] rcvBuffer = new byte[32];
				packet = new DatagramPacket(rcvBuffer, rcvBuffer.length);
				socket.receive(packet);
				
				byte[] temp;
				temp = new byte[4];
				System.arraycopy(rcvBuffer, 0, temp, 0, 4);
				sensor.id = byteToInt(temp);
				sensor.id = swap(sensor.id); //int
				
				System.out.println("sensor.id = " + sensor.id);
				
				System.arraycopy(rcvBuffer, 4, temp, 0, 4);
				sensor.seq = byteToInt(temp);
				sensor.seq = swap(sensor.seq); // int
				
				System.out.println("sensor.seq = " + sensor.seq);
				
				System.arraycopy(rcvBuffer, 8, temp, 0, 4);
				sensor.temperature = byteToFloat(temp);
				sensor.temperature = swap(sensor.temperature); //float
				
				System.out.println("sensor.temperature = " + sensor.temperature);
	
				System.arraycopy(rcvBuffer, 12, temp, 0, 4);
				sensor.humidity = byteToFloat(temp);
				sensor.humidity = swap(sensor.humidity); //float
				
				System.out.println("sensor.humidity = " + sensor.humidity);
				
				Object[] rowData = new Object[] {sensor.seq, sensor.id, sensor.temperature, sensor.humidity};
				DefaultTableModel model = (DefaultTableModel)table.getModel();
				model.addRow(rowData); //table에 넣기
				
				//TemperatureBoard에 그리기
				
				
				//DB에 넣기
				ArrayList<Object> list = new ArrayList<Object>();
				list.add(sensor.id);
				list.add(sensor.seq);
				list.add(sensor.temperature);
				list.add(sensor.humidity);
				
				db.connection();
				db.insertData(list);
				
			} catch (Exception e) {
				// TODO: handle exception
				// display.append("RCV Exception :" + e.getMessage() + "\n");
			}
			
			System.out.println("receive ..." + myPort);
		}
	}
	

	public int byteToInt(byte[] arr)
	{
		return (arr[0] & 0xff)<<24 | (arr[1] & 0xff)<<16 |(arr[2] & 0xff)<<8 | (arr[3] & 0xff);
	} 
	public float byteToFloat(byte[] bytes){
		int intBits = 
			bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
		return Float.intBitsToFloat(intBits);  
	}
	public short swap(short x) {
		return (short)((x << 8) | ((x >> 8) & 0xff));
	}
	public char swap(char x) {
		return (char)((x << 8) | ((x >> 8) & 0xff));
	}
	public int swap(int x) {
		return (int)((swap((short)x) << 16) | (swap((short)(x >> 16)) & 0xffff));
	}
	public long swap(long x) {
		return (long)(((long)swap((int)(x)) << 32) | ((long)swap((int)(x >> 32)) & 0xffffffffL));
	}
	public float swap(float x) {
		return Float.intBitsToFloat(swap(Float.floatToRawIntBits(x)));
	}
	public double swap(double x) {
		return Double.longBitsToDouble(swap(Double.doubleToRawLongBits(x)));
	}
}
