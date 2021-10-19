package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AadharJDBC {
	
	private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/aadhardata?useSSL=false";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "";            //(AadharNum,name, gender, dob,phoneNum,eMail,state,pinCode)
    private static final String INSERT_QUERY = "INSERT INTO useraadhardata (AadharNum, Name, Gender, DOB, PhoneNo, email, state, pincode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    


    public static void insertRecord(String aadharNum, String name, String gender, String dob, String phoneNum,
			String eMail, String state, String pinCode) throws SQLException {

        
        try (Connection connection = DriverManager
            .getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY)) {
            preparedStatement.setString(1, aadharNum);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, gender);
            preparedStatement.setString(4, dob);
            preparedStatement.setString(5, phoneNum);
            preparedStatement.setString(6, eMail);
            preparedStatement.setString(7, state);
            preparedStatement.setString(8, pinCode);

//            System.out.println(preparedStatement);
            
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            
            System.out.println(e);
        }
    }



}
