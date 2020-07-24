package com.filmseven.htmlparse;

import android.content.Context;

public class Movie {
    private String moviecast;
    private String moviecategory;
    private String moviedate;
    private String moviedetail;
    private String moviename;
    private String movieposter;
    private String movieuser;
    private String approved;

    public String getmoviecast() {
        return moviecast;
    }
    public String getmoviecategory() {
        return moviecategory;
    }
    public String getmoviedate() {
        return moviedate;
    }
    public String getmoviedetail() {
        return moviedetail;
    }
    public String getmoviename() {
        return moviename;
    }
    public String getmovieposter() {
        return movieposter;
    }
    public String getmovieuser() {
        return movieuser;
    }
    public String getapproved() {
        return approved;
    }

    public Movie() {
        //empty constructor needed
    }

    public void setmoviecast(String moviecast) {
        this.moviecast = moviecast;
    }
    public void setmoviecategory(String moviecategory) {
        this.moviecategory = moviecategory;
    }
    public void setmoviedate(String moviedate) {
        this.moviedate = moviedate;
    }
    public void setmoviedetail(String moviedetail) {
        this.moviedetail = moviedetail;
    }
    public void setmoviename(String moviename) {
        this.moviename = moviename;
    }
    public void setmovieposter(String movieposter) {
        this.movieposter = movieposter;
    }
    public void setmovieuser(String movieuser) {
        this.movieuser = movieuser;
    }
    public void setapproved(String approved) {
        this.approved = approved;
    }

    public Movie(String moviecast, String moviecategory, String moviedate, String moviedetail, String moviename, String movieposter, String movieuser, String approved) {
        this.moviecast = moviecast;
        this.moviecategory = moviecategory;
        this.moviedate = moviedate;
        this.moviedetail = moviedetail;
        this.moviename = moviename;
        this.movieposter = movieposter;
        this.movieuser = movieuser;
        this.approved = approved;
    }
}
