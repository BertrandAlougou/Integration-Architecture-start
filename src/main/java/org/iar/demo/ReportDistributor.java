package org.iar.demo;

import java.util.List;

/**
 * Example for a chatty interface (chapter 2, section 6):
 * Is this interface chatty? If yes, how would you decompose it?
 * 
 * @author saschaalda
 * (Interface is maintained in Git by further persons: Steve Maier, Julia Kappes, Angie Mills)
 *
 */

public interface ReportDistributor {

	public List getWheatherReports(int x, int y, String a, double bb);

	public List getStockReports(int data, double c, double n, int nn);
	
	public List getHealthReports(String data, String dd, String ff, String abc);
	
	public List getClimateStatistics(int data, int x);
	
    void addWheatherReports(List report);
	
	void addStockReport(List report);
	
	void addHealthReport(List report);

	void addClimateStatistics(List listofStatistics);

}
