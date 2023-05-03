package edu.virginia.cs.hw7.presentation;

import edu.virginia.cs.hw7.Review;
import edu.virginia.cs.hw7.data.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.testng.annotations.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommandLineInterfaceTester {
    private DatabaseManager testDbManager;

    @BeforeEach
    public void setUp() {
        testDbManager = new DatabaseManager("jdbc:sqlite:test-database.db");
    }

    @Test
    public void testLogin() {
        // Add a user to the test database
        testDbManager.createStudent("testuser", "password");

        // Test login with correct credentials
        assertTrue(testDbManager.isPasswordCorrect("testuser", "password"));

        // Test login with incorrect credentials
        assertFalse(testDbManager.isPasswordCorrect("testuser", "wrongpassword"));
    }

    @Test
    public void testCreateUser() {
        // Test creating a user
        assertTrue(testDbManager.createStudent("newuser", "password"));

        // Test creating a user with an existing username
        assertFalse(testDbManager.createStudent("newuser", "password"));
    }

    @Test
    public void testSubmitReview() {
        // Add a user to the test database
        testDbManager.createStudent("testuser", "password");

        // Test submitting a review for a new course
        assertTrue(testDbManager.addReview("testuser", "CS", 1111, "Great course!", 5));

        // Test submitting a review for an existing course
        assertTrue(testDbManager.addReview("testuser", "CS", 1111, "Loved it!", 4));
    }

    @Test
    public void testViewCourseReviews() {
        // Add a user and submit reviews to the test database
        testDbManager.createStudent("testuser", "password");
        testDbManager.addReview("testuser", "CS", 1111, "Great course!", 5);
        testDbManager.addReview("testuser", "CS", 1111, "Loved it!", 4);

        // Test viewing course reviews
        List<Review> reviews = testDbManager.getReviewsForCourse("CS", 1111);
        assertEquals(2, reviews.size());
    }
}
