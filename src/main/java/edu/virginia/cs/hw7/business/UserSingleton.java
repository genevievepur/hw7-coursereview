package edu.virginia.cs.hw7.business;

import edu.virginia.cs.hw7.Student;

public class UserSingleton {

    private static final UserSingleton instance = new UserSingleton();

    private Student currentUser;

    private UserSingleton(){}

    public static UserSingleton getInstance() {
        return instance;
    }

    public Student getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Student user) {
        this.currentUser = user;
    }

}
