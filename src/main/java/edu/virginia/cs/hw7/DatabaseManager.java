package edu.virginia.cs.hw7;

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
}
