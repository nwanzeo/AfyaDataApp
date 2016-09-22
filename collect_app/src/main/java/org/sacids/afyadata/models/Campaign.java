package org.sacids.afyadata.models;


import org.parceler.Parcel;

/**
 * Created by Renfrid-Sacids on 5/25/2016.
 */
@Parcel
public class Campaign {

    private int id;
    private String title;
    private String type;
    private String featured;
    private String jrFormId;
    private String description;
    private String icon;
    private String dateCreated;

    public Campaign() {
    }

    public Campaign(int id, String title, String type, String featured, String jrFormId, String description, String icon, String dateCreated) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.featured = featured;
        this.jrFormId = jrFormId;
        this.description = description;
        this.icon = icon;
        this.dateCreated = dateCreated;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFeatured() {
        return featured;
    }

    public void setFeatured(String featured) {
        this.featured = featured;
    }

    public String getJrFormId() {
        return jrFormId;
    }

    public void setJrFormId(String jrFormId) {
        this.jrFormId = jrFormId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }
}
