package edu.virginia.cs.hw7.presentation;

import edu.virginia.cs.hw7.Student;
import edu.virginia.cs.hw7.business.ReviewSystemService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    Student currentUser;
    ReviewSystemService service;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button login;
    @FXML
    private Button register;
    @FXML
    private Label loginError;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        service = new ReviewSystemService();
        service.initialize();
    }

    public void userLogin(ActionEvent actionEvent) throws IOException {
        checkLogin(actionEvent);
    }

    private void checkLogin(ActionEvent event) throws IOException {
        String name = username.getText();
        String enteredPassword = password.getText();
        if (service.doesNameExists(name)) {
            Student student = service.getStudentByName(name);
            if (service.isPasswordEnteredCorrect(name, enteredPassword)) {
                currentUser = student;
                loginError.setText("Login successful!");
                switchToMainMenu(event);
            } else {
                loginError.setText("Incorrect username or password.");
            }
        } else {
            loginError.setText("Incorrect username or password.");
        }
    }

    public void switchToMainMenu(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ReviewSystemApplication.class.getResource("main-menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
