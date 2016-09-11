/**
 * This code was written by
 * David Cheatham
 * dcheath@projectportfolio.info
 * http://dcheath.projectportfolio.info/
 * 
 * Any person is free to use this code under the terms of Creative Commons
 * Attribution-ShareAlike (CC BY-SA):
 * http://creativecommons.org/licenses/by-sa/3.0/
 */
package info.projectportfolio.dcheath.testapps.thinkgeardriver;

import java.io.*;

/**
 * This class functions exclusively as the entry point for the ThinkGear Test
 * Application.
 * @author David Cheatham <dcheath@projectportfolio.info>
 * @version 0.1
 */
public class Main {

    /**
     * The location in the JAR for the ThinkGear native library. This will be
     * extracted to the working directory if a thinkgear.dll doesn't already
     * exist in the working directory.
     */
    private static String libPath = "com/neurosky/thinkgear/thinkgear.dll";

    /**
     * The entry point of the program begins by ensuring that the library
     * exists in the working directory before handing control over to the GUI
     * class.
     * @param args the command line arguments are ignored by this program
     */
    public static void main(String[] args) {
        checkLibrary();
        new MainWindow();
    }

    /**
     * This library checks whether thinkgear.dll exists in the working directory
     * and, if it does not, creates the file and calls flow to extract the file
     * from the JAR.
     */
    private static void checkLibrary() {
        File lib = new File("thinkgear.dll");
        if (!lib.exists()) {
            try {
                lib.createNewFile();
                flow(ClassLoader.getSystemResourceAsStream(libPath),
                        new FileOutputStream(lib), new byte[512]);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    /**
     * Reads bytes into {@code buf} from {@code is} writing the results to
     * {@code os} until no further bytes are read, then closes the streams when
     * done.
     * @param is the InputStream from which to read bytes
     * @param os the OutputStream to which bytes should be written
     * @param buf the byte array which will serve as the buffer
     * @throws IOException if an error occurs accessing either stream
     */
    private static void flow(InputStream is, OutputStream os, byte[] buf)
            throws IOException {
        int numRead;
        while ((numRead = is.read(buf)) >= 0) {
            os.write(buf, 0, numRead);
        }
        is.close();
        os.close();
    }
}
