package edu.virginia.cs.hw7;

public class Course {
    private String department;
    private int catalogNumber;
    public Course(String department, int catalogNumber) {
        this.department = department;
        this.catalogNumber = catalogNumber;
    }

    public String getDepartment() { return department; }

    public int getCatalogNumber() { return catalogNumber; }

    public String getCourseTitle() {
        String title = this.department + " " + this.catalogNumber;
        return title;
    }
}
