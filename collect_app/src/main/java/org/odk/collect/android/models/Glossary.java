package org.odk.collect.android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Renfrid-Sacids on 6/23/2016.
 */
public class Glossary implements Parcelable {
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

    public Glossary(Parcel in) {
        id = in.readInt();
        title = in.readString();
        code = in.readString();
        description = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(code);
        dest.writeString(description);
    }

    public static final Parcelable.Creator<Glossary> CREATOR = new Parcelable.Creator<Glossary>() {

        @Override
        public Glossary createFromParcel(Parcel source) {
            return new Glossary(source);
        }

        @Override
        public Glossary[] newArray(int size) {
            return new Glossary[size];
        }
    };
}
