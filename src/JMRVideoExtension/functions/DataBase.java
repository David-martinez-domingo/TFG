package JMRVideoExtension.functions;

import java.io.File;
import java.io.Serializable;
import jmr.video.KeyFrameDescriptor;


/**
 *
 * @author David Martinez Domingo
 */


public class DataBase implements Serializable {
    
    
    private File file;
    private KeyFrameDescriptor keyf;
    
    
    public DataBase (File f, KeyFrameDescriptor d){
        this.file = f;
        this.keyf = d;
    }
        


    public File getFile() {
        return file;
    }

    public void setFile(File f) {
        this.file = f;
    }

    public KeyFrameDescriptor getKeyFrameDescriptor() {
        return keyf;
    }

    public void setKeyFrameDescriptor(KeyFrameDescriptor d) {
        this.keyf = d;
    }
    
}
