/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.shkip.ipcam;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.ICodec.ID;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rybakova Ekaterina Olegovna
 */
public class LoadCamConfig {
    /**
     * Конструктор
     * @param path путь к файлам конфигурации
     */
    public LoadCamConfig(String path) {
        cameras = new ArrayList<CameraIP>();
        File pathFile = new File(path);
        String[] list;
        list = pathFile.list();
        for (String dirItem: list) {
            String pathCam =path+dirItem;

            // если это файл, то посылаем его на обработку
            if (new File(pathCam).isFile()) {
                OutputStream out = (new CreateConfigCam(pathCam)).getOutputStream();
                loadCam(out,pathCam);
            }
        }
        String str = "";
    }
    /**
     * получаем класс CameraIP
     */
    private void loadCam(OutputStream out,String pathCam) {
        try {
            CameraIP cam = new CameraIP(null, null, null, null, CameraIP.Format.MJPEG, ID.CODEC_ID_MJPEGB, 0, 0);
            // после окончания получить байты
            byte[] bytes = ((ByteArrayOutputStream) out).toByteArray();
            // создать поток ввода
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufReader = new BufferedReader(reader);
            String str;
            int i = 0;
            while (bufReader.ready()) {
                str = bufReader.readLine();
                // выбираем то, что в кавычках
                String strsub = str.substring(str.indexOf("'")+1,str.length()-1);
                switch (i)
                {
                    case 0:  // имя камеры
                        cam.setNameCam(strsub);
                        break;
                    case 1: // приставка к IP адресу
                        cam.setAddIP(strsub);
                        break;
                    case 2: //Format
                        cam.setFormat(strsub);
                        break;
                    case 3: // Width
                        if (Integer.valueOf(strsub)<0)
                            return;
                        cam.setWidth(Integer.valueOf(strsub));
                        break;
                    case 4: // Height
                        cam.setHeight(Integer.valueOf(strsub));
                        break;
                    case 5: // PixelFormat ??????

                        break;
                    case 6: // CodecID
                        cam.setCodecID(ICodec.ID.valueOf(strsub));
                        break;
                }
                i++;
            }
            if (i>=7)cameras.add(cam);
        } catch (IOException ex) {
            Logger.getLogger(LoadCamConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


        public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LoadCamConfig cam = new LoadCamConfig( "./config/");
                /*File path = new File("./config");
                String[] list;
                list = path.list();
                for (String dirItem: list) {
                    String pathCam = "./config/"+dirItem;
                    // если это файл, то посылаем его на обработку
                    if (new File(pathCam).isFile())
                }*/
            }
        });
    }

    /**
     * Получение камеры с настройками
     * @return cam - объект камеры
     */
    public List<CameraIP> getCameras() {
      return cameras;
    }

    private List<CameraIP> cameras = null;
}
