package org.sacids.afyadata.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Renfrid-Sacids on 6/23/2016.
 */
@org.parceler.Parcel
public class Glossary {
    private int id;
    private String title;
    private String code;
    private String description;

    public Glossary() {
    }

    public Glossary(int id, String title, String code, String description) {
        this.id = id;
        this.title = title;
        this.code = code;
        this.description = description;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
