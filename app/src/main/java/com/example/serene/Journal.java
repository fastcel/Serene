package com.example.serene;

import java.util.List;

public class Journal {
    public String id;
    public boolean isFavorite;
    public String title;
    public String content;
    public String date;
    public long timestamp;
    public List<String> themes;
    public Journal() {}
    public Journal(String title, String content,
                   String date, long timestamp,
                   List<String> themes, boolean isFavorite) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.timestamp = timestamp;
        this.themes = themes;
        this.isFavorite = isFavorite;
    }
}