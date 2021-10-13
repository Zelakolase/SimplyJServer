package lib;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class SparkDB {
    public static HashMap<String, ArrayList<String>> Mapper = new HashMap<String, ArrayList<String>>();
    public static ArrayList<String> Headers = new ArrayList<String>();
    public static int num_queries = 0;
    public static int num_header = 0;

    public void readfromfile(String filename) {
        zero();
        boolean headerisprocessed = false;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            String[] header = null;
            String temp_1 = "";
            String temp_0 = "";
            while ((line = br.readLine()) != null) {
                if (!headerisprocessed) {
                    header = line.split("\",\""); // ","
                    temp_1 = header[header.length - 1].substring(0, header[header.length - 1].length() - 1);
                    temp_0 = header[0].substring(1);
                    Mapper.put(temp_0, new ArrayList<String>());
                    Headers.add(temp_0);
                    for (int i = 1; i < header.length - 1; i++) {
                        Mapper.put(header[i], new ArrayList<String>());
                        Headers.add(header[i]);
                    }
                    Mapper.put(temp_1, new ArrayList<String>());
                    Headers.add(temp_1);
                    num_header = header.length;
                    headerisprocessed = true;
                } else {
                    num_queries++;
                    String[] single_col = line.split("\",\""); // ","
                    Mapper.get(temp_0).add(single_col[0].substring(1));
                    for (int x = 1; x < num_header - 1; x++) {
                        Mapper.get(header[x]).add(single_col[x]);
                    }
                    Mapper.get(temp_1)
                            .add(single_col[num_header - 1].substring(0, single_col[num_header - 1].length() - 1));

                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readfromString(String data) {
        zero();
        InputStream stream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        boolean headerisprocessed = false;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            String[] header = null;
            String temp_1 = "";
            String temp_0 = "";
            while ((line = br.readLine()) != null) {
                if (!headerisprocessed) {
                    header = line.split("\",\""); // ","
                    temp_1 = header[header.length - 1].substring(0, header[header.length - 1].length() - 1);
                    temp_0 = header[0].substring(1);
                    Mapper.put(temp_0, new ArrayList<String>());
                    Headers.add(temp_0);
                    for (int i = 1; i < header.length - 1; i++) {
                        Mapper.put(header[i], new ArrayList<String>());
                        Headers.add(header[i]);
                    }
                    Mapper.put(temp_1, new ArrayList<String>());
                    Headers.add(temp_1);
                    num_header = header.length;
                    headerisprocessed = true;
                } else {
                    num_queries++;
                    String[] single_col = line.split("\",\""); // ","
                    Mapper.get(temp_0).add(single_col[0].substring(1));
                    for (int x = 1; x < num_header - 1; x++) {
                        Mapper.get(header[x]).add(single_col[x]);
                    }
                    Mapper.get(temp_1)
                            .add(single_col[num_header - 1].substring(0, single_col[num_header - 1].length() - 1));
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String get(String FromCol, String ColVal, String ColToFind) {
        return Mapper.get(ColToFind).get(Mapper.get(FromCol).indexOf(ColVal));

    }

    void zero() {
        num_queries = 0;
        num_header = 0;
        Mapper = new HashMap<String, ArrayList<String>>();
        Headers = new ArrayList<String>();
    }
}