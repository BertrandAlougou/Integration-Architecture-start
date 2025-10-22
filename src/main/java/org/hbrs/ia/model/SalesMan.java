package org.hbrs.ia.model;

import org.bson.Document;

import java.util.ArrayList;

public class SalesMan {
    private String firstname;
    private String lastname;
    private Integer sid;

    private ArrayList<SocialPerformanceRecord> records;

    public SalesMan(String firstname, String lastname, Integer sid) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.sid = sid;
        this.records = new ArrayList<>();
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getId() {
        return sid;
    }

    public void setId(Integer sid) {
        this.sid = sid;
    }

    /**
     * Converts SalesMan to Document (instance method)
     */
    public Document toDocument() {
        org.bson.Document document = new Document();
        document.append("firstname" , this.firstname );
        document.append("lastname" , this.lastname );
        document.append("sid" , this.sid);
        document.append("PerformanceRecords", records);
        return document;
    }

    /**
     * Converts Document to SalesMan object (static factory method)
     */
    public static SalesMan fromDocument(Document doc) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        String firstname = doc.getString("firstname");
        String lastname = doc.getString("lastname");
        Integer sid = doc.getInteger("sid");

        if (firstname == null || lastname == null || sid == null) {
            throw new IllegalArgumentException("Required fields are missing in document");
        }

        return new SalesMan(firstname, lastname, sid);
    }


    public String toString(){
        return "firstname: " + this.firstname + "\n" +
                "lastname: " + this.lastname + "\n" +
                "sid: " + this.sid + "\n";
    }

}
