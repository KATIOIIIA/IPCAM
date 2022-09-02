/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.shkip.ipcam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author katen'ka
 */
public class CopyXuggleFiles {
    /**
     * Копирует файлы
     * @param in имя копируемого файла
     * @param out имя директории куда копируем
     * @throws Exception
     */
    private static void copyFile(File in, File out) throws Exception {
    FileInputStream fis  = new FileInputStream(in);
    FileOutputStream fos = new FileOutputStream(out);
    try {
        byte[] buf = new byte[1024];
        int i = 0;
        while ((i = fis.read(buf)) != -1) {
            fos.write(buf, 0, i);
        }
    }
    catch (Exception e) {
        throw e;
    }
    finally {
        if (fis != null) fis.close();
        if (fos != null) fos.close();
    }
    }

    /**
     * Копирует все файлы папки
     * @param strOldFolder Директория откуда копируем файлы
     * @param strNewFolder Директория куда копируем файлы
     */
    public static void copyXuggleDir(String strOldFolder,String strNewFolder) {
        //new File("c://program files//XU//bin").mkdirs();
        //String strFolder = "data//Xuggle//";
        new File(strNewFolder).mkdirs();
        String list[] = new File(strOldFolder).list();
        for(int i = 0; i < list.length; i++) {
            try {
                copyFile(new File(strOldFolder + list[i]), new File(strNewFolder + list[i]));
            } catch (Exception ex) {
                Logger.getLogger(CopyXuggleFiles.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
