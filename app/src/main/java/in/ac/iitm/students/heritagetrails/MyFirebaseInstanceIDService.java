package in.ac.iitm.students.heritagetrails;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sai_praneeth7777 on 05-Jul-16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "Firebase:token" ;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // Log.d(TAG, "Refreshed token: " + refreshedToken);
    }

}
