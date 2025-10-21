package org.hbrs.ia.code;

import org.hbrs.ia.model.SalesMan;
import org.hbrs.ia.model.SocialPerformanceRecord;

import java.util.List;

/**
 * Code lines are commented for suppressing compile errors.
 * Are there any CRUD-operations missing?
 */
public interface ManagePersonal {

    /** Create **/
    public void createSalesMan( SalesMan record );
    /** Read **/
    public SalesMan readSalesMan( int sid );
    public List<SalesMan> readAllSalesMen();
    public List<SocialPerformanceRecord> readSocialPerformanceRecord( SalesMan salesMan );
    public SocialPerformanceRecord readLastSocialPerformanceRecord(SalesMan salesMan);
    // Remark: How do you integrate the year?
    public SocialPerformanceRecord readByYearSocialPerformanceRecord(SalesMan salesMan, int year);
    /** Update **/
    public void updateSalesMan(SalesMan record);
    public void addSocialPerformanceRecord(SocialPerformanceRecord record , SalesMan salesMan );
    // Remark: an SocialPerformanceRecord corresponds to part B of a bonus sheet
    /** Delete **/
    public void deleteSalesMan(int sid);
    public void deleteAllSalesMan();


}
