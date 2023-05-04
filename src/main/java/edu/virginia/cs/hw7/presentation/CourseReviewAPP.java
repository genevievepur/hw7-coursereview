package edu.virginia.cs.hw7.presentation;

import edu.virginia.cs.hw7.presentation.CommandLineInterface;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CourseReviewAPP extends Application {
    private CourseReviewController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize the controller
        controller = new CourseReviewController();

        // Set up the GUI elements
        Label titleLabel = new Label("Course Review System");
        titleLabel.setStyle("-fx-font-size: 24pt; -fx-font-weight: bold; -fx-padding: 10px;");

        Label courseLabel = new Label("Course:");
        ComboBox<Course> courseComboBox = new ComboBox<>();
        courseComboBox.getItems().addAll(controller.getAllCourses());
        courseComboBox.setOnAction(event -> {
            Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
            controller.setCurrentCourse(selectedCourse);
            updateReviewList(reviewListView, selectedCourse);
        });

        Label reviewLabel = new Label("Reviews:");
        ListView<String> reviewListView = new ListView<>();
        reviewListView.setPrefHeight(200);
        updateReviewList(reviewListView, null);

        Button addReviewButton = new Button("Add Review");
        addReviewButton.setOnAction(event -> showAddReviewDialog(primaryStage));

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.getChildren().add(addReviewButton);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(courseLabel, 0, 0);
        gridPane.add(courseComboBox, 1, 0);
        gridPane.add(reviewLabel, 0, 1);
        gridPane.add(reviewListView, 0, 2, 2, 1);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(titleLabel, gridPane, buttonBar);

        // Set up the scene and show the stage
        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Course Review System");
        primaryStage.show();
    }

    private void updateReviewList(ListView<String> listView, Course course) {
        if (course == null) {
            listView.getItems().clear();
        } else {
            listView.getItems().setAll(controller.getReviewsForCourse(course));
        }
    }

    private void showAddReviewDialog(Stage parentStage) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Review");

        Label courseLabel = new Label("Course:");
        Label ratingLabel = new Label("Rating:");
        Label commentLabel = new Label("Comment:");

        ComboBox<Course> courseComboBox = new ComboBox<>();
        courseComboBox.getItems().addAll(controller.getAllCourses());
        TextField ratingTextField = new TextField();
        ratingTextField.setPromptText("Enter a rating (1-5)");
        TextArea commentTextArea = new TextArea();
        commentTextArea.setPromptText("Enter a comment");

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(courseLabel, 0, 0);
        gridPane.add(courseComboBox, 1, 0);
        gridPane.add(ratingLabel, 0, 1);
        gridPane.add(ratingTextField, 1, 1);
        gridPane.add(commentLabel, 0, 2);
        gridPane.add(commentTextArea, 1, 2);

        dialog.getDialogPane().setContent(gridPane);

        // Add buttons
        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        // Set the focus on the rating field initially
        Platform.runLater(() -> ratingTextField.requestFocus());

        // Set the result converter
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                Course course = courseComboBox.getSelectionModel().getSelectedItem();
                int rating = Integer.parseInt(ratingTextField.getText());
                String comment = commentTextArea.getText();
                controller.addReviewForCourse(course, rating, comment);
                updateReviewList(reviewListView, course);
                return "Review added";
            }
            return null;
        });

        dialog.showAndWait();
    }
}
