package communication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class UDP_Server implements Runnable {

	static Thread thread2;

	UDP_Server() {
		thread2 = new Thread(this, "Thread2 created");
		thread2.start();
	}

	public void run() {
		
		while(true) {
			try {
				//System.out.println("Waiting to receive message");
				Thread.sleep(1000);
			}
			catch(InterruptedException e) {
				
				break;
			}
			//System.out.println("Run is over" );
		}		
	}

}
