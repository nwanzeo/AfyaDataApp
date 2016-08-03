package org.sacids.afyadata.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Renfrid-Sacids on 3/15/2016.
 */
public class Feedback implements Parcelable{

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

    public Feedback(Parcel in){
        id = in.readInt();
        formId = in.readString();
        instanceId = in.readString();
        title = in.readString();
        message = in.readString();
        sender = in.readString();
        userName = in.readString();
        chrName = in.readString();
        dateCreated = in.readString();
        status = in.readString();
        replyBy = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(formId);
        dest.writeString(instanceId);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(sender);
        dest.writeString(userName);
        dest.writeString(chrName);
        dest.writeString(dateCreated);
        dest.writeString(status);
        dest.writeString(replyBy);
    }

    public static final Parcelable.Creator<Feedback> CREATOR = new Parcelable.Creator<Feedback>() {

        @Override
        public Feedback createFromParcel(Parcel source) {
            return new Feedback(source);
        }

        @Override
        public Feedback[] newArray(int size) {
            return new Feedback[size];
        }
    };
}


