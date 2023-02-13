package com.timothy.Models;

public class Images {

    private String title;
    private Integer image;
    private String desc;

    public Images(String title, Integer image, String desc) {
        this.title = title;
        this.image = image;
        this.desc = desc;
    }

    public Images() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
