package com.example.elearning.entity;

public class MemberList_Item {
    private String id;
    private String avatar_url;
    private String name;
    private String role;
    private String uis_id;

    public MemberList_Item() {
    }

    public MemberList_Item(String id, String avatar_url, String name, String role, String uis_id) {
        this.id = id;
        this.avatar_url = avatar_url;
        this.name = name;
        this.role = role;
        this.uis_id = uis_id;
    }

    public String getId(){
        return id;
    }

    public String getAvatar_url(){
        return avatar_url;
    }

    public String getName(){
        return name;
    }

    public String getRole(){
        return role;
    }

    public String getUis_id(){
        return uis_id;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setAvatar_url(String avatar_url){
        this.avatar_url = avatar_url;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setRole(String role){
        this.role = role;
    }

    public void setUis_id(String uis_id){
        this.uis_id = uis_id;
    }
}
