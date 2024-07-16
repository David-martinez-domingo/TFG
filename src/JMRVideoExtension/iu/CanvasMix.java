package JMRVideoExtension.iu;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 *
 * @author David Martinez Domingo
 */

public class CanvasMix extends javax.swing.JPanel {

    /**
     * Media player
     */
    private EmbeddedMediaPlayer vlcplayer = null;
    
    /**
     * Component of a media player
     */
    private EmbeddedMediaPlayerComponent mediaPlayerComponent;
    
    /**
     * Video
     */
    private File videoFile;
    
    /**
     * Video name tag
     */
    private JLabel videoNameLabel;

    
    
    public CanvasMix(File videoFile, JLabel videoNameLabel) {
        this.videoFile = videoFile;
        this.videoNameLabel = videoNameLabel;
        initializePlayer();
    }
    
    
    /**
     * Parameter initialization
     */
    private void initializePlayer() {    
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(220, 220);
            }
        };   
        mediaPlayerComponent.setPreferredSize(new Dimension(220, 220));
        vlcplayer = mediaPlayerComponent.getMediaPlayer(); 
        add(mediaPlayerComponent);
        mediaPlayerComponent.getVideoSurface().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (vlcplayer.isPlaying()) {
                    stop(); 
                } else {
                    start(); 
                }
            } 
            @Override
            public void mouseEntered(MouseEvent e) {
                videoNameLabel.setText("Video: " + videoFile.getName()); // Update the video name in the JLabel
            }
        });
        SwingUtilities.invokeLater(() -> start());
    }

    
    
    
    public File getVideo() {
        return videoFile;
    }

    
    public void setVideo(File videoFile) {
        this.videoFile = videoFile;
    }

    
    public EmbeddedMediaPlayer getMediaPlayer() {
        return vlcplayer;
    }
    
    
    /**
     * The video starts playing
     */
    public void start() {
       if (vlcplayer != null) {
            if(vlcplayer.isPlayable()){
                vlcplayer.mute(true);
                vlcplayer.play();
            } else {
                vlcplayer.playMedia(videoFile.getAbsolutePath());
            }
        }
    }

    /**
     * Video playback stops
     */
    public void stop() {
        if (vlcplayer != null) {
            if (vlcplayer.isPlaying()) {
                vlcplayer.pause();
            } else {
                vlcplayer.stop();
            }
        }
    }
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
