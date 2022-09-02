/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * VideoPlayer.java
 *
 * Created on 18.10.2011, 9:44:45
 */

package com.shkip.ipcam;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;
import com.xuggle.xuggler.demos.VideoImage;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author katen'ka
 */
public class VideoPlayer extends JFrame{

    /** Creates new form VideoPlayer */
    public VideoPlayer() {
        initComponents();
        this.setSize(700, 600);
        
    }

    public void connection(File videoFile) {
    

            format = IContainerFormat.make();
            format.setInputFormat("flv");
            // Let's make sure that we can actually convert video pixel formats.
            if (!IVideoResampler.isSupported( IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION))
                throw new RuntimeException("you must install the GPL version" +
                                        " of Xuggler (with IVideoResampler support) for " +
                                        "this demo to work");
            // Create a Xuggler container object
//            container = IContainer.make();
  //          container.setProperty( "probesize", "40");
            knownStreams = new HashMap<Integer, IStreamCoder>();
             
       
    }

    void readVideo(long startTime) {
        InputStream in = null;
       try {
            IPacket packet = IPacket.make();
            in = new FileInputStream(selectFile);
            IContainer container = IContainer.make();
            container.setProperty("probesize", "40");
            if (container.open(in, format, true, false) < 0) {
                throw new IllegalArgumentException("could not open file: " + selectFile.getPath());
            }
            long firstTimestampInStream = Global.NO_PTS;
            knownStreams = new HashMap<Integer, IStreamCoder>();
            while (!bStopPlay && container.readNextPacket(packet) >= 0 && packet.isComplete()) {
                while (bPause) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(VideoPlayer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (knownStreams.get(packet.getStreamIndex()) == null) {
                    container.queryStreamMetaData();
                    IStream stream = container.getStream(packet.getStreamIndex());
                    if (stream.getStreamCoder().open() < 0) {
                        throw new RuntimeException("could not open video decoder for container: " + selectFile.getPath());
                    }
                    knownStreams.put(packet.getStreamIndex(), stream.getStreamCoder());
                }
                IStreamCoder videoCoder = knownStreams.get(packet.getStreamIndex());
                //We allocate a new picture to get the data out of Xuggler
                IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
                int offset = 0;
                while (offset < packet.getSize() && !bStopPlay) {
                    //Now, we decode the video, checking for any errors.
                    int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
                    if (bytesDecoded > 0) {
                        offset += bytesDecoded;
                    } else {
                        System.out.println("Ignore " + (packet.getSize() - offset) + " bytes");
                        offset = packet.getSize();
                        continue;
                    }
                    /*
                     * Some decoders will consume data in a packet, but will not be able to construct
                     * a full video picture yet.  Therefore you should always check if you
                     * got a complete picture from the decoder
                     */
                    if (picture.isComplete()) {
                        IVideoPicture newPic = picture;
                        if (resampler == null && videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
                            resampler = IVideoResampler.make(videoCoder.getWidth(), videoCoder.getHeight(), IPixelFormat.Type.BGR24, videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
                            if (resampler == null) {
                                throw new RuntimeException("could not create color space resampler for: " + selectFile.getPath());
                            }
                        }
                        if (resampler != null) {
                            // we must resample
                            newPic = IVideoPicture.make(resampler.getOutputPixelFormat(), picture.getWidth(), picture.getHeight());
                            if (resampler.resample(newPic, picture) < 0) {
                                throw new RuntimeException("could not resample video from: " + selectFile.getPath());
                            }
                        }
                        if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
                            throw new RuntimeException("could not decode video as BGR 24 bit data in: " + selectFile.getPath());
                        }
                        firstTimestampInStream = picture.getTimeStamp();
                        System.out.println(firstTimestampInStream);
                        if (firstTimestampInStream > startTime) {
                            // Получаем стандартный BufferedImage
                            // And finally, convert the BGR24 to an Java buffered image
                            BufferedImage javaImage = Utils.videoPictureToImage(newPic);
                            // and display it on the Java Swing window
                            System.out.println("paint");
                            Graphics gr = videoPanel.getGraphics();
                            gr.drawImage(javaImage, 0, 0, this);
                           // repaint();
                            startTime += step;
                        }
                    }
                }
            }
            /*
             * Technically since we're exiting anyway, these will be cleaned up by
             * the garbage collector... but because we're nice people and want
             * to be invited places for Christmas, we're going to show how to clean up.
             */
            if (container != null) {
                System.out.println("container close");
                container.close();
                container = null;
            }
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(VideoPlayer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(VideoPlayer.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        videoPanel = new javax.swing.JPanel();
        controlPanel = new javax.swing.JPanel();
        btnStop = new javax.swing.JButton();
        btnPlay = new javax.swing.JButton();
        TimeSlider = new javax.swing.JSlider();
        btnPause = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        btnOpen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        javax.swing.GroupLayout videoPanelLayout = new javax.swing.GroupLayout(videoPanel);
        videoPanel.setLayout(videoPanelLayout);
        videoPanelLayout.setHorizontalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 420, Short.MAX_VALUE)
        );
        videoPanelLayout.setVerticalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 238, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        getContentPane().add(videoPanel, gridBagConstraints);

        controlPanel.setPreferredSize(new java.awt.Dimension(100, 50));

        btnStop.setText("Stop");
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        btnPlay.setText("Play");
        btnPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayActionPerformed(evt);
            }
        });

        TimeSlider.setValue(0);

        btnPause.setText("Pause");
        btnPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout controlPanelLayout = new javax.swing.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnStop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPlay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPause)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TimeSlider, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                .addContainerGap())
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPause)
                    .addComponent(TimeSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(controlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnStop)
                        .addComponent(btnPlay)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        getContentPane().add(controlPanel, gridBagConstraints);

        btnOpen.setText("Open");
        btnOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(btnOpen)
                .addContainerGap(336, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnOpen)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 100.0;
        getContentPane().add(jPanel3, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenActionPerformed
        JFileChooser openDialog = new JFileChooser();
        openDialog.setCurrentDirectory(new File("."));
        // выбрать можно только один файл
        openDialog.setMultiSelectionEnabled(false);
        // принимаются только видеофайлы
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Video file", "mjpeg","*");
        openDialog.setFileFilter(filter);
        int result = openDialog.showOpenDialog(this);
        // openDialog.showialog(this,"open video file");
        if (result == JFileChooser.APPROVE_OPTION) {
            selectFile = openDialog.getSelectedFile();
            System.out.println(selectFile.getPath());
            connection(selectFile);
//            connect(selectFile.getPath());
  //          readStream();
        }
    }//GEN-LAST:event_btnOpenActionPerformed

    private void btnPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayActionPerformed
       // if (in!=null) {
           bStopPlay = false;
            new Thread(new Runnable() {
 public void run() {
           readVideo(100 * 1000 * 1000);}
            }).start();
       // }else{
       //     System.out.println("File not open");
       // }
    }//GEN-LAST:event_btnPlayActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        bStopPlay = true;
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseActionPerformed
        bPause=!bPause;
    }//GEN-LAST:event_btnPauseActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VideoPlayer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider TimeSlider;
    private javax.swing.JButton btnOpen;
    private javax.swing.JButton btnPause;
    private javax.swing.JButton btnPlay;
    private javax.swing.JButton btnStop;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel videoPanel;
    // End of variables declaration//GEN-END:variables
    File selectFile = null;
   // InputStream in = null;
    IContainerFormat format = null;
    IContainer container2 = null;
    IVideoResampler resampler = null;
    Map<Integer, IStreamCoder> knownStreams = null;
    long step = 0;
    Boolean bStopPlay = false;
    Boolean bPause = false;
}
