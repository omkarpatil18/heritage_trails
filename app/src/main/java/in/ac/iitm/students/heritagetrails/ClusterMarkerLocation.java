package in.ac.iitm.students.heritagetrails;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by admin on 27-11-2016.
 */

public class ClusterMarkerLocation implements ClusterItem {
    private LatLng position;
    private String urlString;


    public ClusterMarkerLocation(LatLng latLng )
    {
        position = latLng;
    }

    public ClusterMarkerLocation(LatLng latLng,String urlString )
    {
        position = latLng;
        this.urlString = urlString;
    }

    @Override
    public LatLng getPosition()
    {
        return position;
    }

    public String getUrl(){
        return urlString;
    }
}
