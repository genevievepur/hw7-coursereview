package edu.virginia.cs.hw7;

public class Review {
    private Student student;
    private Course course;
    private String text;
    private int rating;
    public Review(Student student, Course course, String text, int rating) {
        this.student = student;
        this.course = course;
        this.text = text;
        this.rating = rating;
    }

    public Student getStudent() { return student; }

    public Course getCourse() { return course; }

    public String getText() { return text; }

    public int getRating() { return rating; }
}
