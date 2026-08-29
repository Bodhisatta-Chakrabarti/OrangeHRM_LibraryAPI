package com.OrangeLibrary.framework.common.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a Book entity for the Library API.
 * Matches the actual API response structure:
 * { id, title, description, pageCount, excerpt, publishDate }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {

    private int id;
    private String title;
    private String description;
    private int pageCount;
    private String excerpt;
    private String publishDate;

    public Book()
    {
        // Default constructor required by Jackson
    }

    public Book(String title, String description, int pageCount, String excerpt, String publishDate) {
        this.title = title;
        this.description = description;
        this.pageCount = pageCount;
        this.excerpt = excerpt;
        this.publishDate = publishDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", pageCount=" + pageCount +
                ", excerpt='" + excerpt + '\'' +
                ", publishDate='" + publishDate + '\'' +
                '}';
    }
}
