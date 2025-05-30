package atm_sinulation;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class CreateDB {
	static Connection con;
	public static Connection connection_url() throws SQLException {
		try {
			String dbUrl ="jdbc:mysql://localhost:3306/atm";
			Properties prop = new Properties();
			prop.setProperty("user", "root");
			prop.setProperty("password", "mugos");
			 con = DriverManager.getConnection(dbUrl,prop);
		}catch(Exception m) {
			System.out.println("Caught Exception ==>"+m.getMessage());
		}
		return con;
	}
	public void Conn_to_DB() {
		try {
			//Connecting to mysql database
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection_url();
			System.out.println("Database connection successful");
		}catch(Exception ex) {
			System.out.println("Caught Exception ==>"+ex.getMessage());
		}
	}
	
	public void Creat_DB () {
	try {
			connection_url();
			// Creating database
			Statement stmt = con.createStatement();
			String Qury  = "CREATE DATABASE ATM";
			stmt.executeUpdate(Qury);
			
			System.out.println("DATABASE CREATED SUCCESSFULLY");
			
	}catch(Exception e) {
		System.out.println("Caught Exception ==>"+e.getMessage());
	}
	}
	public void create_table() {
		try {
			connection_url();
			Statement stmt = con.createStatement();
			String query = "CREATE TABLE USERS ("
				    + "first_name VARCHAR(50) NOT NULL, "
				    + "last_name VARCHAR(50) NOT NULL, "
				    + "phone_number VARCHAR(15) NOT NULL, "
				    + "email VARCHAR(80) NOT NULL, "
				    + "account_number VARCHAR(100) NOT NULL PRIMARY KEY, "
				    + "pin CHAR(100) NOT NULL, "
				    + "date_of_birth DATE, "
				    + "balance DECIMAL(16, 2)DEFAULT 0"
				    + ")";
			stmt.executeUpdate(query);
			System.out.println("Table created successfully");
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	
		
	}

//	public static void main(String[] args) {
//	
//CreateDB db = new CreateDB();
//db.create_table();
//}
}
