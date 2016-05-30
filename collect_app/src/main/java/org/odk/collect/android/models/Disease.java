package org.odk.collect.android.models;

/**
 * Created by Renfrid-Sacids on 5/30/2016.
 */
public class Disease {
    private long id;
    private String title;
    private String specie_title;
    private String description;

    public Disease() {
    }

    public Disease(long id, String title, String description, String specie_title) {
        this.id = id;
        this.title = title;
        this.specie_title = specie_title;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSpecie_title() {
        return specie_title;
    }

    public void setSpecie_title(String specie_title) {
        this.specie_title = specie_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
