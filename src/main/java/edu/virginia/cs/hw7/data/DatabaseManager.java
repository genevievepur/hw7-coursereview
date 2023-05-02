package edu.virginia.cs.hw7.data;

import edu.virginia.cs.hw7.Course;
import edu.virginia.cs.hw7.Review;
import edu.virginia.cs.hw7.Student;

import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    Connection connection;

    public void connect() {
        String databaseName = "Reviews.sqlite3";
        String databaseURL = "jdbc:sqlite:" + databaseName;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(databaseURL);

            if (connection != null && !connection.isClosed()) {
                throw new IllegalStateException("Already connected to database.");
            }

        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void createTables() {
        String sqlCreateStudents = "CREATE TABLE IF NOT EXISTS Students (ID INT(5) NOT NULL AUTOINCREMENT, " +
                "Name VARCHAR(255) NOT NULL, Password VARCHAR(255) NOT NULL, PRIMARY KEY(ID))";

        String sqlCreateCourses = "CREATE TABLE IF NOT EXISTS Courses (ID INT(5) NOT NULL AUTOINCREMENT, " +
                "Department VARCHAR(4) NOT NULL, Catalog_Number INT(4) NOT NULL, PRIMARY KEY(ID))";

        String sqlCreateReviews = "CREATE TABLE IF NOT EXISTS Reviews (ID (INT(5) NOT NULL AUTOINCREMENT, " +
                "StudentID INT(5) NOT NULL, CourseID INT(5) NOT NULL, Text VARCHAR(255) NOT NULL, " +
                "Rating INT(1) NOT NULL, PRIMARY KEY(ID), FOREIGN KEY (StudentID) REFERENCES Students(ID), " +
                "FOREIGN KEY (CourseID) REFERENCES Courses(ID))";

        try {
            if (connection.isClosed()) { throw new IllegalStateException("Manager has not connected yet."); }

            Statement st = connection.createStatement();
            st.execute(sqlCreateStudents);
            st.execute(sqlCreateCourses);
            st.execute(sqlCreateReviews);
            st.close();

        } catch(SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void addStudent(Student student) {
        String name = student.getName();
        String password = student.getPassword();

        String insertQuery = String.format("""
                INSERT INTO Students (ID, Name, Password)
                VALUES (NULL, "%s", "%s");
                """, name, password);

        try {
            if (connection.isClosed()) { throw new IllegalStateException("Manager has not connected yet."); }

            Statement st = connection.createStatement();
            st.execute(insertQuery);
            st.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean doesNameExist(String name) {
        try {
            if (connection.isClosed()) { throw new IllegalStateException("Manager has not connected yet."); }

            Statement st = connection.createStatement();
            String checkQuery = String.format("""
                    SELECT * FROM Students WHERE Name = "%s";
                    """, name);
            ResultSet sameName = st.executeQuery(checkQuery);

            if (sameName.next()) {
                return true;
            }

            sameName.close();
            st.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return false;
    }

    public Student getStudentByName(String name) {
        Student student;
        try {
            if (connection.isClosed()) { throw new IllegalStateException("Manager has not connected yet."); }

            Statement st = connection.createStatement();
            String selectQuery = String.format("""
                    SELECT * FROM Students WHERE Name = "%s";
                    """, name);
            ResultSet rs = st.executeQuery(selectQuery);

            if (!rs.next()) {
                throw new IllegalArgumentException("No such student exists in database.");
            }

            String password = rs.getString("Password");
            student = new Student(name, password);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return student;
    }

    public Student getStudentByID(int studentID) {
        Student student;
        try {
            if (connection.isClosed()) { throw new IllegalStateException("Manager has not connected yet."); }

            Statement st = connection.createStatement();
            String selectQuery = "SELECT * FROM Students WHERE ID = " + studentID + ";";
            ResultSet rs = st.executeQuery(selectQuery);

            if (!rs.next()) {
                throw new IllegalArgumentException("No such student exists in database.");
            }

            String name = rs.getString("Name");
            String password = rs.getString("Password");
            student = new Student(name, password);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return student;
    }

    public void addCourse(Course course) {
        String department = course.getDepartment();
        int catalogNum = course.getCatalogNumber();

        String insertQuery = String.format("""
                INSERT INTO Courses (ID, Department, Catalog_Number)
                VALUES (NULL, "%s", "%d");
                """, department, catalogNum);

        try {
            if (connection.isClosed()) { throw new IllegalStateException("Manager has not connected yet."); }

            Statement st = connection.createStatement();
            st.execute(insertQuery);
            st.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean doesCourseExist(String department, int catalogNum) {
        try {
            if (connection.isClosed()) { throw new IllegalStateException("Manager has not connected yet."); }

            Statement st = connection.createStatement();
            String checkQuery = String.format("""
                    SELECT * FROM Students WHERE Department = "%s" AND Catalog_Number = "%d";
                    """, department, catalogNum);
            ResultSet sameCourse = st.executeQuery(checkQuery);

            if (sameCourse.next()) {
                return true;
            }

            sameCourse.close();
            st.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return false;
    }

    public void addReview(Review review) {
        Student student = review.getStudent();
        int studentID = getStudentID(student);

        Course course = review.getCourse();
        int courseID = getCourseID(course);

        String insertQuery = String.format("""
                INSERT INTO Reviews (ID, StudentID, CourseID, Text, Rating)
                VALUES (NULL, "%d", "%d", "%s", "%d");
                """, studentID, courseID, review.getText(), review.getRating());

        try {
            if (connection.isClosed()) { throw new IllegalStateException("Manager has not connected yet."); }

            Statement st = connection.createStatement();
            st.execute(insertQuery);
            st.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public int getStudentID(Student student) {
        String name = student.getName();
        // String password = student.getPassword();
        int studentID;

        try {
            if (connection.isClosed()) { throw new IllegalStateException("Manager has not connected yet."); }

            Statement st = connection.createStatement();
            String selectQuery = String.format("""
                    SELECT * FROM Students WHERE Name = "%s";
                    """, name);
            ResultSet rs = st.executeQuery(selectQuery);

            if (!rs.next()) {
                throw new IllegalArgumentException("No such student exists in database.");
            }

            studentID = rs.getInt("ID");

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return studentID;
    }

    public int getCourseID(Course course) {
        String department = course.getDepartment();
        int catalogNum = course.getCatalogNumber();
        int courseID;

        try {
            if (connection.isClosed()) { throw new IllegalStateException("Manager has not connected yet."); }

            Statement st = connection.createStatement();
            String selectQuery = String.format("""
                    SELECT * FROM Courses WHERE Department = "%s" AND Catalog_Number = "%d";
                    """, department, catalogNum);
            ResultSet rs = st.executeQuery(selectQuery);

            if (!rs.next()) {
                throw new IllegalArgumentException("No such course exists in database.");
            }

            courseID = rs.getInt("ID");

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return courseID;
    }

    public List<Review> getReviewsOfCourse(Course course) {
        List<Review> reviews = new ArrayList<>();
        int courseID = getCourseID(course);
        try {
            if (connection.isClosed()) {
                throw new IllegalStateException("Manager has not connected yet.");
            }

            Statement st = connection.createStatement();
//            String selectQuery = String.format("""
//                    SELECT * FROM Reviews WHERE CourseID = "%d";
//                    """, courseID);

            String selectQuery = "SELECT * FROM Reviews WHERE CourseID = " + courseID + ";";
            ResultSet rs = st.executeQuery(selectQuery);

            while (rs.next()) {
                int studentID = rs.getInt("StudentID");
                Student student = getStudentByID(studentID);
                String text = rs.getString("Text");
                int rating = rs.getInt("Rating");
                Review review = new Review(student, course, text, rating);
                reviews.add(review);
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return reviews;
    }
}
