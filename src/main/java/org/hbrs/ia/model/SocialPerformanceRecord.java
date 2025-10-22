package org.hbrs.ia.model;

import org.bson.Document;

public class SocialPerformanceRecord {
    private Integer salesmanId;
    private Integer leadership;
    private Integer openness;
    private Integer behaviour;
    private Integer attitude;
    private Integer communication;
    private Integer integrity;
    private Integer year;

    public SocialPerformanceRecord(int salesmanId, int leadership, int openness, int behaviour,
                                   int attitude, int communication, int integrity,
                                   int year){
        this.salesmanId = salesmanId;
        this.leadership = leadership;
        this.openness = openness;
        this.behaviour = behaviour;
        this.attitude = attitude;
        this.communication = communication;
        this.integrity = integrity;
        this.year = year;
    }
    public Integer getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(Integer salesmanId) {
        this.salesmanId = salesmanId;
    }

    public Integer getLeadership() {
        return leadership;
    }

    public void setLeadership(Integer leadership) {
        this.leadership = leadership;
    }

    public Integer getOpenness() {
        return openness;
    }

    public void setOpenness(Integer openness) {
        this.openness = openness;
    }

    public Integer getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(Integer behaviour) {
        this.behaviour = behaviour;
    }

    public Integer getAttitude() {
        return attitude;
    }

    public void setAttitude(Integer attitude) {
        this.attitude = attitude;
    }

    public Integer getCommunication() {
        return communication;
    }

    public void setCommunication(Integer communication) {
        this.communication = communication;
    }

    public Integer getIntegrity() {
        return integrity;
    }

    public void setIntegrity(Integer integrity) {
        this.integrity = integrity;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * Converts SocialPerformanceRecord to Document (instance method)
     */
    public Document toDocument() {
        org.bson.Document document = new Document();
        document.append("leadership", this.leadership);
        document.append("openness", this.openness);
        document.append("behaviour", this.behaviour);
        document.append("attitude", this.attitude);
        document.append("communication", this.communication);
        document.append("integrity", this.integrity);
        document.append("year", this.year);
        return document;
    }

    /**
     * Converts Document to SocialPerformanceRecord object (static factory method)
     */
    public static SocialPerformanceRecord fromDocument(Document doc) {
        return new SocialPerformanceRecord(
                doc.getInteger("salesmanId"),
                doc.getInteger("leadership"),
                doc.getInteger("openness"),
                doc.getInteger("behaviour"),
                doc.getInteger("attitude"),
                doc.getInteger("communication"),
                doc.getInteger("integrity"),
                doc.getInteger("year")
        );
    }

    public String toString(){
        return  "salesmanId: " + this.getSalesmanId() + "\n" +
                "leadership: " + this.getLeadership() + "\n" +
                "openness: " + this.getOpenness() + "\n" +
                "behaviour: " + this.getBehaviour() + "\n" +
                "attitude: " + this.getAttitude() + "\n" +
                "communication: " + this.getCommunication() + "\n" +
                "integrity: " + this.getIntegrity() + "\n" +
                "year: " + this.getYear() + "\n";
    }

}
