package atm_sinulation;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Scanner;
import java.security.SecureRandom;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class Register {
	
	public static String randomPassword(int length) {
	String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	SecureRandom random = new SecureRandom();
	StringBuilder password = new StringBuilder(length);
	 
	for(int i =0;i< length;i++) {
		password.append(characters.charAt(random.nextInt(characters.length())));
	}
		
	return password.toString();	
		
	}
	
	public static String accNumber(int length) {
		String digits = "0123456789";
		SecureRandom random = new SecureRandom();
		StringBuilder AccNo = new StringBuilder(length);
		
		for(int i =0;i< length;i++) {
			AccNo.append(digits.charAt(random.nextInt(digits.length())));
		}
		return AccNo.toString();
	}
	
	

	public Credentials registration() {
		while(true) {
		try {
			Scanner input = new Scanner(System.in); 
			Connection con = CreateDB.connection_url();
			String Query ="INSERT INTO users(first_name,last_name,phone_number,email,account_number,pin,date_of_birth)VALUES(?,?,?,?,?,?,?)";
			PreparedStatement Pmt = con.prepareStatement(Query);
			System.out.println("Enter Your First Name");
			String firstname = input.nextLine();
			 System.out.println("Enter Your Last Name");
			 String lastname = input.nextLine();
			 System.out.println("Enter Your Phone Number");
			 String phonenumber ="+256"+ input.nextLine();
			 System.out.println("Enter Your Email Address");
			 String email = input.nextLine();
			 System.out.println("Enter Your  Date Of Birth");
			 String DOB = input.nextLine();
			 
			 String plainPassword = randomPassword(4);
			 String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
			 String accNo = accNumber(6);
			 
			 boolean found = true;
			 if(firstname.isEmpty()|| firstname.length()<2) {
				 found = false; System.out.println("Name must be above two characters");}
			 if(lastname.isEmpty()|| lastname.length()<2) { 
				 found = false;System.out.println("Name must be above two characters");}
			 if(!phonenumber .matches("\\+256\\d{9}")) { 
				 found = false;System.out.println("Phone number must be in the format XXXXXXXXX (9 digits).");}
			 if(!email.contains("@")&& !email.contains(".")) { 
				 found = false;System.out.println("Wrong email format , Must contain @ and .");}
			 if(!DOB.matches("\\d{4}-\\d{2}-\\d{2}")) {
				 found = false;System.out.println("Wrong date of birth format yyyy-mm-dd");}
			 
			 if(found) { 
				 Pmt.setString(1, firstname);
				 Pmt.setString(2, lastname);
				 Pmt.setString(3, phonenumber);
				 Pmt.setString(4, email);
				 Pmt.setString(5, accNo);
				 Pmt.setString(6, hashedPassword);
				 Pmt.setString(7 ,DOB);
				 Pmt.executeUpdate();
				 System.out.println("Registration Successfull");
				 
				 return  new Credentials(email,accNo,plainPassword);
				
			 }else {
				 System.out.println("Registration failed");
				 return null;
			 }
		
	}
	/*
	 * catch(SQLIntegrityConstraitViolationException e) {
	 * System.out.println("User already exists"); }
	 */
			
		catch (SQLException e) {
			
			System.out.println("Caught Exception ==>"+e.getMessage());
			
		}
		}
	}
	
	public boolean logIn(String Password, String AccNo) {
		try {
			Connection con = CreateDB.connection_url();
			String Query = "SELECT pin ,account_number FROM users WHERE  account_number=?";
			PreparedStatement pmt = con.prepareStatement(Query);
			pmt.setString(1, AccNo);
			ResultSet result = pmt.executeQuery();
			if(result.next()) {
				String accountnumber = result.getString("account_number");
				String PIN = result.getString("pin");
				if(accountnumber.equals(AccNo)&& BCrypt.checkpw(Password, PIN)){

					System.out.println("LogIn Successful");
				return true;}
				else {
					return false;
				}
			}else {
				System.out.println("Account number Not Found\nPlease Check Your Passsword");
				return false;
			}
		} catch (SQLException e) {
			
			System.out.println("Caught Execption ==>"+e.getMessage());
			return false;
		}
		
	}
	public static  double checkBalance(String ACCno) {
		 double Balance;
		try {
			Connection con = CreateDB.connection_url();
			String query = "SELECT balance FROM users WHERE account_number =? ";
			PreparedStatement pmt = con.prepareStatement(query);
			pmt.setString(1, ACCno);
			ResultSet rs = pmt.executeQuery();
			
				if(rs.next()) {
				 Balance = rs.getDouble("balance");
				System.out.println("Your Account Balance is ==> "+Balance);
				return  Balance;
				
				}else {
					System.out.println("Account number not found");
					return 0.0;
				}
		} catch (SQLException e) {
			
			e.printStackTrace();
			return 0.0;
		}
	}
	public void Deposit(String ACCno) {
		while(true) {
		Scanner input = new Scanner(System.in);
		Connection con;
		try {
			con = CreateDB.connection_url();
		 
		String query = "UPDATE users SET balance = balance + ? WHERE account_number =? ";
		PreparedStatement pmt = con.prepareStatement(query);
		int amount;
		System.out.println("Please Enter The Amount You Wish To Deposit");
		if(input.hasNextInt()) {
		 amount = input.nextInt();
		input.nextLine();
		if(amount >0) {
		
		pmt.setInt(1, amount);
		pmt.setString(2, ACCno);
		int rs = pmt.executeUpdate();
		if(rs ==1) {
			System.out.println("You Have Successfully deposited"+" "+amount);
		}else {
			System.out.println("Transaction Failed");
		}
		break;
		}else {
			System.out.println("Inavlid ; Value Must Be Greater Than Zero");
		}
		
		}
			else { System.out.println("Invalid Input");}
		
	}catch (SQLException e) {
			
			e.printStackTrace();
		}
	}	
	}
	public int withdraw(String ACCno) {
		Scanner input = new Scanner(System.in);
		Connection con;
		try {
			con = CreateDB.connection_url();
		 
		
		System.out.println("Please Enter The Amount You Wish To Withdraw");
		int amount = input.nextInt();
		input.nextLine();
		
		double balance = checkBalance(ACCno);
		if(amount <= balance) {
			String query = "UPDATE users SET balance = balance - ? WHERE account_number =? ";
			PreparedStatement pmt = con.prepareStatement(query);
		pmt.setInt(1, amount);
		pmt.setString(2, ACCno);
		int rs = pmt.executeUpdate();
		if(rs == 1) {
		    System.out.println("You Have Successfully Withdrawn " + amount);
		    
		    String checkQuery = "SELECT balance FROM users WHERE account_number = ?";
		    PreparedStatement checkStmt = con.prepareStatement(checkQuery);
		    checkStmt.setString(1, ACCno);
		    ResultSet rs2 = checkStmt.executeQuery();

		    if (rs2.next()) {
		        Double newBalance = rs2.getDouble("balance");
		        System.out.println("Your Balance is " + newBalance);
		    }
		}else {
			System.out.println("Transaction Failed");
		}
		}else {
			System.out.println("You Have Insufficient Funds ");
		}
	}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public void transactionMenu(String Accountno, Scanner input) {
		System.out.println("Please Select An Option\n1. To Deposit \n 2. To Withdraw\n 3.Check Account Balance\n 4. Exit ");
		while(true) {
			
			int choice ;
			if(input.hasNextInt()) {
				choice = input.nextInt();
				input.nextLine();
				switch(choice) {
				case 1:
					Deposit(Accountno);
					System.out.println("\n\n");

			System.out.println("Please Select An Option\n 2. To Withdraw\n 3.Check Account Balance\n 4. Exit ");
					break;
				case 2:
					withdraw(Accountno);
			System.out.println("Please Select An Option\n1. To Deposit \n  3.Check Account Balance\n 4. Exit ");
					break;
					
				case 3:
					checkBalance(Accountno);
			System.out.println("Please Select An Option\n1. To Deposit \n 2. To Withdraw\n  4. Exit ");
			break;

				case 4:
					System.out.println("Thank you for banking with us. Have a great day!\n Please Take your Card");
					System.exit(0);
					break;
					
					default:
				        System.out.println("Invalid input , value out of range");
				        break;
				}
				
			}else {
				System.out.println("Inavlid input ; Value must be a number\n Please Re-enter your choice");
				input.nextLine();
			}
				}
	}
	
	public static void main(String[] args) {
		try {
		    Class.forName("jakarta.mail.internet.AddressException");
		    System.out.println("AddressException loaded OK");
		} catch (ClassNotFoundException e) {
		    System.out.println("Class not found: " + e.getMessage());
		}

		Register R = new Register();
		Scanner input = new Scanner(System.in);
		
System.out.println("GREETINGS! LET'S GET STARTED WITH YOUR TRANSACTIONS");
System.out.println("\n");
System.out.println("PLEASE INSERT YOUR CARD or ACCNO TO BEGIN");
System.out.println("\n\n");
boolean running = true;
System.out.println("Please Follow The Prompts\n 1. Register An Account\n2. Log Into Your Account\n3. Exit");
while(true) {

System.out.println("Enter Your Choice\n");
int choice;
if(input.hasNextInt()) {
	choice = input.nextInt();
	input.nextLine();

	switch(choice) {
	case 1:
		System.out.println("Please Fill In The Following Details:\n");	
	
	Credentials result = R.registration();
	
	if(result != null) {
		
	String Subject = "Comfirmation Of Registration ";
	String Content ="Your Account Has Been Successfully Registered;\n Your Account Number is ==> "+result.accountNo +"\nAnd Your Password is ==>"+result.password;
	
	Mailing.sendEmail(result.email, Subject, Content);
	
	}else {
		System.out.println("REgistration Failed. Please Try Again");
		System.out.println("Please Follow The Prompts\n 1. Register An Account\n2. Log Into Your Account\n3. Exit");
	}
	break;
	case 2:
		System.out.println("Please Enter Your Account number");
		String accno = input.nextLine();

		System.out.println("Please Enter Your Password");
		String password = input.nextLine();
		
		if(R.logIn(password, accno)) {
			
			R.transactionMenu(accno, input);
		}else {
			System.out.println("Log In Failed ; Please Check Your Credentials");
		}
		break;
	case 3:
		System.out.println("Your transaction is complete. Please take your card.");
		System.exit(0);
		break;
	default:
        System.out.println("Invalid input , value out of range");
	break;
}
	
}else {
	System.out.println("Invalid Input value must be a number\n Please Re-enter your choice ");
	input.nextLine();
}

	}


	}
}
