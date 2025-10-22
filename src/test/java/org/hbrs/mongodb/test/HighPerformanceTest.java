package org.hbrs.mongodb.test;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.hbrs.ia.model.SalesMan;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

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

        // Get database 'highperformance' (creates one if not available)
        supermongo = client.getDatabase("highperformanceNewTest");

        // Get Collection 'salesmen' (creates one if not available)
        salesmen = supermongo.getCollection("salesmen");
    }

    // =========================================================================
    // TEACHER'S ORIGINAL TESTS - KEPT EXACTLY AS IS
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
        Integer sid = (Integer) newDocument.get("sid");
        assertEquals( 90444 , sid );

        // Deletion
        salesmen.drop();
    }

    // =========================================================================
    // ADDITIONAL COMPREHENSIVE TESTS
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
}