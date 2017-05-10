package org.sacids.afyadata.models;

import org.parceler.Parcel;

/**
 * Created by Renfrid-Sacids on 11/22/2016.
 */
@Parcel
public class SearchableData {
    long id;
    long formId;
    String label;
    String value;

    public SearchableData() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFormId() {
        return formId;
    }

    public void setFormId(long formId) {
        this.formId = formId;
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
