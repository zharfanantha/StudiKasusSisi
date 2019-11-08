package com.example.studikasussisi.Repository.Model;

public class HomeMenu {
    private String title;
    private int img;

    public HomeMenu() {
    }

    public HomeMenu(String title, int img) {
        this.title = title;
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
