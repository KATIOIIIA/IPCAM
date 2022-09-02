/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.shkip.ipcam;

import com.xuggle.xuggler.IVideoResampler;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.swing.JOptionPane;

/**
 *
 * @author Rybakova Ekaterina Olegovna
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException, IOException {
            if (!IVideoResampler.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
                     JOptionPane.showInputDialog("you must install the GPL version" +
      		" of Xuggler (with IVideoResampler support) for " +
      		"this demo to work");
                   //  CopyXuggleFiles.copyXuggleDir("data//Xuggle//", "c://program files//XU//bin");
            }

        CamViewer cam = new CamViewer();
        cam.setVisible(true);
   /*     JFrame jframe = new JFrame();
jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
CamApplet axPanel = new CamApplet(jframe);
new Thread(axPanel).start();
jframe.getContentPane().add(axPanel);
jframe.pack();
jframe.show();
axPanel.start();

    *
    */

      /* CamView cam = new CamView();
        cam.setVisible(true);
        cam.run();
       *
    
        
         String mjpgURL ="http://68.15.12.110:8002/cgi-bin/video.jpg?" ;
         String file = "1234.mjpeg";
         URL u = new URL(mjpgURL);
         HttpURLConnection huc = (HttpURLConnection) u.openConnection();
         JFrame frame = new JFrame();

         CameraIP cam = new CameraIP("mycam",file,"demo","demo",CameraIP.Format.H264,352,240);
         CamPanel aCamPanel = new CamPanel(cam);
         aCamPanel.setVisible(true);
         frame.setSize(cam.getDimension());
         frame.add(aCamPanel,BorderLayout.CENTER);
         frame.setVisible(true);
         aCamPanel.connect();
         new Thread(aCamPanel).start();   */
         
    }

}
