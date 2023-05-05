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
            if (connection != null && !connection.isClosed()) {
                throw new IllegalStateException("Already connected to database.");
            }

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(databaseURL);

        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void createTables() {
        String sqlCreateStudents = "CREATE TABLE IF NOT EXISTS Students (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "Name VARCHAR(255) NOT NULL, Password VARCHAR(255) NOT NULL)";

        String sqlCreateCourses = "CREATE TABLE IF NOT EXISTS Courses (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "Department VARCHAR(4) NOT NULL, Catalog_Number INT(4) NOT NULL)";

        String sqlCreateReviews = "CREATE TABLE IF NOT EXISTS Reviews (ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "StudentID INT(5) NOT NULL, CourseID INT(5) NOT NULL, Text VARCHAR(4096) NOT NULL, " +
                "Rating INT(1) NOT NULL, FOREIGN KEY (StudentID) REFERENCES Students(ID) ON DELETE CASCADE, " +
                "FOREIGN KEY (CourseID) REFERENCES Courses(ID) ON DELETE CASCADE)";

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

    public void clear() {
        try {
            if (connection.isClosed()) {
                throw new IllegalStateException("Manager has not connected yet.");
            }

            String clearStudents = "DELETE FROM Students";
            String clearCourses = "DELETE FROM Courses";
            String clearReviews = "DELETE FROM Reviews";

            Statement st = connection.createStatement();
            st.execute(clearStudents);
            st.execute(clearCourses);
            st.execute(clearReviews);
            st.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void deleteTables() {
        try {
            if (connection.isClosed()) {
                throw new IllegalStateException("Manager has not connected yet.");
            }

            String deleteStudents = "DROP TABLE IF EXISTS Students";
            String deleteCourses = "DROP TABLE IF EXISTS Courses";
            String deleteReviews = "DROP TABLE IF EXISTS Reviews";

            Statement st = connection.createStatement();
            st.execute(deleteStudents);
            st.execute(deleteCourses);
            st.execute(deleteReviews);
            st.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void addStudent(Student student) {
        String name = student.getName();
        String password = student.getPassword();

        if (doesNameExist(name)) {
            throw new IllegalArgumentException("Student already exists in database.");
        }

        try {
            if (connection.isClosed()) { throw new IllegalStateException("Manager has not connected yet."); }

            String insertQuery = String.format("""
                INSERT INTO Students (Name, Password)
                VALUES ("%s", "%s");
                """, name, password);

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
            rs.close();
            st.close();
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
            rs.close();
            st.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return student;
    }

    public void addCourse(Course course) {
        String department = course.getDepartment();
        int catalogNum = course.getCatalogNumber();

        String insertQuery = String.format("""
                INSERT INTO Courses (Department, Catalog_Number)
                VALUES ("%s", "%d");
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
                    SELECT * FROM Courses WHERE Department = "%s" AND Catalog_Number = "%d";
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

    public Course getCourseByID(int courseID) {
        Course course;
        try {
            if (connection.isClosed()) { throw new IllegalStateException("Manager has not connected yet."); }

            Statement st = connection.createStatement();
            String selectQuery = "SELECT * FROM Courses WHERE ID = " + courseID + ";";
            ResultSet rs = st.executeQuery(selectQuery);

            if (!rs.next()) {
                throw new IllegalArgumentException("No such course exists in database.");
            }

            String department = rs.getString("Department");
            int catalogNum = rs.getInt("Catalog_Number");
            rs.close();
            st.close();
            course = new Course(department, catalogNum);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        return course;
    }

    public void addReview(Review review) {
        Student student = review.getStudent();
        int studentID = getStudentID(student);

        Course course = review.getCourse();
        int courseID = getCourseID(course);

        String insertQuery = String.format("""
                INSERT INTO Reviews (StudentID, CourseID, Text, Rating)
                VALUES ("%d", "%d", "%s", "%d");
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
            rs.close();
            st.close();

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
            rs.close();
            st.close();
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

    public List<Review> getStudentReviews(Student student) {
        List<Review> reviews = new ArrayList<>();
        int studentID = getStudentID(student);
        try {
            if (connection.isClosed()) {
                throw new IllegalStateException("Manager has not connected yet.");
            }

            Statement st = connection.createStatement();
//            String selectQuery = String.format("""
//                    SELECT * FROM Reviews WHERE CourseID = "%d";
//                    """, courseID);

            String selectQuery = "SELECT * FROM Reviews WHERE StudentID = " + studentID + ";";
            ResultSet rs = st.executeQuery(selectQuery);

            while (rs.next()) {
                int courseID = rs.getInt("CourseID");
                Course course = getCourseByID(courseID);
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
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
