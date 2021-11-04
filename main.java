
public class main extends Server {
	public static void main(String[] args) {
		main m = new main();
		m.start();
	}
	@Override
	byte[] main(String req,byte[] body) {
		byte[] response = "".getBytes();
		// addedResHeaders = "CUSTOMHEADER\r\nANOTHERCUSTOMHEADER\r\n"; 
		// ENTER SOME CODE HERE IF YOU WANT TO GO DYNAMIC!
		return response;
	}
}