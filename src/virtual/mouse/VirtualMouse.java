/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package virtual.mouse;

import imageprocessing.HandDetection_v1_1;

/**
 *
 * @author ghalib
 */
public class VirtualMouse {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //HandDetection_v1_0 detection_v1_0 = new HandDetection_v1_0();
        //detection_v1_0.detect();
        HandDetection_v1_1 detection_v1_1 = new HandDetection_v1_1();
        detection_v1_1.detect();
        //BackgroundProcessor backgroundProcessor = new BackgroundProcessor();
        //backgroundProcessor.doSubstitution();
       
    }
    
}
