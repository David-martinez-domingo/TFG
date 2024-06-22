/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package JMRVideoExtension.funciones;

/**
 *
 * @author redmu
 */
public class NewClass {
    
    
    private Class[] getDBDescriptorClasses(){
        ArrayList<Class> outputL = new ArrayList<>();
        if (this.colorEstructuradoDB.isSelected())
            outputL.add(MPEG7ColorStructure.class);
        if (this.colorEscalableDB.isSelected())
            outputL.add(MPEG7ScalableColor.class);
        if (this.colorMedioDB.isSelected())
            outputL.add(SingleColorDescriptor.class);
        Class output[] = new Class[outputL.size()];
        for(int i=0; i<outputL.size(); i++)
            output[i] = outputL.get(i);
        return output;
    }


    private void botonNewDBActionPerformed(java.awt.event.ActionEvent evt) {
        // Creamos la base de datos vacía
        database = new ListDB(getDBDescriptorClasses());
        // Activamos/desactivamos botones
        setDataBaseButtonStatus(false);
        updateInfoDBStatusBar("New DB (not saved)");
    }

    private void botonCloseDBActionPerformed(java.awt.event.ActionEvent evt) {
        database.clear();
        database = null;
        // Activamos/desactivamos botones
        setDataBaseButtonStatus(true);
        updateInfoDBStatusBar(null);
    }

    private void botonAddRecordDBActionPerformed(java.awt.event.ActionEvent evt) {
        if (database != null) {
            java.awt.Cursor cursor = this.getCursor();
            setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
            //Incorporamos a la BD todas las imágenes del escritorio
            JInternalFrame ventanas[] = escritorio.getAllFrames();
            JMRImageInternalFrame viAnalyzed;
            for (JInternalFrame vi : ventanas) {
                if (vi instanceof JMRImageInternalFrame) {
                    viAnalyzed = (JMRImageInternalFrame) vi;
database.add(viAnalyzed.getImage(),viAnalyzed.getURL());
                }
            }
            setCursor(cursor);
            updateInfoDBStatusBar("Updated DB (not saved)");
        }
    }

    private void botonSearchDBActionPerformed(java.awt.event.ActionEvent evt) {
        if (database != null) {
            BufferedImage img = this.getSelectedImage();
            if (img != null) {
                List<ListDB<BufferedImage>.Record> queryResult = database.query(img, 10);
                ImageListInternalFrame listFrame = new ImageListInternalFrame();
                for(ListDB.Record r: queryResult){
listFrame.add(r.getLocator(),r.getLocator().getFile());
                }
                this.escritorio.add(listFrame);
                listFrame.setVisible(true);
            }
            //Opcion con metadata
            /*if (img != null) {
                ListDB.Record q_record = database.new Record(img);
                List<ResultMetadata> resultList = database.queryMetadata(q_record);
                ImageListInternalFrame listFrame = new ImageListInternalFrame();
                java.net.URL url;
                String label;
                for(ResultMetadata r: resultList){
                    url = ((ListDB.Record)r.getMetadata()).getLocator();
                    label = "Distance: "+r.getResult()+", File:"+url.getFile();
                    listFrame.add(url,label);
                }
                this.escritorio.add(listFrame);
                listFrame.setVisible(true);
            }*/
        }
    }

    private void updateInfoDBStatusBar(String fichero) {
        String infoDB = "Not open";
        if (database != null) {
            infoDB = fichero + " [#" + database.size() + "] [";
            for (Class c : database.getDescriptorClasses()) {
                infoDB += c.getSimpleName() + ",";
            }
            infoDB = infoDB.substring(0, infoDB.length() - 1) + "]";
        }
        this.infoDB.setText(infoDB);
    }

    private void botonOpenDBActionPerformed(java.awt.event.ActionEvent evt) {
        File file = new File("prueba.jmr.db");
        try {
            database = ListDB.open(file);
            setDataBaseButtonStatus(false);
            updateInfoDBStatusBar(file.getName());
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println(ex);
        }
    }

    private void botonSaveDBActionPerformed(java.awt.event.ActionEvent evt) {
        File file = new File("prueba.jmr.db");
        try {
            database.save(file);
            updateInfoDBStatusBar(file.getName());
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
    
    
    
}









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

/**
 * Colección de frames de un video
 * 
 * @author David Martinez Domingo
 */

public class KeyFrameCollection extends ArrayList<BufferedImage> implements Video{
    
    /**
     * Frame rate del video por defecto
     */
    public static final int DEFAULT_FR = 25;
    
    
    public KeyFrameCollection() {
        
    }
     
    public KeyFrameCollection(Collection<? extends BufferedImage> c) {
        this.addAll(c); 
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
    
    
    /**
     * Obtener altura del video
     * @return Altura
     */
    @Override
    public int getHeight() {
        return isEmpty() ? 0 : get(0).getHeight();
    }

    /**
     * Obtener anchura del video
     * @return Anchura
     */
    @Override
    public int getWidth() {
        return isEmpty() ? 0 : get(0).getWidth();
    }

    /**
     * Obtener un frame del video
     * @param indice_frame Indice del frame seleccionado
     * @return Frame seleccionado
     */
    @Override
    public BufferedImage getFrame(int indice_frame) {
        return get(indice_frame);
    }

    /**
     * Obtener numero de frames del video
     * @return Tamaño del video
     */
    @Override
    public int getNumberOfFrames() {
        return size();
    }
    
    /**
     * Obtener frame rate del video
     * @return Frame rate
     */
    @Override
    public int getFrameRate() {
        return DEFAULT_FR;
    }
    
    
     
    
}
