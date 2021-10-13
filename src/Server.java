import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import lib.IO;
import lib.Network;
import lib.SparkDB;
import lib.TextToHashmap;
import lib.log;

public abstract class Server {
	static boolean dynamic = false; // Dynamic mode?
	static int port = 80; // Port Number
	static int MaxConcurrentRequests = 1024; // mcr in settings.conf
	static int CurrentConcurrentRequests = 0; // Current Concurrent Requests
	static boolean gzip = false; // GZip compression?
	static int MAX_REQ_SIZE = 300000; // Max bytes to read
	String defaultResponseMIME = "text/html"; // default content type
	String defaultResponseCode = "HTTP/1.1 200 OK"; // default HTTP code
	/*
	 * Starts the Server. The entry point, basically.
	 */

	void start() {
		translator();
		try {
			ServerSocket SS = new ServerSocket(port);
			log.s("Server started on port " + port);
			while (true) {
				/*
				 * Number of requests processed at the same time MUST be limited!
				 */
				if (CurrentConcurrentRequests <= MaxConcurrentRequests) {
					Socket S = SS.accept();
					Engine e = new Engine(S);
					e.start();
					CurrentConcurrentRequests++;
				}
			}
		} catch (Exception e) {
			log.e("Error Happened, check if the port is available, this traceback may help.\n" + e.getStackTrace());
		}
	}

	/*
	 * Reads settings.conf and assigns things yk.
	 */
	void translator() {
		try {
			SparkDB db = new SparkDB();
			db.readfromfile("settings.conf");
			if (db.get("name", "mode", "value").equals("dynamic"))
				dynamic = true;
			port = Integer.parseInt(db.get("name", "port", "value"));
			MaxConcurrentRequests = Integer.parseInt(db.get("name", "mcr", "value"));
			if (db.get("name", "gzip", "value").equals("1"))
				gzip = true;
			MAX_REQ_SIZE = Integer.parseInt(db.get("name", "mrs", "value"));
		} catch (Exception e) {
			log.e("Cannot read from settings.conf, this traceback may help.\n" + e.getStackTrace());
		}
	}

	/*
	 * Reads the HTTP request, then makes a hashmap to store headers n' stuff.
	 */
	HashMap<String, String> reqTranslator(String req) {
		HashMap<String, String> data = new HashMap<String, String>();
			String[] lines = req.split("\r\n");
		String[] fir_data = lines[0].split(" ");
		data.put("method", fir_data[0]);
		data.put("path", fir_data[1]);
		for (int i = 1; i < lines.length; i++) {
			String[] temp = lines[i].split(": ");
			data.put(temp[0], temp[1]);
		}
		return data;
	}
	/*
	* For /index.html?user=test&pass=test
	* [user=test,pass=test]
	*/
	HashMap<String, String> getArgs(String path) {
		HashMap<String, String> data = new HashMap<String, String>();
		String args = path.substring(path.indexOf("?") + 1); // user=test&pass=test
		data = TextToHashmap.Convert(args, "&", "=");
		return data;
	}
	/*
	 * Main HTTP Engine
	 */
	public class Engine extends Thread {
		DataInputStream DIS;
		DataOutputStream DOS;

		Engine(Socket i) {
			try {
				DIS = new DataInputStream(i.getInputStream());
				DOS = new DataOutputStream(i.getOutputStream());
			} catch (IOException e) {
				log.e("Couldn't able to convert Socket Streams to DIS,DOS");
			}
		}

		/*
		 * Where it all begins!
		 */
		public void run() {
			// Read from Socket
			ArrayList<Byte> req = Network.read(DIS,MAX_REQ_SIZE);
			// From ArrayList to String, through Byte array!
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(baos);
			for (byte element : req) {
				try {
					out.write(element);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			String o = new String(baos.toByteArray());
			/*
			 * If we're on dynamic mode, go and read output from main(..) Else, go static!
			 */
			if (dynamic) {
				Network.write(DOS, main(o), defaultResponseMIME, defaultResponseCode,gzip);
			} else {
				String path = o.split("\r\n")[0].split(" ")[1];
				path = pathfilter(path);
				try {
					String extension1 = Files.probeContentType(Paths.get("./www" + path));
					Network.write(DOS, IO.read("./www" + path), extension1, defaultResponseCode,gzip);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			CurrentConcurrentRequests--;
		}
	}

	/*
	 * When you go dynamic, override this in main.java
	 */
	abstract byte[] main(String req);

	/*
	 * File Path Tweaks
	 */
	public String pathfilter(String path) {
		String res = path;
		res = res.replaceAll("\\.\\.", ""); // LFI protection, i guess?
		res = res.replaceAll("//", "/");
		if (res.endsWith("/"))
			res = res + "index.html";
		return res;
	}
}