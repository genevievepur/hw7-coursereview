package edu.virginia.cs.hw7.presentation;

import edu.virginia.cs.hw7.Student;
import edu.virginia.cs.hw7.business.ReviewSystemService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ReviewSystemController implements Initializable {
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

    public void userLogin(ActionEvent actionEvent) {
        checkLogin();
    }

    private void checkLogin() {
        String name = username.getText();
        String enteredPassword = password.getText();
        if (service.doesNameExists(name)) {
            Student student = service.getStudentByName(name);
            if (service.isPasswordEnteredCorrect(name, enteredPassword)) {
                loginError.setText("Login successful!");
            } else {
                loginError.setText("Incorrect username or password.");
            }
        } else {
            loginError.setText("Incorrect username or password.");
        }
    }
}
