package edu.virginia.cs.hw7.presentation;

import edu.virginia.cs.hw7.Student;
import edu.virginia.cs.hw7.business.ReviewSystemService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {
    Student currentUser;
    ReviewSystemService service;
    @FXML
    private Label welcomeText;
    @FXML
    private Button logout;
    @FXML
    private Button submitReview;
    @FXML
    private Button seeReviews;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        service = new ReviewSystemService();
        service.initialize();

        LoginController loginController = new LoginController();
        currentUser = loginController.currentUser;
        welcomeText.setText("Hello, " + currentUser.getName() + "!");
    }
}
