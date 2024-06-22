package JMRVideoExtension.funciones;


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
     
    int k;
    double umbral_similitud;
    int intervalo;
    LinkedHashMap<Integer, Mat> Dic_Key = new LinkedHashMap<>();
    String filePath;
    
    
    /**
     * Constructor con parametros por defecto
     */
    public VideoSegmentationOp(){
        this.k = 63;
        this.umbral_similitud = 4;
        this.intervalo = 2;
    }
    
    
    /**
    * Obtener ruta del archivo
    * @return Ruta del archivo
    */
    public String getfilePath() {
        return filePath;
    }

    /**
     * Establecer ruta del archivo
     * @param filePath Ruta del archivo
     */
    public void setfilePath(String filePath) {
        this.filePath = filePath;
    }
    
    /**
     * Obtener valor de K
     * @return 
     */
    public int getK() {
        return k;
    }

    /**
     * Establecer valor de K
     * @param k 
     */
    public void setK(int k) {
        this.k = k;
    }
    
    /**
     * Obtener valor del umbral de similitud
     * @return Umbral de similitud
     */
    public double getUmbral_similitud() {
        return umbral_similitud;
    }

    /**
     * Establecer valor del umbral de similitud
     * @param umbral_similitud 
     */
    public void setUmbral_similitud(double umbral_similitud) {
        this.umbral_similitud = umbral_similitud;
    }
    
    /**
     * Obtener valor del intervalo
     * @return Intervalo
     */
    public int getIntervalo() {
        return intervalo;
    }

    /**
     * Establecer valor del intervalo
     * @param intervalo 
     */
    public void setIntervalo(int intervalo) {
        this.intervalo = intervalo;
    }
    
    /**
     * Obtener valor del diccionario de KeyFrames
     * @return Diccionario
     */
    public LinkedHashMap getDic_Key() {
        return Dic_Key;
    }

    /**
     * Establecer valor del diccionario de KeyFrames
     * @param Dic_Key 
     */
    public void setDic_Key(LinkedHashMap Dic_Key) {
        this.Dic_Key = Dic_Key;
    }
    
      
    
    /**
     * Calcula los keyFrames de un video
     * @param filePath ruta del video
     * @param k numero de caracteristicas de cada frame
     * @param umbral_similitud umbral de similaridad
     * @param intervalo de cuantos en cuantos frames se realizarán los cálculos
     * @return Diccionario con los keyframes
     */
    public static LinkedHashMap<Integer, Mat> calculo_Keyframes (String filePath, int k, double umbral_similitud, int intervalo) {                           

        
    ////////// PARTE 1: EXTRACCIÓN DE CARACTERÍSTICAS  /////////////////////////
        
        
        VideoCapture video = new VideoCapture(filePath);                                            // Creo el objeto con el video
        
        int[][] mat_datos = new int[0][1944];                                                              // Creo matriz de 0 filas y 1944 columnas para almacenar histogramas de color 'aplanados'. Las filas serán el numero de frames del video
        HashMap<Integer, Mat> Diccionario_frames = new HashMap<>();                                        // Para guardar núm_frame - frame
        int contador_num_frames = 0;                                                                       // Contar el número de frames del vídeo  
        

        if (!video.isOpened()) {                                                                           // Si se pasa una imagen en lugar de un video
            Mat image = Imgcodecs.imread(filePath);          
            LinkedHashMap<Integer, Mat> Dic_Key = new LinkedHashMap<>();
            Dic_Key.put(0, image);
            return Dic_Key;
        }
         
                                                                                     
        
        while (video.isOpened()) { 
            
            Mat frame = new Mat();
            boolean ret = video.read(frame);                                                          // Leo el video

            if (ret) {
                
                Mat frameRGB = new Mat();
                Imgproc.cvtColor(frame, frameRGB, Imgproc.COLOR_BGR2RGB);                          // Convertir el fotograma de BGR a RGB, ya que OpenCV lo lee en otro orden. Se guarda en frameRGB
                Diccionario_frames.put(contador_num_frames, frameRGB);                                // Guardar cada frame en el diccionario para poder identificar luego los KeyFrames
                
                if (contador_num_frames % intervalo == 0) {                                                    // Solo se harán cálculos cada x frames (x=intervalo)
                                                         
                    List<Integer> featureVector = new ArrayList<>();                                           // Vector de caracteristicas (valores de los histogramas, como la distribución de intensidades de color) extraidas de cada frame 

                    int height = frameRGB.rows();                                                              // Divido el frame en 9 bloques (3*3), por lo que cojo su altura y anchura y la divido en 3
                    int width = frameRGB.cols();                                                                
                   
                    int hChunk, wChunk;
                    if (height % 3 == 0){ hChunk = height / 3;                                                 // Si height es 1200, hChunk será 400 (cada bloque medirá 400 de alto)
                    } else {              hChunk = height / 3 + 1;   }

                    if (width % 3 == 0){ wChunk = width / 3;                                                   // Si width es 1280, hChunk será 427 (1280/3 = 426.6 = 427)(cada bloque medirá 427 de ancho)
                    } else {             wChunk = width / 3 + 1;     }

                    int h_ini = 0;
                    int w_ini = 0;

                    for (int fila = 1; fila < 4; fila++) {

                        while((hChunk*fila) > height){ hChunk--;} 
                        int h_fin = hChunk * fila;
                        
                        for (int columna = 1; columna < 4; columna++) {

                                                                          // Esto ocurre cuando se le ha sumado 1 en las lineas anteriores al no ser divisor exacto
                            while((wChunk*columna) > width){ wChunk--; }                                            // Hay que quitarle ese valor extra para que no se salga de los límites del array  
                            int w_fin = wChunk * columna;

                            Mat bloque = frameRGB.submat(h_ini, h_fin, w_ini, w_fin);                                                 // En lugar de trabajar con todo el frame, se coge solamente el bloque correspondiente 
                                                                                                                                                                // definido por las coordenadas de sus bordes (ROI=Region de interes)

                            MatOfFloat ranges = new MatOfFloat(0, 256, 0, 256, 0, 256);                                                            // Definir el rango para cada color RGB de 0 a 255
                            MatOfInt histSize = new MatOfInt(6, 6, 6);                                                                                    // Definir el tamaño. Cuantos contenedores para cada canal de color 6*6*6=216
                            Mat histograma = new Mat();                                                                                                        
                            Imgproc.calcHist(Arrays.asList(bloque), new MatOfInt(0, 1, 2), new Mat(), histograma, histSize, ranges);         // MatOfInt(0 rojo, 1 verde, 2 azul) son los indices de los canales de color
                                                                                                                                                                // new Mat() para indicar que no se usa ninguna máscara

                            Mat histograma_unidim = histograma.reshape(1, (int) histograma.total());                                                         //Se convierte la matriz en una sola fila y muchas columnas (aplanar = matriz unidimensional)                                                              

                            MatOfByte matOfByte = new MatOfByte();                                                       
                            histograma_unidim.convertTo(matOfByte, CvType.CV_8U);                                                                        // Se convierte la matriz unidimensional en otra matriz de 8 bits sin signo

                            byte[] byteValues = new byte[(int) (matOfByte.total() * matOfByte.channels())];                                                     // Array de bytes con tamaño num elementos matriz * num canales matriz
                            matOfByte.get(0, 0, byteValues);                                                                                        // Se copian los valores de la matriz MatOfByte al array byteValues

                            for (byte value : byteValues) {
                                featureVector.add((int) value);                                    
                            }

                            w_ini = w_fin;                                                                     // Actualizar la posición de coordenada horizontal para el próximo bloque
                        }

                        h_ini = h_fin;                                                                         // Se pasa a la siguiente fila 
                        w_ini = 0;                                                                             // Reset de la coordenada de la columna al cambiar de fila
                    }

                    List<int[]> arrList = new ArrayList<>(Arrays.asList(mat_datos));                          // Nuevo arraylist que contiene matrices de enteros. Es una estructura dinamica, en cada frame se añade un nuevo vector al arrList
                    arrList.add(featureVector.stream().mapToInt(Integer::intValue).toArray());               // Agregar 'featureVector' a la lista // .stream = para convertir la lista en un flujo de elementos // .toArray = convierte el flujo en array
                    mat_datos = arrList.toArray(new int[0][]);                                                       // Convertir la lista de arrays de nuevo a un array bidimensional para el siguiente frame

                                                                                        // Se terminan los calculos de los 9 bloques del frame, se pasa al siguiente
                }
                contador_num_frames++; 
            }else{
                break;
            }
        }

        RealMatrix final_mat_Float = new OpenMapRealMatrix(mat_datos[0].length, mat_datos.length);            // Se crea una Realmatrix 
        for (int i = 0; i < mat_datos.length; i++) {
            for (int j = 0; j < mat_datos[0].length; j++) {                                                                  // Se transpone para mostrar mas coherencia
                final_mat_Float.setEntry(j, i, (float)mat_datos[i][j]);                                              // Se hace una copia de mat_datos pero con numeros flotantes para poder descomponerla en la siguiente parte
            }
        }

        
        
        
    ///////// PARTE 2: DESCOMPOSICIÓN   ///////////////////////////////////////
        
     
        SingularValueDecomposition svd = new SingularValueDecomposition(final_mat_Float);                             // Descomposición en valores singulares (SVD) de final_mat_Float
        RealMatrix s = svd.getS();                                                                                        // Matriz diagonal de valores singulares
        RealMatrix vt = svd.getVT();                                                                                      // Matriz de vectores singulares (caracteristicas originales)
         
        if (s.getColumnDimension() > k) {                                                                             // Se reduce la dimension de las matrices. Explicacion de K
            s = s.getSubMatrix(0, k - 1, 0, k - 1);                                                      // Solo se retienen los k primeros valores 
            vt = vt.getSubMatrix(0, k - 1, 0, vt.getColumnDimension() - 1);
        }else {
            k = s.getColumnDimension(); 
            s = s.getSubMatrix(0, k - 1, 0, k - 1);
            vt = vt.getSubMatrix(0, k - 1, 0, vt.getColumnDimension() - 1);
        }

        RealMatrix vt_1 = vt.transpose();                                                                                 // Transposicion de la matriz
        RealMatrix projections = vt_1.multiply(s);                                                                      // "projections" contiene los datos del frame pero con dimensiones reducidas


        
        
      
    ///////// PARTE 3: COMPARACIÓN Y ASIGNACION DE DATOS  ////////////////////////////////////
        
        
        HashMap<Integer, RealMatrix> Diccionar1 = new HashMap<>();                        // Diccionario 1 que contendrá una matriz de frames por cada escena                                  
        for (int i = 0; i < projections.getRowDimension(); i++) {                                       // Crear matrices vacías para inicializar 
            double[][] emptyData = new double[1][k];
            RealMatrix emptyMatrix = MatrixUtils.createRealMatrix(emptyData);
            Diccionar1.put(i, emptyMatrix);
        }
         
        RealMatrix clust0 = MatrixUtils.createRealMatrix(new double[1][k]);                                            // Se crea clust0 para añadir el primer frame en el indice 0 del diccionario     
        clust0.setRowMatrix(0, projections.getRowMatrix(0));                                              // Esto se hace para poder comparar despues similitud con los siguientes frames
        Diccionar1.put(0, clust0);
   
       
       
        HashMap<Integer, RealMatrix> Diccionar2 = new HashMap<>();                        // Diccionario 2 que contendrá los valores de los centroides de cada escena
        for (int i = 0; i < projections.getRowDimension(); i++) {                                     // Crear matrices vacías para inicializar 
            double[][] emptyData = new double[1][k];
            RealMatrix emptyMatrix = MatrixUtils.createRealMatrix(emptyData);
            Diccionar2.put(i, emptyMatrix);
        }
                
        
        RealMatrix clust1 = Diccionar1.get(0);                                                                   // Obtenemos la matriz asociada al indice 0 del diccionario 1 == clust1
        RealVector centroid0 = new ArrayRealVector(clust1.getRow(0), false);          
        centroid0 = centroid0.add(clust1.getRowVector(0));                                                     // Se obtienen los datos del primer frame, que inicializaran el vector del diccionario 2
        Diccionar2.put(0, MatrixUtils.createRowRealMatrix(centroid0.toArray()));                     // Guarda ese centroide en el primer cluster del diccionario 2

        
        
        int contador_escenas = 0;
        boolean nueva_escena = false;
        for (int i = 1; i < projections.getRowDimension(); i++) {                                                  // Para el resto de frames, ya que el primero ya lo habiamos añadido
         
            RealVector proj_i = projections.getRowVector(i).unitVector();                                       // Normalizamos el vector proj_i
            RealMatrix Dicc2_escena = Diccionar2.get(contador_escenas);
            RealVector Dicc2_vect_centroides = Dicc2_escena.getRowVector(0).unitVector();                       // Normalizamos la primera fila de Dicc2_escena 

            double dotProduct = proj_i.dotProduct(Dicc2_vect_centroides);                                        // Producto punto entre los vectores normalizados
            double similarity = Math.max(0.0, Math.min(1.0, dotProduct));
            
          //  System.out.println(similarity);
            
            if (similarity < umbral_similitud) {                                                                   // Incrementar el contador si la similitud es menor que el umbral
                contador_escenas++;                                                                                // Si no se incrementa es que seguimos en la misma escena
                nueva_escena = true;
            }
  
            if (nueva_escena){                                                                                     // Si es nueva escena, la primera vez la matriz se crea (esto se hace para sobreescribir la fila de 0 que se crea al inicializar)    
                RealMatrix clust2 = Diccionar1.get(contador_escenas);                                          // Se inicializa a false porque en la primera escena ya se quitó la fila de ceros al introducir a mano los dos primeros
                clust2.setRowMatrix(0, projections.getRowMatrix(i));
                Diccionar1.put(contador_escenas, clust2);
                nueva_escena = false;
            }else{                                                                                                 // Si no es nueva escena, la matriz se extiende
                RealMatrix clust2 = Diccionar1.get(contador_escenas);
                RealMatrix extendedMatrix = MatrixUtils.createRealMatrix(clust2.getRowDimension() + 1, clust2.getColumnDimension());
                for (int a = 0; a < clust2.getRowDimension(); a++) {
                    extendedMatrix.setRow(a, clust2.getRow(a));
                }
                extendedMatrix.setRowMatrix(clust2.getRowDimension(),projections.getRowMatrix(i));
                Diccionar1.put(contador_escenas, extendedMatrix);
            }
            
             
            RealMatrix clust4 = Diccionar1.get(contador_escenas);                                              // Calcula el centroide del cluster correspondiente del diccionario1 para guardarlo en el 2
            RealVector sumVector2 = clust4.getRowVector(0).copy();
            for (int p = 1; p < clust4.getRowDimension(); p++) {
                sumVector2 = sumVector2.add(clust4.getRowVector(p));
            }
            RealVector centroid4 = sumVector2.mapDivide(clust4.getRowDimension());
            Diccionar2.put(contador_escenas, MatrixUtils.createRowRealMatrix(centroid4.toArray()));        
        }    
            
         
        
    ///////// PARTE 4: IDENTIFICACION DE KEY_ESCENES Y KEY_FRAMES  ///////////////////////////////////////
        

        List<Integer> num_frames_cada_escena = new ArrayList<>();                                                  // Lista para almacenar la cantidad de frames en cada índice del diccionario (num_frames_cada_escena)

        for (int i = 0; i < projections.getRowDimension(); i++) {
            int num_f = Diccionar1.get(i).getRowDimension();                                               // Obtener el número de filas de cada matriz D[0] -> cada fila es un frame de la escena
            num_frames_cada_escena.add(num_f);                                                              // Todos tienen 1 fila por defecto pq no se pueden crear vacias las matrices 
        }
      
       // System.out.println(num_frames_cada_escena);  
       
        
        int indice_final = -1;                                                                                  // Para evitar recorrer el diccionario hasta el final, cuando solo queden unos (1) en el
        for (int i = num_frames_cada_escena.size()-1; i >= 0; i--) {                                          // diccionario desde un indice hasta el final, significa que ya no hay mas escenas, 
            if (num_frames_cada_escena.get(i) == 1) {                                                      // y no necesitamos recorrer esa parte, ya que no hay frames en esas matrices
                indice_final = i;
            } else {
                break; 
            }
        }
        
                                             
        
        List<Integer> Key_Escenes = new ArrayList<>();                                                          // Crear una lista para almacenar los índices de las escenas densas (densa = +20 frames, evitamos transiciones)

        for (int i = 0; i < indice_final; i++) {
            if (num_frames_cada_escena.get(i) >=  (25/intervalo) ) {                                      // Verificar si el tamaño del cluster es al menos 25, para evitar frames que sea transiciones, los cuales no son considerados KeyFrames
               Key_Escenes.add(i);                                                                           // Agregar el índice del cluster a la lista de clusters densos
            }
        }

       // System.out.println(Key_Escenes);                                                                     // Imprimir los indices de las escenas del video (las que tienen mas de 20 frames)
          
        
        List<Integer> Key_Frames = new ArrayList<>();
        int cont_frames = 0;
         
        for(int w=0; w<indice_final;w++){          
            cont_frames += num_frames_cada_escena.get(w)*intervalo;                                       // Recorremos cada frame del video
            for(int e=0; e<Key_Escenes.size();e++){
                if(w == Key_Escenes.get(e)){                                                              // Cuando se encuentre una escena considerada clave (densa), se guarda su penultimo frame como KeyFrame
                    Key_Frames.add(cont_frames - intervalo);                                                           // Se guarda el penultimo frame de la escena
                }   
            }            
        }
        
        if(Key_Frames.isEmpty()){
            Key_Frames.add(0);
        }
       // System.out.println(Key_Frames);
           
        LinkedHashMap<Integer, Mat> Dic_Key = new LinkedHashMap<>();
        for (int c : Key_Frames) {
            Mat frameRGB1 = new Mat();
            Imgproc.cvtColor(Diccionario_frames.get(c), frameRGB1, Imgproc.COLOR_RGB2BGR);             // Convertir imagen de RGB a BGR
            Dic_Key.put(c, frameRGB1);
        }

        return Dic_Key;
    }
    
  
}
