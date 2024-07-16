package JMRVideoExtension.functions;

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


public class VideoComparator implements Comparator<KeyFrameDescriptor, List<Double>> {

    /**
     * Obtaining the minimum between two descriptors along with the indices of said minimum
     * @param t KeyFrameDescriptor
     * @param u KeyFrameDescriptor
     * @return list containing the minimum and the two iterators that generated the minimum
     */
    @Override
    public List<Double> apply(KeyFrameDescriptor t, KeyFrameDescriptor u) {
        
        Double min_distance = Double.MAX_VALUE;
        List<Double> list = new ArrayList<>();
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
                        list.clear();
                        list.add(min_distance);
                        list.add((double)j);
                        list.add((double)i);
                    }
                }
            }
        } catch (ClassCastException e) {
            throw new InvalidParameterException("The comparision between descriptors is not interpetrable as a double value.");
        } catch (Exception e) {
            throw new InvalidParameterException("The descriptors are not comparables.");
        }
        return list;
    }
        
}