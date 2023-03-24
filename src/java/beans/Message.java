/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.sql.Timestamp;

/**
 *
 * @author Lazar
 */
public class Message {
    private Integer contactId;
    private String contactEmail;
    private String contactContent;
    private Timestamp contactDate;

    public Message() {
    }

    public Message(Integer contactId, String contactEmail, String contactContent, Timestamp contactDate) {
        this.contactId = contactId;
        this.contactEmail = contactEmail;
        this.contactContent = contactContent;
        this.contactDate = contactDate;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactContent() {
        return contactContent;
    }

    public void setContactContent(String contactContent) {
        this.contactContent = contactContent;
    }

    public Timestamp getContactDate() {
        return contactDate;
    }

    public void setContactDate(Timestamp contactDate) {
        this.contactDate = contactDate;
    }
}
