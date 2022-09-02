/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CamPanel.java
 *
 * Created on 10.09.2011, 21:23:30
 */

package com.shkip.ipcam;


import com.xuggle.mediatool.IMediaWriter;
import simple.simplePlayer;
import com.xuggle.xuggler.Configuration;
import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IContainerParameters;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import com.xuggle.mediatool.ToolFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainerFormat;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author Rybakova Ekaterina Olegovna
 */
public class CamPanel extends javax.swing.JPanel implements Runnable{

    /**
     * Creates new form CamPanel
     *  @param aCam - камера, информация с которой будет отображаться
     */
    public CamPanel(CameraIP aCam, Dimension size) {
        initComponents();
        aCameraIP = aCam;
        settingsConnection = new SettingsConnection();
        //        canvasVideoPicture = new Canvas();
        //      canvasVideoPicture.addMouseMotionListener(this);
        this.setSize(size);
        //resizeWindowPlayer(size);
        /*canvasVideoPicture.setVisible(true);
        canvasVideoPicture.setEnabled(true);
        canvasVideoPicture.setFocusable(true);
        canvasVideoPicture.setIgnoreRepaint(true);
        canvasVideoPicture.setSize(size);*/
        //this.setLayout(new BorderLayout());
        this.add(jToolBar1);
        jToolBar1.setVisible(false);
        this.setBackground(Color.white);
        // this.setBackground(Color.BLACK);
        // topImagePtr = calcTopImagePoint(new Dimension(vWidth, vHight), this.getSize());
    }
 
    /**
     * Запуск потока вещания
     */
    public void run() {
        startPlayer();
    }

    /**
     * Завершение потока вещания
     */
    public void stopShow(){
        System.out.println("stop show");
        endShow = true;
    }

    public InputStream getInputStreamURL(String urlPath) {
        try {
            URL url = new URL(urlPath);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.connect();
            return huc.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(simplePlayer.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }




    /**
     * Запись патока вещания в файла
     * @param fileOutputPath - имя выходного файла
     * @param streamCoder - поток данных
     */
    public void writeConnection(String fileOutputPath, IStreamCoder streamCoder) {
     IMediaWriter writer = ToolFactory.makeWriter(fileOutputPath);

		writer.addListener(ToolFactory.makeDebugListener());


        IContainer writeContainer = writer.getContainer();
        IContainerFormat containerFormat_live = IContainerFormat.make();
        containerFormat_live.setOutputFormat("mov",fileOutputPath, null);
        writeContainer.setInputBufferLength(0);
        int retVal = writeContainer.open(fileOutputPath, IContainer.Type.WRITE, containerFormat_live);
        if (retVal < 0) {
            System.err.println("Could not open output container for live stream");
            System.exit(1);
        }
        //видео
        IStream streamVideo = writeContainer.addNewStream(0);
        IStreamCoder coderVideo = streamVideo.getStreamCoder();
        ICodec codec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
        coderVideo.setNumPicturesInGroupOfPictures(5);
        coderVideo.setCodec(codec);
        coderVideo.setBitRate(200000);
        coderVideo.setPixelType(IPixelFormat.Type.YUV420P);
        int width = aCameraIP.getDimension().width;
        int height = aCameraIP.getDimension().height;
        coderVideo.setHeight(height);
        coderVideo.setWidth(width);
        System.out.println("[ENCODER] video size is " + width + "x" + height);
        coderVideo.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true);
        coderVideo.setGlobalQuality(0);
        IRational frameRate = IRational.make(5, 1);
        coderVideo.setFrameRate(frameRate);
        coderVideo.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator()));

        Properties props = new Properties();
        InputStream is;
        try {
            is = new FileInputStream("./libx264-normal.ffpreset");
            props.load(is);
        } catch (IOException e) {
            System.err.println("You need the libx264-normal.ffpreset file from the Xuggle distribution in your classpath.");
        }
         Configuration.configure(props, coderVideo);
        coderVideo.open();



outAudioStream = writeContainer.addNewStream(1);
		  outAudioCoder = outAudioStream.getStreamCoder();


                  /*
                  
            		  outAudioCoder.setSampleRate(audioCoder.getSampleRate());
		  outAudioCoder.setChannels(audioCoder.getChannels());
		  outAudioCoder.setDefaultAudioFrameSize(audioCoder.getDefaultAudioFrameSize());
		  outAudioCoder.setTimeBase(audioCoder.getTimeBase());


		  outAudioCoder.setCodec(audioCoder.getCodec());
		  outAudioCoder.setFrameRate(audioCoder.getFrameRate());
*/

		/*  outAudioCoder.setSampleRate(simplerate);
		  outAudioCoder.setChannels(1);
		  outAudioCoder.setTimeBase(IRational.make(1, 16000));


		  outAudioCoder.setCodec(ICodec.ID.CODEC_ID_PCM_S16LE);
		  outAudioCoder.setFrameRate(IRational.make(0,0));
*/
                  
                  outAudioCoder.setCodec(ICodec.ID.CODEC_ID_PCM_S16LE);
                  outAudioCoder.setSampleRate(simplerate);
                  outAudioCoder.setChannels(1);
                  outAudioCoder.setTimeBase(IRational.make(1, 16000));
                  outAudioCoder.setFrameRate(IRational.make(0, 0));
                  outAudioCoder.setDefaultAudioFrameSize(5760);

                  
                  outAudioCoder.setBitRate(256000);

               
                  
                  
		  outAudioCoder.open();
		 
		writer.addListener(ToolFactory.makeDebugListener());
		if (writer.getContainer().writeHeader() < 0)
		{
			System.err.println("cannot write headers");
			System.exit(11);
		}


                
/*
        //	writer = ToolFactory.makeWriter("output.mp4", reader);
        IMediaReader reader = ToolFactory.makeReader(settingsConnection.getReadContainer());
//writer = ToolFactory.makeWriter("output.mp4");
        writer = ToolFactory.makeWriter("output.mp4");

		writer.addVideoStream(0,0, coderVideo.getCodec(), frameRate,width, height);
writer.getContainer().open("24 "+fileOutputPath, IContainer.Type.WRITE, containerFormat_live);


//writer.getContainer().addNewStream(0);


*/
//settingsConnection.setWriteContainer(writeContainer);
               // writeContainer.writeHeader();
                
settingsConnection.setStreamDecoder(coderVideo);
                   settingsConnection.setWriter(writer);
        //settingsConnection.setWriteContainer(writeContainer);




    }
    
    /**
     * Подключение
     * @param inputFormatStr - имя входного фаормата
     */
    public void readConnection(String inputFormatStr) {
        try {
            IContainer readContainer = IContainer.make();
            IContainerParameters params = IContainerParameters.make();
            params.setVideoWidth(aCameraIP.getDimension().width);
            params.setVideoHeight(aCameraIP.getDimension().height);
            params.setPixelFormat(IPixelFormat.Type.YUV420P);
            readContainer.setParameters(params);
            InputStream is = null; //getInputStreamURL("http://83.235.21.102:8090/axis-cgi/mjpg/video.cgi?resolution=640x480");
            if (aCameraIP.getUrlCam() == null) {
                if (readContainer.open("temp.mjpeg", IContainer.Type.READ, null) < 0)
                      throw new IllegalArgumentException("could not open file: ");
            } else {
                URL url = aCameraIP.getUrlCam();
                String userPass = aCameraIP.getUserName() + ":" + aCameraIP.getPasswordCam();
                String encoding = new sun.misc.BASE64Encoder().encode(userPass.getBytes());
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                if (!aCameraIP.getUserName().isEmpty())
                huc.setRequestProperty("Authorization", "Basic " + encoding);
                huc.connect();
                is = huc.getInputStream();
                readContainer.setReadRetryCount(100);
                IContainerFormat format = IContainerFormat.make();
                if (format.setInputFormat(inputFormatStr) < 0) {
                    throw new IllegalArgumentException("couldn't open cam device:");
                }
                if (readContainer.open(new DataInputStream(is), format, true, false ) < 0)
                    throw new IllegalArgumentException("could not open file: " +aCameraIP.getUrlCam().toString());
            }
            getVideoStreamCoder(readContainer);
            IVideoResampler resampler = getVideoResampler(settingsConnection.getStreamCoder());
            settingsConnection.setVideoResampler(resampler);
            settingsConnection.setReadContainer(readContainer);
        } catch (IOException ex) {
            Logger.getLogger(CamPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Получение кодека видеопотока
     * @param container
     */
    public void getVideoStreamCoder(IContainer container) {
        int numStreams = container.getNumStreams();
        Integer videoStreamId = -1;
        IStreamCoder videoCoder = null;
        for (int i = 0; i < numStreams; i++) {
            // Поиск объекта потока
            IStream stream = container.getStream(i);
            // Получение coder, который сможет декодирировать наш поток
            IStreamCoder coder = stream.getStreamCoder();
            if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                videoStreamId = i;
                videoCoder = coder;
                break;
            }
        }
        videoCoder.setTimeBase(videoCoder.getTimeBase());
        videoCoder.setHeight(aCameraIP.getDimension().height);
        videoCoder.setWidth(aCameraIP.getDimension().width);
        videoCoder.setPixelType(IPixelFormat.Type.YUVJ422P);
        if (videoCoder.open() < 0) {
            throw new RuntimeException("could not open video coder for container: ");
        }
        settingsConnection.setStreamCoder(videoCoder);
        settingsConnection.setVideoStreamId(videoStreamId);
    }

    /**
     * Получение 
     * @param videoCoder
     * @return
     */
    public IVideoResampler getVideoResampler(IStreamCoder videoCoder) {
        IVideoResampler resampler = null;
        if (videoCoder.getPixelType() != IPixelFormat.Type.YUVJ422P) {
            // Если поток не в YUVJ422P то конвертируем его при помощи VideoResampler.
            resampler = IVideoResampler.make(
                    videoCoder.getWidth(),
                    videoCoder.getHeight(),
                    IPixelFormat.Type.YUVJ422P,
                    videoCoder.getWidth(),
                    videoCoder.getHeight(),
                    videoCoder.getPixelType());
            if (resampler == null) {
                throw new RuntimeException("could not create color space " + "resampler for: ");
            }
        }
        return resampler;
    }

    /**
     * Получение изображения с видеопотока
     * @param packet - пакет данных
     * @param picture - изображение
     * @param resampler
     * @param firstTimestampInStream - время, начиная с которого считываем
     * @param systemClockStartTime - системное время
     * @return
     */
    public long getNewPicture(  IPacket packet,
                                IVideoPicture picture,
                                IVideoResampler resampler,
                                long firstTimestampInStream,
                                long systemClockStartTime) {
         
        int offset = 0;
        long millisecondsToSleep=0;
        long millisecondsStreamTimeSinceStartOfVideo = 0;
         //Samples samples = IAudioSamples.make(1024, audioCoder.getChannels());
        long nextFrameTime = 0;

        while (offset < packet.getSize()) {
            int bytesDecoded =settingsConnection.getStreamCoder().decodeVideo(picture, packet, offset);
            if (bytesDecoded < 0) {
                throw new RuntimeException("got error decoding video in: ");
            }
            offset += bytesDecoded;
            // Некоторые декодоры заносят данные в пакет, хотя они не являются
            // изображением видео. Проверяем получили ли полное изображение.
            if (picture.isComplete()) {
                IVideoPicture newPic = picture;

                // Если не получили resampler, то значит мы получили видео
                // не в формате YUVJ422P. Преобразовываем его в YUVJ422P.
                if (resampler != null) {
                    // можем resample
                    newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),picture.getWidth(), picture.getHeight());
                    if (resampler.resample(newPic, picture) < 0) {
                        throw new RuntimeException("could not resample video from: ");
                    }
                }
                if (newPic.getPixelType() != IPixelFormat.Type.YUVJ422P) {
//                    throw new RuntimeException("could not decode video as BGR 24 bit data in: ");
                }
                if (firstTimestampInStream == Global.NO_PTS) {
                    // первое время старта
                    firstTimestampInStream = picture.getTimeStamp();
                    // системное время старта
                    systemClockStartTime = System.currentTimeMillis();
                } else {
                    long systemClockCurrentTime = System.currentTimeMillis();
                    long millisecondsClockTimeSinceStartofVideo =
                            systemClockCurrentTime - systemClockStartTime;
                    // вычисляем сколькьо времени от первого времени в потоке
                    // запоминаем IVideoPicture и IAudioSamples timestamps
                    // (получаем MICROSECONDS, для получения milliseconds делим на 1000)
                    millisecondsStreamTimeSinceStartOfVideo =
                            (picture.getTimeStamp() - firstTimestampInStream) / 1000;
                    // даем 50 ms допуска
                    final long millisecondsTolerance = 50;
                    millisecondsToSleep =
                            millisecondsStreamTimeSinceStartOfVideo -
                            (millisecondsClockTimeSinceStartofVideo +
                            millisecondsTolerance);
                    if (millisecondsToSleep > 0) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(millisecondsToSleep);
                        } catch (InterruptedException e) {
                            System.out.println("Sleep: " + e.getMessage());
                            return 0;
                        }
                    }
                }
                // сонвертируем YUVJ422P в Java buffered image
                javaImage = Utils.videoPictureToImage(newPic);
                // меняем масштаб для экрана
                //System.out.println("size: " + vWidth + "  "+ vHight);
                imageCamera = javaImage.getScaledInstance(
                       vWidth,
                       vHight,
                       // aCameraIP.getDimension().width,
                       // aCameraIP.getDimension().height,
                        Image.SCALE_DEFAULT);
                // прорисовываем на канвасе
                System.out.println("read image");
                if (panelFullScreen!=null && panelFullScreen.isVisible()) {
                    panelFullScreen.setImage(javaImage);
                }
                // перерисовка
                repaint();/*
                IPacket packet2 = IPacket.make();
                                  IConverter converter = ConverterFactory.createConverter(javaImage, settingsConnection.getStreamDecoder().getPixelType());
                                  long now = System.currentTimeMillis();
                                  long firstTimeStamp = picture.getTimeStamp();
                                  if (firstTimeStamp == -1)  firstTimeStamp = now;
                 long timeStamp = (now - firstTimeStamp)*1000; // convert to microseconds
                                IVideoPicture outFrame = converter.toPicture(javaImage,  timeStamp);
                outFrame.setQuality(0);
                settingsConnection.getStreamDecoder().encodeVideo(packet2, outFrame, 0);

                if (packet2.isComplete()) settingsConnection.getWriteContainer().writePacket(packet2);*/
              
                // если записываем и если открыт поток на запись
                if (settingsConnection.getWriter()!=null
                        &&   settingsConnection.getWriter().getContainer().isOpened()) {
                    
                    IPacket packet2 = IPacket.make();
                    IConverter converter = ConverterFactory.createConverter(javaImage, IPixelFormat.Type.YUV420P);
                    long now = System.currentTimeMillis();
                    long timeStamp = (now - firstTimeStamp) * 1000;
                    IVideoPicture outFrame = converter.toPicture(javaImage, timeStamp);

                    // если начинаем писать
                    if (j == 0) {
                        //make first frame keyframe
                        outFrame.setKeyFrame(true);
                    }
                   outFrame.setQuality(0);
                   settingsConnection.getStreamDecoder().encodeVideo(packet2, outFrame, 0);
                   outFrame.delete();
                   if (packet2.isComplete()) {
                     
                     //  settingsConnection.getWriteContainer().writePacket(packet2);
                     
                      settingsConnection.getWriter().getContainer().writePacket(packet2);
                      // writer.encodeVideo(0, , now, TimeUnit.NANOSECONDS);
                       System.out.println("[ENCODER] writing VIDEO packet of size " + packet2.getSize() + " for elapsed time ");
                       j++;
                   }
                }
            }
        }
        return millisecondsToSleep;
    }
    
    /**
     * Вычисляем верхний левый угол для того, чтобы изображение было по середине
     * @param panel
     * @param image
     * @return
     */
    public static Point calcTopImagePoint(Dimension panel, Dimension image) {
       Dimension centerImg = new Dimension(image.width/2,image.height/2);
       Dimension centerPanel = new Dimension(panel.width/2,panel.height/2);
       Point topPtr = new Point(0,0);
       if (centerPanel.width - centerImg.width>0) topPtr.x = centerPanel.width - centerImg.width;
       if (centerPanel.height - centerImg.height>0) topPtr.y = centerPanel.height - centerImg.height;
       return topPtr;
    }

    /**
     * Измнение размера окна панели
     * @param sizeWin
     */
    public void resizeWindowPlayer(Dimension sizeWin) {
        // смотрим как установлем тоолбар и в зависимости от этого вычитаем нужные размеры
        int height = sizeWin.height;//-jToolBar1.getHeight();
        int width = sizeWin.width;//-jToolBar1.getWidth();
        System.out.println("size not toolbar "+width+"  "+height);
        this.setSize(sizeWin);
        this.setSize(new Dimension(width, height));
        // запоминаем размер для картинки
        double k = aCameraIP.getDimension().width / aCameraIP.getDimension().height;
        double k2 = width / height;
        if (k2<k) {
            float scaleW = (float)width*1.0f/vWidth;
            vWidth = width;
            vHight*=scaleW;
        } else {
            float scaleH = (float)height*1.0f/vHight;
            vHight = height;
            vWidth*=scaleH;      
        }
        topImagePtr = calcTopImagePoint(sizeWin,new Dimension(vWidth, vHight));
        //System.out.println("size not toolbar "+vWidth+"  "+vHight);
        repaint();
    }
    
    /**
     * Запуск потока вещания
     */
    public void startPlayer(){
        readConnection(aCameraIP.getInputFormatStr());
        //    startWriteStreamVideo();
        IContainer readContainer = settingsConnection.getReadContainer();
        IStreamCoder videoCoder = settingsConnection.getStreamCoder();
        IVideoResampler resampler = settingsConnection.getVideoResampler();
        setVisible(true);
        // получаем пакет данных
        IPacket packet = IPacket.make();
        long firstTimestampInStream = Global.NO_PTS;
        long systemClockStartTime = 0;

        new Thread(new Runnable() {
            public void run() {
                  getAudioContainer();
            }
        }).start();


        while (readContainer.readNextPacket(packet) >= 0 && !endShow) {
            // если пользователь приостановил показ камеры, то ждем включения или завершения работы просмотра камеры
            while (stopView && !endShow) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CamPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            //смотрим принадлежит ли пакет нашему видеопотоку
            if (packet.getStreamIndex() == settingsConnection.getVideoStreamId()) {
                //создаем новое изображение, чтобы получать данные из Xuggler
                IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(), videoCoder.getWidth(), videoCoder.getHeight());
                // заливаем фон черным
                this.setBackground(Color.black);
                long millisecondsToSleep = getNewPicture(packet,picture,resampler,firstTimestampInStream,systemClockStartTime );
            } else {
                //пакет не наш, пропускаем его
                do {
                } while (false);
            }
        }
        if (videoCoder != null) {
            videoCoder.close();
            videoCoder = null;
        }
        if (readContainer != null) {
            readContainer.close();
            readContainer = null;
        }
    }

    /**
     * Рисуем изображение на панели
     * @param g
     */
    protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // если нет изображения, то ставим иконку "Загрузка"
            if (imageCamera == null) {
                ImageIcon icon = new ImageIcon("./lib/icon/loading5.gif");
                Point calcTopImagePoint = calcTopImagePoint(this.getSize(), new Dimension(icon.getIconWidth(), icon.getIconWidth()));
                g.drawImage(icon.getImage(), calcTopImagePoint.x,calcTopImagePoint.y, this);
            } else {
                // рисуем изображение
                g.drawImage(imageCamera, topImagePtr.x, topImagePtr.y, this);
            }
            // canvasVideoPicture.getGraphics().drawImage(imageCamera, 0, 0, this);
    }


    /**
     * Отрисовка изображения на панели
     */
    private void paint() {
        // масштаб картинки
        if (javaImage!=null) {
            imageCamera = javaImage.getScaledInstance(
                        vWidth,
                        vHight,
                      Image.SCALE_DEFAULT);
            repaint();
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

        jToolBar1 = new javax.swing.JToolBar();
        jButton6 = new javax.swing.JButton();
        btnFullScreen = new javax.swing.JButton();
        btnStartOrStopCam = new javax.swing.JButton();
        btnPlayAudio = new javax.swing.JButton();
        btnSavePicture = new javax.swing.JButton();
        btnSaveVideo = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                formMouseMoved(evt);
            }
        });
        setLayout(null);

        jToolBar1.setBorder(null);
        jToolBar1.setRollover(true);
        jToolBar1.setAutoscrolls(true);
        jToolBar1.setMaximumSize(new java.awt.Dimension(280, 39));
        jToolBar1.setMinimumSize(new java.awt.Dimension(280, 32));
        jToolBar1.setOpaque(false);
        jToolBar1.setPreferredSize(new java.awt.Dimension(280, 32));

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/toolBarPanelCamera/info.png"))); // NOI18N
        jButton6.setToolTipText("Information");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setOpaque(false);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton6);

        btnFullScreen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/toolBarPanelCamera/fullscreen.png"))); // NOI18N
        btnFullScreen.setToolTipText("Full screen");
        btnFullScreen.setFocusable(false);
        btnFullScreen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnFullScreen.setOpaque(false);
        btnFullScreen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnFullScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFullScreenActionPerformed(evt);
            }
        });
        jToolBar1.add(btnFullScreen);

        btnStartOrStopCam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/toolBarPanelCamera/closeCam32.png"))); // NOI18N
        btnStartOrStopCam.setToolTipText("On/Off camera");
        btnStartOrStopCam.setFocusable(false);
        btnStartOrStopCam.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStartOrStopCam.setOpaque(false);
        btnStartOrStopCam.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnStartOrStopCam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartOrStopCamActionPerformed(evt);
            }
        });
        jToolBar1.add(btnStartOrStopCam);

        btnPlayAudio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/toolBarPanelCamera/sound2.png"))); // NOI18N
        btnPlayAudio.setToolTipText("Run/Stop save video");
        btnPlayAudio.setFocusable(false);
        btnPlayAudio.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnPlayAudio.setOpaque(false);
        btnPlayAudio.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnPlayAudio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlayAudioActionPerformed(evt);
            }
        });
        jToolBar1.add(btnPlayAudio);

        btnSavePicture.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/toolBarPanelCamera/image.png"))); // NOI18N
        btnSavePicture.setToolTipText("Save image");
        btnSavePicture.setFocusable(false);
        btnSavePicture.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSavePicture.setOpaque(false);
        btnSavePicture.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSavePicture.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSavePictureActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSavePicture);

        btnSaveVideo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/toolBarPanelCamera/floppy_disc_accept.png"))); // NOI18N
        btnSaveVideo.setToolTipText("Run/Stop save video");
        btnSaveVideo.setFocusable(false);
        btnSaveVideo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveVideo.setOpaque(false);
        btnSaveVideo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnSaveVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveVideoActionPerformed(evt);
            }
        });
        jToolBar1.add(btnSaveVideo);

        add(jToolBar1);
        jToolBar1.setBounds(2, 2, 390, 40);
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
       paint();
    }//GEN-LAST:event_formComponentResized

    private void formMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseMoved
        if (evt.getY()<5) {
            // System.out.println("начало");
            // показываем панель "Работа с видеопотоком"
            jToolBar1.setVisible(true);
        } else {
            if (jToolBar1.isVisible()) {
                // скрываем панель "Работа с видеопотокм"
                jToolBar1.setVisible(false);
                // перерисовываем картинку
                repaint();
            }
        }
    }//GEN-LAST:event_formMouseMoved

    /**
     * FullScreen видеопотока
     * @param evt
     */
    private void btnFullScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFullScreenActionPerformed
        //запускаем видеопоток FullScreen в новом потоке
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                panelFullScreen = new ZoomImagePanel(imageCamera);
                panelFullScreen.setVisible(true);
            }
        });
    }//GEN-LAST:event_btnFullScreenActionPerformed

     /**
     * Обработка нажатия на клавишу "Снимок с экрана"
     * @param evt
     */
    private void btnSavePictureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSavePictureActionPerformed
        createShortPic();
}//GEN-LAST:event_btnSavePictureActionPerformed
    /**
     * Обработка нажатия на кнопку Остановить/Воспроизвести видеопоток с камеры
     * @param evt
     */
    private void btnStartOrStopCamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartOrStopCamActionPerformed
        // если камера показывает, то делаем иконку на "завершить показ", останавливаем камеру
        if(!stopView) {
           btnStartOrStopCam.setIcon(new ImageIcon(this.getClass().getResource("./resourse/icon/toolbarpanelcamera/camConnect32.png")));
        } else {
           btnStartOrStopCam.setIcon(new ImageIcon(this.getClass().getResource("./resourse/icon/toolbarpanelcamera/closeCam32.png")));
        }
        stopView=!stopView;
    }//GEN-LAST:event_btnStartOrStopCamActionPerformed

    /**
     * Обработка нажатия на кнопку Записывать/остановить показ с видеокамеры
     * @param evt
     */
    private void btnSaveVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveVideoActionPerformed
        // если пишем, то меняем иконку на "остановить запись", начинаем запись
        if(!(settingsConnection.getWriter()!=null &&  settingsConnection.getWriter().getContainer().isOpened())) {
           btnSaveVideo.setIcon(new ImageIcon(this.getClass().getResource("./resourse/icon/toolbarpanelcamera/floppy_disc_remove.png")));
           startWriteStreamVideo();
        }
        else {
           btnSaveVideo.setIcon(new ImageIcon(this.getClass().getResource("./resourse/icon/toolbarpanelcamera/floppy_disc_accept.png")));
           stopWriteStreamVideo();
        }
    }//GEN-LAST:event_btnSaveVideoActionPerformed

    private void btnPlayAudioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlayAudioActionPerformed
        playAudio=!playAudio;
    }//GEN-LAST:event_btnPlayAudioActionPerformed

    /**
     * Создание скриншота с видеопотока
     */
    public void createShortPic() {
        try {
            Date dNow = new Date();
            // получаем текущую дату для названия файла
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd 'at' hh-mm-ss'.png'");
            ImageIO.write(javaImage, "PNG", new File(".", ft.format(dNow)));
        } catch (IOException ex) {
            Logger.getLogger(simplePlayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Останавливаем запись видеопотока
     */
    public void stopWriteStreamVideo() {
        IContainer writeContainer = settingsConnection.getWriter().getContainer();
        if (writeContainer!=null && writeContainer.isOpened()){
            System.out.println("------------- Stop write");
            writeContainer.writeTrailer();
            writeContainer.close();
            System.out.println("------------- Stop write");
        }
    }

    /**
     * Начинаем запись видеопотока
     */
    public void startWriteStreamVideo() {
        
        if (settingsConnection.getWriter()==null || !settingsConnection.getWriter().getContainer().isOpened()) {
            Date dNow = new Date();
            SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd 'at' hh-mm-ss'.mjpeg'");
                       writeConnection(ft.format(dNow), settingsConnection.getStreamCoder());

            firstTimeStamp = System.currentTimeMillis();
        }
    }



    private void getAudioContainer() {
        try {
            audioContainer = IContainer.make();
            // Open up the container
            // if (container.open(filename, IContainer.Type.READ, null) < 0) {
            //    throw new IllegalArgumentException("could not open file: " + filename);
            //}
            URL url = new URL("http://192.168.0.20/audio.cgi");
            String userPass = "admin:smile200";
            String encoding = new sun.misc.BASE64Encoder().encode(userPass.getBytes());
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setRequestProperty("Authorization", "Basic " + encoding);
            huc.connect();
            InputStream is = huc.getInputStream();
            IContainerFormat format = IContainerFormat.make();
            if (format.setInputFormat("wav") < 0) {
                throw new IllegalArgumentException("couldn't open cam device:");
            }
            if (audioContainer.open(new DataInputStream(is), format, true, false) < 0) {
                throw new IllegalArgumentException("couldn't open container");
            }

            audioContainer.setReadRetryCount(100);
            // query how many streams the call to open found
            int numStreams = audioContainer.getNumStreams();
            // and iterate through the streams to find the first audio stream
            int audioStreamId = -1;
            for (int i = 0; i < numStreams; i++) {

                  // Find the stream object
                IStream stream = audioContainer.getStream(i);

                // Get the pre-configured decoder that can decode this stream;
                IStreamCoder coder = stream.getStreamCoder();
                if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO) {
                    audioStreamId = i;
                    audioCoder = coder;
                    break;
                }
            }
            if (audioStreamId == -1) {
                throw new RuntimeException("could not find audio stream in container: ");
            }
            if (audioCoder.open() < 0) {
                throw new RuntimeException("could not open audio decoder for container: ");
            }
            openJavaSound(audioCoder);





            // Now, we start walking through the container looking at each packet.
           // IMediaWriter writer = ToolFactory.makeWriter("123.wav");

            System.out.println("audio Frame size : " + audioCoder.getAudioFrameSize());
            IPacket packet = IPacket.make();
            while (audioContainer.readNextPacket(packet) >= 0) {
                // Now we have a packet, let's see if it belongs to our audio stream
                if (packet.getStreamIndex() == audioStreamId) {


                    samples = IAudioSamples.make(1024, audioCoder.getChannels());

                    int offset = 0;

                    while (offset < packet.getSize()) {
                        int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
                        if (bytesDecoded < 0) {
                            throw new RuntimeException("got error decoding audio in: " );
                        }
                        offset += bytesDecoded;

                        if (samples.isComplete()) {
                            if(playAudio)playJavaSound(samples);

                // если записываем и если открыт поток на запись
                if (settingsConnection.getWriter()!=null
                        &&   settingsConnection.getWriter().getContainer().isOpened()) {

                    packet.getPts();
				 packet.getStreamIndex();
				 packet.getFormattedTimeStamp();
				 packet.isComplete();
				 audioCoder.decodeAudio(samples, packet, 0);
				// samples.setTimeBase(outAudioCoder.getTimeBase());
				 samples.setComplete(true, samples.getNumSamples(), outAudioCoder.getSampleRate(), outAudioCoder.getChannels(), outAudioCoder.getSampleFormat(), packet.getPts());
                                 IPacket npacket = IPacket.make();

				 outAudioCoder.encodeAudio(npacket, samples, 0);
				// npacket.setDuration(packet.getDuration());
				 npacket.setPts(packet.getPts());
                                 long now = System.currentTimeMillis();
                    long timeStamp = (now - firstTimeStamp);
                    npacket.setDuration(timeStamp);
				 npacket.setKeyPacket(true);
				 if (npacket.isComplete())
				 {
                                     System.out.println("[ENCODER] writing AUDIO packet of size " + npacket.getSize() + " for elapsed time ");

					 settingsConnection.getWriter().getContainer().writePacket(npacket, true);
npacket.delete();
				 }

                            }
                        }
                    }
                } else {
                //пакет не наш, пропускаем его
                do {

                    System.out.println("Пакетик аудио не наш, ждем");
                } while (false);
                }
            }
            closeJavaSound();
            if (audioCoder != null) {
                audioCoder.close();
                audioCoder = null;
            }
            if (audioContainer != null) {
                audioContainer.close();
                audioContainer = null;
            }
        } catch (IOException ex) {
            Logger.getLogger(CamPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
  }

//31 683
    //31 258

  private static void openJavaSound(IStreamCoder aAudioCoder)
  {
    AudioFormat audioFormat = new AudioFormat(aAudioCoder.getSampleRate(),
        (int)IAudioSamples.findSampleBitDepth(aAudioCoder.getSampleFormat()),
        aAudioCoder.getChannels(),
        true,
        false);
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
    try {
      mLine = (SourceDataLine) AudioSystem.getLine(info);
      mLine.open(audioFormat);
      mLine.start();
    } catch (LineUnavailableException e) {
      throw new RuntimeException("could not open audio line");
    }


  }

  private static void playJavaSound(IAudioSamples aSamples)
  {
    // заносим все в линию аудипотока
    byte[] rawBytes = aSamples.getData().getByteArray(0, aSamples.getSize());
    mLine.write(rawBytes, 0, aSamples.getSize());
  }

  private static void closeJavaSound()
  {
    if (mLine != null)
    {
      // ждем завершения аудиопотока
      mLine.drain();
      //закрываем линию
      mLine.close();
      mLine=null;
    }
  }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFullScreen;
    private javax.swing.JButton btnPlayAudio;
    private javax.swing.JButton btnSavePicture;
    private javax.swing.JButton btnSaveVideo;
    private javax.swing.JButton btnStartOrStopCam;
    private javax.swing.JButton jButton6;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

  //  Canvas canvasVideoPicture = null;
    // объект камеры

    private CameraIP aCameraIP = null;
    private Boolean stopView = false;
    private Boolean endShow = false;
    private SettingsConnection settingsConnection = null;
    int vWidth = 320;//640;
    int vHight = 240;//540;
    private BufferedImage javaImage = null;
    Image imageCamera = null;
    private Point topImagePtr;
    ZoomImagePanel panelFullScreen;
    //Начальное время записи видеоряда для временной шкалы
    long firstTimeStamp = 0;
    int j = 0;
    private static SourceDataLine mLine;
    IAudioSamples samples = null;
    private Boolean playAudio = false;
    private IContainer audioContainer = null;
   //  int streamIndex = 0;
   // private Dimension sizePanelCam;
   // IMediaWriter writer;

            IStreamCoder audioCoder = null;
IStream outAudioStream = null;
		IStreamCoder outAudioCoder = null;
		IAudioSamples audioSamples = null;
		IAudioSamples audioSamples_resampled = null;
               int simplerate = 16000;
               
} 
