package org.hbrs.ia.code;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.hbrs.ia.model.SalesMan;
import org.hbrs.ia.model.SocialPerformanceRecord;

import java.util.ArrayList;
import java.util.List;

public class ManagePersonalImpl implements ManagePersonal{


    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> salesmenCollection;
    private MongoCollection<Document> socialperformanceCollection;

    public ManagePersonalImpl() {
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.mongoDatabase = mongoClient.getDatabase("highperformance");
        this.salesmenCollection = this.mongoDatabase.getCollection("salesman");
        this.socialperformanceCollection = this.mongoDatabase.getCollection("performanceRecords");
    }

    /**
     * @param record
     */
    @Override
    public void createSalesMan(SalesMan record) {
    salesmenCollection.insertOne(record.toDocument());
    }

    /**
     * @param sid
     * @return
     */
    @Override
    public SalesMan readSalesMan(int sid) {
        Document query = new Document("sid", sid);
        Document salesman = salesmenCollection.find(query).first();
        if(salesman != null){
            return new SalesMan(
                    salesman.getString("firstname"),
                    salesman.getString("lastname"),
                    salesman.getInteger("salesmanId")
            );
        }

        return null;
    }

    /**
     * @return
     */
    @Override
    public List<SalesMan> readAllSalesMen() {
        return List.of();
    }

    /**
     * @param salesMan
     * @return
     */
    @Override
    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan) {
     return List.of();
    }

    /**
     * @param salesMan
     * @return
     */
    @Override
    public SocialPerformanceRecord readLastSocialPerformanceRecord(SalesMan salesMan) {
        return null;
    }

    /**
     * @param salesMan
     * @param year
     * @return
     */
    @Override
    public SocialPerformanceRecord readByYearSocialPerformanceRecord(SalesMan salesMan, int year) {
        return null;
    }

    /**
     * @param record
     */
    @Override
    public void updateSalesMan(SalesMan record) {

    }

    /**
     * @param record
     * @param salesMan
     */
    @Override
    public void addSocialPerformanceRecord(SocialPerformanceRecord record, SalesMan salesMan) {
        socialperformanceCollection.insertOne(record.toDocument());
    }

    /**
     * @param sid
     */
    @Override
    public void deleteSalesMan(int sid) {
       Document query = new Document("sid", sid);
       salesmenCollection.deleteOne(query);
    }

    /**
     *
     */
    @Override
    public void deleteAllSalesMan() {
        Document query = new Document();
        salesmenCollection.deleteMany(new Document());
    }
}
