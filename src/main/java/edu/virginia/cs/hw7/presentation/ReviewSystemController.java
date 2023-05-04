package edu.virginia.cs.hw7.presentation;

import edu.virginia.cs.hw7.Course;
import edu.virginia.cs.hw7.Student;
import edu.virginia.cs.hw7.business.ReviewSystemService;
import edu.virginia.cs.hw7.business.UserSingleton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReviewSystemController implements Initializable {
    //Student currentUser;
    ReviewSystemService service;
    UserSingleton userSingleton = UserSingleton.getInstance();

    // FXMLs for Log In
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

    // FXMLs for Main Menu
    @FXML
    private Label welcomeText;
    @FXML
    private Button logout;
    @FXML
    private Button submitReview;
    @FXML
    private Button seeReviews;

    // FXMLs for Submit Review
    @FXML
    private TextField department;
    @FXML
    private TextField catalogNum;
    @FXML
    private TextField rating;
    @FXML
    private TextField reviewText;
    @FXML
    private Label departmentError;
    @FXML
    private Label catNumError;
    @FXML
    private Label ratingError;
    @FXML
    private Label emptyError;
    @FXML
    private Button submit;

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
                userSingleton.setCurrentUser(student);
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
        String fxml = "main-menu.fxml";
        switchScenes(event, fxml);
    }

    public void switchToSubmitReview(ActionEvent event) throws IOException {
        String fxml = "submit-review.fxml";
        switchScenes(event, fxml);
    }

    public void switchScenes(ActionEvent event, String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ReviewSystemApplication.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void submitButtonPressed(ActionEvent event) {
        String departmentEntered = department.getText();
        String catNumString = catalogNum.getText();
        String ratingString = rating.getText();
        String reviewTextEntered = reviewText.getText();

        int catNumEntered = -1;
        try {
            catNumEntered = Integer.parseInt(catNumString);
        } catch (NumberFormatException e) {
            catNumError.setText("Please enter a number");
        }

        int ratingEntered = -1;
        try {
            ratingEntered = Integer.parseInt(ratingString);
        } catch (NumberFormatException e) {
            ratingError.setText("Please enter a number");
        }

        if (isValidDepartment(departmentEntered) && isValidCatalogNumber(catNumEntered) && isValidRating(ratingEntered)) {
            submitReview(departmentEntered, catNumEntered, reviewTextEntered, ratingEntered);
        }
    }

    private boolean isValidDepartment(String department) {
        if (!service.isDepartmentValid(department)) {
            departmentError.setText("Invalid department");
            return false;
        }
        return true;
    }

    private boolean isValidCatalogNumber(int catalogNum) {
        if (!service.isCatalogNumberValid(catalogNum)) {
            if (!catNumError.getText().equals("Please enter a number")) {
                catNumError.setText("Invalid catalog number");
                return false;
            }
        }
        return true;
    }

    private boolean isValidRating(int rating) {
        if (!service.isRatingValid(rating)) {
            if (!ratingError.getText().equals("Please enter a number")) {
                ratingError.setText("Please enter number 1-5");
                return false;
            }
        }
        return true;
    }

    private void submitReview(String department, int catNum, String text, int rating) {
        Course course = new Course(department, catNum);
        Student currentUser = userSingleton.getCurrentUser();

        if (!service.doesCourseExists(department, catNum)) {
            service.addCourse(course);
            service.addReview(currentUser, course, text, rating);
        } else {
            if (service.hasUserSubmittedCourseReviewAlready(currentUser, course)) {
                emptyError.setText("You have already reviewed this course.");
            } else {
                service.addReview(currentUser, course, text, rating);
                emptyError.setText("Review submitted!");
            }
        }
    }

}
