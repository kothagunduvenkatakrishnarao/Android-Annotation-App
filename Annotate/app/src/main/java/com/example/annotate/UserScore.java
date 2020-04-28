package com.example.annotate;

public class UserScore {
    private double score;
    private String userEmail;
    private String enrolledProjects;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getEnrolledProjects() {
        return enrolledProjects;
    }

    public void setEnrolledProjects(String enrolledProjects) {
        this.enrolledProjects = enrolledProjects;
    }
}
