package JMRVideoExtension.functions;


import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgproc.Imgproc;
import java.util.*;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.opencv.imgcodecs.Imgcodecs;


/**
 *
 * @author David Martinez Domingo
 */

public class VideoSegmentationOp {
    
    
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
     
    private final int CONST_K = 63;
    private final double CONST_SIMILARITY_THRESHOLD = 4;
    private final int CONST_RANGE = 2;
    
    
    
    int k;
    double similarity_threshold;
    int range;
    LinkedHashMap<Integer, Mat> DicKey = new LinkedHashMap<>();
    String filePath;
    

    
    public VideoSegmentationOp(){
        this.k = CONST_K;
        this.similarity_threshold = CONST_SIMILARITY_THRESHOLD;
        this.range = CONST_RANGE;
    }
    
    
   
    
    /**
     * Calculate the keyFrames of a video
     * @param filePath 
     * @param k number of characteristics of each frame
     * @param similarity_threshold 
     * @param range how many in how many frames the calculations will be carried out
     * @return Dictionary of keyframes
     */
    public static LinkedHashMap<Integer, Mat> calculationOfKeyFrames (String filePath, int k, double similarity_threshold, int range) {                           

        
    ////////// PART 1: FEATURE EXTRACTION  /////////////////////////
        
        
        VideoCapture video = new VideoCapture(filePath);                                             // I create the object with the video
        
        int[][] matrix_data = new int[0][1944];                                                             // I create array of 0 rows and 1944 columns to store 'flattened' color histograms. The rows will be the number of frames of the video
        HashMap<Integer, Mat> dictionary_frames = new HashMap<>();                                          // To save frame_number - frame
        int counter_number_frames = 0;                                                                      // Count the number of frames in the video
        

        if (!video.isOpened()) {                                                                            // If an image is passed instead of a video
            Mat image = Imgcodecs.imread(filePath);          
            LinkedHashMap<Integer, Mat> dictionary_KeyFrames = new LinkedHashMap<>();
            dictionary_KeyFrames.put(0, image);
            return dictionary_KeyFrames;
        }
         
                                                                                     
        
        while (video.isOpened()) { 
            
            Mat frame = new Mat();
            boolean ret = video.read(frame);                                                           // I read the video

            if (ret) {
                
                Mat frameRGB = new Mat();
                Imgproc.cvtColor(frame, frameRGB, Imgproc.COLOR_BGR2RGB);                       // Convert the frame from BGR to RGB, since OpenCV reads it in a different order. Saved in frameRGB
                dictionary_frames.put(counter_number_frames, frameRGB);                            // Save each frame in the dictionary to later identify the KeyFrames
                
                if (counter_number_frames % range == 0) {                                                   // Calculations will only be made every x frames (x=range)
                                                         
                    List<Integer> featureVector = new ArrayList<>();                                        // Vector of features (histogram values, such as the distribution of color intensities) extracted from each frame

                    int height = frameRGB.rows();                                                           // I divide the frame into 9 blocks (3*3), so I take its height and width and divide it into 3
                    int width = frameRGB.cols();                                                                
                   
                    int hChunk, wChunk;
                    if (height % 3 == 0){ hChunk = height / 3;                                              // If height is 1200, hChunk will be 400 (each block will be 400 tall)
                    } else {              hChunk = height / 3 + 1;   }

                    if (width % 3 == 0){ wChunk = width / 3;                                                // If width is 1280, hChunk will be 427 (1280/3 = 426.6 = 427)(each block will measure 427 wide)
                    } else {             wChunk = width / 3 + 1;     }

                    int h_init = 0;
                    int w_init = 0;

                    for (int row = 1; row < 4; row++) {

                        while((hChunk*row) > height){ hChunk--;} 
                        int h_final = hChunk * row;
                        
                        for (int column = 1; column < 4; column++) {

                                                                                                                                                                // This occurs when 1 has been added in the previous lines as it is not an exact divisor.
                            while((wChunk*column) > width){ wChunk--; }                                                                                         // You have to remove that extra value so that it does not go beyond the limits of the array 
                            int w_final = wChunk * column;

                            Mat block = frameRGB.submat(h_init, h_final, w_init, w_final);                                            // Instead of working with the entire frame, only the corresponding block is taken
                                                                                                                                                                 // defined by the coordinates of its edges (ROI=Region of interest)

                            MatOfFloat ranges = new MatOfFloat(0, 256, 0, 256, 0, 256);                                                             // Define the range for each RGB color from 0 to 255
                            MatOfInt histSize = new MatOfInt(6, 6, 6);                                                                                     // Define the size. How many containers for each color channel 6*6*6=216
                            Mat histogram = new Mat();                                                                                                        
                            Imgproc.calcHist(Arrays.asList(block), new MatOfInt(0, 1, 2), new Mat(), histogram, histSize, ranges);            // MatOfInt(0 red, 1 green, 2 blue) are the indices of the color channels
                                                                                                                                                                 // new Mat() to indicate that no mask is used

                            Mat histogram_unidimensional = histogram.reshape(1, (int) histogram.total());                                                     // Converts the array into a single row and many columns (flatten = one-dimensional array)                                               

                            MatOfByte matOfByte = new MatOfByte();                                                       
                            histogram_unidimensional.convertTo(matOfByte, CvType.CV_8U);                                                                  // Converts the one-dimensional array into another unsigned 8-bit array

                            byte[] byteValues = new byte[(int) (matOfByte.total() * matOfByte.channels())];                                                      // Array of bytes with size num elements array * num channels array
                            matOfByte.get(0, 0, byteValues);                                                                                         // The values ​​of the MatOfByte array are copied to the byteValues ​​array

                            for (byte value : byteValues) {
                                featureVector.add((int) value);                                    
                            }

                            w_init = w_final;                                                               // Update horizontal coordinate position for next block
                        }

                        h_init = h_final;                                                                   // Go to the next row
                        w_init = 0;                                                                         // Reset the column coordinate when changing row
                    }

                    List<int[]> arrList = new ArrayList<>(Arrays.asList(matrix_data));               // New arraylist containing arrays of integers. It is a dynamic structure, in each frame a new vector is added to the arrList
                    arrList.add(featureVector.stream().mapToInt(Integer::intValue).toArray());            // Add 'featureVector' to the list // .stream = to convert the list to a stream of elements // .toArray = convert the stream to array
                    matrix_data = arrList.toArray(new int[0][]);                                            // Convert the array list back to a two-dimensional array for the next frame

                                                                                                            // The calculations of the 9 blocks of the frame are finished, we move on to the next
                }
                counter_number_frames++; 
            }else{
                break;
            }
        }

        RealMatrix final_mat_Float = new OpenMapRealMatrix(matrix_data[0].length, matrix_data.length);            
        for (int i = 0; i < matrix_data.length; i++) {
            for (int j = 0; j < matrix_data[0].length; j++) {                                                              // It is transposed to show more coherence
                final_mat_Float.setEntry(j, i, (float)matrix_data[i][j]);                                        // A copy of matrix_data is made but with floating numbers to be able to decompose it in the next part
            }
        }

        
        
        
    ///////// PART 2: DECOMPOSITION   ///////////////////////////////////////
        
     
        SingularValueDecomposition svd = new SingularValueDecomposition(final_mat_Float);                           // Final_mat_Float Singular Value Decomposition (SVD)
        RealMatrix s = svd.getS();                                                                                        // Diagonal matrix of singular values
        RealMatrix vt = svd.getVT();                                                                                      // Matrix of singular vectors (original characteristics)
         
        if (s.getColumnDimension() > k) {                                                                                 // The size of the matrices is reduced. Explanation of K
            s = s.getSubMatrix(0, k - 1, 0, k - 1);                                                      // Only the first k values ​​are retained
            vt = vt.getSubMatrix(0, k - 1, 0, vt.getColumnDimension() - 1);
        }else {
            k = s.getColumnDimension(); 
            s = s.getSubMatrix(0, k - 1, 0, k - 1);
            vt = vt.getSubMatrix(0, k - 1, 0, vt.getColumnDimension() - 1);
        }

        RealMatrix vt_transpose = vt.transpose();                                                                         // Matrix transposition
        RealMatrix projections = vt_transpose.multiply(s);                                                              // "projections" contains the frame data but with reduced dimensions


        
        
      
    ///////// PART 3: COMPARISON AND ALLOCATION OF DATA  ////////////////////////////////////
        
        
        HashMap<Integer, RealMatrix> dictionary_Scenes = new HashMap<>();                                                 // dictionary_Scenes that will contain an array of frames for each scene                         
        for (int i = 0; i < projections.getRowDimension(); i++) {                                                         // Create empty arrays to initialize
            double[][] emptyData = new double[1][k];
            RealMatrix emptyMatrix = MatrixUtils.createRealMatrix(emptyData);
            dictionary_Scenes.put(i, emptyMatrix);
        }
         
        RealMatrix clust0 = MatrixUtils.createRealMatrix(new double[1][k]);                                               // clust0 is created to add the first frame at index 0 of the dictionary 
        clust0.setRowMatrix(0, projections.getRowMatrix(0));                                                 // This is done to be able to later compare similarity with the following frames.
        dictionary_Scenes.put(0, clust0);
   
       
       
        HashMap<Integer, RealMatrix> dictionary_Centroids = new HashMap<>();                                              // dictionary_Centroids that will contain the values ​​of the centroids of each scene
        for (int i = 0; i < projections.getRowDimension(); i++) {                                                         // Create empty arrays to initialize
            double[][] emptyData = new double[1][k];
            RealMatrix emptyMatrix = MatrixUtils.createRealMatrix(emptyData);
            dictionary_Centroids.put(i, emptyMatrix);
        }
                
        
        RealMatrix clust1 = dictionary_Scenes.get(0);                                                                 // We obtain the matrix associated with index 0 of dictionary_Scenes == clust1
        RealVector centroid0 = new ArrayRealVector(clust1.getRow(0), false);          
        centroid0 = centroid0.add(clust1.getRowVector(0));                                                          // The data from the first frame is obtained, which will initialize the dictionary_Centroids
        dictionary_Centroids.put(0, MatrixUtils.createRowRealMatrix(centroid0.toArray()));                // Save that centroid in the first cluster of dictionary_Centroids

        
        
        int counter_scenes = 0;
        boolean new_scene = false;
        for (int i = 1; i < projections.getRowDimension(); i++) {                                                        // For the rest of the frames, since we had already added the first one
         
            RealVector proj_i = projections.getRowVector(i).unitVector();                                            // We normalize the vector proj_i
            RealMatrix dictionary_Centroids_Scene = dictionary_Centroids.get(counter_scenes);
            RealVector dictionary_Centroids_Vector = dictionary_Centroids_Scene.getRowVector(0).unitVector();        // We normalize the first row of dictionary_Centroids_Scene

            double dotProduct = proj_i.dotProduct(dictionary_Centroids_Vector);                                        // Dot product between normalized vectors
            double similarity = Math.max(0.0, Math.min(1.0, dotProduct));
            
          //  System.out.println(similarity);
            
            if (similarity < similarity_threshold) {                                                                     // Increment counter if similarity is less than threshold
                counter_scenes++;                                                                                        // If it does not increase, it means that we are still in the same scene
                new_scene = true;
            }
  
            if (new_scene){                                                                                              // If it is a new scene, the first time the array is created (this is done to overwrite the row of 0 that is created at initialization)  
                RealMatrix clust2 = dictionary_Scenes.get(counter_scenes);                                           // It is initialized to false because in the first scene the row of zeros was already removed when entering the first two by hand
                clust2.setRowMatrix(0, projections.getRowMatrix(i));
                dictionary_Scenes.put(counter_scenes, clust2);
                new_scene = false;
            }else{                                                                                                       // If it is not new scene, the array is extended
                RealMatrix clust2 = dictionary_Scenes.get(counter_scenes);
                RealMatrix extendedMatrix = MatrixUtils.createRealMatrix(clust2.getRowDimension() + 1, clust2.getColumnDimension());
                for (int a = 0; a < clust2.getRowDimension(); a++) {
                    extendedMatrix.setRow(a, clust2.getRow(a));
                }
                extendedMatrix.setRowMatrix(clust2.getRowDimension(),projections.getRowMatrix(i));
                dictionary_Scenes.put(counter_scenes, extendedMatrix);
            }
            
             
            RealMatrix clust3 = dictionary_Scenes.get(counter_scenes);                                               // Calculates the centroid of the corresponding cluster from dictionary_Scenes to save it in dictionary_Centroids
            RealVector sumVector = clust3.getRowVector(0).copy();
            for (int p = 1; p < clust3.getRowDimension(); p++) {
                sumVector = sumVector.add(clust3.getRowVector(p));
            }
            RealVector centroid_Vector = sumVector.mapDivide(clust3.getRowDimension());
            dictionary_Centroids.put(counter_scenes, MatrixUtils.createRowRealMatrix(centroid_Vector.toArray()));        
        }    
            
         
        
    ///////// PART 4: IDENTIFICATION OF KEY_SCENES AND KEY_FRAMES  ///////////////////////////////////////
        

        List<Integer> count_frames_each_scene = new ArrayList<>();                                                       // List to store the number of frames at each dictionary index (count_frames_each_scene)

        for (int i = 0; i < projections.getRowDimension(); i++) {
            int num_f = dictionary_Scenes.get(i).getRowDimension();                                                  // Get the number of rows of each matrix D[0] -> each row is a frame of the scene
            count_frames_each_scene.add(num_f);                                                                       // They all have 1 row by default because you cannot create empty arrays
        }
      
       // System.out.println(count_frames_each_scene);  
       
        
        int final_index = -1;                                                                                           // To avoid traversing the dictionary to the end, when there are only ones (1) left in the
        for (int i = count_frames_each_scene.size()-1; i >= 0; i--) {                                                   // dictionary from an index to the end, means that there are no more scenes,
            if (count_frames_each_scene.get(i) == 1) {                                                             // and we do not need to go through that part, since there are no frames in those arrays
                final_index = i;
            } else {
                break; 
            }
        }
        
                                             
        
        List<Integer> Key_Scenes = new ArrayList<>();                                                                  // Create a list to store the indices of dense scenes (dense = +20 frames, we avoid transitions)

        for (int i = 0; i < final_index; i++) {
            if (count_frames_each_scene.get(i) >=  (25/range) ) {                                                  // Check if the cluster size is at least 25, to avoid frames that are transitions, which are not considered KeyFrames
               Key_Scenes.add(i);                                                                                    // Add the cluster index to the list of dense clusters
            } 
        }

       // System.out.println(Key_Scenes);                                                                              // Print the indexes of the video scenes (those that have more than 20 frames)
          
        
        List<Integer> Key_Frames = new ArrayList<>();
        int count_frames = 0;
         
        for(int i=0; i<final_index;i++){          
            count_frames += count_frames_each_scene.get(i)*range;                                                  // We go through each frame of the video
            for(int j=0; j<Key_Scenes.size();j++){
                if(i == Key_Scenes.get(j)){                                                                       // When a scene considered key (dense) is found, its penultimate frame is saved as KeyFrame
                    Key_Frames.add(count_frames - range);                                                               // The penultimate frame of the scene is saved
                }   
            }            
        }
        
        if(Key_Frames.isEmpty()){
            Key_Frames.add(0);
        }
       // System.out.println(Key_Frames);
           
        LinkedHashMap<Integer, Mat> Dictionary_KeyFrames = new LinkedHashMap<>();
        for (int key : Key_Frames) {
            Mat frameRGB1 = new Mat();
            Imgproc.cvtColor(dictionary_frames.get(key), frameRGB1, Imgproc.COLOR_RGB2BGR);                 // Convert image from RGB to BGR
            Dictionary_KeyFrames.put(key, frameRGB1);
        }

        return Dictionary_KeyFrames;
    }
    
    
    
    
    public String getFilePath() {
        return filePath;
    }


    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
  
    public int getK() {
        return k;
    }

 
    public void setK(int k) {
        this.k = k;
    }
    

    public double getSimilarityThreshold() {
        return similarity_threshold;
    }


    public void setSimilarityThreshold(double similarity_threshold) {
        this.similarity_threshold = similarity_threshold;
    }
    
    
    public int getRange() {
        return range;
    }


    public void setRange(int range) {
        this.range = range;
    }
    
    
    public LinkedHashMap getDicKey() {
        return DicKey;
    }


    public void setDicKey(LinkedHashMap Dic_Key) {
        this.DicKey = Dic_Key;
    }
    
  
}
