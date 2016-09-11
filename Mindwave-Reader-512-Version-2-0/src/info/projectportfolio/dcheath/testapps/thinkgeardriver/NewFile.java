/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.projectportfolio.dcheath.testapps.thinkgeardriver;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import javax.swing.JFileChooser;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class NewFile {
        public void simpleMessage() {
        //System.out.println("this is NewFile class");      // this won't work unless simpleMessage is there
        String MyDocFolder=new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
                               // gets path to MyDocuments for different computers
        String MyPath = MyDocFolder + File.separator +"Mindwave";
        new File(MyPath).mkdir(); // creates a directory

        File mySaveFile=new File(MyPath + File.separator + "Mindwave512temp.csv");  // creeates variable File("c:/MyDocuments/Mindwave-Save/Mindwave.csv")

        OutputStream stream = null;

               try {

                stream = new FileOutputStream(mySaveFile);
                
                //BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(mySaveFile)); // True so that info does not overwrite
                //Start writing to the output stream 
        
                //String myLine = "Time, Raw, Attention, Meditation,";
                             
                //bufferedWriter.append(myLine);
                //bufferedWriter.newLine();
                //bufferedWriter.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }finally{
                try {
                stream.close();
                } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }
        }









