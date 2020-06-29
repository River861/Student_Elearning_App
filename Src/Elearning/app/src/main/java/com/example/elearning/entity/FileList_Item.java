package com.example.elearning.entity;

import com.example.elearning.R;

public class FileList_Item {
    private String type;
    private int img;
    private String name;
    private String files_url;
    private String folders_url;
    private int size;
    private String download_url;

    public FileList_Item() {
    }
    // 文件夹
    public FileList_Item(String type, String name, String files_url, String folders_url) {
        this.type = type;
        this.name = name;
        this.files_url = files_url;
        this.folders_url = folders_url;
        img = R.drawable.folder;
    }
    // 文件
    public FileList_Item(String type, String name, int size, String download_url) {
        this.type = type;
        this.name = name;
        this.size = size;
        this.download_url = download_url;
        switch (type){
            case "folder": img = R.drawable.folder; break;
            case "doc": img = R.drawable.doc; break;
            case "text": img = R.drawable.text; break;
            case "pdf": img = R.drawable.pdf; break;
            case "zip": img = R.drawable.zip; break;
            case "ppt": img = R.drawable.ppt; break;
            default: img = R.drawable.file;
        }
    }

    public String getType() {
        return type;
    }

    public int getImg(){
        return img;
    }

    public String getName() {
        return name;
    }

    public String getFiles_url(){
        return files_url;
    }

    public String getFolders_url(){
        return folders_url;
    }

    public int getSize() {
        return size;
    }

    public String getDownload_url(){
        return download_url;
    }

    public void setType(String type) {
        this.type = type;
        switch (type){
            case "folder": img = R.drawable.folder; break;
            case "doc": img = R.drawable.doc; break;
            case "text": img = R.drawable.text; break;
            case "pdf": img = R.drawable.pdf; break;
            case "zip": img = R.drawable.zip; break;
            default: img = R.drawable.file;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFiles_url(String files_url){
        this.files_url = files_url;
    }

    public void setFolders_url(String folders_url){
        this.folders_url = folders_url;
    }

    public void setSize(int size){
        this.size = size;
    }

    public void setDownload_url(String download_url){
        this.download_url = download_url;
    }
}
