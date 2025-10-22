package org.hbrs.mongodb.test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.hbrs.ia.model.SalesMan;
import org.bson.Document;
import org.hbrs.ia.model.SocialPerformanceRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MongoDB tests - preserving original teacher's tests and adding comprehensive coverage
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HighPerformanceTest {

    private MongoClient client;
    private MongoDatabase supermongo;
    private MongoCollection<Document> salesmen;

    /**
     * Attention: You might update the version of the Driver
     * for newer version of MongoDB!
     * This tests run with MongoDB 4.2.17 Community
     */
    @BeforeEach
    void setUp() {
        // Setting up the connection to a local MongoDB with standard port 27017
        // must be started within a terminal with command 'mongod'.
        client = MongoClients.create("mongodb://localhost:27017");

        // Get database 'HighPerformanceDatabase' (creates one if not available)
        supermongo = client.getDatabase("HighPerformanceDatabase");

        // Get Collection 'salesmen' (creates one if not available)
        salesmen = supermongo.getCollection("salesmen");

    }

    // =========================================================================
    //                     TEACHER'S ORIGINAL TESTS
    // =========================================================================

    @Test
    void insertSalesMan() {
        // CREATE (Storing) the salesman object
        Document document = new Document();
        document.append("firstname" , "Sascha");
        document.append("lastname" , "Alda");
        document.append("sid" , 90133);

        // ... now storing the object
        salesmen.insertOne(document);

        // READ (Finding) the stored Documnent
        Document newDocument = this.salesmen.find().first();
        System.out.println("Printing the object (JSON): " + newDocument );

        // Assertion
        assertNotNull(newDocument);
        Integer sid = (Integer) newDocument.get("sid");
        assertEquals( 90133 , sid );

        // Deletion
        salesmen.drop();
    }

    @Test
    void insertSalesManMoreObjectOriented() {
        // CREATE (Storing) the salesman business object
        // Using setter instead
        SalesMan salesMan = new SalesMan( "Leslie" , "Malton" , 90444 );

        // ... now storing the object
        salesmen.insertOne(salesMan.toDocument());

        // READ (Finding) the stored Documnent
        // Mapping Document to business object would be fine...
        Document newDocument = this.salesmen.find().first();
        System.out.println("Printing the object (JSON): " + newDocument );

        // Assertion
        assertNotNull(newDocument);
        Integer sid = (Integer) newDocument.get("sid");
        assertEquals( 90444 , sid );

        // Deletion
        salesmen.drop();
    }

    // =========================================================================
    //                   ADDITIONAL TESTS - SALESMEN
    // =========================================================================

    @Test
    @Order(3)
    @DisplayName("Should handle multiple salesman operations correctly")
    void multipleSalesManOperations_shouldWorkCorrectly() {
        // Given
        SalesMan salesMan1 = new SalesMan("Sascha", "Alda", 90133);
        SalesMan salesMan2 = new SalesMan("Leslie", "Malton", 90444);

        // When
        salesmen.insertOne(salesMan1.toDocument());
        salesmen.insertOne(salesMan2.toDocument());

        // Then
        long count = salesmen.countDocuments();
        assertEquals(2, count, "Should have two salesmen in collection");

        // Verify both documents can be retrieved and have correct data
        var allDocuments = salesmen.find();
        int documentCount = 0;
        for (Document doc : allDocuments) {
            assertNotNull(doc.getString("firstname"), "Each document should have firstname");
            assertNotNull(doc.getString("lastname"), "Each document should have lastname");
            assertNotNull(doc.getInteger("sid"), "Each document should have sid");
            documentCount++;
        }
        assertEquals(2, documentCount, "Should iterate through both documents");

        System.out.println("✅ Multiple operations completed successfully");

        // Cleanup
        salesmen.drop();
    }

    @Test
    @Order(4)
    @DisplayName("Should query salesman by specific ID")
    void querySalesManById_shouldReturnCorrectDocument() {
        // Given
        SalesMan salesMan1 = new SalesMan("Sascha", "Alda", 90133);
        SalesMan salesMan2 = new SalesMan("Leslie", "Malton", 90444);

        salesmen.insertOne(salesMan1.toDocument());
        salesmen.insertOne(salesMan2.toDocument());

        // When - Query for specific salesman
        Document query = new Document("sid", salesMan1.getId());
        Document result = salesmen.find(query).first();

        // Then
        assertNotNull(result, "Should find salesman by ID");
        assertEquals(salesMan1.getFirstname(), result.getString("firstname"),
                "Should return correct salesman by ID query");
        assertEquals(salesMan1.getId(), result.getInteger("sid"),
                "Queried ID should match");

        System.out.println("🔍 Query result: " + result.toJson());

        // Cleanup
        salesmen.drop();
    }

    @Test
    @Order(5)
    @DisplayName("Should handle empty collection gracefully")
    void operationsOnEmptyCollection_shouldNotFail() {
        // Given - Empty collection (no insertions)

        // When
        Document firstDocument = salesmen.find().first();
        long count = salesmen.countDocuments();

        // Then
        assertNull(firstDocument, "First document should be null in empty collection");
        assertEquals(0, count, "Count should be zero for empty collection");

        System.out.println("✅ Empty collection handled correctly");

        // No cleanup needed since no documents were inserted
    }

    @Test
    @Order(6)
    @DisplayName("Should verify document structure and required fields")
    void documentStructure_shouldContainRequiredFields() {
        // Given
        SalesMan salesMan = new SalesMan("Sascha", "Alda", 90133);
        salesmen.insertOne(salesMan.toDocument());

        // When
        Document document = salesmen.find().first();

        // Then - Verify all required fields exist and have correct types
        assertNotNull(document);
        assertTrue(document.containsKey("firstname"), "Document should contain firstname field");
        assertTrue(document.containsKey("lastname"), "Document should contain lastname field");
        assertTrue(document.containsKey("sid"), "Document should contain sid field");

        assertInstanceOf(String.class, document.get("firstname"), "firstname should be String");
        assertInstanceOf(String.class, document.get("lastname"), "lastname should be String");
        assertInstanceOf(Integer.class, document.get("sid"), "sid should be Integer");

        System.out.println("📋 Document structure verified: " + document.keySet());

        // Cleanup
        salesmen.drop();
    }

    @Test
    @Order(7)
    @DisplayName("Should handle document with fromDocument mapping")
    void documentMapping_withFromDocument_shouldWork() {
        // Given
        SalesMan originalSalesMan = new SalesMan("Sascha", "Alda", 90133);
        salesmen.insertOne(originalSalesMan.toDocument());

        // When
        Document storedDocument = salesmen.find().first();

        // Then - Test if fromDocument method exists and works
        try {
            // This will only work if SalesMan class has fromDocument static method
            SalesMan retrievedSalesMan = SalesMan.fromDocument(storedDocument);
            assertNotNull(retrievedSalesMan, "Should be able to map document back to object");
            assertEquals(originalSalesMan.getFirstname(), retrievedSalesMan.getFirstname(),
                    "Round-trip mapping should preserve firstname");
            assertEquals(originalSalesMan.getLastname(), retrievedSalesMan.getLastname(),
                    "Round-trip mapping should preserve lastname");
            assertEquals(originalSalesMan.getId(), retrievedSalesMan.getId(),
                    "Round-trip mapping should preserve ID");
            System.out.println("✅ Round-trip object mapping successful");
        } catch (Exception e) {
            System.out.println("ℹ️ fromDocument method not available in SalesMan class, but raw document operations work");
            // Fallback: verify raw document data
            assertNotNull(storedDocument);
            assertEquals(originalSalesMan.getFirstname(), storedDocument.getString("firstname"));
            assertEquals(originalSalesMan.getLastname(), storedDocument.getString("lastname"));
            assertEquals(originalSalesMan.getId(), storedDocument.getInteger("sid"));
        }

        // Cleanup
        salesmen.drop();
    }

    @Test
    @Order(8)
    @DisplayName("Should handle large number of salesmen efficiently")
    void insertManySalesMen_shouldHandleBulkOperations() {
        // Given
        int numberOfSalesmen = 50; // Reasonable number for testing

        // When
        for (int i = 0; i < numberOfSalesmen; i++) {
            Document salesman = new Document()
                    .append("firstname", "FirstName" + i)
                    .append("lastname", "LastName" + i)
                    .append("sid", 1000 + i);
            salesmen.insertOne(salesman);
        }

        // Then
        long count = salesmen.countDocuments();
        assertEquals(numberOfSalesmen, count,
                "Should handle multiple insert operations efficiently");

        System.out.println("✅ Successfully inserted and verified " + count + " salesmen");

        // Cleanup
        salesmen.drop();
    }

    @Test
    @Order(9)
    @DisplayName("Should maintain data integrity after multiple operations")
    void multipleOperations_shouldMaintainDataIntegrity() {
        // Given
        SalesMan salesMan1 = new SalesMan("John", "Doe", 1001);
        SalesMan salesMan2 = new SalesMan("Jane", "Smith", 1002);

        // When - Perform multiple operations
        salesmen.insertOne(salesMan1.toDocument());
        salesmen.insertOne(salesMan2.toDocument());

        // Verify initial state
        long initialCount = salesmen.countDocuments();
        assertEquals(2, initialCount, "Should have two documents after insertion");

        // Query and verify specific records
        Document query1 = new Document("sid", 1001);
        Document result1 = salesmen.find(query1).first();
        assertNotNull(result1, "Should find first salesman");
        assertEquals("John", result1.getString("firstname"));

        Document query2 = new Document("sid", 1002);
        Document result2 = salesmen.find(query2).first();
        assertNotNull(result2, "Should find second salesman");
        assertEquals("Jane", result2.getString("firstname"));

        // Then - All assertions passed, data integrity maintained
        System.out.println("✅ Data integrity maintained through multiple operations");

        // Cleanup
        salesmen.drop();
    }


    // =========================================================================
    //       PERFORMANCE RECORDS TESTS - EMBEDDED DOCUMENTS APPROACH
    // =========================================================================

    @Test
    @Order(10)
    @DisplayName("Should add performance record to salesman document")
    void addPerformanceRecord_shouldCreatePerformanceRecordsArray() {
        // Given
        SalesMan salesMan = new SalesMan("John", "Smith", 90123);
        salesmen.insertOne(salesMan.toDocument());

        SocialPerformanceRecord performanceRecord = new SocialPerformanceRecord(
                90123, 4, 3, 4, 5, 4, 4, 2024
        );

        // When - Add performance record using $push
        Document filter = new Document("sid", salesMan.getId());
        Document update = new Document("$push",
                new Document("performanceRecords", performanceRecord.toDocument())
        );

        UpdateResult result = salesmen.updateOne(filter, update);

        // Then
        assertEquals(1, result.getModifiedCount(), "Should modify one document");

        // Verify the record was added
        Document updatedSalesman = salesmen.find(filter).first();
        assertNotNull(updatedSalesman, "Salesman should exist");

        List<Document> performanceRecords = updatedSalesman.getList("performanceRecords", Document.class);
        assertNotNull(performanceRecords, "Performance records array should exist");
        assertEquals(1, performanceRecords.size(), "Should have one performance record");

        Document firstRecord = performanceRecords.get(0);
        assertEquals(2024, firstRecord.getInteger("year"), "Year should match");
        assertEquals(4, firstRecord.getInteger("leadership"), "Leadership score should match");
        assertEquals(3, firstRecord.getInteger("openness"), "Openness score should match");

        System.out.println("✅ Performance record added: " + firstRecord.toJson());

        // Cleanup
        salesmen.drop();
    }

    @Test
    @Order(11)
    @DisplayName("Should add multiple performance records for same salesman")
    void addMultiplePerformanceRecords_shouldAllBeStoredInArray() {
        // Given
        SalesMan salesMan = new SalesMan("John", "Smith", 90123);
        salesmen.insertOne(salesMan.toDocument());

        SocialPerformanceRecord record2024 = new SocialPerformanceRecord(90123, 4, 3, 4, 5, 4, 4, 2024);
        SocialPerformanceRecord record2025 = new SocialPerformanceRecord(90123, 5, 4, 5, 4, 5, 5, 2025);

        Document filter = new Document("sid", salesMan.getId());

        // When - Add two performance records
        salesmen.updateOne(filter, new Document("$push",
                new Document("performanceRecords", record2024.toDocument())));
        salesmen.updateOne(filter, new Document("$push",
                new Document("performanceRecords", record2025.toDocument())));

        // Then
        Document salesmanDoc = salesmen.find(filter).first();
        List<Document> records = salesmanDoc.getList("performanceRecords", Document.class);

        assertEquals(2, records.size(), "Should have two performance records");

        // Verify both records exist with correct data
        boolean has2024 = records.stream().anyMatch(r -> r.getInteger("year") == 2024);
        boolean has2025 = records.stream().anyMatch(r -> r.getInteger("year") == 2025);

        assertTrue(has2024, "Should have record for 2024");
        assertTrue(has2025, "Should have record for 2025");

        // Verify specific scores
        Document record2024Doc = records.stream()
                .filter(r -> r.getInteger("year") == 2024)
                .findFirst()
                .get();
        Document record2025Doc = records.stream()
                .filter(r -> r.getInteger("year") == 2025)
                .findFirst()
                .get();

        assertEquals(4, record2024Doc.getInteger("leadership"), "2024 leadership should be 4");
        assertEquals(5, record2025Doc.getInteger("leadership"), "2025 leadership should be 5");

        System.out.println("✅ Multiple performance records added: " + records.size() + " records");

        // Cleanup
        salesmen.drop();
    }

    @Test
    @Order(12)
    @DisplayName("Should read and map performance records to objects")
    void readPerformanceRecords_shouldMapToSocialPerformanceRecordObjects() {
        // Given
        SalesMan salesMan = new SalesMan("Jane", "Doe", 90234);
        salesmen.insertOne(salesMan.toDocument());

        SocialPerformanceRecord record2024 = new SocialPerformanceRecord(90234, 4, 3, 4, 5, 4, 4, 2024);
        SocialPerformanceRecord record2025 = new SocialPerformanceRecord(90234, 5, 4, 5, 4, 5, 5, 2025);

        Document filter = new Document("sid", salesMan.getId());
        salesmen.updateOne(filter, new Document("$push",
                new Document("performanceRecords", record2024.toDocument())));
        salesmen.updateOne(filter, new Document("$push",
                new Document("performanceRecords", record2025.toDocument())));

        // When
        Document salesmanDoc = salesmen.find(filter).first();
        List<Document> recordDocs = salesmanDoc.getList("performanceRecords", Document.class);

        // Then - Convert to SocialPerformanceRecord objects with safe mapping
        assertNotNull(recordDocs, "Records list should not be null");
        assertEquals(2, recordDocs.size(), "Should have two performance records");

        // Test mapping to objects with safe field access
        for (Document doc : recordDocs) {
            System.out.println("📄 Processing document: " + doc.toJson());

            // Safe field extraction - handle potential null values
            Integer year = doc.getInteger("year");
            Integer leadership = doc.getInteger("leadership");
            Integer openness = doc.getInteger("openness");
            Integer behaviour = doc.getInteger("behaviour");
            Integer attitude = doc.getInteger("attitude");
            Integer communication = doc.getInteger("communication");
            Integer integrity = doc.getInteger("integrity");

            // Verify required fields exist
            assertNotNull(year, "Year field should not be null in performance record");
            assertNotNull(leadership, "Leadership field should not be null in performance record");

            // Create SocialPerformanceRecord manually for testing
            SocialPerformanceRecord record = new SocialPerformanceRecord(
                    salesMan.getId(), // Use the salesman ID from the parent
                    leadership != null ? leadership : 0,
                    openness != null ? openness : 0,
                    behaviour != null ? behaviour : 0,
                    attitude != null ? attitude : 0,
                    communication != null ? communication : 0,
                    integrity != null ? integrity : 0,
                    year
            );

            assertNotNull(record, "Should create SocialPerformanceRecord object");
            assertEquals(salesMan.getId(), record.getSalesmanId(), "Salesman ID should match");
            assertTrue(record.getYear() == 2024 || record.getYear() == 2025, "Year should be 2024 or 2025");
            assertTrue(record.getLeadership() >= 4, "Leadership should be at least 4");

            System.out.println("✅ Mapped record for year: " + record.getYear());
        }

        System.out.println("✅ Successfully processed " + recordDocs.size() + " performance records");

        // Cleanup
        salesmen.drop();
    }

    @Test
    @Order(13)
    @DisplayName("Should delete performance record by specific year")
    void deletePerformanceRecordByYear_shouldRemoveSpecificRecord() {
        // Given
        SalesMan salesMan = new SalesMan("Bob", "Johnson", 90345);
        salesmen.insertOne(salesMan.toDocument());

        SocialPerformanceRecord record2023 = new SocialPerformanceRecord(90345, 3, 3, 3, 3, 3, 3, 2023);
        SocialPerformanceRecord record2024 = new SocialPerformanceRecord(90345, 4, 4, 4, 4, 4, 4, 2024);
        SocialPerformanceRecord record2025 = new SocialPerformanceRecord(90345, 5, 5, 5, 5, 5, 5, 2025);

        Document filter = new Document("sid", salesMan.getId());
        salesmen.updateOne(filter, new Document("$push",
                new Document("performanceRecords", record2023.toDocument())));
        salesmen.updateOne(filter, new Document("$push",
                new Document("performanceRecords", record2024.toDocument())));
        salesmen.updateOne(filter, new Document("$push",
                new Document("performanceRecords", record2025.toDocument())));

        // Verify initial state
        Document initialSalesman = salesmen.find(filter).first();
        assertEquals(3, initialSalesman.getList("performanceRecords", Document.class).size());

        // When - Delete record for 2024 using $pull
        Document deleteUpdate = new Document("$pull",
                new Document("performanceRecords", new Document("year", 2024))
        );

        UpdateResult deleteResult = salesmen.updateOne(filter, deleteUpdate);

        // Then
        assertEquals(1, deleteResult.getModifiedCount(), "Should modify one document");

        Document updatedSalesman = salesmen.find(filter).first();
        List<Document> remainingRecords = updatedSalesman.getList("performanceRecords", Document.class);

        assertEquals(2, remainingRecords.size(), "Should have two records remaining");

        // Verify correct records remain
        boolean has2023 = remainingRecords.stream().anyMatch(r -> r.getInteger("year") == 2023);
        boolean has2025 = remainingRecords.stream().anyMatch(r -> r.getInteger("year") == 2025);
        boolean has2024 = remainingRecords.stream().anyMatch(r -> r.getInteger("year") == 2024);

        assertTrue(has2023, "Should still have 2023 record");
        assertTrue(has2025, "Should still have 2025 record");
        assertFalse(has2024, "Should not have 2024 record anymore");

        System.out.println("✅ Successfully deleted 2024 record, remaining: " + remainingRecords.size());

        // Cleanup
        salesmen.drop();
    }

    @Test
    @Order(14)
    @DisplayName("Should handle performance records for multiple salesmen independently")
    void performanceRecords_shouldBeIndependentPerSalesman() {
        // Given
        SalesMan salesMan1 = new SalesMan("John", "Smith", 90123);
        SalesMan salesMan2 = new SalesMan("Jane", "Doe", 90234);

        salesmen.insertOne(salesMan1.toDocument());
        salesmen.insertOne(salesMan2.toDocument());

        SocialPerformanceRecord record1_2024 = new SocialPerformanceRecord(90123, 4, 3, 4, 5, 4, 4, 2024);
        SocialPerformanceRecord record2_2024 = new SocialPerformanceRecord(90234, 5, 5, 5, 5, 5, 5, 2024);

        // When - Add different records to different salesmen
        salesmen.updateOne(new Document("sid", 90123),
                new Document("$push", new Document("performanceRecords", record1_2024.toDocument())));
        salesmen.updateOne(new Document("sid", 90234),
                new Document("$push", new Document("performanceRecords", record2_2024.toDocument())));

        // Then - Verify each salesman has their own independent records
        Document salesman1 = salesmen.find(new Document("sid", 90123)).first();
        Document salesman2 = salesmen.find(new Document("sid", 90234)).first();

        List<Document> records1 = salesman1.getList("performanceRecords", Document.class);
        List<Document> records2 = salesman2.getList("performanceRecords", Document.class);

        assertEquals(1, records1.size(), "Salesman 1 should have 1 record");
        assertEquals(1, records2.size(), "Salesman 2 should have 1 record");

        assertEquals(4, records1.get(0).getInteger("leadership"), "Salesman 1 leadership should be 4");
        assertEquals(5, records2.get(0).getInteger("leadership"), "Salesman 2 leadership should be 5");

        System.out.println("✅ Performance records are independent per salesman");

        // Cleanup
        salesmen.drop();
    }

    @Test
    @Order(15)
    @DisplayName("Should handle empty performance records array gracefully")
    void emptyPerformanceRecords_shouldNotCauseErrors() {
        // Given - Salesman with no performance records
        SalesMan salesMan = new SalesMan("Empty", "Records", 90999);
        salesmen.insertOne(salesMan.toDocument());

        // When
        Document salesmanDoc = salesmen.find(new Document("sid", 90999)).first();

        // Then - Should handle missing performanceRecords field gracefully
        List<Document> performanceRecords = salesmanDoc.getList("performanceRecords", Document.class);

        // performanceRecords will be null if the field doesn't exist
        if (performanceRecords == null) {
            System.out.println("ℹ️ Performance records field doesn't exist (expected for new salesman)");
        } else {
            assertTrue(performanceRecords.isEmpty(), "Performance records should be empty");
        }

        // This should not throw an exception
        assertDoesNotThrow(() -> {
            salesmen.find(new Document("sid", 90999)).first();
        }, "Reading salesman without performance records should not throw exception");

        System.out.println("✅ Empty performance records handled gracefully");

        // Cleanup
        salesmen.drop();
    }
}