/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CamViewer.java
 *
 * Created on 10.09.2011, 21:19:52
 */

package com.shkip.ipcam;

import com.xuggle.xuggler.ICodec;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Point;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;

/**
 *
 * @author Rybakova Ekaterina Olegovna
 */
public class CamViewer extends javax.swing.JFrame {

    /** Creates new form CamViewer */
    public CamViewer() {
       try {
            initComponents();
             setIconImage((new ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/Security-Camera-icon.png"))).getImage());
       
            cameras = new ArrayList<CameraIP>();
//            cameras.add(new CameraIP("mycam", "e:/tmp/temp.mjpeg", "demo", "demo", CameraIP.Format.H264, ICodec.ID.CODEC_ID_MJPEGB,352, 240));
             cameras.add(new CameraIP("MyDlink", new URL("http://192.168.0.20/mjpeg.cgi"), "admin", "smile200", CameraIP.Format.MJPEG,  ICodec.ID.CODEC_ID_MJPEGB,320, 240));

         cameras.add(new CameraIP("Axis Крит", new URL("http://83.235.21.102:8090/axis-cgi/mjpg/video.cgi?resolution=640x480"), "", "", CameraIP.Format.MJPEG, ICodec.ID.CODEC_ID_MJPEGB,640, 480));
            cameras.add(new CameraIP("DSC-3220", new URL("http://68.15.12.110:8002/cgi-bin/video.jpg?"), "demo", "demo", CameraIP.Format.JPEG,  ICodec.ID.CODEC_ID_MJPEGB,352, 240));

           cameras.add(new CameraIP("Panasonic", new URL("http://68.15.12.110:8005/nphMotionJpeg?Resolution=640x480&Quality=Clarity"), "", "", CameraIP.Format.MJPEG,  ICodec.ID.CODEC_ID_MJPEGB,640, 480));
            cameras.add(new CameraIP("MVD CHEB", new URL("http://10.12.3.18/cgi-bin/video.jpg?"), "user", "321", CameraIP.Format.MJPEG,  ICodec.ID.CODEC_ID_MJPEGB,640, 480));
           cameras.add(new CameraIP("Canon EB-230 ", new URL("http://ebdemo.8800.org:65315/-wvhttp-01-/GetOneShot"), "demo", "demo", CameraIP.Format.MJPEG,  ICodec.ID.CODEC_ID_MJPEGB,640, 480));
         cameras.add(new CameraIP("Axis", new URL("http://83.64.164.6/axis-cgi/mjpg/video.cgi?resolution=640x480"), "", "", CameraIP.Format.MJPEG,  ICodec.ID.CODEC_ID_MJPEGB,640, 480));
        cameras.add(new CameraIP("Vivotek", new URL("http://195.5.6.11/axis-cgi/mjpg/video.cgi?resolution=320x240"), "", "", CameraIP.Format.MJPEG,  ICodec.ID.CODEC_ID_MJPEGB,320, 240));
  
        
        cameras.add(new CameraIP("Sony", new URL("http://193.68.124.87/axis-cgi/mjpg/video.cgi?camera=1&resolution=320x240"), "", "", CameraIP.Format.MJPEG,  ICodec.ID.CODEC_ID_MJPEGB,320, 240));
    
         /*
           Dimension sizeWindow = Toolkit.getDefaultToolkit().getScreenSize();
 
 int fsW = sizeWindow.width;
 int fsH = sizeWindow.height;`^
 Insets inset = getInsets();
 fsH -= inset.bottom;
 fsW += (fsW % 2);
 fsH += (fsH % 2);
 this.setBounds(0, 0, fsW, fsH);
 if(getGraphicsConfiguration().getDevice().isFullScreenSupported())
 getGraphicsConfiguration().getDevice().setFullScreenWindow(this);
        this.toFront();*/
        // ----------------//
        //Load the pet images and create an array of indexes.
           
initComboBoxViewer();
        // создаем countViewers количество панелей
 countViewers = 4;
        createPanelViewers();
        update(this.getGraphics());
        
           updateListCameras();
        } catch (MalformedURLException ex) {
            Logger.getLogger(CamViewer.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    /**
     * Создает панели-просмотра в виде комбо-бокса с иконками
     */
    private void initComboBoxViewer() {
        //String[] countViewersStr = {"1x1", "2x2", "3x3", "4x4", "5x5","6x6","8x8"};
        String[] iconViewersAddr = {"1x1.png","2x2.png", "3x3.png", "4x4.png", "5x5.png","6x6.png","8x8.png"};
        
        ImageIcon[]  images = new ImageIcon[iconViewersAddr.length];
        Integer[] intArray = new Integer[iconViewersAddr.length];
        for (int i = 0; i < iconViewersAddr.length; i++) {
            intArray[i] = new Integer(i);
            images[i] = new ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/toolBarMain/Viewers/"+iconViewersAddr[i]));
            //if (images[i] != null) {
            //    images[i].setDescription(countViewersStr[i]);
            //}
        }

        //Create the combo box.
        JComboBox comboBoxList = new JComboBox(intArray);
        ComboBoxRenderer renderer= new ComboBoxRenderer(images,new String[]{"","","","","","",""});
        comboBoxList.setRenderer(renderer);
        comboBoxList.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
               System.out.println(((JComboBox)e.getSource()).getSelectedIndex());
               // смотрим какая клавиша
               int selectNum = ((JComboBox)e.getSource()).getSelectedIndex()+1;
               if (selectNum>6) selectNum++;
               countViewers =(selectNum*selectNum);
               //System.out.println(countViewers);
               // создаем countViewers количество панелей
               createPanelViewers();
               //update(super.getGraphics());
               repaint();
            }
        });
       // petList.setMaximumRowCount(3);

       comboBoxList.setMaximumSize(new Dimension(70, 40));
       jToolBarCam.add(comboBoxList);

    }
    /**
     * Проверяет есть ли камера с таким же названием
     * @param name имя, которое проверяем
     * @return  true - имя новое, false  - такое имя уже есть 
     */
    private Boolean bNewNameCam(String name) {
        for(CameraIP aCam:cameras) {
             if (aCam.getNameCam().equals(name)) return false;
        }
        return true;
    }
            
    /*
     * Добавление новой камеры в список
     * @param cam - объект новой камеры
     */
    public void addNewCam(CameraIP cam)
    {
        // проверяем есть ли уже такая камера с таким именем,
        // если да, то переименовываем
        String nameNewCam = cam.getNameCam();
        while (!bNewNameCam(nameNewCam)) nameNewCam+=" copy";
        cam.setNameCam(nameNewCam);
        cameras.add(cam);
        updateListCameras();
    }

    /**
     * Обновление списка камера
     */
    public void updateListCameras(){
        listCameras.removeAll();
        menuSetCam.removeAll();
        final ArrayList<String> nameCameras =  new ArrayList<String>();
        for (CameraIP cam: cameras) {
            // получаем имя камеры
            String name = cam.getNameCam();
            nameCameras.add(name);
            // добавляем в контекстное меню
            JMenuItem item = new JMenuItem(name);
           // ActionListener listener = new
            //item.
            item.addActionListener(
                    new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            itemSetCamActionPerformed(evt);
                        }
            });
            menuSetCam.add(item);
        }
        // добавляем в список выбора
        listCameras.setModel(new javax.swing.AbstractListModel() {
            Object[] strings = nameCameras.toArray();
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        pmenuSettingsCam = new javax.swing.JPopupMenu();
        menuSetCam = new javax.swing.JMenu();
        jMenuSaveStream = new javax.swing.JMenu();
        jMenuItemSavePic = new javax.swing.JMenuItem();
        jMenuSaveVideoStream = new javax.swing.JMenu();
        jMenuItemSaveStart = new javax.swing.JMenuItem();
        jMenuItemSaveStop = new javax.swing.JMenuItem();
        menuOpenFile = new javax.swing.JMenuItem();
        popupMenuViewers = new javax.swing.JPopupMenu();
        menuViewersOne = new javax.swing.JMenuItem();
        jToolBarCam = new javax.swing.JToolBar();
        jButton4 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        panelSplitMain = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        panelListCameras = new javax.swing.JPanel();
        listCameras = new javax.swing.JList();
        panelCameras = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        menuItemEditCameraEdit = new javax.swing.JMenuItem();
        menuItemEditCameraDelete = new javax.swing.JMenuItem();
        menuItemEditUpdateCameras = new javax.swing.JMenuItem();
        About = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        menuSetCam.setText("Set camera");
        pmenuSettingsCam.add(menuSetCam);

        jMenuSaveStream.setText("Save");

        jMenuItemSavePic.setText("Picture");
        jMenuItemSavePic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSavePicActionPerformed(evt);
            }
        });
        jMenuSaveStream.add(jMenuItemSavePic);

        jMenuSaveVideoStream.setText("Video");

        jMenuItemSaveStart.setText("Start");
        jMenuItemSaveStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveStartActionPerformed(evt);
            }
        });
        jMenuSaveVideoStream.add(jMenuItemSaveStart);

        jMenuItemSaveStop.setText("Stop");
        jMenuItemSaveStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveStopActionPerformed(evt);
            }
        });
        jMenuSaveVideoStream.add(jMenuItemSaveStop);

        jMenuSaveStream.add(jMenuSaveVideoStream);

        pmenuSettingsCam.add(jMenuSaveStream);

        menuOpenFile.setText("Open file");
        menuOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenFileActionPerformed(evt);
            }
        });
        pmenuSettingsCam.add(menuOpenFile);

        menuViewersOne.setText("jMenuItem2");
        popupMenuViewers.add(menuViewersOne);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("IPCAM");
        setMinimumSize(new java.awt.Dimension(650, 700));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                formAncestorResized(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jToolBarCam.setRollover(true);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/toolBarMain/Folders-Video-icon.png"))); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarCam.add(jButton4);
        jToolBarCam.add(jSeparator2);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/toolBarMain/page_process.png"))); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarCam.add(jButton3);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/toolBarMain/web_camera_add.png"))); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarCam.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/toolBarMain/web_camera_remove.png"))); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarCam.add(jButton2);
        jToolBarCam.add(jSeparator1);

        jLabel1.setText("    Viewers: ");
        jToolBarCam.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        getContentPane().add(jToolBarCam, gridBagConstraints);

        panelSplitMain.setLayout(new java.awt.BorderLayout());

        panelListCameras.setMinimumSize(new java.awt.Dimension(100, 100));
        panelListCameras.setLayout(new java.awt.GridBagLayout());

        listCameras.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        listCameras.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listCameras.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        listCameras.setMaximumSize(new java.awt.Dimension(150, 80));
        listCameras.setMinimumSize(new java.awt.Dimension(150, 80));
        listCameras.setPreferredSize(new java.awt.Dimension(150, 80));
        listCameras.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                listCamerasComponentResized(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        panelListCameras.add(listCameras, gridBagConstraints);

        jSplitPane1.setLeftComponent(panelListCameras);

        panelCameras.setPreferredSize(new java.awt.Dimension(128, 450));
        panelCameras.setLayout(new java.awt.GridLayout(1, 1));
        jSplitPane1.setRightComponent(panelCameras);

        panelSplitMain.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 100.0;
        gridBagConstraints.weighty = 100.0;
        getContentPane().add(panelSplitMain, gridBagConstraints);

        jMenu1.setText("File");

        jMenuItem1.setText("New camera");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenu3.setText("Camera");

        menuItemEditCameraEdit.setText("Edit");
        menuItemEditCameraEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemEditCameraEditActionPerformed(evt);
            }
        });
        jMenu3.add(menuItemEditCameraEdit);

        menuItemEditCameraDelete.setText("Delete");
        menuItemEditCameraDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemEditCameraDeleteActionPerformed(evt);
            }
        });
        jMenu3.add(menuItemEditCameraDelete);

        jMenu2.add(jMenu3);

        menuItemEditUpdateCameras.setText("Update list cameras");
        menuItemEditUpdateCameras.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemEditUpdateCamerasActionPerformed(evt);
            }
        });
        jMenu2.add(menuItemEditUpdateCameras);

        jMenuBar1.add(jMenu2);

        About.setText("About");

        jMenu5.setText("info");
        About.add(jMenu5);

        jMenuBar1.add(About);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Открытие диалога добавления новой камеры
     * @param evt
     */
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // загружаем файлы конфигурации
        configCameras = new LoadCamConfig("./config/");
        aNewCameraDialog = new NewCameraDialog(this, true,configCameras.getCameras());
        aNewCameraDialog.setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * Показ камеры на панели
     * @param panel панель с камерой
     * @param cam объект класса CameraIP, которая должна быть установлена
     */
    private void showcam(JPanel panel, CameraIP cam)
    {
         CamPanel aCamPanel = new CamPanel(cam,panel.getSize());
         aCamPanel.resizeWindowPlayer(panel.getSize());
         aCamPanel.setVisible(true);
         aCamPanel.addMouseListener(panel.getMouseListeners()[0]);

         aCamPanel.addMouseMotionListener(panel.getMouseMotionListeners()[0]);
         aCamPanel.setComponentPopupMenu(pmenuSettingsCam);
         panel.add(aCamPanel);
//         aCamPanel.connect();
         new Thread(aCamPanel).start();
    }

  
    /**
     * Создание панелей просмотра видео
     */
    private void createPanelViewers()
    {
        int sqrtCountViewers =(int) Math.sqrt(countViewers);
        System.out.println(sqrtCountViewers);
        panelCameras.setLayout(new GridLayout(sqrtCountViewers,sqrtCountViewers));
        // удаляем лишнии панели, если новое количество камер меньше предыдушего 
        int countViewersPrev = panelCameras.getComponents().length;
        if (countViewersPrev > countViewers){
            for (Component panel: panelCameras.getComponents()){
                if (panelCameras.getComponentZOrder((JPanel)panel) > countViewers-1){
                    if (((JPanel)panel).getComponentCount()>0) 
                        ((CamPanel)((JPanel)panel).getComponents()[0]).stopShow();
                    panelCameras.remove(panel);
                }
            }
        }else {
            // пробегаемся по уже существующим и меняем размер:
            resizeCamerasViewer(new Dimension(panelCameras.getSize().width/sqrtCountViewers,panelCameras.getSize().height/sqrtCountViewers));
            for(int i=0;i<countViewers-countViewersPrev;i++) {
                
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(1,1));
                panel.setComponentPopupMenu(pmenuSettingsCam);
                Border borderPanel = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
                panel.setBorder(borderPanel);
                //JLabel label = new JLabel(String.valueOf(i));
                //panel.add(label);
                panel.addMouseListener(new java.awt.event.MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        
                    }

                    public void mousePressed(MouseEvent e) {
                        JPanel sourcePanel = ((JPanel)((JPanel)e.getSource()).getParent());
                        // убираем выделение с панелей просмотра камер
                        Border borderError = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
                        for (Component iPanel: sourcePanel.getComponents())
                            ((JPanel)iPanel).setBorder(borderError);
                        // выделяем красной рамкой панель просмотра
                        Border borderErr = BorderFactory.createLineBorder(Color.GREEN);
                        ((JPanel)e.getSource()).setBorder(borderErr);
                    }

                    public void mouseReleased(MouseEvent e) {
                       // если перемещаем панель
                       
                             if (_state == State.DRAGGING_START) {
                                 System.out.println("stop drag");
                               Point ptComp = new Point(e.getLocationOnScreen().x-panelCameras.getLocationOnScreen().x, e.getLocationOnScreen().y-panelCameras.getLocationOnScreen().y);
                               Point ptCompStart = new Point(_start.x-panelCameras.getLocationOnScreen().x, _start.y-panelCameras.getLocationOnScreen().y);

                               Component comp = panelCameras.getComponentAt(ptComp);
                               Component compStart = panelCameras.getComponentAt(ptCompStart);
                            // меняем панели местами от панели, на которой опустили и ту, которую передвигаем
                            if (comp!=null && comp.getClass().equals(JPanel.class)
                                         && compStart!=null && compStart.getClass().equals(JPanel.class)) {
                                //panelCameras.getComponents()[panelCameras.getComponentZOrder(_panelDraggedStart)] = comp;
                               // panelCameras.setComponentZOrder(comp, panelCameras.getComponentZOrder(_panelDraggedStart));
                                int i =  panelCameras.getComponentZOrder(compStart);
                                int j = panelCameras.getComponentZOrder(comp);
                                //panelCameras.remove(i);
                                //panelCameras.remove(j);

                                
                                panelCameras.add(compStart,j);
                                //panelCameras.remove(i);
                                panelCameras.add(comp,i);
                                //comp = _panelDraggedStart;
                                
                                panelCameras.updateUI();
System.out.println("label_2: "+i);
System.out.println("label_2: "+j);

                                //System.out.println("label_2: "+((JLabel)((JPanel)panelCameras.getComponent(0)).getComponent(0)).getText());
                                //System.out.println("label_2: "+((JLabel)((JPanel)panelCameras.getComponent(2)).getComponent(0)).getText());
                            }
                           // _panelDraggedStart = (JPanel)comp;

                            

                            _state = State.DRAGGING_STOP;
                        }
                    }

                    public void mouseEntered(MouseEvent e) {
                       // throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void mouseExited(MouseEvent e) {
                       // throw new UnsupportedOperationException("Not supported yet.");
                    }
                });
                panel.addMouseMotionListener(new MouseMotionListener() {

                    public void mouseDragged(MouseEvent e) {
                        if (_state == State.DRAGGING_STOP) {
                            _start = e.getLocationOnScreen();
                            _state = State.DRAGGING_START;
                        }
                    }

                    public void mouseMoved(MouseEvent e) {
                    }
                });
                panelCameras.add(panel);
            }
        }
        // перерисовка
        panelCameras.revalidate();
        
    }

    /**
     * Пользователь сщелкнул на кнопку с выбором количества просмотров
     */
    private void menuOpenFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenFileActionPerformed
        JFileChooser openFileChooser = new JFileChooser();
        // устанавливаем текущий каталог
        openFileChooser.setCurrentDirectory(new File("."));
        // устанавлием фильтр файлов
        openFileChooser.setFileFilter(new FileNameExtensionFilter("Video files", "h264","mjpeg", "avi", "flv"));
        int result = openFileChooser.showOpenDialog(this);
        String path="";
        if (result == JFileChooser.APPROVE_OPTION)
            path = openFileChooser.getSelectedFile().getPath();
       // if (!path.isEmpty())
         //   showcam((JPanel)((JPopupMenu)((JMenuItem)evt.getSource()).getParent()).getInvoker(), new CameraIP("file with comp", "", "", CameraIP.Format.H264,ICodec.ID.CODEC_ID_MJPEGB, 352, 240));
    }//GEN-LAST:event_menuOpenFileActionPerformed

    /**
     * Получение камеры по имени
     */
    private CameraIP getCamera(String nameCamera) {
        CameraIP camSelect = null;
        for (CameraIP iCam:cameras) {
            if (iCam.getNameCam().equals(nameCamera)) {
                camSelect = iCam;
                break;
            }
        }
        return camSelect;
    }

    /**
     * Просмотр или изменение настроек текущей камеры
     * @param evt
     */
    private void menuItemEditCameraEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemEditCameraEditActionPerformed
        // получаем имя выделенной камеры
        String selectCamName = listCameras.getSelectedValue().toString();
        // находим камеру с этим именим
        CameraIP camSelect = getCamera(selectCamName);
        if (camSelect!=null) {
            EditCameraDialog editDialog = new EditCameraDialog(this, true, camSelect);
            editDialog.setVisible(true);
        }
    }//GEN-LAST:event_menuItemEditCameraEditActionPerformed

    /**
     * Удаление выделенной камеры
     * @param evt
     */
    private void menuItemEditCameraDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemEditCameraDeleteActionPerformed
        int selectOptionPane = JOptionPane.showConfirmDialog(
            this,
            "Delete select camera?",
            "Delete",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (selectOptionPane == JOptionPane.OK_OPTION) {
            // получаем имя выделенной камеры
            String selectCamName = listCameras.getSelectedValue().toString();
            // находим камеру с этим именим
            CameraIP camSelect = getCamera(selectCamName);
            if (camSelect!=null) {
                cameras.remove(camSelect);
                updateListCameras();
            }
        }
    }//GEN-LAST:event_menuItemEditCameraDeleteActionPerformed

    /**
     * Обновление списка камер
     * @param evt
     */
    private void menuItemEditUpdateCamerasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemEditUpdateCamerasActionPerformed
        updateListCameras();
    }//GEN-LAST:event_menuItemEditUpdateCamerasActionPerformed
    
    /**
     * Перемещение между панелями 
     * @param evt
     */
    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_formKeyPressed

    private void formAncestorResized(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_formAncestorResized
      
    }//GEN-LAST:event_formAncestorResized


    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized

        //System.out.println(panelCameras.getSize());
        //System.out.println("component count =" +panelCameras.getComponentCount());
        int count = (int )Math.sqrt(panelCameras.getComponentCount());
        if (count!=0)resizeCamerasViewer(new Dimension(panelCameras.getSize().width/count,panelCameras.getSize().height/count));
    //    repaint();
    }//GEN-LAST:event_formComponentResized

    private void listCamerasComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_listCamerasComponentResized
        System.out.println(this.getSize());
        formComponentResized(evt);
    }//GEN-LAST:event_listCamerasComponentResized

    private void jMenuItemSavePicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSavePicActionPerformed
        JPopupMenu pop = (JPopupMenu)((JMenuItem)evt.getSource()).getParent();
        JMenu menu = (JMenu)pop.getInvoker();
        JPanel panel = (JPanel) (((JPopupMenu)menu.getParent()).getInvoker());
        if (panel!=null && panel.getClass().equals(CamPanel.class)) {
           ((CamPanel)panel).createShortPic();
        }
    }//GEN-LAST:event_jMenuItemSavePicActionPerformed

    private void jMenuItemSaveStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveStartActionPerformed
        JPopupMenu pop = (JPopupMenu)((JMenuItem)evt.getSource()).getParent();
        JMenu menu = (JMenu)pop.getInvoker();
        JPopupMenu popSave = menu.getPopupMenu();
        JMenu menuSave = (JMenu)popSave.getInvoker();
JPopupMenu popSave2 = menuSave.getPopupMenu();
JMenu menuSave3 = (JMenu)popSave2.getInvoker();
        JPanel panel = (JPanel) ((menuSave3.getPopupMenu()).getInvoker());
        if (panel!=null && panel.getClass().equals(CamPanel.class)) {
           ((CamPanel)panel).startWriteStreamVideo();
        }
    }//GEN-LAST:event_jMenuItemSaveStartActionPerformed

    private void jMenuItemSaveStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveStopActionPerformed
        JPopupMenu pop = (JPopupMenu)((JMenuItem)evt.getSource()).getParent();
        JMenu menu = (JMenu)pop.getInvoker();
        JPanel panel = (JPanel) (((JPopupMenu)menu.getParent()).getInvoker());
        if (panel!=null && panel.getClass().equals(CamPanel.class)) {
           ((CamPanel)panel).stopWriteStreamVideo();
        }
    }//GEN-LAST:event_jMenuItemSaveStopActionPerformed
    /**
     * Меняем все размеры просмоторщиков
     *
     */
    private void resizeCamerasViewer(Dimension size) {


            for(Component panel: panelCameras.getComponents()) {
                if (panel.getClass().equals(JPanel.class) && ((JPanel)panel).getComponentCount()>0 )
                for(Component panel2: ((JPanel)panel).getComponents()) {
                    ((CamPanel)panel2).resizeWindowPlayer(((JPanel)panel).getSize());
                    System.out.println(((JPanel)panel).getSize());
                }
            }
        
    }
    /**
     * Получение файла конфигурации
     * @return файл конфигурации
     */
    public LoadCamConfig getConfigCameras() {
        return configCameras;
    }

    /**
     * Обработчик события выбора контекстного меню "Установить камеру"
     */
    private void itemSetCamActionPerformed(java.awt.event.ActionEvent evt) {
      JPopupMenu pop = (JPopupMenu)((JMenuItem)evt.getSource()).getParent();
      JMenu menu = (JMenu)pop.getInvoker();
      JPanel panel = (JPanel) (((JPopupMenu)menu.getParent()).getInvoker());
      // номер камеры в списке контекстного меню
      int iComponentIndex = pop.getComponentIndex(((JMenuItem)evt.getSource()));
      // выбираем камеру и отрисовываем ее на панели
      if (iComponentIndex>=0) showcam(panel,cameras.get(iComponentIndex));

    }

    private static void initLookAndFeel() {
        String lookAndFeel = null;

        if (LOOKANDFEEL != null) {
            if (LOOKANDFEEL.equals("Metal")) {
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
              //  an alternative way to set the Metal L&F is to replace the
              // previous line with:
              // lookAndFeel = "javax.swing.plaf.metal.MetalLookAndFeel";

            }

            else if (LOOKANDFEEL.equals("System")) {
                lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            }

            else if (LOOKANDFEEL.equals("Motif")) {
                lookAndFeel = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            }

            else if (LOOKANDFEEL.equals("GTK")) {
                lookAndFeel = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            }
             else if (LOOKANDFEEL.equals("NIMROD")) {
                lookAndFeel = "com.nilo.plaf.nimrod.NimRODLookAndFeel";
            }
            else if (LOOKANDFEEL.equals("SQUARENESS")) {
                lookAndFeel = "net.beeger.squareness.SquarenessLookAndFeel";
            }
            else {
                System.err.println("Unexpected value of LOOKANDFEEL specified: "
                                   + LOOKANDFEEL);
                lookAndFeel = UIManager.getCrossPlatformLookAndFeelClassName();
            }

            try {
                

                UIManager.setLookAndFeel(lookAndFeel);

            //    MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
/*
                // If L&F = "Metal", set the theme

                if (LOOKANDFEEL.equals("Metal")) {
                  if (THEME.equals("DefaultMetal"))
                     MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                  else if (THEME.equals("Ocean"))
                     MetalLookAndFeel.setCurrentTheme(new OceanTheme());
                  
                  UIManager.setLookAndFeel(new MetalLookAndFeel());
                }
 * 
 */




            }

            catch (ClassNotFoundException e) {
                System.err.println("Couldn't find class for specified look and feel:"
                                   + lookAndFeel);
                System.err.println("Did you include the L&F library in the class path?");
                System.err.println("Using the default look and feel.");
            }

            catch (UnsupportedLookAndFeelException e) {
                System.err.println("Can't use the specified look and feel ("
                                   + lookAndFeel
                                   + ") on this platform.");
                System.err.println("Using the default look and feel.");
            }

            catch (Exception e) {
                System.err.println("Couldn't get specified look and feel ("
                                   + lookAndFeel
                                   + "), for some reason.");
                System.err.println("Using the default look and feel.");
                e.printStackTrace();
            }
        }
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                   initLookAndFeel();

        //Make sure we have nice window decorations.


                    new CamViewer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu About;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList jList1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItemSavePic;
    private javax.swing.JMenuItem jMenuItemSaveStart;
    private javax.swing.JMenuItem jMenuItemSaveStop;
    private javax.swing.JMenu jMenuSaveStream;
    private javax.swing.JMenu jMenuSaveVideoStream;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBarCam;
    private javax.swing.JList listCameras;
    private javax.swing.JMenuItem menuItemEditCameraDelete;
    private javax.swing.JMenuItem menuItemEditCameraEdit;
    private javax.swing.JMenuItem menuItemEditUpdateCameras;
    private javax.swing.JMenuItem menuOpenFile;
    private javax.swing.JMenu menuSetCam;
    private javax.swing.JMenuItem menuViewersOne;
    private javax.swing.JPanel panelCameras;
    private javax.swing.JPanel panelListCameras;
    private javax.swing.JPanel panelSplitMain;
    private javax.swing.JPopupMenu pmenuSettingsCam;
    private javax.swing.JPopupMenu popupMenuViewers;
    // End of variables declaration//GEN-END:variables

    // Коллекция с камерами
    private ArrayList<CameraIP> cameras;
    private NewCameraDialog aNewCameraDialog = null;
    // Количество видов камеры на панели
    private Integer countViewers = 1;
    // файлы конфигурации
    private LoadCamConfig configCameras = null;
    //"System", "Motif","Metal"
    // and "GTK"

    final static String LOOKANDFEEL = "NIMROD";
   //final static String LOOKANDFEEL = "SQUARENESS";
    // If you choose the Metal L&F, you can also choose a theme.
    // Specify the theme to use by defining the THEME constant
    // Valid values are: "DefaultMetal", "Ocean",  and "Test"
    final static String THEME = "Ocean";

    private int nSelectPanel = 0;
    private enum  State { DRAGGING_STOP, DRAGGING_START, DRAGGING_MOVE }
    private State _state = State.DRAGGING_STOP;
   
    private Point _start = null;

}
