package edu.virginia.cs.hw7.business;

import edu.virginia.cs.hw7.Review;
import edu.virginia.cs.hw7.Student;
import edu.virginia.cs.hw7.Course;
import edu.virginia.cs.hw7.data.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewSystemService {
    DatabaseManager dbManager;
    UserSingleton userSingleton = UserSingleton.getInstance();

    public void initialize() {
        if (userSingleton.getDbManager() == null) {
            userSingleton.setDbManager(new DatabaseManager());
            userSingleton.getDbManager().connect();
        }
        dbManager = userSingleton.getDbManager();
    }

    public boolean doesNameExists(String name) {
        return dbManager.doesNameExist(name);
    }

    public Student getStudentByName(String name) {
        return dbManager.getStudentByName(name);
    }

    public boolean isPasswordEnteredCorrect(String name, String password) {
        if (doesNameExists(name)) {
            Student student = getStudentByName(name);
            return student.getPassword().equals(password);
        }
        return false;
    }

    public boolean doCreatedPasswordsMatch(String password, String passwordConfirm){
        return passwordConfirm.equals(password);
    }

    public void registerUser(String name, String password) {
        Student student = new Student(name, password);
        dbManager.addStudent(student);
    }

    public boolean isDepartmentValid(String department) {
        if (department.length() >= 1 && department.length() <= 4) {
            if (department.toUpperCase().equals(department)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCatalogNumberValid (int catalogNum) {
        String catNumString = Integer.toString(catalogNum);
        if (catNumString.length() == 4) { return true; }
        return false;
    }

    public boolean doesCourseExists(String department, int catalogNum) {
        return dbManager.doesCourseExist(department, catalogNum);
    }

    public void addCourse(Course course) {
        dbManager.addCourse(course);
    }

    public boolean hasUserSubmittedCourseReviewAlready(Student user, Course course){
        List<Review> studentReviews = getStudentReviews(user);
        for (Review studentReview : studentReviews) {
            Course reviewCourse = studentReview.getCourse();
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

    public boolean isRatingValid(int rating) {
        return rating <= 5 && rating >= 1;
    }

    public void addReview(Student student, Course course, String text, int rating) {
        Review review = new Review(student, course, text, rating);
        dbManager.addReview(review);
    }

    public List<Review> getReviewsOfCourse(Course course) {
        return dbManager.getReviewsOfCourse(course);
    }

    public boolean doesCourseHaveReviews (List<Review> courseReviews) {
        if (courseReviews.size() == 0) { return false; }
        return true;
    }

    public double averageRating (List<Review> courseReviews) {
        List<Integer> listOfRatings = courseReviews.stream()
                .map(Review::getRating)
                .collect(Collectors.toList());

        // URL: https://www.baeldung.com/java-stream-sum
        // Description: Used the code in "4. Using IntStream.sum()" to sum the integers in the list (lines 110-112).
        int sumOfRatings = listOfRatings.stream()
                .mapToInt(Integer::intValue)
                .sum();

        int numOfRatings = courseReviews.size();

        // URL: https://stackoverflow.com/questions/7747469/how-can-i-truncate-a-double-to-only-two-decimal-places-in-java
        // Description: used the code in the first answer to truncate the double to two decimal places (line 119).
        double average = (double)sumOfRatings/numOfRatings;
        average = Math.floor(average * 100) / 100;
        return average;
    }

    public void disconnect() {
        dbManager.disconnect();
    }
}
