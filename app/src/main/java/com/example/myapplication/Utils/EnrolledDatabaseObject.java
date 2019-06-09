package com.example.myapplication.Utils;

public class EnrolledDatabaseObject {

    String image_path;
    String image_name;
    int image_id;

    public EnrolledDatabaseObject(String image_path, String image_name, int image_id){
        this.image_path = image_path;
        this.image_name = image_name;
        this.image_id = image_id;
    }


    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }


    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }
}
