package fr.pyvain.websight.websight;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <p>Helper class for writing and reading files</p>
 *
 * <p>
 * @author Vincent LEVALLOIS
 * </p>
 */
class FileIO {
    /**
     * Writes a String into a specified file
     *
     * @param c     Context
     * @param file  Name of the file where the String will be written
     * @param data  String that will be written
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void writeInFile(Context c, String file, String data)
            throws IOException
    {
        FileOutputStream output;
        output = c.openFileOutput(file, Context.MODE_PRIVATE);
        output.write(data.getBytes());
        System.out.println("Write in file ("+ file + ") : " + data);
        output.close();
    }


    /**
     * Reads the content of a file
     *
     * @param c    Context
     * @param file File from which the data should be read
     * @return A string with the content of the file
     * @throws IOException
     */
    public static String readFile (Context c, String file) throws IOException {
        String ret = "";
        InputStream inputStream = c.openFileInput(file);

        System.out.println("Read from " + file);

        if ( inputStream != null ) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            inputStream.close();
            ret = stringBuilder.toString();
        }
        return ret;
    }
}
