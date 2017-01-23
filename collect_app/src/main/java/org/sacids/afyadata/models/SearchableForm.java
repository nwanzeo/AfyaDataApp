package org.sacids.afyadata.models;

import org.parceler.Parcel;

/**
 * Created by Renfrid-Sacids on 11/22/2016.
 */
@Parcel
public class SearchableForm {
    long id;
    String title;
    String jrFormId;

    public SearchableForm() {
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

    public String getJrFormId() {
        return jrFormId;
    }

    public void setJrFormId(String jrFormId) {
        this.jrFormId = jrFormId;
    }
}
