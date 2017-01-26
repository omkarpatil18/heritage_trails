package in.ac.iitm.students.heritagetrails;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import java.util.ArrayList;

/**
 * Created by admin on 25-01-2017.
 */

public class OwnTrailIconRendered extends DefaultClusterRenderer<ClusterMarkerLocation> {
    private ArrayList<MarkerOptions> trailMarkerOptionsArray;
    private Context context;


    public OwnTrailIconRendered(Context context, GoogleMap map,
                                ClusterManager<ClusterMarkerLocation> clusterManager, ArrayList<MarkerOptions> trailMarkerOptionsArray) {
        super(context, map, clusterManager);
        this.trailMarkerOptionsArray= trailMarkerOptionsArray;
        this.context= context;
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterMarkerLocation item, MarkerOptions markerOptions) {

        for(MarkerOptions mOptions : trailMarkerOptionsArray){
            if(mOptions.getPosition()==item.getPosition()){
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp))
                        .alpha(0.6f)
                        .title(mOptions.getTitle())
                        .snippet(mOptions.getSnippet());
            }
        }

        super.onBeforeClusterItemRendered(item, markerOptions);
    }


}
