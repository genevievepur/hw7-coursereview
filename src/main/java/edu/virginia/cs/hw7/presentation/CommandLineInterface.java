package edu.virginia.cs.hw7.presentation;

import edu.virginia.cs.hw7.Course;
import edu.virginia.cs.hw7.Review;
import edu.virginia.cs.hw7.Student;
import edu.virginia.cs.hw7.data.DatabaseManager;

import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {
    private DatabaseManager dbManager;
    private Scanner scanner;

    public CommandLineInterface() {
        dbManager = new DatabaseManager();
        scanner = new Scanner(System.in);
    }

    public void run() {
        dbManager.connect();
        dbManager.createTables();
        displayWelcomeMessage();
        mainLoop();
    }

    private void displayWelcomeMessage() {
        System.out.println("Welcome to the Course Review System!");
    }

    private void mainLoop() {
        boolean running = true;

        while (running) {
            displayMainMenu();

            int choice = getIntegerInput();

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    createUser();
                    break;
                case 3:
                    submitReview();
                    break;
                case 4:
                    viewCourseReviews();
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Login");
        System.out.println("2. Create user");
        System.out.println("3. Submit a review");
        System.out.println("4. View course reviews");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getIntegerInput() {
        int input;

        try {
            input = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            input = -1;
        }

        return input;
    }

    private void login() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (dbManager.doesNameExist(name)) {
            Student student = dbManager.getStudentByName(name);
            if (student.getPassword().equals(password)) {
                System.out.println("Login successful!");
            } else {
                System.out.println("Incorrect password!");
            }
        } else {
            System.out.println("No such user exists.");
        }
    }

    private void createUser() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (dbManager.doesNameExist(name)) {
            System.out.println("User with this name already exists.");
        } else {
            Student student = new Student(name, password);
            dbManager.addStudent(student);
            System.out.println("User created successfully!");
        }
    }

    private void submitReview() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();

        if (dbManager.doesNameExist(name)) {
            Student student = dbManager.getStudentByName(name);

            System.out.print("Enter course department (e.g., CS): ");
            String department = scanner.nextLine();
            System.out.print("Enter course catalog number (e.g., 1111): ");
            int catalogNum = getIntegerInput();

            Course course = new Course(department, catalogNum);
            if (!dbManager.doesCourseExist(department, catalogNum)) {
                dbManager.addCourse(course);
            }

            System.out.print("Enter your review: ");
            String reviewText = scanner.nextLine();
            System.out.print("Enter your rating (1-5): ");
            int rating = getIntegerInput();

            Review review = new Review(student, course, reviewText, rating);
            dbManager.addReview(review);

            System.out.println("Review submitted successfully!");
        } else {
            System.out.println("User not found. Please create an account first.");
        }
    }

    private void viewCourseReviews() {
        System.out.print("Enter course department (e.g., CS): ");
        String department = scanner.nextLine();
        System.out.print("Enter course catalog number (e.g., 1111): ");
        int catalogNum = getIntegerInput();

        Course course = new Course(department, catalogNum);

        if (dbManager.doesCourseExist(department, catalogNum)) {
            List<Review> reviews = dbManager.getReviewsOfCourse(course);

            if (reviews.isEmpty()) {
                System.out.println("No reviews found for this course.");
            } else {
                System.out.println("Reviews for " + department + " " + catalogNum + ":");
                for (Review review : reviews) {
                    System.out.println("---------------------------------");
                    System.out.println("Student: " + review.getStudent().getName());
                    System.out.println("Rating: " + review.getRating());
                    System.out.println("Review: " + review.getText());
                }
                System.out.println("---------------------------------");
            }
        } else {
            System.out.println("No such course exists in the database.");
        }
    }


    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.run();
    }
}

