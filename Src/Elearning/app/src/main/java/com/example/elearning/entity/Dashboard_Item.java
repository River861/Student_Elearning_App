package com.example.elearning.entity;

public class Dashboard_Item {
    private String title;
    private String subTitle;
    private String color;
    private String course_id;

    public Dashboard_Item() {
    }

    public Dashboard_Item(String course_id, String title, String subTitle, String color) {
        this.course_id = course_id;
        this.title = title;
        this.subTitle= subTitle;
        this.color = color;
    }

    public String getCourse_id(){
        return course_id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getColor() {
        return color;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setColor(String color){
        this.color = color;
    }
}
