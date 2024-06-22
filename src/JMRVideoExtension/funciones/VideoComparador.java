package JMRVideoExtension.funciones;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import jmr.descriptor.Comparator;
import jmr.descriptor.MediaDescriptor;
import jmr.video.KeyFrameDescriptor;

/**
 *
 * @author David Martinez Domingo
 */


public class VideoComparador implements Comparator<KeyFrameDescriptor, List<Double>> {

    /**
     * Obtencion del minimo entre dos descriptores junto con los indices de dicho minimo
     * @param t KeyFrameDescriptor
     * @param u KeyFrameDescriptor
     * @return lista que contiene el minimo y los dos iteradores que generaron el minimo
     */
    @Override
    public List<Double> apply(KeyFrameDescriptor t, KeyFrameDescriptor u) {
        Double min_distance = Double.MAX_VALUE;
        List<Double> lista = new ArrayList<>();
        try {
            Double item_distance;
            MediaDescriptor m1, m2;
            List<MediaDescriptor> l1, l2;
            l1 = (List<MediaDescriptor>) t.getDescriptors();
            l2 = (List<MediaDescriptor>) u.getDescriptors();
            
            for (int i = 0; i < l1.size(); i++) {
                m1 = l1.get(i);
                for (int j = 0; j < l2.size(); j++) {
                    m2 = l2.get(j);
                    item_distance = (Double) m1.compare(m2);
                    if (item_distance < min_distance) {
                        min_distance = item_distance; 
                        lista.clear();
                        lista.add(min_distance);
                        lista.add((double)j);
                        lista.add((double)i);
                    }
                }
            }
        } catch (ClassCastException e) {
            throw new InvalidParameterException("The comparision between descriptors is not interpetrable as a double value.");
        } catch (Exception e) {
            throw new InvalidParameterException("The descriptors are not comparables.");
        }
        return lista;
    }
        
}