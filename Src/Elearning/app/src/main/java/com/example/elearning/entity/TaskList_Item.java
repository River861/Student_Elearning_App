package com.example.elearning.entity;

import com.example.elearning.R;

public class TaskList_Item {
    private String content;
    private boolean has_done;
    private int endImg;
    private int task_id;

    public TaskList_Item() {
    }

    public TaskList_Item(String content, boolean has_done, int task_id) {
        this.content = content;
        this.has_done = has_done;
        this.task_id = task_id;
        if(has_done == true) endImg = R.drawable.task_finish;
        else endImg = R.drawable.task_unfinish;
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

    public int getTask_id(){
        return task_id;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setHas_done(boolean has_done){
        this.has_done = has_done;
        if(has_done == true) endImg = R.drawable.task_finish;
        else endImg = R.drawable.task_unfinish;
    }

    public void setTask_id(int task_id){
        this.task_id = task_id;
    }

}
