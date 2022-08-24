package edu.neu.madcourse.testmusic;

import java.util.List;
import java.util.Objects;

public class User {
    String userName;
    String state;
    String city;
    String miles;
    String email;
    List<String> playList;
    int step;
    String id;

    public User(){
    }

    public User(String userName){
        this.userName = userName;
    }

    public User(String userName, String city, int step, String email){
        this.userName = userName;
        this.city = city;
        this.step = step;
        this.email = email;
    }

    public User(String userName, String state,  String city,String email){
        this.userName = userName;
        this.city = city;
        this.state = state;
        this.email = email;
    }

    public User(String userName, String state, String city, String email, int step) {
        this.userName = userName;
        this.state = state;
        this.city = city;
        this.email = email;
        this.step = step;
    }

    public User(String userName, String state, String city, String email, int step, String id) {
        this.userName = userName;
        this.state = state;
        this.city = city;
        this.email = email;
        this.step = step;
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public String getCity() {
        return city;
    }

    public String getMiles() {
        return miles;
    }
    public String getState(){
        return this.state;
    }

    public String getEmail(){
        return this.email;
    }
    public List<String> getPlayList(){
        return this.playList;
    }
    public int getStep(){
        return this.step;
    }
    public String getId(){
        return this.id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, state, city, miles, email, step);
    }
}
