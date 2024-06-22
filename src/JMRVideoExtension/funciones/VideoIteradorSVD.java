package JMRVideoExtension.funciones;

import static JMRVideoExtension.App.isImagen;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import jmr.video.Video;
import jmr.video.VideoIterator;
import org.opencv.core.Mat;
import static JMRVideoExtension.funciones.VideoSegmentationOp.calculo_Keyframes;

/**
 *
 * @author David Martinez Domingo
 */



    /**
     * Clase para el iterador optimo al obtener los keyframes
     * del video con los parametros mas adecuados
     */
    public class VideoIteradorSVD implements VideoIterator<BufferedImage>{
     
        /**
         * Objeto de la segmentacion de vidoe
         */
        VideoSegmentationOp VS = new VideoSegmentationOp();    
        
        /**
         * Diccionario con los fotogramas clave obtenidos de la segmentacion
         */
        LinkedHashMap<Integer, Mat> keyf;
        
        /**
         * Lista con los numeros de fotogramas claves
         */
        List<Integer> keyList;
        
        /**
         * Video seleccionado
         */
        Video video;
        
        /**
         * Tamaño de la lista de fotogramas clave
         */
        private int tamano;
        
        /**
         * Indice de la lista de fotogramas clave
         */
        int posicion;
        
       
        public VideoIteradorSVD(Video video){
            setVideo(video);
        }
        
        public VideoIteradorSVD(Video video, String filePath){
            VS.setfilePath(filePath);
            setVideo(video);           
        }
        

        /**
         * Se establecen los parametros al realizar la segmentacion de keyframes al video
         * @param video Video al que se le realiza la segmentacion
         */
        @Override
        public void setVideo(Video video) {
            this.video = video;                           
            posicion = 0;
            keyf = calculo_Keyframes(VS.getfilePath(),VS.getK(),VS.getUmbral_similitud() / 10.0,VS.getIntervalo());
            keyList = new ArrayList<>(keyf.keySet());
            tamano = keyList.size();
        }
   

        /**
         * Obtener el video
         * @return Video
         */
        @Override
        public Video getVideo() {
            return video;
        }

        /**
         * Inicializacion de la posicion del indice
         */
        @Override
        public void init(){
            posicion = 0;
        }

        /**
         * Comprobar si se puede realizar la siguiente iteracion
         * @return True si se puede realizar una iteracion mas, False si no
         */
        @Override
        public boolean hasNext() {
            return (posicion < tamano);
        }

        /**
         * Saltar a la siguiente posicion en la lista
         * @return Frame clave correspondiente a la posicion actual
         */
        @Override
        public BufferedImage next() {
            if (posicion >= tamano) {
                throw new NoSuchElementException("No more frames");
            }
            return video.getFrame(keyList.get(posicion++));
        } 
        
        /**
         * Obtener diccionario de fotogramas clave con su numero de fotograma
         * @return Diccionario de frames
         */
        public LinkedHashMap<Integer, Mat> getkeyf() {
            return keyf;
        }

        /**
         * Obtener lista de indices de fotogramas clave
         * @return Lista de indices
         */
        public List<Integer> getkeyList() {
            return keyList;
        }
         
        
    }