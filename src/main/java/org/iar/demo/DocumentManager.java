package org.iar.demo;
import org.iar.demo.internal.Document;

/**
 * Example for a back door in code (chapter 2, section 6):
 * Is there a back door given in this code?
 * 
 * @author saschaalda
 * 
 */

public class DocumentManager {
	
	public void checkAndSaveDocument( Document data ) {
		if ( checkDocument ( data ) == true ) {
			this.saveDocument(data);
		}
	}
	
	public void saveDocument( Document data ) {
		// Save to DB-Disk without check (should be ensured before!)
	}
	
	private boolean checkDocument( Document data ) {
		
		// Checks the consistency of the document
		// if good --> return true
		// if bad --> return false
		
		return false; // only to ensure correct compilation
	}

}
