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

/**
 * Note, this is written in Java 7  32-bit
 * If you try to run it in a Java 64-bit version, it won't work
 * (but it is OK if your computer is 64-bit
 */

/**
 * And modified to save the results to a text file
 * by katie356 - with advice from David Cheatham
 * a project of Brainwaves.io
 * http://brainwaves.io/wp/
 * Please go there if you have any comments or suggestions
 * 
 * Any person is free to use this code under the terms of Creative Commons
 * Attribution-ShareAlike (CC BY-SA):
 * http://creativecommons.org/licenses/by-sa/3.0/
 */

package info.projectportfolio.dcheath.testapps.thinkgeardriver;

import javax.swing.*;
import javax.swing.BoxLayout;        // added this of the boxlayout
import javax.swing.BorderFactory;    // added this for the borders
import java.awt.Dimension;           // added this to change button size
import java.awt.Font;                // added this to change font
import javax.swing.JFrame;           
import java.awt.*;
import java.awt.event.*;
import info.projectportfolio.dcheath.drivers.thinkgear.*;
import info.projectportfolio.dcheath.drivers.thinkgear.ThinkGearDevice.DataType;

// All Added
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;   // for time
import java.util.Date;               // for time
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Displays a GUI and launches a thread which passes an array of values every
 * time any one value is updating. Since this data is for pure debugging
 * purposes, no effort has been made to use only updated values, values are then
 * displayed in the order they appear in the enumeration.
 * @author David Cheatham <dcheath@projectportfolio.info>
 * @version 0.1
 * @todo Add visual display elements.
 */
public class MainWindow extends JFrame implements ActionListener {

    /**
     * This object manages the connection used by the application and is
     * referenced by the dataThread.
     */
    private ThinkGearDevice headset = new ThinkGearDevice();
    /**
     * The Thread which is responsible for waiting for new data from the headset
     * and calling the update method on the GUI.
     * 
     */
    private Thread dataThread = null;
    /**
     * The task which was assigned to the dataThread at it's creation.
     */
    private DataCollector dataTask = null;
    //GUI compenents
    private JTextArea debugArea;
    private JPanel buttonPanel;
    private JLabel comPortLabel;
    private JSpinner comPortSpinner;
    private JButton conButton, disButton;
    private JButton saveButton, stopButton;
    private JButton helpButton;
    private JLabel connectLabel;   // added
    private JLabel saveLabel;      // added
    //private File mySaveFile;       // added
    private String strStart = "";
    private String MyDocFolder=new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
                               // gets path to MyDocuments for different computers
    private String MyPath = MyDocFolder + File.separator +"Mindwave";
    private File mySaveFile = new File(MyPath + File.separator + "Mindwave512temp.csv");  // creeates variable File("c:/MyDocumen
    
    private BufferedWriter bufferedWriter;
            
    /**
     * The minimum and starting size for the JFrame.
     */
    private Dimension frameTargetSize = new Dimension(400, 470); // (width, height)

    /**
     * The sole constructor accepts no parameters sets up the GUI and
     * initializes the ThinkGearDevice, but does not connect to it.
     */
    public MainWindow() {

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                MainWindow.this.disconnect();
                headset.dispose();
                MainWindow.this.dispose();
                System.exit(0);
            }
        });
        // Added
        
        String MyPath = MyDocFolder + File.separator + "Mindwave";
        new File(MyPath).mkdir(); // creates a directory
        
        // Added
        bufferedWriter = null;
        try{ 
        //Construct the BufferedWriter object
        bufferedWriter = new BufferedWriter(new FileWriter(mySaveFile, true)); // True so that info does not overwrite           
        //Start writing to the output stream
        //bufferedWriter.append("Test");
        //bufferedWriter.newLine();

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //       

        debugArea = new JTextArea();
        debugArea.setPreferredSize(new Dimension(400, 400));
        debugArea.setMargin(new Insets(10, 10, 10, 10));
        debugArea.setOpaque(false);
        debugArea.setEditable(false);
        debugArea.setEnabled(false);
        debugArea.setDisabledTextColor(Color.black);
        getContentPane().add("West", debugArea);

        buttonPanel = new JPanel();                                 // creating panel for top buttons
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());  // adding border to button panel
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        //buttonPanel.setLayout(new GridLayout(3,3));
        getContentPane().add("North", buttonPanel);
        getContentPane().setBackground(Color.WHITE);   // changed color of the getContentPane()
        buttonPanel.add(Box.createRigidArea(new Dimension(8, 8))); // adding a spacer before Label   
        JLabel emptyLabel9 = new JLabel ("|");
        buttonPanel.add(emptyLabel9);
        helpButton = new JButton("Mindwave Reader 512 - Help and About");
        helpButton.addActionListener(this);
        buttonPanel.add(helpButton);
        JLabel emptyLabel7 = new JLabel ("|");
        buttonPanel.add(emptyLabel7);
        comPortLabel = new JLabel("COM Port Number: ");
        buttonPanel.add(comPortLabel);
        comPortSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 128, 1));
        comPortSpinner.setMaximumSize(new Dimension(70,70));        
        buttonPanel.add(comPortSpinner);
        conButton = new JButton("Connect");
        conButton.addActionListener(this);
        JLabel emptyLabel8 = new JLabel ("|");
        buttonPanel.add(emptyLabel8);
        buttonPanel.add(conButton);
        disButton = new JButton("Disconnect");
        disButton.addActionListener(this);
        disButton.setEnabled(false);
        buttonPanel.add(disButton);
        JLabel emptyLabel5 = new JLabel ("|");
        buttonPanel.add(emptyLabel5);
        connectLabel = new JLabel("");
        connectLabel.setFont(new Font("Arial New", Font.BOLD, 14));
        connectLabel.setForeground(Color.red);
        buttonPanel.add(connectLabel);
        JLabel emptyLabel6 = new JLabel ("|");
        buttonPanel.add(emptyLabel6);
        
        
        
// Added        
        JPanel rawPanel = new JPanel();                        // creating panel for saving raw to file
        rawPanel.setLayout(new BoxLayout(rawPanel, BoxLayout.Y_AXIS));
        //rawPanel.setLayout(new GridLayout(8, 1));
        rawPanel.setBackground(Color.WHITE);                    // changing color of rawPanel
        rawPanel.add(Box.createRigidArea(new Dimension(8, 8))); // adding a spacer before timeLabel
        JLabel timeLabel = new JLabel("Time");                 // creating time Label              
        JLabel emptyLabel1 = new JLabel("|");
        saveButton = new JButton("        Save to File       ");      // creating Save Button
        saveButton.addActionListener(this);
        saveButton.setEnabled(false);
        JLabel emptyLabel2 = new JLabel("|");        
        saveLabel = new JLabel("| MyDocuments\\mindwave\\mindwave512.csv");
               saveLabel.setFont(new Font("Arial New", Font.PLAIN, 14));
               saveLabel.setForeground(Color.red);               
        JLabel emptyLabel3 = new JLabel("|");
        stopButton = new JButton("STOP saving to File"); 
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);
        rawPanel.add(timeLabel);                               // adding time label to panel
        rawPanel.add(emptyLabel1);
        rawPanel.add(saveButton);                              // adding save button to panel
        saveButton.setPreferredSize(new Dimension(100,50));    // this doesn't seem to work
        rawPanel.add(emptyLabel2);
        rawPanel.add(saveLabel); 
        rawPanel.add(emptyLabel3);
        rawPanel.add(stopButton);         
        getContentPane().add("South", rawPanel);               // add rawPanel to the getContentPane
        
//        
        setTitle("Mindwave Reader 512 - Version 2.0");

        displayValues(new int[DataType.values().length]);

        setSize(frameTargetSize);
        setMinimumSize(frameTargetSize);
        setPreferredSize(frameTargetSize);
        this.setLocation(400, 50);
        setVisible(true);
    }

    /**
     * Responds to button click events by calling the appropriate methods.
     * @param evt an object describing the event that occurred
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        
        if (evt.getSource() == helpButton){
            String strHelp = "The Mindwave Reader 512 - Version 2.0\n "
                    + "is based on Java software written by David Cheatham \n"
                    + "http://dcheath.projectportfolio.info/85 \n"
                    + "and modified to save to file by katie356, for more info see: \n"
                    + "http://brainwaves.io/wp/reader512/ \n";
                    
            Help.infoBox(strHelp, "Help and About");
        }
        
        if (evt.getSource() == conButton) {
            connect();
        }

        if (evt.getSource() == disButton) {
            disconnect();
        }
        
        if (evt.getSource() == saveButton){
            saveLabel.setText("             Saving to File ");
            saveButton.setEnabled(false);
            stopButton.setEnabled(true);
            NewFile NewFileObject = new NewFile();  // calls Class
            NewFileObject.simpleMessage();         // calls the method in 
            strStart = "Start";            
        }
        
        if (evt.getSource() == stopButton){
            saveLabel.setText("|");   
            saveButton.setEnabled(true);
            stopButton.setEnabled(false); 
        try{
            
       // Delete Last Line
        try {
            String strFileName = MyPath + File.separator + "Mindwave512temp.csv";
            
            RandomAccessFile raf;
            raf = new RandomAccessFile(strFileName, "rw");
            long length = raf.length() - 1;
            byte b = 0;
            do {
                try {
                    length -= 1;
                    raf.seek(length);
                    b = raf.readByte();
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            } while(b != 10);
            raf.setLength(length+1);
            raf.close();
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
      // Delete First Line
        
                try {
            String strFileName = MyPath + File.separator + "Mindwave512temp.csv";
           
            RandomAccessFile raf = new RandomAccessFile(strFileName, "rw");  
         //Initial write position                                             
    long writePosition = raf.getFilePointer();                            
    raf.readLine();                                                       
    // Shift the next lines upwards.                                      
    long readPosition = raf.getFilePointer();                             

    byte[] buff = new byte[1024];                                         
    int n;                                                                
    while (-1 != (n = raf.read(buff))) {                                  
        raf.seek(writePosition);                                          
        raf.write(buff, 0, n);                                            
        readPosition += n;                                                
        writePosition += n;                                               
        raf.seek(readPosition);                                           
    }                                                                     
    raf.setLength(writePosition);                                         
    raf.close();                                       

            
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         
        //Appending
        //http://codereview.stackexchange.com/questions/77583/copying-contents-of-two-files-into-one-file-bufferedreader-close-calls
     
        String inputfile2 = MyPath + File.separator + "Mindwave512temp.csv";        
        String output = MyPath + File.separator + "Mindwave512.csv";

    BufferedWriter bufferedWriter=null;
    BufferedReader bufferedReader2 = null;
    try{
        bufferedWriter = new BufferedWriter(new FileWriter(output));
        bufferedReader2 = new BufferedReader(new FileReader(inputfile2));
        String s1 =null;

        bufferedWriter.write("Time, Raw, Attention, Meditation, \n");
            
        while((s1 = bufferedReader2.readLine()) != null){    
            bufferedWriter.write(s1);            
            bufferedWriter.write("\n"); 
        } 
    }
    catch (IOException e){
            int FLAG = 1; 
    }finally{
            try {
                if(bufferedReader2 != null)bufferedReader2.close();
                if(bufferedWriter != null)bufferedWriter.close();  
                
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }    
        }      
    
        Runtime.getRuntime().exec("notepad " + output);
        }
        catch(IOException ex){
        ex.printStackTrace();
        }
        } 
      strStart = "Stop";
    }

    /**
     * If a dataTask is not already assigned, this method will disable the COM
     * port spinner and connect button, connect to the selected COM port and set
     * up the necessary dataTask and Thread before enabling the disconnect
     * button. If the connection fails, the GUI changes are reverted.
     */
    private void connect() {
        if (dataTask == null) {
            conButton.setEnabled(false);
            comPortSpinner.setEnabled(false);
            connectLabel.setText("Connecting... can take a minute or two.");
            if (headset.connect(
                    ((Integer) comPortSpinner.getValue()).byteValue())) {
                dataTask = new DataCollector();
                dataThread = new Thread(dataTask);
                dataThread.start();                
                disButton.setEnabled(true);
            } else {
                conButton.setEnabled(true);
                comPortSpinner.setEnabled(true);
            }
        }
    }

    /**
     * If a data task exists, this method disconnects the ThinkGearDevice and
     * then clears the references to the thread. The thread will be stopped
     * automatically by the disconnection of the headset. This method also
     * disables the disconnect button and enables the connect button and COM
     * port spinner.
     */
    private void disconnect() {
        if (dataTask != null) {
            disButton.setEnabled(false);
            headset.disconnect();
            dataTask = null;
            dataThread = null;
            conButton.setEnabled(true);
            comPortSpinner.setEnabled(true);
            connectLabel.setText("");
        }
    }
    //
 
    /**
     * This methods expects an array whose length matches the enumeration of
     * {@link DataType} and whose data matches the order of that enumeration.
     * The array is used to construct a string which is then displayed in the
     * debugArea.
     * @param values the values collected from the headset, in the order of the
     *                  DataType enumeration
     */
    private void displayValues(int[] values) {
        
        StringBuilder output = new StringBuilder("");         
    
        //int i = 0;
        //for (DataType type : DataType.values()) {
            //output.append(type.toString());
            //output.append("=");
            output.append(values[4]);
            //output.append("\n");
            output.append(",");               
            output.append(values[2]);            
            output.append(",");              
            output.append(values[3]);
            output.append(","); 
            // example: DataType.Raw   for individual type results          
        //}
        
        //output.append(DataType.values());
        
        
        debugArea.setText(output.toString());
        String strOutput = output.toString();
        //System.out.println(strOutput.length());
        if (strOutput.length() > 6){
            connectLabel.setText("You are connected!");
            saveButton.setEnabled(true);
        }
 
    
        Date myTime = new Date();
        String myTimeMicro = new SimpleDateFormat("HH:mm:ss.SSSSSS").format(new Date());
        //Date myTime = new Date();
        // System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").format(d));
        String myLine = myTimeMicro + "," + output.toString();  // don't need \n because of bufferedWriter.newLine();

        try{ 
        //Start writing to the output stream
        bufferedWriter.append(myLine);//        
        bufferedWriter.newLine();
        //bufferedWriter.append(myLine);

   } catch (FileNotFoundException ex) {
        ex.printStackTrace();
    } catch (IOException ex) {
        ex.printStackTrace();
    } 
    }


    /**
     * The data collection task waits for a packet to be read and passes all
     * data to the {@link #displayValues} method. This task will end when the
     * associated {@link ThinkGearDevice#disconnect() } is called.
     */
    private class DataCollector implements Runnable {

        /**
         * The run method loops as long as
         * {@link ThinkGearDevice#waitForPacket() } returns true, pulling data
         * and passing it to {@link #displayValues}.
         */
        @Override
        public void run() {                                                   // VERY IMPORTANT
            while (headset.waitForPacket()) {
                int[] data = new int[DataType.values().length];
                int i = 0;
                for (DataType type : DataType.values()) {
                    data[i++] = (int) headset.getValue(type);                   
                }
                displayValues(data);    
        }
        }
    }
}

    