package net.beaconpe.blockclient.utility;

import java.io.*;

/**
 * Utility class for BlockClient.
 */
public class Utils {

    public static void copyResource(String source, String dest) throws IOException {
        File destFile = new File(dest);
        if(!destFile.exists()){
            destFile.createNewFile();
        }

        InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(source);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile)));

        String line = "";
        while((line = reader.readLine()) != null){
            writer.write(line + "\n");
            writer.flush();
        }
        reader.close();
        writer.close();
    }
}
