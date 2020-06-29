package com.example.elearning.entity;

public class AnnounceList_Item {
    private String title;
    private String time;
    private String author;
    private String content;

    public AnnounceList_Item() {
    }

    public AnnounceList_Item(String title, String time, String author, String content) {
        this.title = title;
        this.time = time;
        this.author = author;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getAuthor(){
        return author;
    }

    public String getContent(){
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setContent(String content){
        this.content = content;
    }
}
