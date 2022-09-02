/*
 * ZoomImagePanel.java
 *
 * Created on 07.10.2011, 9:54:32
 *
 */

package com.shkip.ipcam;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.JToolBar;
import javax.swing.border.Border;

/**
 *
 * @author ybakova Ekaterina Olegovna
 */
public class ZoomImagePanel extends javax.swing.JPanel implements MouseListener,MouseMotionListener, KeyListener{

    /** Класс JPanel, на котором рисуется изображение и которых находится на scroll pane. 
     */
    public class DrawingPane extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(imageNew, topPtr.x,topPtr.y, this);
        }
    }
   
    /** 
     * Конструктор класса 
     * @param img изображение, к которому будет применять зум
     */
    public ZoomImagePanel(Image img) {
        initComponents();
        //super(new BorderLayout());

        loadZoomCursor("./icon/zoom_in.png", "./icon/zoom_out.png");
        Dimension sizeNew = getMinSizeImage(Toolkit.getDefaultToolkit().getScreenSize(), new Dimension(img.getWidth(this), img.getHeight(this)));

        topPtr = CamPanel.calcTopImagePoint(Toolkit.getDefaultToolkit().getScreenSize(),sizeNew);
        image = imageNew = img.getScaledInstance((int)(sizeNew.width), (int)(sizeNew.height), Image.SCALE_DEFAULT);
        initializeComponent();
    }
    
    /**
     * Обработка событий клавиатуры
     * Если нажата клавиша Ctrl - то устанавливаем курсор - "увеличиение масштаба" 
     */
    public void keyPressed(KeyEvent e) {
        if (e.isControlDown()) drawingPane.setCursor(cursorZoomOut);
    }
    public void keyReleased(KeyEvent e) {
        drawingPane.setCursor(cursorZoomIn);
    }
    public void keyTyped(KeyEvent e) {}
    
    /**
     * Обработка событий мыши
     * Изменение масштаба изображения
     */
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == 1) {
            firstPoint = e.getPoint();
            if (e.isControlDown()) scaleH=1/1.2;
            else scaleH=1.2;
            // ограничиваем количество увеличения и уменьшения изображения 
            if (imageNew.getWidth(this)*scaleH<4000 && imageNew.getHeight(this)*scaleH<4000
                    && imageNew.getWidth(this)*scaleH>100 && imageNew.getHeight(this)*scaleH>100)
                calcNewSize();
        }
    }
    public void mouseMoved(MouseEvent e) {
        if (e.getY()<40) jToolBar1.setVisible(true);
        else jToolBar1.setVisible(false);
        System.out.println("ka");
    }
    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited (MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}

    private void initializeComponent() {
        frame = new JFrame();
        // Форма без рамки
        frame.setUndecorated(true);
        frame.addKeyListener(this);
        
        // создаем drawingPane  
        drawingPane = new DrawingPane();
        drawingPane.addMouseMotionListener(this);
        drawingPane.addMouseListener(this);
        drawingPane.setLayout(new BorderLayout());
        drawingPane.setCursor(cursorZoomIn);
        //добавляем drawingPane в ScrollPane
        scroller = new JScrollPane(drawingPane);
        scroller.setPreferredSize(new Dimension(400,500));
setLayout(null);
        // вычисляем величину ScrollPane
        newScrool();
        this.setOpaque(true); //content panes must be opaque
        frame.setContentPane(this);
        
        
        add(jToolBar1);
        add(scroller);
        //frame.getContentPane().add(jToolBarClose);
        //Display the window
        frame.pack();
        // fullscreen

        frame.setBounds(getBoundsFullScreen());
        if(getGraphicsConfiguration().getDevice().isFullScreenSupported())
            getGraphicsConfiguration().getDevice().setFullScreenWindow(frame);
        
jToolBar1.setBounds(frame.getWidth()-90,0, 70, 60);

scroller.setBounds(0,0,frame.getWidth(),frame.getHeight());
        frame.setVisible(true);
    }



    private Dimension getMinSizeImage(Dimension sizeWin, Dimension sizeImg){
        Dimension sizeMin = new Dimension(1,1);
        double k = sizeImg.width / sizeImg.height;
        double k2 = sizeWin.width / sizeWin.height;
        if (k2<k) {
            float scaleW = (float)sizeWin.width*1.0f/sizeImg.width;
             sizeMin.width = sizeWin.width;
             sizeMin.height= (int) (sizeImg.height*scaleW);
        } else {
            float scaleH = (float)sizeWin.height*1.0f/sizeImg.height;
            sizeMin.height = sizeWin.height;
             sizeMin.width= (int) (sizeImg.width*scaleH);
        }
        return sizeMin;
    }
    private Rectangle getBoundsFullScreen() {
        Dimension sizeWindow = Toolkit.getDefaultToolkit().getScreenSize();
        int fsW = sizeWindow.width;
        int fsH = sizeWindow.height;
        Insets inset = getInsets();
        fsH -= inset.bottom;
        fsW += (fsW % 2);
        fsH += (fsH % 2);
        return new Rectangle(0,0,fsH, fsH);
    }
    /**
     * Загрузаем иконки масштаба 
     * @param pathZoomPlus - адрес иконки "Увеличение машстаба"
     * @param pathZoomMinus - адрес иконки "Уменьшение масштаба"
     */
    private void loadZoomCursor(String pathZoomPlus,String pathZoomMinus) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        cursorZoomIn = toolkit.createCustomCursor(toolkit.getImage(pathZoomPlus), new Point(0,0), "Cursor");
        cursorZoomOut = toolkit.createCustomCursor(toolkit.getImage(pathZoomMinus), new Point(0,0), "Cursor");
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
        btnClose = new javax.swing.JButton();

        jToolBar1.setBorder(null);
        jToolBar1.setRollover(true);
        jToolBar1.setAutoscrolls(true);
        jToolBar1.setMaximumSize(new java.awt.Dimension(280, 39));
        jToolBar1.setMinimumSize(new java.awt.Dimension(280, 32));
        jToolBar1.setOpaque(false);

        btnClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/shkip/ipcam/resourse/icon/toolBarPanelCamera/close.png"))); // NOI18N
        btnClose.setFocusable(false);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setOpaque(false);
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });
        jToolBar1.add(btnClose);

        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        frame.setVisible(false);
    }//GEN-LAST:event_btnCloseActionPerformed

    
    public void setImage(BufferedImage imgBuff) {
        
        image = imageNew = imgBuff.getScaledInstance(imageNew.getWidth(this), imageNew.getHeight(this), Image.SCALE_DEFAULT);
        repaint();
    }


    private Dimension calcNewSize() {
        Dimension sizeNew = new Dimension((int)(imageNew.getWidth(this) * scaleH), (int)(imageNew.getHeight(this)* scaleH));
        imageNew = image.getScaledInstance((int)(sizeNew.width), (int)(sizeNew.height), Image.SCALE_DEFAULT);
         newScrool();
         
        Point ptr = new Point(scroller.getViewport().getWidth(),scroller.getViewport().getHeight());
        scroller.getViewport().setViewPosition(new Point((int)(firstPoint.x*scaleH-ptr.x/2), (int)(firstPoint.y *scaleH-ptr.y/2)));
        return sizeNew;
    }
    private void newScrool() {
        Integer W = (int)(imageNew.getWidth(this));
        Integer H = (int)(imageNew.getHeight(this));
        Integer x = this.getX() - W/2;
        Integer y = this.getY() - H/2;
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        topPtr = CamPanel.calcTopImagePoint(Toolkit.getDefaultToolkit().getScreenSize(),new Dimension(W, H));
       
        Rectangle rect = new Rectangle(topPtr.x, topPtr.y, W+topPtr.x, H+topPtr.y);
        drawingPane.scrollRectToVisible(rect);
        //Update client's preferred size because
        //the area taken up by the graphics has
        //gotten larger or smaller (if cleared).
        drawingPane.setPreferredSize(rect.getSize());
        //Let the scroll pane know to update itself
        //and its scrollbars.
        drawingPane.revalidate();
        drawingPane.repaint();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
    private Image image = null;
    private Image imageNew = null;
    private Double scaleH = 1.2;
    private JPanel drawingPane;
    private JScrollPane scroller = null;
    // точка сщелчка
    private Point firstPoint;
    private static JFrame frame;
    private Cursor cursorZoomIn = null;
    private Cursor cursorZoomOut = null;
    private Point topPtr;
}
