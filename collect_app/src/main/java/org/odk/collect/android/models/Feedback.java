package org.odk.collect.android.models;

/**
 * Created by Renfrid-Sacids on 3/15/2016.
 */
public class Feedback {

    private long id;
    private long userId;
    private String userName;
    private String formId;
    private String instanceId;
    private String message;
    private String dateCreated;
    private String viewedBy;
    public String sender;

    //empty Constructor
    public Feedback(){

    }



    //Another constructor
    public Feedback(long _id, String _formId, String _instanceId, String _message, String _date){
        this.id = _id;
        this.formId = _formId;
        this.instanceId = _instanceId;
        this.message = _message;
        this.dateCreated = _date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getViewedBy() {
        return viewedBy;
    }

    public void setViewedBy(String viewedBy) {
        this.viewedBy = viewedBy;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", formId='" + formId + '\'' +
                ", instanceId='" + instanceId + '\'' +
                ", message='" + message + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", viewedBy='" + viewedBy + '\'' +
                ", sender='" + sender + '\'' +
                '}';
    }
}


