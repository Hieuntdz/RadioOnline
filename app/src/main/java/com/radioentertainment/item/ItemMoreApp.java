package com.radioentertainment.item;

public class ItemMoreApp {
    private int id;
    private String name;
    private String url;
    private String thumb;



    public ItemMoreApp(int id, String name, String url, String thumb) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
