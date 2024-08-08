package JMRVideoExtension.events;

import java.util.EventListener;
import uk.co.caprica.vlcj.player.MediaPlayer;

/**
 *
 * @author David Martinez Domingo
 */


public interface MediaPlayerEventListener extends EventListener{
    
    public void playing(MediaPlayer MediaPlayer);
    
    public void paused(MediaPlayer MediaPlayer);
    
    public void finished(MediaPlayer MediaPlayer);
    
}
