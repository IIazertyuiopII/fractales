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
public class FractalViewer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        ViewWindow window = new ViewWindow(1600,800,0,0,-0.8,0.156);
        //ViewWindow window = new ViewWindow(1600,800,-1,0);
        window.draw();
    }
    
}
