package JMRVideoExtension.functions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import jmr.video.FrameCollection;
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
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);            // Load native OpenCV library
    }
    
    
    /**
     * Reads a video, divides it into frames, and saves them to a collection
     * @param video selected video
     * @return video frame collection
     */
    public static FrameCollection readVideo (File video) {
        
        FrameCollection collect = new FrameCollection();
        String pathVideo = video.getAbsolutePath();
        
        VideoCapture videoCapture = new VideoCapture(pathVideo);                                      
        if (!videoCapture.isOpened()) {
            System.out.println("Error opening video.");
        }
        
        Mat frame = new Mat();                                                                            
        while (videoCapture.read(frame)) {                                                           
            BufferedImage img = conversionMat2BufImg(frame);
            addImage(img, collect);
        }   
        videoCapture.release();  
        
        return collect;
    }
   

    /**
     * Reads a video, divides it into frames, and saves them to a folder
     * @param video selected video
     */
    public static void readSaveVideo (File video){
        
        JFileChooser dlg = new JFileChooser();
        String userDir = System.getProperty("user.dir");
        dlg.setCurrentDirectory(new File(userDir));
        dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int resp = dlg.showOpenDialog(null);
        
        if (resp == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = dlg.getSelectedFile();
            String pathFolder = selectedFolder.getAbsolutePath() + "/" + video.getName() + "_Frames/";

            // Create folder if it does not exists
            File folder = new File(pathFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
        
            VideoCapture videoCapture = new VideoCapture(video.getAbsolutePath());                                 

            if (!videoCapture.isOpened()) {
                System.out.println("Error opening video.");
            }
            int frameNumber = 0;
            Mat frame = new Mat();                                                                             
            while (videoCapture.read(frame)) {                                                            
                String outputPath = pathFolder + "/frame_" + frameNumber + ".png"; 
                Imgcodecs.imwrite(outputPath, frame);

                frameNumber++;
            }   
            videoCapture.release();   
        }
    }
    
 
    /**
     * Read images from a directory
     * @param co collection of files
     * @return colección saved
     */
    public static FrameCollection readFolder (File... co) {
        
        FrameCollection collect = new FrameCollection();
        
        for (File c : co) {
            addItem(c, collect);
        }   
        return collect;
    } 
    
    
    /**
     * Some frames are passed to it and it saves them in a folder
     * @param Dic_Key frame list
     * @param video selected video
     */
    public static void saveKeyFrames(LinkedHashMap<Integer, Mat> Dic_Key, File video){
        
        JFileChooser dlg = new JFileChooser();
        String userDir = System.getProperty("user.dir");
        dlg.setCurrentDirectory(new File(userDir));
        dlg.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int resp = dlg.showOpenDialog(null);
        
        if (resp == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = dlg.getSelectedFile();
            String pathFolder = selectedFolder.getAbsolutePath() + "/" + video.getName() + "_KeyFrames/";

            // Crear la carpeta si no existe
            File folder = new File(pathFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }

            for (Integer key : Dic_Key.keySet()) {
                String frameNumStr = Integer.toString(key);
                String fileName = pathFolder + "frame" + frameNumStr + ".png";
                Imgcodecs.imwrite(fileName, Dic_Key.get(key));
            }  
        }
    }  
   
    
    /**
     * Add a new image to a collection
     * @param img image to add
     * @param coll collection to be added
     */
    public static void addImage(BufferedImage img, FrameCollection coll) {
        
        if(img!=null){
            coll.add(img);
        }
    }
     
      
    /**
     * Add a new file to a collection
     * @param file file to add
     * @param coll collection to be added
     */
    private static void addItem(File file, FrameCollection coll) {
        
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
                addItem(f, coll);
            }
        }
    }
    
    
    /**
     * Extract the data within Mat to obtain its associated image
     * @param mat Mat containing the image data
     * @return image obtained
     */
    public static BufferedImage conversionMat2BufImg (Mat mat){
        
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
