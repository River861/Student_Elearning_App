package com.example.elearning;

public class GlobalFlag {
    private static String last_course_id = null;
    private static String new_color = null;

    public static void setNew_color(String course_id, String new_color) {
        GlobalFlag.last_course_id = course_id;
        GlobalFlag.new_color = new_color;
    }

    public static String getLast_course_id(){
        return last_course_id;
    }

    public static String getNew_color(){
        return new_color;
    }

}
