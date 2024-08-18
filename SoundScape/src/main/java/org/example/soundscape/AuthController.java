package org.example.soundscape;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.soundscape.Models.UserModel;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    @FXML
    private Button loginButton;

    @FXML
    private PasswordField loginPassword;

    @FXML
    private TextField loginUsername;

    @FXML
    private AnchorPane loginpage;

    @FXML
    private ImageView logo;

    @FXML
    private PasswordField signupPassword;

    @FXML
    private TextField signupUsername;

    @FXML
    private Button signupbutton;

    @FXML
    private AnchorPane signuppage;

    @FXML
    private Label switchtologin;

    @FXML
    private Label switchtosignup;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        switchtologin.setOnMouseClicked(e->{
            loginpage.setVisible(true);
            signuppage.setVisible(false);
        });
        switchtosignup.setOnMouseClicked(e->{
            loginpage.setVisible(false);
            signuppage.setVisible(true);
        });
        signupbutton.setOnMouseClicked(e->{
            if (!validateSignup()) {
                return;
            }
            String userID = saltUserName(signupUsername.getText());
            UserModel newUser = new UserModel(userID,signupUsername.getText(),signupPassword.getText());
            newUser = newUser.saveToDB();
            if(newUser.getSuccessStatus()){
                //displayAlert("SUCCESS","Success","Sign up success");
                try {
                    navigateToUser(newUser);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }else {
                displayAlert("ERROR","Failure","Couldn't sign you up");
            }
        });
        loginButton.setOnMouseClicked(e->{
            if (!validateLogin()) {
                return;
            }
            if(isAdmin()){
                //displayAlert("SUCCESS","Login","Logged in as admin");
                try {
                    navigateToAdmin();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                return;
            }
            UserModel user = UserModel.LoginUser(loginUsername.getText(),loginPassword.getText());
            if(user != null){
                //displayAlert("SUCCESS","Login","Logged in as user");
                try {
                    navigateToUser(user);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }else {
                displayAlert("ERROR","Login","Invalid login credentials");
            }
        });
    }
    private String saltUserName(String username){
        Random random = new Random();
        int rand1 = random.nextInt(100000);
        int rand2 = random.nextInt(100000);
        System.out.println(String.format("%d%s%d",rand1,username,rand2));
        return String.format("%d%s%d",rand1,username,rand2);
    }
    private void displayAlert(String type,String title,String message) {
        if(type.equals("ERROR")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(message);
            alert.setHeaderText(title);
            alert.show();
        }else if(type.equals("WARNING")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(message);
            alert.setHeaderText(title);
            alert.show();
        } else if(type.equals("SUCCESS")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(message);
            alert.setHeaderText(title);
            alert.show();
        }
    }
    private Boolean validateSignup(){
        if(signupUsername.getText().isEmpty()||signupPassword.getText().isEmpty()){
            displayAlert("ERROR","Validation Error","Please make sure all fields are completed");
            return false;
        }
        if (signupPassword.getText().length()<=4) {
            displayAlert("ERROR","Validation Error","Password must be at least 5 characters long");
            return false;
        }
        return true;
    }
    private Boolean validateLogin(){
        if(loginUsername.getText().isEmpty()||loginPassword.getText().isEmpty()){
            displayAlert("ERROR","Validation Error","Please make sure all fields are completed");
            return false;
        }
        return true;
    }
    private Boolean isAdmin(){
        if(loginUsername.getText().equals("admin")&&loginPassword.getText().equals("admin")){
            return true;
        }
        return false;
    }
    private void navigateToAdmin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/admindashboard.fxml"));
        Parent root = loader.load();
        ScrollPane scroller = new ScrollPane(root);
        Scene scene = new Scene(scroller);
        Stage adminStage = new Stage();
        adminStage.setScene(scene);
        adminStage.setResizable(false);
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.close();
        adminStage.show();
    }

    private void navigateToUser(UserModel user) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/userdashboard.fxml"));
        Parent root = loader.load();
        UserDashboardController userDashboardController = loader.getController();
        userDashboardController.setUser(user);
        ScrollPane scrollPane = new ScrollPane(root);
        Scene scene = new Scene(scrollPane);
        Stage userStage = new Stage();
        userStage.setScene(scene);
        userStage.setResizable(false);
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.close();
        userStage.show();
    }

}

