package JMRVideoExtension.funciones;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jmr.video.Video;
import org.opencv.core.Mat;
import static JMRVideoExtension.funciones.VideoIOVLC.conversion_Mat2BufImg;
import jmr.video.FrameCollection;

/**
 * Colección de frames de un video
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
     * Recibe una lista de Mat, las transforma en imagenes y las muestra
     * @param lista Conjunto de Mat
     * @return lista de imagenes
     */
    public static List<BufferedImage> mostrar_Frames (List<Mat> lista){
        
        List<BufferedImage> imagesList = new ArrayList<>();
        for (Mat mat : lista) {
            BufferedImage image = conversion_Mat2BufImg(mat);    
            image = redimensionar_Imagen(image,280,300);
            imagesList.add(image);
        }
        return imagesList;
    }
   
    
   /**
    * Redimensiona la imagen original
    * @param originalImage imagen a modificar
    * @param targetWidth nueva anchura
    * @param targetHeight nueva altura
    * @return imagen obtenida tras la modificacion
    */
    public static BufferedImage redimensionar_Imagen(BufferedImage originalImage, int targetWidth, int targetHeight) {
        
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }
    
    

    
}
