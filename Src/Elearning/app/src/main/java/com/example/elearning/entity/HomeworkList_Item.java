package com.example.elearning.entity;

import com.example.elearning.R;

public class HomeworkList_Item {
    private String name;
    private boolean can_dup;
    private String startTime;
    private String deadline;
    private String content;
    private boolean has_done;
    private int endImg;

    public HomeworkList_Item() {
    }

    public HomeworkList_Item(String name, boolean can_dup, String startTime, String deadline, String content, boolean has_done) {
        this.name = name;
        this.can_dup = can_dup;
        this.startTime = startTime;
        this.deadline = deadline;
        this.content = content;
        this.has_done = has_done;
        if(has_done == true) endImg = R.drawable.finished;
        else endImg = R.drawable.unfinished;
    }

    public String getName() {
        return name;
    }

    public boolean getCan_dup(){
        return can_dup;
    }

    public String getStartTime(){
        return startTime;
    }

    public String getDeadline(){
        return deadline;
    }

    public String getContent(){
        return content;
    }

    public boolean getHas_done(){
        return has_done;
    }

    public int getEndImg(){
        return endImg;
    }

    public void setCan_dup(boolean can_dup){
        this.can_dup = can_dup;
    }

    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public void setDeadline(String deadline){
        this.deadline = deadline;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setHas_done(boolean has_done){
        this.has_done = has_done;
        if(has_done == true) endImg = R.drawable.finished;
        else endImg = R.drawable.unfinished;
    }

}
