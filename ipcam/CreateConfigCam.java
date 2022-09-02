/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.shkip.ipcam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rybakova Ekaterina Olegovna
 */
public class CreateConfigCam {
    /**
     * Конструктор
     * @param filePath txt file  
     */
    public CreateConfigCam(String filePath) {
        configLoad = (new String("LibraryConfigFile13.2.1Load")).toCharArray();
        configLoadSignature = new char[256];
        outputStream = new ByteArrayOutputStream();
        initConfigLoadSignature();
        loadFile(filePath);
    }
    /**
     * Загружаем txt file 
     * @param filePath txt file
     */
    private void loadFile(String filePath) {
        try {
            //InputStream in = new FileInputStream(filePath);
            //OutputStream out = new FileOutputStream("doc/books.xml");
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            BufferedWriter out = null;
           if (genFile) out = new BufferedWriter(new FileWriter(filePath+"new"));

                    int c;
            while ((c = in.read())!= -1) {
                if (!genFile) outputStream.write((char) (c^findConfig()));
                else out.append((char)(c^findConfig()));
            }
            if (outputStream!=null) outputStream.flush();
            if (out!=null) out.flush();
        } catch (IOException ex) {
            Logger.getLogger(CreateConfigCam.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private void initConfigLoadSignature() {
	int i=0,j=0;
	char tmp;
	for (i=0;i<256;i++) configLoadSignature[i]=(char)i;
	for (i=0;i<256;i++)
	{
		j=(j+configLoadSignature[i]+configLoad[i%configLoad.length])%256;
		tmp=configLoadSignature[i];
		configLoadSignature[i]=configLoadSignature[j];
		configLoadSignature[j]=tmp;
	}
    }
    
    private char findConfig() {
            char tmp;
            i = (i+1)&256;
            j = (j+configLoadSignature[i])&256;
            tmp=configLoadSignature[i];
            configLoadSignature[i]=configLoadSignature[j];
            configLoadSignature[j]=tmp;
            return configLoadSignature[(configLoadSignature[i]+configLoadSignature[j])&255];
    }
    
    public OutputStream getOutputStream() {
        return outputStream;
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
             genFile = true;
              CreateConfigCam cam = new CreateConfigCam("config/AXIS-211A.cam");
            }
        });
    }
    private char[] configLoad;
    private char[] configLoadSignature;
    private int i,j;
    private OutputStream outputStream = null;
    private static Boolean genFile = false;
}
