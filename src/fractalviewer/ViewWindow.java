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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class ViewWindow extends JFrame implements KeyListener{
    
    private JPanel panel;
    private BufferedImage canvas;
    private FractalParametersObject p;
    private Thread.UncaughtExceptionHandler eHandler;
    private ExecutorService threadPool;
            
    public ViewWindow(int width, int height, double x0, double y0) {
        this(width,height,x0,y0,0,0);
        p.type=0;
    }
    
    public ViewWindow(int width, int height, double x0, double y0, double Pr, double Pi) {
        
        threadPool = Executors.newFixedThreadPool(4);
        eHandler = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
            th.interrupt();
                System.out.println("finini");
            repaint();
        }
        };
        p = new FractalParametersObject();
        p.nIter=255;
        p.zoomFactor=1.5;
        p.zoomLevel=1;
        p.type=1;
        p.drawStyle=0;
        p.x0=x0;
        p.y0=y0;
        p.Pr=Pr;
        p.Pi=Pi;
        p.frameH=2;
        p.frameL=2*width/height;
        
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
                double newx0 = p.x0-p.frameL/2+(double)(e.getPoint().x)/canvas.getWidth()*p.frameL;
                double newy0 = p.y0-p.frameH/2+(double)(e.getPoint().y)/canvas.getHeight()*p.frameH;
                p.x0=newx0;
                p.y0=newy0;
                if(e.getButton()==MouseEvent.BUTTON1){
                    zoomFac = p.zoomFactor;
                }else{
                    zoomFac = 1/p.zoomFactor;
                }
                
                p.zoomLevel = p.zoomLevel*zoomFac;
                p.frameH=p.frameH/zoomFac;
                p.frameL=p.frameL/zoomFac;
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

        for(int i=0; i < 4 ; i++){
            
            int s_x=i*canvas.getWidth()/4;
            int e_x=(i+1)*canvas.getWidth()/4;
            int s_y=i*canvas.getHeight()/4;
            int e_y=(i+1)*canvas.getHeight()/4;
            
            Thread currentThread = new Thread(new FractalCalculator(s_x,e_x,s_y,e_y,canvas,p));
            threadPool.submit(currentThread);
        } 
        
        if(p.type==1){
            setTitle("Ensemble de Julia : Pr = "+rdn(p.Pr,5)+",Pi = "+rdn(p.Pi,5)+",zoom = "+rdn(p.zoomLevel,1)+",centré sur ("+rdn(p.x0,5)+","+rdn(p.y0,5)+")");
        }else{
            setTitle("Ensemble de Mandelbrot : itérations = "+p.nIter+", zoom = "+rdn(p.zoomLevel,1)+",centré sur ("+rdn(p.x0,5)+","+rdn(p.y0,5)+")");
        }
    }
    
    public double rdn(double n,int r) {
        return Math.round(Math.pow(10,r)*n)/Math.pow(10, r);
    }
   

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        //System.out.println(ke.getKeyCode());
        if(ke.getKeyCode()==68){
            p.drawStyle = 1 - p.drawStyle;
            draw();
        }
        
        if(p.type==1){
            switch (ke.getKeyCode()){
                case 38:
                    p.Pi=p.Pi+0.02<=1?p.Pi+0.02:p.Pi;
                    break;
                case 40:
                    p.Pi=p.Pi-0.02>=-1?p.Pi-0.02:p.Pi;
                    break;
                case 37:
                    p.Pr=p.Pr+0.02<=1?p.Pr+0.02:p.Pr;
                    break;
                case 39:
                    p.Pr=p.Pr-0.02>=-1?p.Pr-0.02:p.Pr;
                    break;
            }
        draw();            
        }
        if(p.type==0){
            switch (ke.getKeyCode()){
                case 38:
                    p.nIter=p.nIter+50;
                    break;
                case 40:
                    p.nIter=p.nIter-50>0?p.nIter-50:1;
                    break;
            }
        draw();            
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
}
