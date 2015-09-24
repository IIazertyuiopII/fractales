/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fractalviewer;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author IAZERTYUIOPI
 */
public class FractalCalculator implements Runnable{

    private int startX;
    private int endX;
    private int startY;
    private int endY;
    private BufferedImage canvas;
    private FractalParametersObject p;
    
    @Override
    public void run() {
        
        if(p.type==1){ 
            double x;
            double y;
            double tmp;
            int couleur;
            
            for(int i=startX; i<endX; i++){
                for(int j=startY; j<endY;j++){
                    x = p.x0 - 0.5*p.frameL + p.frameL*((double)i)/canvas.getWidth();
                    y = p.y0 - 0.5*p.frameH + p.frameH*((double)j)/canvas.getHeight();
                    couleur=0;
                                        
                    do{
                        tmp=p.Pr+x*x-y*y;
                        y=p.Pi+2*x*y;
                        x=tmp;
                        couleur++;
                    }while(Math.sqrt(x*x+y*y)<=2 && couleur<3*p.nIter);
                    canvas.setRGB(i, j, getCouleurRGB(couleur));
                }
            }
            throw new RuntimeException();
        }
        if(p.type==0){
            double mandelPr;
            double mandelPi;
            int couleur;
            double x;
            double y;
            double tmp;

            for(int i=startX; i<endX; i++){
                for(int j=startY; j<endY; j++){

                    mandelPr = p.x0 - 0.5*p.frameL + p.frameL*((double)i)/canvas.getWidth();
                    mandelPi = p.y0 - 0.5*p.frameH + p.frameH*((double)j)/canvas.getHeight();
                    couleur=0;
                    x = 0;
                    y = 0;
                    do{
                        tmp=mandelPr+x*x-y*y;
                        y=mandelPi+2*x*y;
                        x=tmp;
                        couleur++;
                    }while(Math.sqrt(x*x+y*y)<=2 && couleur<p.nIter);
                    canvas.setRGB(i, j, getCouleurRGB(couleur));
                }
            }
            throw new RuntimeException();
        }
        
    }
    
    public FractalCalculator(int startX,int endX,int startY,int endY, BufferedImage canvas,FractalParametersObject p){
        
        this.startX=startX;
        this.startY=startY;
        this.endX=endX;
        this.endY=endY;
        this.canvas=canvas;
        this.p=p;    
    
    }
    
    public int getCouleurRGB(int couleur) {
        couleur = (int)Math.round(couleur*(double)255/p.nIter);
        double zoomedCouleur = couleur*Math.abs(Math.round((1+Math.log10(p.zoomLevel))));
        int rouge,vert,bleu;
        if(p.drawStyle==0){
            rouge = p.type==0?scaleColor(zoomedCouleur,0.05,0.6):scaleColor(zoomedCouleur,0.04,0.7);
            vert = p.type==0?scaleColor(zoomedCouleur,1.2*0.05,0.6):scaleColor(zoomedCouleur,1.2*0.04,0.7);
            bleu = p.type==0?scaleColor(zoomedCouleur,1.4*0.05,0.6):scaleColor(zoomedCouleur,1.4*0.04,0.7);
        }
        else{
            rouge = (int)Math.round(8*zoomedCouleur)%255;
            vert = 90+(int)Math.round(9*zoomedCouleur)%255/2;
            bleu = 90+(int)Math.round(10*zoomedCouleur)%255/2;    
        }
        return (new Color(rouge,vert,bleu)).getRGB();  
    };
    
    public int scaleColor(double c,double f,double e) {
        return (int)Math.abs(Math.round(255*Math.cos(1*f*Math.pow(c,e))));
    }
    
}
