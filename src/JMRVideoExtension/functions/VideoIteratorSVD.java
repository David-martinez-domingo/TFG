package JMRVideoExtension.functions;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import jmr.video.Video;
import jmr.video.VideoIterator;
import org.opencv.core.Mat;
import static JMRVideoExtension.functions.VideoSegmentationOp.calculationOfKeyFrames;

/**
 *
 * @author David Martinez Domingo
 */



    /**
     * Class for the optimal iterator when obtaining the video keyframes 
     * with the most appropriate parameters
     */
    public class VideoIteratorSVD implements VideoIterator<BufferedImage>{
     
        /**
         * Object of video segmentation
         */
        VideoSegmentationOp VS = new VideoSegmentationOp();    
        
        /**
         * Dictionary with the keyframes obtained from the segmentation
         */
        LinkedHashMap<Integer, Mat> keyf;
        
        /**
         * List with keyframe numbers
         */
        List<Integer> keyList;
        
        /**
         * Selected video
         */
        Video video;
        
        /**
         * Keyframe List Size
         */
        private int size;
        
        /**
         * Keyframe List Index
         */
        int position;
        
       
        public VideoIteratorSVD(Video video){
            setVideo(video);
        }
        
        public VideoIteratorSVD(Video video, String filePath){
            VS.setFilePath(filePath);
            setVideo(video);           
        }
        

        @Override
        public void setVideo(Video video) {
            this.video = video;                           
            this.position = 0;
            this.keyf = calculationOfKeyFrames(VS.getFilePath(),VS.getK(),VS.getSimilarityThreshold()/ 10.0,VS.getRange());
            this.keyList = new ArrayList<>(keyf.keySet());
            this.size = keyList.size();
        }
   
        @Override
        public Video getVideo() {
            return video;
        }

        @Override
        public void init(){
            position = 0;
        }

        @Override
        public boolean hasNext() {
            return (position < size);
        }

        @Override
        public BufferedImage next() {
            if (position >= size) {
                throw new NoSuchElementException("No more frames");
            }
            return video.getFrame(keyList.get(position++));
        } 
        
        
        public LinkedHashMap<Integer, Mat> getkeyf() {
            return keyf;
        }

        public List<Integer> getkeyList() {
            return keyList;
        }
         
        
    }