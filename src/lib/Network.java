package lib;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

public class Network {
    /*
     * GZIP Compression shit
     */
    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data);
        gzip.close();
        byte[] compressed = bos.toByteArray();
        bos.close();
        return compressed;
    }

    /*
     * HTTP Write Response Function
     */
    public static void write(DataOutputStream s, byte[] res, String content, String S,boolean gzip) {
        try {
            if (gzip)
                res = compress(res);
            s.write((S + "\r\n").getBytes());
            s.write("Server: SimplyJServer 1.0\r\n".getBytes());
            s.write(("Connection: close\r\n").getBytes());
            if (gzip)
                s.write("Content-Encoding: gzip\r\n".getBytes());
            if (content.equals("text/html")) {
                s.write(("Content-Type: " + content + ";charset=UTF-8\r\n").getBytes());
            } else {
                s.write(("Content-Type: " + content + "\r\n").getBytes());
            }
            s.write(("Content-Length: " + res.length + "\r\n\r\n").getBytes());
            s.write(res);
            s.flush();
            s.close();
        } catch (Exception e) {

        }
    }

    /*
     * Reads from socket into ArrayList
     */
    public static ArrayList<Byte> read(DataInputStream s,int MAX_REQ_SIZE) {
        ArrayList<Byte> result = new ArrayList<Byte>();
        try {
            do {
                result.add(s.readByte());
            } while (s.available() <= MAX_REQ_SIZE*1000 && s.available() > 0);
        } catch (IOException e) {

        }
        return result;
    }
}
