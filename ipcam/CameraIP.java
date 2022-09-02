/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.shkip.ipcam;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IPixelFormat;
import java.awt.Dimension;
import java.net.URL;

/**
 *
 * @author Rybakova Ekaterina Olegovna
 */
public class CameraIP {
    /*
     * Конструктор класса
     * @param name - произвольное имя камеры
     * @param url адрес камеры
     * @param login - имя пользователя
     * @param password - пароль пользователя
     * @param format - входной формат данных (MJPEG, H264, etc)
     * @param codecID - кодек
     * @param height - высота
     * @param width - ширина
     */
    public CameraIP(String name, URL url, String login, String password, Format format,ICodec.ID codecID, Integer width, Integer height)
    {
        this.name = name;
        urlCam = url;
        this.formatStream = format;
        this.height = height;
        this.width = width;
        this.login = login;
        this.password = password;
        this.codecID = codecID;
    }

    /*
     * Получение потока данных
     * @param name - имя пользователя
     * @param password - пароль пользователя
     * @return void
     *
    public void setInputStream(String name, String password) throws IOException
    { 
        HttpURLConnection huc = null;
        if (!urlCam.equals(new URL("")))
        {
            huc =(HttpURLConnection) urlCam.openConnection();
            aInputVideoStream = huc.getInputStream();
        }
        else
        {
            aInputVideoStream = null;
        }
    }
    */

    /**
     * Получение имени камеры
     */
    public String getNameCam(){
        return name;
    }

    /**
     * Установка имени камеры
     * @param newName новое имя камеры
     */
    public void setNameCam(String newName){
        this.name = newName;
    }
    /**
     * Получение адреса камеры
     * @return url cam
     */
    public URL getUrlCam(){
        return urlCam;
    }

     
    /**
     * Получение формата входных данных
     * @return формат потока
     */
    public String getInputFormatStr()
    {
        return formatStream.getStrFormat();
    }

    /**
     * Получение формата входных данных
     * @return формат потока
     */
    public Format getInputFormat()
    {
        return formatStream;
    }
    /**
     * Получение размеров камеры
     * @return видеоразрешение
     */
    public Dimension getDimension()
    {
        return (new Dimension(width,height));
    }
    /**
     * Получение логина к камере
     * @return логин
     */
    public String getUserName()
    {
        return login;
    }
    /**
     * Получение пароля к камере
     * @return пароль
     */
    public String getPasswordCam()
    {
        return password;
    }

    /**
     * Получение codec id
     * @return codec id
     */
    public ICodec.ID getCodecID()
    {
        return codecID;
    }

     /**
     * Установка codec id
     * @param codec id
     */
    public void setCodecID(ICodec.ID codecId)
    {
        this.codecID = codecId;
    }

    /**
     * Установка приставки к IP адресу
     */
    public void setAddIP(String addIp) {
        this.addIP = addIp;
    }

    /**
     * Установка формата
     * @param format - новый формат
     */
    public void setFormat(String format) {

        formatStream = Format.valueOf(format.toUpperCase());
    }
    
    /**
     * Установка ширины экрана
     * @param size - новая ширина экрана
     */
    public void setWidth(int size) {
        width = size;
    }

    /**
     * Установка высоты экрана
     * @param size - новая высота экрана
     */
    public void setHeight(int size) {
        height = size;
    }

    /**
     * Установка id codec
     * @param codecid
     */
    public void setPixelFormat(ICodec.ID codecId) {
        this.codecID = codecId;
    }
    /**
     * Получение добавочного адреса
     */
    public String getAddIP() {
        return addIP;
    }
    /**
     * Устанавливаем url
     */
    public void setUrl(URL url) {
        urlCam = url;
    }
    /**
     * Установка имени пользователя
     */
    public void setUserName(String name) {
        login = name;
    }
    /**
     * Установка пароля
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * Сохранение заводского имени камеры
     */
    public void saveCompanyNameCamera(String name) {
        companyNameCame = name;
    }

    /**
     * Получение заводского имени камеры
     */
    public String getCompanyNameCamera() {
        return companyNameCame;
    }
    public enum Format
    {
        MJPEG("mjpeg"), JPEG("jpeg"), H264("h264");
        private Format(String strFormat)
        {
            this.strFormat = strFormat;
        }
        public String getStrFormat()
        {
            return strFormat;
        }
        String strFormat;
    };

    private URL urlCam = null;  // IPадрес камеры
    private Integer height;
    private Integer width;
   // InputStream aInputVideoStream=null; // поток с входными данными
    // login к камере
    private String login;
    // pasword к камере
    private String password;
    // тип
    private Format formatStream;
    // произвольное имя камеры
    private String name;
    // Codec.ID
    private ICodec.ID codecID;
    // приставка к айпи адресу
    private String addIP;
    // фирменное название камеры
    private String companyNameCame = null;
}
