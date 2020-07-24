package com.filmseven.htmlparse;


public class User {
    private String authorization;
    private String email;
    private String name;
    private String surname;
    private String username;
    private String moviecategory;
    private String squestion;
    private String sanswer;

    public String getauthorization() {
        return authorization;
    }
    public String getemail() {
        return email;
    }
    public String getname() {
        return name;
    }
    public String getsurname() {
        return surname;
    }
    public String getusername() {
        return username;
    }
    public String getmoviecategory() {
        return moviecategory;
    }
    public String getsquestion() {
        return squestion;
    }
    public String getsanswer() {
        return sanswer;
    }

    public User() {
        //empty constructor needed
    }

    public void setauthorization(String authorization) {
        this.authorization = authorization;
    }
    public void setemail(String email) {
        this.email = email;
    }
    public void setname(String name) {
        this.name = name;
    }
    public void setsurname(String surname) {
        this.surname = surname;
    }
    public void setusername(String username) {
        this.username = username;
    }
    public void setmoviecategory(String moviecategory) {
        this.moviecategory = moviecategory;
    }
    public void setsquestion(String squestion) {
        this.squestion = squestion;
    }
    public void setsanswer(String sanswer) {
        this.sanswer = sanswer;
    }

    public User(String authorization, String email, String name, String surname, String username, String moviecategory, String squestion, String sanswer) {
        this.authorization = authorization;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.moviecategory = moviecategory;
        this.squestion = squestion;
        this.sanswer = sanswer;
    }
}
