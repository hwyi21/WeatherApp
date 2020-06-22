package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ConnectionManager {
	static ConnectionManager instance;
	String url = "jdbc:oracle:thin:@localhost:1521:XE";
	String user="c##android";
	String password = "1234";
	
	//데이터베이스 접속
	public Connection getConnection() {
		Connection con = null;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			con = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}
	
	//데이터베이스 자원 해제
	//윈도우 창 닫을 때 호출
	public void closeDB(Connection con) {
		if(con!=null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
		
	//DML(insert, update, delete)만 수행한 경우
	//PreparedStatement 만 반납
	public void closeDB(PreparedStatement pstmt) {
		if(pstmt!=null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	//select문 경우 PreparedStatement, ResultSet 닫음
	public void closeDB(PreparedStatement pstmt, ResultSet rs) {
		if(rs!=null) {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(pstmt!=null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static ConnectionManager getInstance() {
		if(instance == null) {
			instance = new ConnectionManager();
		}
		
		return instance; 
	}
	
}
