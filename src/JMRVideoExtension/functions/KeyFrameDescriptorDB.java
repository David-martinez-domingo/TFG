/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JMRVideoExtension.functions;

import java.util.List;
import jmr.video.KeyFrameDescriptor;
import jmr.video.Video;

/**
 *
 * @author redmu
 */


public class KeyFrameDescriptorDB extends KeyFrameDescriptor {
    
    private List<int[]> mpeg7ScalableColor;
    
    
   
    public KeyFrameDescriptorDB(List<int[]> mpeg7ScalableColor) {
        super(null); 
        this.mpeg7ScalableColor = mpeg7ScalableColor;
    }
    
    
    
    
    
    
}