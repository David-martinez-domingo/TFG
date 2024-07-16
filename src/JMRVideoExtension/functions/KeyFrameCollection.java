package JMRVideoExtension.functions;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.opencv.core.Mat;
import static JMRVideoExtension.functions.VideoIOVLC.conversionMat2BufImg;
import jmr.video.FrameCollection;

/**
 * Collection of frames from a video
 * 
 * @author David Martinez Domingo
 */

public class KeyFrameCollection extends FrameCollection{
    
     
    public KeyFrameCollection() {
        
    }
    
    
    public KeyFrameCollection(Collection<? extends BufferedImage> c) {
        super(c); 
    }
    
    
    /**
     * Receives a list from Mat, transforms them into images and displays them
     * @param list Mat List
     * @return images list
     */
    public static List<BufferedImage> showFrames(List<Mat> list){
        
        List<BufferedImage> imagesList = new ArrayList<>();
        for (Mat mat : list) {
            BufferedImage image = conversionMat2BufImg(mat);    
            image = resizeImage(image,280,300);
            imagesList.add(image);
        }
        return imagesList;
    }
   
    
   /**
    * Resize the original image
    * @param originalImage image to modify
    * @param targetWidth new width
    * @param targetHeight new height
    * @return image post-resize
    */
    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }
    
}
