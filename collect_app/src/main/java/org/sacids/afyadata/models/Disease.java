/*
 * Copyright (C) 2016 Sacids Tanzania
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.sacids.afyadata.models;


import org.parceler.Parcel;

/**
 * Created by Renfrid-Sacids on 5/30/2016.
 */
@Parcel
public class Disease {
    long id;
    String title;
    String specie_title;
    String description;
    String causes;
    String symptoms;
    String diagnosis;
    String treatment;

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
