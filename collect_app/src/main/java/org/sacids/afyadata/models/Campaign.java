package org.sacids.afyadata.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Renfrid-Sacids on 5/25/2016.
 */
public class Campaign implements Parcelable {

    private int id;
    private String title;
    private String type;
    private String formId;
    private String description;
    private String icon;
    private String dateCreated;

    public Campaign() {
    }

    public Campaign(int id, String title, String type, String formId, String description, String icon, String dateCreated) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.formId = formId;
        this.description = description;
        this.icon = icon;
        this.dateCreated = dateCreated;
    }

    public Campaign(Parcel in){
        id = in.readInt();
        title = in.readString();
        type = in.readString();
        formId = in.readString();
        description = in.readString();
        icon = in.readString();
        dateCreated = in.readString();
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

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(formId);
        dest.writeString(description);
        dest.writeString(icon);
        dest.writeString(dateCreated);
    }

    public static final Parcelable.Creator<Campaign> CREATOR = new Parcelable.Creator<Campaign>() {

        @Override
        public Campaign createFromParcel(Parcel source) {
            return new Campaign(source);
        }

        @Override
        public Campaign[] newArray(int size) {
            return new Campaign[size];
        }
    };
}
