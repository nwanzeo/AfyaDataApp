package org.odk.collect.android.models;

/**
 * Created by Renfrid-Sacids on 6/30/2016.
 */
public class FormDetails {
    private long id;
    private String label;
    private String type;
    private String value;
    private String instanceId;


    public FormDetails() {
    }

    public FormDetails(long id, String label, String type, String value, String instanceId) {
        this.id = id;
        this.label = label;
        this.type = type;
        this.value = value;
        this.instanceId = instanceId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
