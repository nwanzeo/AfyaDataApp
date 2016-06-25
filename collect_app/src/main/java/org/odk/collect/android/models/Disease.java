package org.odk.collect.android.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Renfrid-Sacids on 5/30/2016.
 */
public class Disease implements Parcelable {
    private long id;
    private String title;
    private String specie_title;
    private String description;
    private String causes;
    private String symptoms;
    private String diagnosis;
    private String treatment;

    public Disease() {
    }

    public Disease(long id, String title, String description, String causes, String symptoms, String diagnosis, String treatment, String specie_title) {
        this.id = id;
        this.title = title;
        this.specie_title = specie_title;
        this.description = description;
        this.causes = causes;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
    }

    public Disease(Parcel in){
        id = in.readLong();
        title = in.readString();
        specie_title = in.readString();
        description = in.readString();
        causes = in.readString();
        symptoms = in.readString();
        diagnosis = in.readString();
        treatment = in.readString();
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

    public String getCauses() {
        return causes;
    }

    public void setCauses(String causes) {
        this.causes = causes;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
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
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(specie_title);
        dest.writeString(description);
        dest.writeString(causes);
        dest.writeString(symptoms);
        dest.writeString(diagnosis);
        dest.writeString(treatment);
    }

    public static final Parcelable.Creator<Disease> CREATOR = new Parcelable.Creator<Disease>() {

        @Override
        public Disease createFromParcel(Parcel source) {
            return new Disease(source);
        }

        @Override
        public Disease[] newArray(int size) {
            return new Disease[size];
        }
    };
}
