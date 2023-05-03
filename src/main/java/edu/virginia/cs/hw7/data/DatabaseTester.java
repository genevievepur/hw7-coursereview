package edu.virginia.cs.hw7.data;

import edu.virginia.cs.hw7.Course;
import edu.virginia.cs.hw7.Review;
import edu.virginia.cs.hw7.Student;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTester {

    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.connect();
        dbManager.deleteTables();
        dbManager.createTables();

        Student amy1 = new Student("Amy", "qwerty");
        Student john = new Student("John", "123456");
        Student amy2 = new Student("Amy", "abcdef");

        Course course1 = new Course("CS", 1110);
        Course course2 = new Course("CS", 3140);

        dbManager.addStudent(amy1);
        dbManager.addStudent(john);
        dbManager.addCourse(course1);
        dbManager.addCourse(course2);
        //dbManager.addStudent(amy2);

        Review review1 = new Review(amy1, course1, "Great course!", 5);
        Review review2 = new Review(john, course1, "This course sucked!", 1);


        dbManager.addReview(review1); dbManager.addReview(review2);

        Student amyByID = dbManager.getStudentByID(1);
        System.out.println("Student 1: " + amyByID.getName());

        boolean existsSDE = dbManager.doesCourseExist("CS", 3140);
        System.out.println("SDE exists in database: " + existsSDE);

        List<Review> introReviews = dbManager.getReviewsOfCourse(course1);
        System.out.println("---- Intro to CS Reviews ----");
        for (int i = 0; i < introReviews.size(); i++) {
            Review review = introReviews.get(i);
            System.out.println("Review: " + review.getText() + "- Rating: " + review.getRating());
        }

        List<Review> amyReviews = dbManager.getStudentReviews(amy1);
        System.out.println("---- Amy's Reviews ----");
        for (int i = 0; i < amyReviews.size(); i++) {
            Review review = amyReviews.get(i);
            System.out.println("Course: " + review.getCourse() + "- Review: " + review.getText() + "- Rating: " + review.getRating());
        }
    }
}
