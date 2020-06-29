package com.example.elearning.entity;

import com.example.elearning.R;

public class TodoList_Item {
    private String title;
    private String subtitle;
    private String type;
    private int frontImg;
    private int endImg;

    public TodoList_Item() {
    }

    public TodoList_Item(String title, String subtitle, String type) {
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        if(type.equals("homework")){
            frontImg = R.drawable.homework;
            endImg = R.drawable.unfinished;
        }
        else{
            frontImg = R.drawable.calendar_task;
            endImg = R.drawable.unfinished;
        }
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle(){
        return subtitle;
    }

    public String getType(){
        return type;
    }

    public int getEndImg(){
        return endImg;
    }

    public int getFrontImg() {
        return frontImg;
    }

    public void setSubtitle(String subtitle){
        this.subtitle = subtitle;
    }

    public void setType(String type){
        this.type = type;
        if(type.equals("homework")){
            frontImg = R.drawable.homework;
            endImg = R.drawable.unfinished;
        }
        else{
            frontImg = R.drawable.calendar_task;
            endImg = R.drawable.unfinished;
        }
    }

}
