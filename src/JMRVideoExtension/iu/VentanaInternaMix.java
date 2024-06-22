package JMRVideoExtension.iu;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import static JMRVideoExtension.funciones.KeyFrameCollection.redimensionar_Imagen;
import java.awt.BorderLayout;


/**
 *
 * @author David Martinez Domingo
 */


public class VentanaInternaMix extends javax.swing.JInternalFrame {
    
    /**
     * Lista de videos de la primera fila de la ventana
     */
    private final List<File> videoFiles;
    
    /**
     * Lista de imagenes de los videos mas similares de la BBDD
     */
    private final List<BufferedImage> images1;
    
    /**
     * Lista de imagenes del video seleccionado
     */
    private final List<BufferedImage> images2;

    /**
     * Constructor de la ventana
     * @param videoFiles Lista de videos de la primera fila de la ventan
     * @param images1 Lista de imagenes de los videos mas similares de la BBDD
     * @param images2 Lista de imagenes del video seleccionado
     */
    public VentanaInternaMix(List<File> videoFiles, List<BufferedImage> images1, List<BufferedImage> images2) {
        this.videoFiles = videoFiles;
        this.images1 = images1;
        this.images2 = images2;
        initComponents();
        initFrame();     
    }
    
    

    /**
     * Inicializacion y creacion de la ventana
     */
    private void initFrame() {
    
        JPanel videoGridPanel = new JPanel(new GridLayout(3, videoFiles.size(),2,2));
     
        for (File videoFile : videoFiles) {                                         //Añadir fila de videos
            LienzoMix videoPanel = new LienzoMix(videoFile, videoNameLabel); 
            videoGridPanel.add(videoPanel);     
        }


        //Añadir fila 1 de imagenes
        /*JLabel label1 = new JLabel("KeyFrames más similares");
        label1.setFont(new Font("Arial", Font.BOLD, 14));
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        label1.setVerticalAlignment(SwingConstants.CENTER);*/
        
        
        
        for (int i=0; i<=images1.size(); i++) {
            JPanel imagePanel = new JPanel();
            //if(i==0) imagePanel.add(label1);
            if(i>0){
                BufferedImage im = redimensionar_Imagen(images1.get(i-1), 220,220);
                JLabel imageLabel = new JLabel(new ImageIcon(im));
                imagePanel.add(imageLabel);
            }
            videoGridPanel.add(imagePanel);
        }
        
        
        //Añadir fila 2 de imagenes
        /*JLabel label2 = new JLabel("KeyFrames Video Original");
        label2.setFont(new Font("Arial", Font.BOLD, 14));
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        label2.setVerticalAlignment(SwingConstants.CENTER);*/
        
        for (int i=0; i<=images2.size(); i++) {
            JPanel imagePanel = new JPanel();
            //if(i==0) imagePanel.add(label2);
            if(i>0){
                BufferedImage im = redimensionar_Imagen(images2.get(i-1), 220,220);
                JLabel imageLabel = new JLabel(new ImageIcon(im));
                imagePanel.add(imageLabel);
            }
            videoGridPanel.add(imagePanel);
        }
          
        getContentPane().add(videoGridPanel);
        pack();
        setVisible(true);
          
        JScrollPane scrollPane = new JScrollPane(videoGridPanel);
        getContentPane().add(scrollPane);
    }
    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        videoNameLabel = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        videoNameLabel.setText("Video: ");
        jPanel1.add(videoNameLabel);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel videoNameLabel;
    // End of variables declaration//GEN-END:variables
}
