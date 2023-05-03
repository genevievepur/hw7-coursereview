package edu.virginia.cs.hw7.business;

import edu.virginia.cs.hw7.Review;
import edu.virginia.cs.hw7.Student;
import edu.virginia.cs.hw7.Course;
import edu.virginia.cs.hw7.data.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class ReviewSystemService {
    DatabaseManager dbManager;

    public void initialize() {
        dbManager = new DatabaseManager();
        dbManager.connect();
        dbManager.createTables();
    }

    public boolean doesNameExists(String name) {
        return dbManager.doesNameExist(name);
    }

    public Student getStudentByName(String name) {
        return dbManager.getStudentByName(name);
    }

    public void registerUser(String name, String password) {
        Student student = new Student(name, password);
        dbManager.addStudent(student);
    }

    public boolean doesCourseExists(String department, int catalogNum) {
        return dbManager.doesCourseExist(department, catalogNum);
    }

    public void addCourse(Course course) {
        dbManager.addCourse(course);
    }

    public boolean hasUserSubmittedCourseReviewAlready(Student user, Course course){
        List<Review> studentReviews = getStudentReviews(user);
        for (int i = 0; i < studentReviews.size(); i++) {
            Course reviewCourse = studentReviews.get(i).getCourse();
            if (reviewCourse.getCourseTitle().equals(course.getCourseTitle())) {
                return true;
            }
        }
        return false;
    }

    public List<Review> getStudentReviews(Student student) {
        List<Review> studentReviews = dbManager.getStudentReviews(student);
        return studentReviews;
    }

    public void addReview(Student student, Course course, String text, int rating) {
        Review review = new Review(student, course, text, rating);
        dbManager.addReview(review);
    }

    public List<Review> getReviewsOfCourse(Course course) {
        return dbManager.getReviewsOfCourse(course);
    }
}
