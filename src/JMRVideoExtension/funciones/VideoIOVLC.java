package JMRVideoExtension.funciones;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


/**
 *
 * @author David Martinez Domingo
 */


public class VideoIOVLC {
     
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);            // Cargar la biblioteca nativa de OpenCV
    }
    
    
    /**
     * Lee un video, lo divide en frames, y los guarda en una colección
     * @param video video seleccionado
     * @return colección de frames del video
     */
    public static KeyFrameCollection leer_video (File video) {
        
        KeyFrameCollection collect = new KeyFrameCollection();
        String ruta_video = video.getAbsolutePath();
        
        VideoCapture videoCapture = new VideoCapture(ruta_video);                                      
        if (!videoCapture.isOpened()) {
            System.out.println("Error al abrir el video.");
        }
        
        Mat frame = new Mat();                                                                            
        while (videoCapture.read(frame)) {                                                           
            BufferedImage img = conversion_Mat2BufImg(frame);
            add_Imagen(img, collect);
        }   
        videoCapture.release();  
        
        return collect;
    }
   

    /**
     * Lee un video, lo divide en frames, y los guarda en una carpeta
     * @param video video seleccionado
     */
    public static void leer_video_guardar (File video){
        
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int resp = dlg.showOpenDialog(null);
        
        if (resp == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = dlg.getSelectedFile();
            String ruta_carpeta = selectedFolder.getAbsolutePath() + "/" + video.getName() + "_Frames/";

            // Crear la carpeta si no existe
            File carpeta = new File(ruta_carpeta);
            if (!carpeta.exists()) {
                carpeta.mkdir();
            }
        
            VideoCapture videoCapture = new VideoCapture(video.getAbsolutePath());                                 

            if (!videoCapture.isOpened()) {
                System.out.println("Error al abrir el video.");
            }
            int frameNumber = 0;
            Mat frame = new Mat();                                                                             
            while (videoCapture.read(frame)) {                                                            
                String outputPath = ruta_carpeta + "/frame_" + frameNumber + ".png"; 
                Imgcodecs.imwrite(outputPath, frame);

                frameNumber++;
            }   
            videoCapture.release();   
        }
    }
    
 
    /**
     * Lee imagenes de un directorio
     * @param co conjunto de imagenes
     * @return colección donde se guardan
     */
    public static KeyFrameCollection leer_carpeta (File... co) {
        
        KeyFrameCollection collect = new KeyFrameCollection();
        
        for (File c : co) {
            add_Item(c, collect);
        }   
        return collect;
    } 
    
    
    /**
     * Se le pasan unos frames y los guarda en una carpeta
     * @param Dic_Key lista de fotogramas
     * @param video video seleccionado
     */
    public static void guardar_KeyFrames(LinkedHashMap<Integer, Mat> Dic_Key, File video){
        
        JFileChooser dlg = new JFileChooser();
        dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int resp = dlg.showOpenDialog(null);
        
        if (resp == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = dlg.getSelectedFile();
            String ruta_carpeta = selectedFolder.getAbsolutePath() + "/" + video.getName() + "_KeyFrames/";

            // Crear la carpeta si no existe
            File carpeta = new File(ruta_carpeta);
            if (!carpeta.exists()) {
                carpeta.mkdir();
            }

            for (Integer clave : Dic_Key.keySet()) {
                String frameNumStr = Integer.toString(clave);
                String fileName = ruta_carpeta + "frame" + frameNumStr + ".png";
                Imgcodecs.imwrite(fileName, Dic_Key.get(clave));
            }  
        }
    }  
   
    
    /**
     * Añadir una nueva imagen a una coleccion
     * @param img imagen a añadir
     * @param coll colección a la que se añadirá
     */
    public static void add_Imagen(BufferedImage img, KeyFrameCollection coll) {
        if(img!=null){
            coll.add(img);
        }
    }
     
      
    /**
     * Añadir un nuevo file a una coleccion
     * @param file archivo a añadir
     * @param coll coleccion a la que se añadirá
     */
    private static void add_Item(File file, KeyFrameCollection coll) {
        if (!file.isDirectory()) { 
            try {  
                BufferedImage img = ImageIO.read(file);
                if(img!=null) coll.add(img);
            } catch (IOException ex) {
                System.err.println("Can't read input file ("+file+")");
            }
        } else {
            File[] fList = file.listFiles();
            for (File f : fList) {
                add_Item(f, coll);
            }
        }
    }
    
    
    /**
     * Extraer los datos dentro de Mat para obtener su imagen asociada
     * @param mat Mat que contiene los datos de la imagen
     * @return imagen obtenida
     */
    public static BufferedImage conversion_Mat2BufImg (Mat mat){
        
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            Mat matConverted = new Mat();
            Imgproc.cvtColor(mat, matConverted, Imgproc.COLOR_BGR2RGB);
            mat = matConverted;
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        byte[] data = new byte[mat.rows() * mat.cols() * (int) mat.elemSize()];
        mat.get(0, 0, data);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        return image;
    }

    

  
}
