package edu.virginia.cs.hw7.presentation;

import edu.virginia.cs.hw7.Course;
import edu.virginia.cs.hw7.Review;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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
    @FXML
    private TextField confirmPassword;
    @FXML
    private Label registerError;
    @FXML
    private Button backToLogin;

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
    private Label textError;
    @FXML
    private Label emptyError;
    @FXML
    private Button submit;

    // FXMLs for See Reviews
    @FXML
    VBox displayBox;
    @FXML
    Label errorOrRating;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (userSingleton.getService() == null) {
            userSingleton.setService(new ReviewSystemService());
            userSingleton.getService().initialize();
        }
        service = userSingleton.getService();
    }

    public void switchScenes(ActionEvent event, String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ReviewSystemApplication.class.getResource(fxml));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    // -------------------------- METHODS FOR LOGIN --------------------------

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

    public void switchToRegister(ActionEvent event) throws IOException {
        String fxml = "register.fxml";
        switchScenes(event, fxml);
    }

    public void exitApp(ActionEvent event) {
        service.disconnect();
        System.exit(0);
    }

    // -------------------------- METHODS FOR REGISTER --------------------------

    public void userRegister(ActionEvent event) throws IOException {
        String newName = username.getText();
        String newPassword = password.getText();
        String confirmNewPass = confirmPassword.getText();
        checkRegister(event, newName, newPassword, confirmNewPass);
    }

    public void goBackToLogIn(ActionEvent event) throws IOException {
        String fxml = "login.fxml";
        switchScenes(event, fxml);
    }

    private void checkRegister(ActionEvent event, String newUser, String newPassword, String confirmNewPassword) throws IOException {
        if (newUser.equals("")) {
            registerError.setText("Please enter a username");
        } else if (service.doesNameExists(newUser)) {
            registerError.setText("Username already taken");
        } else if (newPassword.equals("") || confirmNewPassword.equals("")) {
            registerError.setText("Please enter a password and confirm it");
        } else if (!service.doCreatedPasswordsMatch(newPassword, confirmNewPassword)) {
            registerError.setText("Passwords do not match");
        } else {
            service.registerUser(newUser, newPassword);
            Student user = service.getStudentByName(newUser);
            userSingleton.setCurrentUser(user);
            switchToMainMenu(event);
        }
    }

    // -------------------------- METHODS FOR MAIN MENU --------------------------

    public void switchToMainMenu(ActionEvent event) throws IOException {
        String fxml = "main-menu.fxml";
        switchScenes(event, fxml);
    }

    public void switchToSubmitReview(ActionEvent event) throws IOException {
        String fxml = "submit-review.fxml";
        switchScenes(event, fxml);
    }

    public void logOutPressed(ActionEvent event) throws IOException {
        goBackToLogIn(event);
    }

    public void switchToSeeReviews(ActionEvent event) throws IOException {
        String fxml = "see-reviews.fxml";
        switchScenes(event, fxml);
    }

    // -------------------------- METHODS FOR SUBMIT REVIEW --------------------------

    public void submitButtonPressed(ActionEvent event) {
        departmentError.setText("");
        catNumError.setText("");
        ratingError.setText("");
        textError.setText("");
        emptyError.setText("");

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

        if (isValidDepartment(departmentEntered) && isValidCatalogNumber(catNumEntered)
                && isValidRating(ratingEntered) && isValidText(reviewTextEntered)) {
            submitReview(departmentEntered, catNumEntered, reviewTextEntered, ratingEntered);
        }
    }

    private boolean isValidDepartment(String department) {
        if (department.equals("")) {
            departmentError.setText("Please enter a department");
            return false;
        } else if (!service.isDepartmentValid(department)) {
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

    private boolean isValidText(String text) {
        if (text.equals("")) {
            textError.setText("Please write a review");
            return false;
        }
        return true;
    }

    private void submitReview(String department, int catNum, String text, int rating) {
        Course course = new Course(department, catNum);
        Student currentUser = userSingleton.getCurrentUser();

        if (!service.doesCourseExists(department, catNum)) {
            service.addCourse(course);
            service.addReview(currentUser, course, text, rating);
            emptyError.setText("Review submitted!");
            clearTextFields();
        } else {
            if (service.hasUserSubmittedCourseReviewAlready(currentUser, course)) {
                emptyError.setText("You have already reviewed this course.");
            } else {
                emptyError.setText("Review submitted!");
                service.addReview(currentUser, course, text, rating);
                clearTextFields();
            }
        }
    }

    private void clearTextFields() {
        department.setText("");
        catalogNum.setText("");
        rating.setText("");
        reviewText.setText("");
    }

    // -------------------------- METHODS FOR SEEING REVIEWS --------------------------

    public void seeReviewsButtonPressed(ActionEvent event) {
        departmentError.setText("");
        catNumError.setText("");
        errorOrRating.setText("");
        displayBox.getChildren().clear();

        String departmentEntered = department.getText();
        String catNumString = catalogNum.getText();

        int catNumEntered = -1;
        try {
            catNumEntered = Integer.parseInt(catNumString);
        } catch (NumberFormatException e) {
            catNumError.setText("Please enter a number");
        }

        if (isValidDepartment(departmentEntered) && isValidCatalogNumber(catNumEntered)) {
            displayReviews(departmentEntered, catNumEntered);
        }
    }

    private void displayReviews(String department, int catalogNum){
        Course course = new Course(department, catalogNum);

        if (service.doesCourseExists(department, catalogNum)) {
            List<Review> courseReviews = service.getReviewsOfCourse(course);
            if (service.doesCourseHaveReviews(courseReviews)) {
                setAverageRatingLabel(courseReviews);
                addReviewLabels(courseReviews);
            } else {
                setErrorLabel();
            }
        } else {
            setErrorLabel();
        }
    }

    private void setErrorLabel() {
        errorOrRating.setTextFill(Color.RED);
        errorOrRating.setFont(new Font("System", 12));
        errorOrRating.setText("Sorry! This course does not have any reviews.");
    }

    private void setAverageRatingLabel(List<Review> courseReviews) {
        double avgRating = service.averageRating(courseReviews);
        String avgRatingText = "Average Rating = " + avgRating + "/5";
        errorOrRating.setText(avgRatingText);
        errorOrRating.setFont(new Font("System", 16));
        errorOrRating.setTextFill(Color.WHITE);
    }

    private void addReviewLabels(List<Review> courseReviews) {
        for (Review review : courseReviews) {
            String reviewText = getReviewString(review);
            Label toAdd = new Label(reviewText);
            toAdd.setTextFill(Color.WHITE);
            toAdd.setFont(new Font("System", 15));
            displayBox.getChildren().add(toAdd);
        }
    }

    private String getReviewString(Review review) {
        int rating = review.getRating();
        String text = review.getText();
        String description = rating + "/5 - " + text;
        return description;
    }

}
