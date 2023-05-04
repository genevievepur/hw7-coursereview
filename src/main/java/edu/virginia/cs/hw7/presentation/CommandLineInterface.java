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
        this(new DatabaseManager("jdbc:sqlite:reviews.sqlite3"));
    }


    public CommandLineInterface(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        scanner = new Scanner(System.in);
    }


    public void run() {
        dbManager.connect();
        dbManager.createTables();
        displayWelcomeMessage();
        mainLoop();
    }


    void displayWelcomeMessage() {
        System.out.println("Welcome to the Course Review System!");
    }


    void mainLoop() {
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



    void displayMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Login");
        System.out.println("2. Create user");
        System.out.println("3. Submit a review");
        System.out.println("4. View course reviews");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }


    int getIntegerInput() {
        int input;


        try {
            input = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            input = -1;
        }


        return input;
    }


    public Student login() {
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


    void createUser() {
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


    void submitReview(Student loggedInUser) {
        if (loggedInUser != null) {
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
        } else {
            System.out.println("You must log in to submit a review.");
        }
    }


    void viewCourseReviews() {
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
