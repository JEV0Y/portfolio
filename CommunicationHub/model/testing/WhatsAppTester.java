package model.testing;

import model.CommunicationHub;

public class WhatsAppTester {
	
	public static void main(String[] args) {
		/* demonstrating that two Users with the same 
		 * 	1. phone number will not be created
		 *  2. name can be created 
		 *  */
		
		CommunicationHub wsa = new CommunicationHub();

		wsa.register("Phil", "Jackson", "879-111-0000");
		wsa.register("Jenn", "Phipps", "879-111-0000");
		wsa.register("Amari", "Apple", "876-131-0010");
		wsa.register("Amari", "Apple", "876-132-0010");
		
		System.out.println(wsa.getFullInformation());
	}
}


