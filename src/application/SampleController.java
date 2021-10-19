package application;

import java.net.URL;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class SampleController extends FaceDetectionController implements Initializable {
	
	@FXML
	private ComboBox<String> selectState;
	@FXML
	private ComboBox<String> selectGender;
	
	@FXML
	private ComboBox<String> selectDistrict;
	
	@FXML
	private TextField txtPincode;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private DatePicker selectDOB;
	
	@FXML
	private TextField txtPhoneNo;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	public Button btnSubmit;
	
	@FXML
	private Button btnGenrateAadharNum;
	
	@FXML
	private Label lblAddharNum;
	
	
	String AadharNum = "";
	
	private ObservableList<String> stateList=FXCollections.observableArrayList("Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Chhattisgarh","Goa","Gujarat"," Haryana","Himachal Pradesh","Jammu and Kashmir","Jharkhand","Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland","Orissa","Punjab","Rajasthan","Sikkim","Tamil Nadu","Tripura","Uttar Pradesh","Uttarakhand","West Bengal");
	private ObservableList<String> genderList=FXCollections.observableArrayList("Male","Female","Other");
	private ObservableList<String> s1List=FXCollections.observableArrayList("1","2","3");
	private ObservableList<String> s2List=FXCollections.observableArrayList("Male","Female","Other");
	

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		selectState.setItems(stateList);
		selectGender.setItems(genderList);
		
		
	}
	
	

	
	@FXML
	public void getValues(ActionEvent event) throws SQLException {
		Window owner = btnSubmit.getScene().getWindow();
		if (txtName.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                "Please enter your name");
            return;
        }
		if (txtPincode.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                "Please enter Pincode");
            return;
        }
		if (txtPhoneNo.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                "Please enter Phone Number");
            return;
        }
		String state = selectState.getValue();
		String name = txtName.getText();
		String pinCode = txtPincode.getText();
		String gender = selectGender.getValue();
		String phoneNum = txtPhoneNo.getText();
		String eMail = txtEmail.getText();
		String dob = selectDOB.getValue().toString();
		
//		String AadharNum = "318027233402";
		
		
		
		
		AadharJDBC.insertRecord(AadharNum,name, gender, dob,phoneNum,eMail,state,pinCode);

        showAlert(Alert.AlertType.CONFIRMATION, owner, "Registration Successful!",
            "Welcome " + name);
	
		
		
	}
	
	private String genrateAadharNum() {
		Random rand = new Random();
		char[] digits = new char[12];
		digits[0] = (char)(rand.nextInt(9)+'1');
		for(int i=1;i<12;i++) {
			digits[i] = (char)(rand.nextInt(10)+'0');
		}
		
		
		
		return new String(digits);
	}

	@FXML
	private void setAadharNum(ActionEvent event) {
		AadharNum = genrateAadharNum();
//		AadharJDBC.getRecord(txtName.getText());
		lblAddharNum.setText(AadharNum);
	}


	private void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
	

	@FXML
	public void setData(ActionEvent event) {
		
		if(selectState.getValue()=="Andhra Pradesh") {
			selectDistrict.setItems(s1List);
		}else {
			selectDistrict.setItems(s2List);;
		}
			
		
		
	}
	
	
}
