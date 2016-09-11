
package info.projectportfolio.dcheath.testapps.thinkgeardriver;

// http://stackoverflow.com/questions/7080205/popup-message-boxes

import javax.swing.JOptionPane;

public class Help{

public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
}