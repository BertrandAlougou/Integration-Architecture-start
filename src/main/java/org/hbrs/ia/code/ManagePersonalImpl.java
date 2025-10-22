package org.hbrs.ia.code;

import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.hbrs.ia.model.SalesMan;
import org.hbrs.ia.model.SocialPerformanceRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManagePersonalImpl implements ManagePersonal{


    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> salesmenCollection;
    private MongoCollection<Document> socialperformanceCollection;

    public ManagePersonalImpl(){
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.mongoDatabase = mongoClient.getDatabase("HighPerformanceDatabase");
        this.salesmenCollection = this.mongoDatabase.getCollection("salesmen");
        this.socialperformanceCollection = this.mongoDatabase.getCollection("performanceRecords");
    }

    // create a SalesMan Document in the collection by giving a SalesMan object
    @Override
    public void createSalesMan(SalesMan salesMan) {
        Document salesManDocument = salesMan.toDocument();
        salesmenCollection.insertOne(salesManDocument);
    }

    //delete a SalesMan in the collection by sid
    @Override
    public void deleteSalesMan(int sid) {
        salesmenCollection.deleteOne(new Document("sid", sid));
    }

    //delete all SalesMan Documents in the collection
    @Override
    public void deleteAllSalesMan() {
        salesmenCollection.deleteMany(new Document());
    }

    // add a SocialPerformanceRecord to a SalesMan, by searching for the SalesMan by sid
    // and adding a SocialPerformanceRecord into the records ArrayList of the SalesMan
    @Override
    public void addSocialPerformanceRecord(SocialPerformanceRecord record, SalesMan salesMan) {
        int sid = salesMan.getId();

        // Update the SALESMAN document, not the performance collection
        Document filter = new Document("sid", sid);
        Document update = new Document("$push",
                new Document("performanceRecords", record.toDocument())  // Note: lowercase 'p'
        );

        salesmenCollection.updateOne(filter, update);
        System.out.println("✅ Added performance record to salesman ID: " + sid);
    }

    // read a SalesMan by sid
    @Override
    public SalesMan readSalesMan(int sid) {
        Document query = new Document("sid", sid);
        Document salesman = salesmenCollection.find().first();
        if(salesman != null){
            return new SalesMan(
                    salesman.getString("firstname"),
                    salesman.getString("lastname"),
                    salesman.getInteger("sid")
            );
        }
        return null;
    }

    // read all SalesMan in the Collection
    @Override
    public List<SalesMan> readAllSalesMen() {
        List<SalesMan> salesmanList = new ArrayList<>();

        try (MongoCursor<Document> cursor = salesmenCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document salesmanDocument = cursor.next();
                try {
                    SalesMan salesman = SalesMan.fromDocument(salesmanDocument); // Use model method
                    salesmanList.add(salesman);
                } catch (Exception e) {
                    System.err.println("❌ Error mapping document to SalesMan: " + e.getMessage());
                    System.err.println("Problematic document: " + salesmanDocument.toJson());
                    // Continue processing other documents instead of failing completely
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error reading salesmen from database: " + e.getMessage());
            throw new RuntimeException("Failed to read salesmen", e);
        }

        System.out.println("✅ Retrieved " + salesmanList.size() + " salesmen from database");
        return salesmanList;
    }

    @Override
    public List<SocialPerformanceRecord> readSocialPerformanceRecord(SalesMan salesMan) {
        return readByYearSocialPerformanceRecord(salesMan, null); // All years
    }

    //social performance record for a specific year
    @Override
    public List<SocialPerformanceRecord> readByYearSocialPerformanceRecord(SalesMan salesMan, Integer year) {
        if (salesMan == null) {
            return Collections.emptyList();
        }

        int salesmanId = salesMan.getId();
        List<SocialPerformanceRecord> performanceRecords = new ArrayList<>();

        try {
            // Build query - separate collections approach
            Document query = new Document("salesmanId", salesmanId);
            if (year != null) {
                query.append("year", year);
            }

            // Add sorting by year descending (most recent first)
            FindIterable<Document> results = socialperformanceCollection.find(query)
                    .sort(new Document("year", -1));

            try (MongoCursor<Document> cursor = results.iterator()) {
                while (cursor.hasNext()) {
                    SocialPerformanceRecord record = SocialPerformanceRecord.fromDocument(cursor.next());
                    performanceRecords.add(record);
                }
            }

        } catch (Exception e) {
            System.err.println("Error reading performance records for salesman " + salesmanId + ": " + e.getMessage());
            // Return empty list instead of propagating exception for read operations
        }

        return performanceRecords;
    }

    // read the last added SocialPerformanceRecord of a specific salesMan
    @Override
    public SocialPerformanceRecord readLastSocialPerformanceRecord(SalesMan salesMan) {
        if (salesMan == null) {
            throw new IllegalArgumentException("SalesMan cannot be null");
        }

        int salesmanId = salesMan.getId();

        try {
            // Query: Find by salesmanId, sort by year descending, limit to 1
            Document query = new Document("salesmanId", salesmanId);
            Document sort = new Document("year", -1); // Most recent first

            Document lastRecordDoc = socialperformanceCollection.find(query)
                    .sort(sort)
                    .limit(1)
                    .first();

            if (lastRecordDoc == null) {
                System.out.println("No performance records found for salesman: " + salesmanId);
                return null;
            }

            return SocialPerformanceRecord.fromDocument(lastRecordDoc);

        } catch (Exception e) {
            System.err.println("Error reading last performance record for salesman " + salesmanId + ": " + e.getMessage());
            return null;
        }
    }




    /**
     * @param record
     */
    @Override
    public void updateSalesMan(SalesMan record) {
    }


    @Override
    public void deleteByYearSocialPerformanceRecord(SalesMan salesMan, int year) {
        if (salesMan == null) {
            throw new IllegalArgumentException("SalesMan cannot be null");
        }

        int salesmanId = salesMan.getId();

        try {
            // Single operation - delete all records matching salesmanId and year
            Document query = new Document("salesmanId", salesmanId).append("year", year);

            DeleteResult result = socialperformanceCollection.deleteMany(query);

            if (result.getDeletedCount() > 0) {
                System.out.println("✅ Deleted " + result.getDeletedCount() +
                        " performance records for year " + year + " from salesman " + salesmanId);
            } else {
                System.out.println("ℹ️ No records found to delete for year " + year + " and salesman " + salesmanId);
            }

        } catch (Exception e) {
            System.err.println("❌ Error deleting performance records for salesman " + salesmanId +
                    " and year " + year + ": " + e.getMessage());
            throw new RuntimeException("Failed to delete performance records", e);
        }
    }

    @Override
    public void deleteLastSocialPerformanceRecord(SalesMan salesMan) {
        if (salesMan == null) {
            throw new IllegalArgumentException("SalesMan cannot be null");
        }

        int salesmanId = salesMan.getId();

        try {
            // Find the last record by sorting and limiting
            Document query = new Document("salesmanId", salesmanId);
            Document sort = new Document("year", -1).append("_id", -1);

            Document lastRecord = socialperformanceCollection.find(query)
                    .sort(sort)
                    .limit(1)
                    .first();

            if (lastRecord == null) {
                System.out.println("ℹ️ No performance records found for salesman " + salesmanId);
            }

            // Delete the specific record by its _id
            assert lastRecord != null;
            ObjectId recordId = lastRecord.getObjectId("_id");
            DeleteResult result = socialperformanceCollection.deleteOne(new Document("_id", recordId));

            if (result.getDeletedCount() > 0) {
                System.out.println("✅ Deleted last performance record for salesman " + salesmanId);
            } else {
                System.out.println("❌ Failed to delete last performance record for salesman " + salesmanId);
            }

        } catch (Exception e) {
            System.err.println("❌ Error deleting last performance record for salesman " + salesmanId +
                    ": " + e.getMessage());
            throw new RuntimeException("Failed to delete last performance record", e);
        }
    }

    public void debugCollections() {
        System.out.println("🔍 DEBUG COLLECTIONS:");

        // Check salesmen collection
        System.out.println("Salesmen collection documents:");
        for (Document doc : salesmenCollection.find()) {
            System.out.println(" - " + doc.toJson());
        }

        // Check performance collection
        System.out.println("Performance collection documents:");
        for (Document doc : socialperformanceCollection.find()) {
            System.out.println(" - " + doc.toJson());
        }
    }

}
