package org.sacids.afyadata.models;


import org.parceler.Parcel;

/**
 * Created by Renfrid-Sacids on 3/15/2016.
 */
@Parcel
public class Feedback{

    private long id;
    private String formId;
    private String instanceId;
    private String title;
    private String message;
    public String sender;
    private String userName;
    private String chrName;
    private String dateCreated;
    private String status;
    private String replyBy;

    public Feedback() {
    }

    public Feedback(long id, String formId, String instanceId, String title, String message, String sender, String userName, String chrName, String dateCreated, String status, String replyBy) {
        this.id = id;
        this.formId = formId;
        this.instanceId = instanceId;
        this.title = title;
        this.message = message;
        this.sender = sender;
        this.userName = userName;
        this.chrName = chrName;
        this.dateCreated = dateCreated;
        this.status = status;
        this.replyBy = replyBy;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getChrName() {
        return chrName;
    }

    public void setChrName(String chrName) {
        this.chrName = chrName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReplyBy() {
        return replyBy;
    }

    public void setReplyBy(String replyBy) {
        this.replyBy = replyBy;
    }
}


