package org.sacids.afyadata.models;

import org.parceler.Parcel;

/**
 * Created by Renfrid-Sacids on 10/5/2016.
 */
@Parcel
public class LabRequest {
    private long id;
    private String label;
    private String value;

    public LabRequest() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
