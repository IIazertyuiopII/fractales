/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fractalviewer;

/**
 *
 * @author IAZERTYUIOPI
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class ViewWindow extends JFrame implements KeyListener{
    
    public int nIter = 255;
    private JPanel panel;
    private BufferedImage canvas;
    private double x0;
    private double y0;
    private double Pr;
    private double Pi;
    private double frameL;
    private double frameH;
    private int type;
    public double zoomFactor;
    private double zoomLevel;

    public ViewWindow(int width, int height, double x0, double y0) {
        this(width,height,x0,y0,0,0);
        type=0;
    }
    
    public ViewWindow(int width, int height, double x0, double y0, double Pr, double Pi) {
        
        zoomLevel=1;
        type=1;
        this.x0=x0;
        this.y0=y0;
        this.Pr=Pr;
        this.Pi=Pi;
        this.frameH=2;
        this.frameL=2*width/height;
        
        //fill the Image
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        //define paint method of JPanel
        panel = new JPanel(){public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(canvas, null, null);
        }};
    
        //add Jpanel to view
        add(panel);

        //add mouse listener
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                double zoomFac;
                double newx0 = getX0()-getFrameL()/2+(double)(e.getPoint().x)/getWidth()*getFrameL();
                double newy0 = getY0()-getFrameH()/2+(double)(e.getPoint().y)/getHeight()*getFrameH();
                setX0(newx0);
                setY0(newy0);
                if(e.getButton()==MouseEvent.BUTTON1){
                    zoomFac = zoomFactor;
                }else{
                    zoomFac = 1/zoomFactor;
                }
                
                zoomLevel = zoomLevel*zoomFac;
                setFrameH(getFrameH()/zoomFac);
                setFrameL(getFrameL()/zoomFac);
                draw();
            }
        });
        //init view
        setSize(width,height);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
        addKeyListener(this);
    }
    
    public void draw() {
        if(type==1){
            setTitle("Ensemble de Julia : Pr = "+rdn(Pr,5)+",Pi = "+rdn(Pi,5)+",zoom = "+rdn(zoomLevel,1)+",centré sur ("+rdn(x0,5)+","+rdn(y0,5)+")");
            Julia();
        }else{
            setTitle("Ensemble de Mandelbrot : itérations = "+nIter+", zoom = "+rdn(zoomLevel,1)+",centré sur ("+rdn(x0,5)+","+rdn(y0,5)+")");
            Mandelbrot();
        }
    }
    
    public double rdn(double n,int r) {
        return Math.round(Math.pow(10,r)*n)/Math.pow(10, r);
    }

    public void Julia() {
        
        double x;
        double y;
        double tmp;
        int couleur;
        
        for(int i=0; i<getWidth(); i++){
            for(int j=0; j<getHeight();j++){
                x = x0 - 0.5*frameL + frameL*((double)i)/getWidth();
                y = y0 - 0.5*frameH + frameH*((double)j)/getHeight();
                couleur=0;
                do{
                    tmp=Pr+x*x-y*y;
                    y=Pi+2*x*y;
                    x=tmp;
                    couleur++;
                }while(Math.sqrt(x*x+y*y)<=2 && couleur<3*nIter);
                
                canvas.setRGB(i, j, getCouleurRGB(couleur));
            }
        }
        repaint();
    }
    
    public int getCouleurRGB(int couleur) {
        couleur = (int)Math.round(couleur*(double)255/nIter);
        double zoomedCouleur = couleur*Math.abs(Math.round((1+Math.log10(zoomLevel))));
        int rouge = type==0?scaleColor(zoomedCouleur,0.05,0.6):scaleColor(zoomedCouleur,0.04,0.7);
        int vert = type==0?scaleColor(zoomedCouleur,1.2*0.05,0.6):scaleColor(zoomedCouleur,1.2*0.04,0.7);
        int bleu = type==0?scaleColor(zoomedCouleur,1.4*0.05,0.6):scaleColor(zoomedCouleur,1.4*0.04,0.7);
        return (new Color(rouge,vert,bleu)).getRGB();  
    };
    
    public int scaleColor(double c,double f,double e) {
        return (int)Math.abs(Math.round(255*Math.cos(1*f*Math.pow(c,e))));
    }
   
    public void Mandelbrot() {
     
        double mandelPr;
        double mandelPi;
        int couleur;
        double x;
        double y;
        double tmp;
        
        for(int i=0; i<canvas.getWidth(); i++){
            for(int j=0; j<canvas.getHeight(); j++){
            
                mandelPr = x0 - 0.5*frameL + frameL*((double)i)/getWidth();
                mandelPi = y0 - 0.5*frameH + frameH*((double)j)/getHeight();
                couleur=0;
                x = 0;
                y = 0;
                do{
                    tmp=mandelPr+x*x-y*y;
                    y=mandelPi+2*x*y;
                    x=tmp;
                    couleur++;
                }while(Math.sqrt(x*x+y*y)<=2 && couleur<nIter);
                canvas.setRGB(i, j, getCouleurRGB(couleur));
            }
        }
        repaint();
    }

    /**
     * @return the x0
     */
    public double getX0() {
        return x0;
    }

    /**
     * @param x0 the x0 to set
     */
    public void setX0(double x0) {
        this.x0 = x0;
    }

    /**
     * @return the y0
     */
    public double getY0() {
        return y0;
    }

    /**
     * @param y0 the y0 to set
     */
    public void setY0(double y0) {
        this.y0 = y0;
    }

    /**
     * @return the Pr
     */
    public double getPr() {
        return Pr;
    }

    /**
     * @param Pr the Pr to set
     */
    public void setPr(double Pr) {
        this.Pr = Pr;
    }

    /**
     * @return the Pi
     */
    public double getPi() {
        return Pi;
    }

    /**
     * @param Pi the Pi to set
     */
    public void setPi(double Pi) {
        this.Pi = Pi;
    }

    /**
     * @return the frameL
     */
    public double getFrameL() {
        return frameL;
    }

    /**
     * @param frameL the frameL to set
     */
    public void setFrameL(double frameL) {
        this.frameL = frameL;
    }

    /**
     * @return the frameH
     */
    public double getFrameH() {
        return frameH;
    }

    /**
     * @param frameH the frameH to set
     */
    public void setFrameH(double frameH) {
        this.frameH = frameH;
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if(type==1){
            switch (ke.getKeyCode()){
                case 38:
                    Pi=Pi+0.02<=1?Pi+0.02:Pi;
                    break;
                case 40:
                    Pi=Pi-0.02>=-1?Pi-0.02:Pi;
                    break;
                case 37:
                    Pr=Pr+0.02<=1?Pr+0.02:Pr;
                    break;
                case 39:
                    Pr=Pr-0.02>=-1?Pr-0.02:Pr;
                    break;
            }
        draw();            
        }
        if(type==0){
            switch (ke.getKeyCode()){
                case 38:
                    nIter=nIter+50;
                    break;
                case 40:
                    nIter=nIter-50>0?nIter-50:1;
                    break;
            }
        draw();            
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}
