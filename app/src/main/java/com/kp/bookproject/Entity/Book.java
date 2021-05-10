package com.kp.bookproject.Entity;

import java.util.ArrayList;

public class Book {
    private int id;
    private ArrayList<Integer> tags_id;
    private String book_name;
    private String authorName;
    private String description;
    private String image_url;
    private int rating;

    public Book(int id, ArrayList<Integer> tags_id, String book_name, String authorName, String description, String image_url,int rating) {
        this.id = id;
        this.tags_id = tags_id;
        this.book_name = book_name;
        this.authorName = authorName;
        this.description = description;
        this.image_url = image_url;
        this.rating=rating;
    }
//для recyclerview, чтоб сократить время запроса
    public Book(int id, String book_name, String authorName, String image_url) {
        this.id = id;
        this.book_name = book_name;
        this.authorName = authorName;
        this.image_url = image_url;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Integer> getTags_id() {
        return tags_id;
    }

    public void setTags_id(ArrayList<Integer> tags_id) {
        this.tags_id = tags_id;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", book_name='" + book_name + '\'' +
                ", authorName='" + authorName + '\'' +
                ", image_url='" + image_url + '\'' +
                '}';
    }
}
