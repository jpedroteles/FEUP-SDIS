package communication;

public class Server {

	public static void main(String args[]) {

		while(true) {
			TCP_Server tcp_server = new TCP_Server();
			UDP_Server udp_server = new UDP_Server();
			System.out.println("..."); 
			try
			{
				if(tcp_server.thread1.isAlive()) {
					while(tcp_server.thread1.isAlive())
					{
						System.out.println("Thread1"); 
						Thread.sleep(1500);
					}
					System.out.println("TCP"); 
				}
				else if(udp_server.thread2.isAlive()){
					while(udp_server.thread2.isAlive())
					{
						System.out.println(".."); 
						Thread.sleep(1500);
					}
					System.out.println("UDP"); 
				}
			}
			catch(InterruptedException e)
			{
				System.out.println("Error in message");
			}
		}
	}
}
