package org.sacids.afyadata.models;


import org.parceler.Parcel;

/**
 * Created by Renfrid-Sacids on 5/30/2016.
 */
@Parcel
public class Disease {
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

}
