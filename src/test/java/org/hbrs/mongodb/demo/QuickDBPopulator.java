package org.hbrs.mongodb.demo;

import org.hbrs.ia.code.ManagePersonal;
import org.hbrs.ia.code.ManagePersonalImpl;
import org.hbrs.ia.model.SalesMan;
import org.hbrs.ia.model.SocialPerformanceRecord;

public class QuickDBPopulator {

    public static void main(String[] args) {
        ManagePersonalImpl manager = new ManagePersonalImpl();

        // Clear any old data
        manager.deleteAllSalesMan();

        // Create salesman
        SalesMan john = new SalesMan("John", "Smith", 1001);
        manager.createSalesMan(john);
        System.out.println("✅ Created salesman");

        // Add performance record
        SocialPerformanceRecord record = new SocialPerformanceRecord(1001, 4, 3, 4, 5, 4, 4, 2024);
        manager.addSocialPerformanceRecord(record, john);
        System.out.println("✅ Added performance record");

        // Debug to see what happened
        manager.debugCollections();

        //manager.close();
    }
}
