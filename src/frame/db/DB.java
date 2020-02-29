package frame.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class DB {
	private String url = "jdbc:mysql://localhost/euiseob?charEncoding=utf8";
	private String user = "euiseob";
	private String password = "fvu9m6zc";
	private Connection conn = null;
	
	public DB() {
		
	}
	
	public void connection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver Loading OK!!");
			
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("DB Connect OK!!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertData(ArrayList<Object> list) {
		PreparedStatement pstmt = null;
		
		try {
			String sql = null;
			sql = "insert into sensor_table(id, seq, temperature, humidity, time) values( ?, ?, ?, ?, now() )";
			
			pstmt = conn.prepareStatement(sql);

			pstmt.setInt(1, (Integer)list.get(0));
			pstmt.setInt(2, (Integer)list.get(1));
			pstmt.setFloat(3, (Float)list.get(2));
			pstmt.setFloat(4, (Float)list.get(3));
			int cnt = pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
				System.out.println("Connection close 완료 !!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ArrayList<String> selectAll() {
		Statement stmt = null;
		ArrayList<String> list = null;
		try {
			stmt = conn.createStatement();
			
			String sql = null;
			sql = "select * from second_table";
			ResultSet rs;
			rs = stmt.executeQuery(sql);
			
			list =  new ArrayList<>();
			
			int total = 0;
			
			while(rs.next()) {
				int idx = rs.getInt("idx");
				String id = rs.getString("id");
				String name = rs.getString("name");
				int age = rs.getInt("age");
				Timestamp time = rs.getTimestamp("time");
				list.add(String.valueOf(idx));
				list.add(id);
				list.add(name);
				list.add(String.valueOf(age));
				list.add(time.toString());	
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
}
