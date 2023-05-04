package edu.virginia.cs.hw7.presentation;

import edu.virginia.cs.hw7.Course;
import edu.virginia.cs.hw7.Review;
import edu.virginia.cs.hw7.Student;
import edu.virginia.cs.hw7.data.DatabaseManager;

import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {

    private final DatabaseManager dbManager;
    private final Scanner scanner;

    public CommandLineInterface() {
        this(new DatabaseManager("jdbc:sqlite:reviews.sqlite3"));
    }

    public CommandLineInterface(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.scanner = new Scanner(System.in);
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
        Student loggedInUser = null;

        while (running) {
            if (loggedInUser == null) {
                loggedInUser = login();
            } else {
                displayMainMenu();

                int choice = getIntegerInput();

                switch (choice) {
                    case 1:
                        loggedInUser = login();
                        break;
                    case 2:
                        createUser();
                        break;
                    case 3:
                        submitReview(loggedInUser);
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

    private Student login() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        if (dbManager.doesNameExist(name)) {
            Student student = dbManager.getStudentByName(name);
            if (student.getPassword().equals(password)) {
                System.out.println("Login successful!");
                return student;
            } else {
                System.out.println("Incorrect password!");
            }
        } else {
            System.out.println("No such user exists.");
        }
        return null;
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


        private void submitReview(Student loggedInUser) {
            if (loggedInUser == null) {
                System.out.println("You must log in to submit a review.");
                return;
            }

            System.out.print("Enter course department (e.g., CS): ");
            String department = scanner.nextLine();
            System.out.print("Enter course number: ");
            int number = Integer.parseInt(scanner.nextLine());
//            System.out.print("Enter semester (e.g., Spring 2022): ");
//            String semester = scanner.nextLine();
//            System.out.print("Enter year (e.g., 2022): ");
//            int year = getIntegerInput();
            System.out.print("Enter your rating (1-5): ");
            int rating = getIntegerInput();
            System.out.print("Enter your written review: ");
            String text = scanner.nextLine();

            Course course = dbManager.getCourseByDepartmentAndCatalogNumber(department, number);
            if (course == null) {
                System.out.println("No such course exists.");
                return;
            }

            Review review = new Review(loggedInUser, course, text, rating);
            dbManager.addReview(review);
            System.out.println("Review submitted successfully!");
        }

        private void viewCourseReviews() {
            System.out.print("Enter course department (e.g., CS): ");
            String department = scanner.nextLine();
            System.out.print("Enter course number: ");
            int number = Integer.parseInt(scanner.nextLine());
            Course course = dbManager.getCourseByDepartmentAndCatalogNumber(department, number);
            if (course == null) {
                System.out.println("No such course exists.");
                return;
            }

            List<Review> reviews = dbManager.getReviewsOfCourse(course);
            if (reviews.isEmpty()) {
                System.out.println("There are no reviews for this course yet.");
            } else {
                System.out.println("Reviews for " + course.getDepartment() + " " + course.getCatalogNumber() + ":");
                for (Review review : reviews) {
                    System.out.println(review.getStudent().getName() + " - " + review.getRating() + " stars");
                    System.out.println(review.getText());
                    System.out.println();
                }
            }
        }


    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();
        cli.run();
    }
}
