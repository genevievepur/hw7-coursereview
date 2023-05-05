package edu.virginia.cs.hw7;

import edu.virginia.cs.hw7.business.ReviewSystemService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class ReviewSystemServiceTester {

    ReviewSystemService testService;

    @BeforeEach
    public void setup() {
        testService = new ReviewSystemService();
        testService.initialize();
    }

    @Test
    public void testDoesNameExistTrue() {
        String name = "Amy";
        assertTrue(testService.doesNameExists(name));
    }

    @Test
    public void testDoesNameExistFalse() {
        String name = "Sarah";
        assertFalse(testService.doesNameExists(name));
    }

    @Test
    public void testGetStudentByNameStudentExits() {
        String name = "Amy";
        Student amy = testService.getStudentByName(name);
        assertEquals(name, amy.getName());
        assertEquals("qwerty", amy.getPassword());
    }

    @Test
    public void testGetStudentByNameStudentDoesNotExits() {
        String name = "Sarah";
        assertThrows(IllegalArgumentException.class, () -> testService.getStudentByName(name));
    }

    @Test
    public void testWrongPasswordEntered() {
        String name = "Amy";
        String password = "qqwerty";
        assertFalse(testService.isPasswordEnteredCorrect(name, password));
    }

    @Test
    public void testRightPasswordEntered() {
        String name = "Amy";
        String password = "qwerty";
        assertTrue(testService.isPasswordEnteredCorrect(name, password));
    }

    @Test
    public void testDoPasswordsMatchWrongConfirmation() {
        String newUserPassword = "abc123";
        String wrongConfirmation = "abcd1234";
        assertFalse(testService.doCreatedPasswordsMatch(newUserPassword, wrongConfirmation));
    }

    @Test
    public void testDoPasswordsMatchRightConfirmation() {
        String newUserPassword = "abc123";
        String wrongConfirmation = "abc123";
        assertTrue(testService.doCreatedPasswordsMatch(newUserPassword, wrongConfirmation));
    }

//    @Test
//    public void testRegisterUserNewName() {
//        String newUser = "Sarah";
//        String newUserPassword = "abc123";
//        assertFalse(testService.doesNameExists(newUser));
//        testService.registerUser(newUser, newUserPassword);
//        assertTrue(testService.doesNameExists(newUser));
//        assertTrue(testService.isPasswordEnteredCorrect(newUser, newUserPassword));
//    }

    @Test
    public void testInvalidDepartmentTooLong() {
        String department = "APMAS";
        assertFalse(testService.isDepartmentValid(department));
    }

    @Test
    public void testInvalidDepartmentLowerCase() {
        String department = "apma";
        assertFalse(testService.isDepartmentValid(department));
    }

    @Test
    public void testInvalidDepartmentTooShort() {
        String department = "";
        assertFalse(testService.isDepartmentValid(department));
    }

    @Test
    public void testInvalidDepartmentTooLongAndMixedCases() {
        String department = "CompSci";
        assertFalse(testService.isDepartmentValid(department));
    }

    @Test
    public void testValidDepartment2Letters() {
        String department = "CS";
        assertTrue(testService.isDepartmentValid(department));
    }

    @Test
    public void testValidDepartment4Letters() {
        String department = "APMA";
        assertTrue(testService.isDepartmentValid(department));
    }

    @Test
    public void testInvalidCatalogNumTooShort() {
        int catNum = 1;
        assertFalse(testService.isCatalogNumberValid(catNum));
    }

    @Test
    public void testInvalidCatalogNumTooLong() {
        int catNum = 12345;
        assertFalse(testService.isCatalogNumberValid(catNum));
    }

    @Test
    public void testValidCatalogNum() {
        int catNum = 3140;
        assertTrue(testService.isCatalogNumberValid(catNum));
    }

//    @Test
//    public void testValidCatalogNumAllZeroes() {
//        int catNum = 0000;
//        assertTrue(testService.isCatalogNumberValid(catNum));
//    }

    @Test
    public void testDoesCourseExistsFalse() {
        String department = "CS";
        int catNum = 3100;
        assertFalse(testService.doesCourseExists(department, catNum));
    }

    @Test
    public void testDoesCourseExistsTrue() {
        String department = "CS";
        int catNum = 1110;
        assertTrue(testService.doesCourseExists(department, catNum));
    }

    @Test
    public void testHasUserSubmittedCourseFalse() {
        Student user = new Student("Amy", "qwerty");
        Course course = new Course("CS", 3140);
        assertFalse(testService.hasUserSubmittedCourseReviewAlready(user, course));
    }

    @Test
    public void testHasUserSubmittedCourseTrue() {
        Student user = new Student("Amy", "qwerty");
        Course course = new Course("CS", 1110);
        assertTrue(testService.hasUserSubmittedCourseReviewAlready(user, course));
    }

    @Test
    public void testGetStudentReviews() {
        Student student = new Student("Amy", "qwerty");
        Course course = new Course("CS", 1110);
        String text = "Great course!";
        int rating = 5;

        Course course2 = new Course("APMA", 3100);
        String text2 = "It was hard to stay on top of all the work";
        int rating2 = 3;

        Review review = new Review(student, course, text, rating);
        Review review2 = new Review(student, course2, text2, rating2);
        List<Review> expectedReviews = new ArrayList<>();
        expectedReviews.add(review);
        expectedReviews.add(review2);

        List<Review> actualReviews = testService.getStudentReviews(student);

        //assertEquals(expectedReviews, actualReviews);

        for (int i = 0; i < actualReviews.size(); i++) {
            Review actReview = actualReviews.get(i);
            Review expReview = expectedReviews.get(i);
            assertEquals(expReview.getStudent().getName(), actReview.getStudent().getName());
            assertEquals(expReview.getText(), actReview.getText());
            assertEquals(expReview.getRating(), actReview.getRating());
        }
    }

    @Test
    public void testInvalidRatingTooLarge() {
        int rating = 6;
        assertFalse(testService.isRatingValid(rating));
    }

    @Test
    public void testInvalidRatingZero() {
        int rating = 0;
        assertFalse(testService.isRatingValid(rating));
    }

    @Test
    public void testValidRating5() {
        int rating = 5;
        assertTrue(testService.isRatingValid(rating));
    }

    @Test
    public void testDoesCourseHaveReviewsFalse() {
        Course course = new Course("CS", 3140);
        List<Review> courseReviews = testService.getReviewsOfCourse(course);
        assertFalse(testService.doesCourseHaveReviews(courseReviews));
    }

    @Test
    public void testDoesCourseHaveReviewsTrue() {
        Course course = new Course("CS", 1110);
        List<Review> courseReviews = testService.getReviewsOfCourse(course);
        assertTrue(testService.doesCourseHaveReviews(courseReviews));
    }

    @Test
    public void testAverageRating() {
        Course course = new Course("CS", 1110);
        List<Review> courseReviews = testService.getReviewsOfCourse(course);

        double expected = 2.33;
        double actual = testService.averageRating(courseReviews);

        assertEquals(expected, actual, 0.1);
    }

}
