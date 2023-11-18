package club.unburn.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class MySQL {
	private static Connection con = null;

	public MySQL(String Username, String Password, String Database, String Hostname, int Port) {
		connect(Username, Password, Database, Hostname, Port);
	}

	public static Connection getCon() {
		return con;
	}

	private void connect(String Username, String Password, String Database, String Hostname, int Port) {
		String conStr = "jdbc:mysql://" + Hostname + ":" + Port + "/" + Database + "?user=" + Username + "&password="
				+ Password + "&serverTimezone=UTC";
		try {
			if (con != null && !con.isClosed())
				throw new Exception("Connection already established!");
			con = DriverManager.getConnection(conStr);
			ScheduledExecutorService thread = Executors.newSingleThreadScheduledExecutor();
			thread.scheduleAtFixedRate(new Runnable() {
				public void run() {
					try {
						MySQL.this.antiTimeoutLoop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 0L, 1L, TimeUnit.SECONDS);
		} catch (Exception ex) {
			System.out.println(Prefix.MYSQL + ex.getMessage());
		}
	}

	public static ResultSet Query(String sql, String... args) {
		try {
			PreparedStatement stmt = con.prepareStatement(sql);
			for (int i = 0; i < args.length; i++)
				stmt.setString(i + 1, args[i]);
			return stmt.executeQuery();
		} catch (Exception ex) {
			System.out.println(Prefix.MYSQL + ex.getMessage());
			return null;
		}
	}
	
	


	public static int Exec(String sql, String... args) {
	    try {
	        PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        for (int i = 0; i < args.length; i++)
	            stmt.setString(i + 1, args[i]);
	        stmt.executeUpdate();
	        ResultSet rs = stmt.getGeneratedKeys();
	        if (rs.next()) {
	            return rs.getInt(1);
	        } else {
	            return 0;
	        }
	    } catch (Exception ex) {
	        System.out.println(Prefix.MYSQL + ex.getMessage());
	        return -1;
	    }
	}


	private void antiTimeoutLoop() throws SQLException, InterruptedException {
		while (!con.isClosed()) {
			PreparedStatement stmt = con.prepareStatement("SELECT 1+1");
			stmt.execute();
			Thread.sleep(3000L);
		}
	}
}