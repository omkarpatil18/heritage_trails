package in.ac.iitm.students.heritagetrails;


import android.animation.Animator;
import android.Manifest;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.kml.KmlLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;
    private int trailCount=0;
    private ClusterManager<ClusterMarkerLocation> mClusterManager=null;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ArrayList<Marker> mTrail1Markers = new ArrayList<>(), mTrail2Markers = new ArrayList<>();
    private Marker mCurrLocationMarker;
    private ArrayList<MarkerOptions> mTrail1MarkerOptions= new ArrayList<>(), mTrail2MarkerOptions= new ArrayList<>();
    private MarkerOptions mHeritageCenterMarkerOptions;
    private LocationRequest mLocationRequest;
    private AutoCompleteTextView searchField;
    private Polyline polyline;
    private PolylineOptions lineOptions;
    private Boolean isVisible = false, isBusRouteShown = false, isDownloaded = false, isSearchResultShown=false, isTrail1Shown=false, isTrail2Shown=false;
    private Toolbar myToolbar;
    private Menu myMenu;
    private CoordinatorLayout coordinatorLayout;
    private ArrayList<String> placesArray;
    private ProgressDialog progress, pDialog;
    private Context context;
    private float searchBarPosX, searchBarPosY;
    private KmlLayer layer=null;
    private String url;
    private ArrayList<Marker> searchResultMarkers= new ArrayList<>();
    private ArrayList<MarkerOptions> searchResultMarkerOptions= new ArrayList<>();

    private static final LatLng MAIN_GATE = new LatLng(13.005976, 80.242486);
    private static final LatLng JAM_BUS_STOP = new LatLng(12.986634, 80.238757);
    private static final LatLng GAJENDRA_CIRCLE_BUS_STOP = new LatLng(12.991780, 80.233772);
    private static final LatLng HSB_BUS_STOP = new LatLng(12.990925, 80.231896);
    private static final LatLng VELACHERY_GATE = new LatLng(12.988461, 80.223328);
    private static final LatLng BT_BUS_STOP= new LatLng(12.989977, 80.227707);
    private static final LatLng CRC_BUS_STOP = new LatLng(12.988204, 80.230125);
    private static final LatLng TGH_BUS_STOP = new LatLng(12.986574, 80.233254);
    private static final LatLng NARMADA_BUS_STOP = new LatLng(12.986473, 80.235324);

    private static final LatLng FOURTH_CROSS_STREET_BUS_STOP = new LatLng(12.99599484, 80.23595825);
    private static final LatLng KV_BUS_STOP = new LatLng(12.99398019, 80.23437828);
    private static final LatLng VANVANI_BUS_STOP = new LatLng(12.9987747, 80.23919707 );



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        context = this;
        pDialog = new ProgressDialog(this);
        progress = new ProgressDialog(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coord_layout);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            //Toast.makeText(this,version, Toast.LENGTH_SHORT).show();
            checkVersionMatch();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        placesArray = new ArrayList<>();

        getSuggestions();

        searchField = (AutoCompleteTextView) findViewById(R.id.auto_comp_tv_search);
        searchBarPosX = searchField.getX();
        searchBarPosY = searchField.getY();
        searchField.setVisibility(View.GONE);

        searchField.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                final String selection = (String) parent.getItemAtPosition(position);

                pDialog.setMessage("Searching...");
                pDialog.setCancelable(false);
                pDialog.show();
                searchField.setText("");
                removeSearchResult();
                searchField.setHint(selection);
                isSearchResultShown=true;

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        doMySearch(selection);
                    }
                };
                thread.start();
            }
        });

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                                  @Override
                                                  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                                      if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                                          final Editable selection = searchField.getText();
                                                          pDialog.setMessage("Searching...");
                                                          pDialog.setCancelable(false);
                                                          pDialog.show();
                                                          removeSearchResult();
                                                          searchField.setText("");
                                                          searchField.setHint(selection.toString());
                                                          isSearchResultShown=true;

                                                          Thread thread = new Thread() {
                                                              @Override
                                                              public void run() {
                                                                  doMySearch(selection.toString());
                                                              }
                                                          };
                                                          thread.start();
                                                          return true;
                                                      }

                                                      return false;
                                                  }
                                              }

        );

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)

        {
            checkLocationPermission();
        }
        // Initializing

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void checkVersionMatch() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/map/get_names.php
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("heritage")
                .appendPath("check_version.php");
        final String url = builder.build().toString();

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {


                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;
                    int i;
                    String locationName;
                    for (i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        locationName = jsonObject.getString("locname");
                        placesArray.add(locationName);
                    }
                    isDownloaded = true;
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                            android.R.layout.simple_dropdown_item_1line, placesArray);
                    searchField.setAdapter(adapter);
                } catch (JSONException e) {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error getting data, try again later...", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Couldn't Retrive Version Information", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removeSearchResult(){
        for(Marker marker:searchResultMarkers){
            marker.remove();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        myMenu = menu;
        // Associate searchable configuration with the SearchView

        return true;
    }

    public void doMySearch(String query) {


        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/map/get_location.php?
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("heritage")
                .appendPath("get_location.php")
                .appendQueryParameter("locname", query);
        String url = builder.build().toString();

        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;
                    int i;
                    String locationName, locationDescription, latitude = "12.991780", longitude = "80.233772";
                    LatLng latLong;

                    if(searchResultMarkers!=null) searchResultMarkers.clear();
                    if(searchResultMarkerOptions!=null) searchResultMarkerOptions.clear();

                    for (i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        locationName = jsonObject.getString("locname");
                        String location_url = jsonObject.getString("file_name");
                        String location_trail = jsonObject.getString("trail");
                        latitude = jsonObject.getString("lat");
                        longitude = jsonObject.getString("long");

                        latLong = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        MarkerOptions markerOption=new MarkerOptions()
                                .title(locationName)
                                .snippet("Click for more info...")
                                .position(latLong);
                        Marker currMarker = mMap.addMarker(markerOption);
                        currMarker.setTag(location_url);

                        searchResultMarkerOptions.add(markerOption);
                        searchResultMarkers.add(currMarker);

                    }

                    if (pDialog.isShowing()) pDialog.dismiss();
                    LatLng latLngGC;
                    if (jsonArray.length() == 1) {
                        latLngGC = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngGC, 18));
                    } else {
                        latLngGC = new LatLng(12.991780, 80.233772);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngGC, 14));
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Showing all related results...", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } catch (JSONException e) {

                    if (pDialog.isShowing()) pDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "No result found!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    e.printStackTrace();

                }

                hideKeyboard(MapsActivity.this);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (pDialog.isShowing()) pDialog.dismiss();
                VolleyLog.d("VolleyResponseError", error);
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Couldn't connect to the server.", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
    }

    private void getSuggestions() {


        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/map/get_names.php
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("heritage")
                .appendPath("get_names.php");
        final String url = builder.build().toString();

        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {


                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;
                    int i;
                    String locationName;
                    for (i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        locationName = jsonObject.getString("locname");
                        placesArray.add(locationName);
                    }
                    isDownloaded = true;
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                            android.R.layout.simple_dropdown_item_1line, placesArray);
                    searchField.setAdapter(adapter);
                } catch (JSONException e) {
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error getting data, try again later...", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Couldn't update data.", Toast.LENGTH_SHORT).show();

            }
        });
        MySingleton.getInstance(context).addToRequestQueue(jsonObjReq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.intro_btn :{
                Intent i = new Intent(MapsActivity.this, WelcomeActivity.class);
                i.putExtra("start","true");
                startActivity(i);
                // close this activity
                finish();
            }
            case R.id.search_go_btn: {

                if (isVisible) {
                    animateSearchOut();
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mMap.getUiSettings().setCompassEnabled(true);
                    removeSearchResult();
                    isSearchResultShown=false;

                } else {
                    if (!isDownloaded) getSuggestions();
                    animateSearchIn();
                    searchField.setHint(R.string.search_hint);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mMap.getUiSettings().setCompassEnabled(false);
                }

                return true;
            }
            case R.id.trail_btn: {

                if (trailCount == 0) {
                    item.setIcon(R.drawable.ic_trail_selected);
                    trailCount =1;
                    isTrail1Shown=true;
                    startTrail(trailCount);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Showing Trail (1/2)", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else if(trailCount == 1){
                    item.setIcon(R.drawable.ic_trail_selected1);
                    trailCount =2;
                    isTrail1Shown=false;
                    isTrail2Shown=true;
                    startTrail(trailCount);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Showing Trail (2/2)", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else {
                    if (!isDownloaded) getSuggestions();
                    item.setIcon(R.drawable.ic_trail_deselected);
                    trailCount = 0;
                    isTrail2Shown=false;
                    for(Marker marker: mTrail2Markers){
                        marker.remove();
                    }
                }

                return true;
            }
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;
            case R.id.bus_route:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                if (!isBusRouteShown) {

                    if (polyline != null) {
                        mMap.addPolyline(lineOptions);
                        item.setIcon(R.drawable.ic_bus_selected);
                        setUpClusterer();
                        showCampusBoundary();

                    } else {
                        progress.setMessage("Getting bus route.");
                        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                        progress.show();

                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                        buildDirectionsUri();
                        setUpClusterer();
                        showBusRoute();

                    }
                    isBusRouteShown = true;

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        item.setIcon(getResources().getDrawable(R.drawable.ic_bus_deselected, this.getTheme()));
                    }

                    polyline.setVisible(false);
                    item.setIcon(R.drawable.ic_bus_deselected);
                    isBusRouteShown = false;

                    mMap.clear();
                    mMap.setOnCameraIdleListener(null);
                    mMap.setOnMarkerClickListener(null);
                    showCampusBoundary();

                    searchResultMarkers=new ArrayList<>();
                    if(isSearchResultShown){
                        for(MarkerOptions markerOptions : searchResultMarkerOptions){
                            searchResultMarkers.add(mMap.addMarker(markerOptions));
                        }
                    }
                    mTrail1Markers=new ArrayList<>();
                    mTrail2Markers= new ArrayList<>();
                    if(isTrail1Shown){
                        for(MarkerOptions markerOptions:mTrail1MarkerOptions){
                            mTrail1Markers.add(mMap.addMarker(markerOptions));
                        }
                    }else if(isTrail2Shown){
                        for(MarkerOptions markerOptions:mTrail2MarkerOptions){
                            mTrail1Markers.add(mMap.addMarker(markerOptions));
                        }
                    }

                    if(!mCurrLocationMarker.isVisible()){
                        mMap.addMarker(mHeritageCenterMarkerOptions);
                    }

                }

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void showCampusBoundary(){
        if (layer == null) {
            try {
                layer = new KmlLayer(mMap, R.raw.boundary, getApplicationContext());
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            layer.addLayerToMap();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not load boundary", Toast.LENGTH_SHORT).show();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            Toast.makeText(this, "Could not load boundary", Toast.LENGTH_SHORT).show();
        }
    }

    private void buildDirectionsUri() {

        LatLng origin = MAIN_GATE;
        LatLng dest = JAM_BUS_STOP;

        String str_origin = origin.latitude + "," + origin.longitude;

        String str_dest = dest.latitude + "," + dest.longitude;

        String waypoints = "12.991780,80.233772|12.990925,80.231896|12.990287,80.227627|12.987857,80.223127|12.989977,80.227707|12.988204,80.230125|12.986574,80.233254|12.988461,80.223328";

        // Sensor enabled
        String sensor_is = "false";

        //https://maps.googleapis.com/maps/api/directions/outputFormat?parameters
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("directions")
                .appendPath("json")
                .appendQueryParameter("origin", str_origin)
                .appendQueryParameter("destination", str_dest)
                .appendQueryParameter("waypoints", waypoints)
                .appendQueryParameter("sensor", sensor_is);

        // Building the url to the web service
        url = builder.build().toString();
    }


    private void startTrail(final Integer trailCount) {

        final String url = getString(R.string.trail_url)+trailCount;
        final ArrayList<Marker> trailMarkers = new ArrayList<>();
        final ArrayList<MarkerOptions> trailMarkerOptions= new ArrayList<>();
        // Request a string response from the provided URL.
        //Toast.makeText(MapsActivity.this,url, Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            for(Marker marker: mTrail1Markers){
                                marker.remove();
                            }
                            //Toast.makeText(MapsActivity.this,response, Toast.LENGTH_SHORT).show();
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject;
                            int i;
                            String locationName, latitude = "12.991780", longitude = "80.233772";
                            LatLng latLong;
                            for (i = 0; i < jsonArray.length(); i++) {
                                jsonObject = jsonArray.getJSONObject(i);
                                locationName = jsonObject.getString("locname");
                                String location_url = jsonObject.getString("file_name");
                                latitude = jsonObject.getString("lat");
                                longitude = jsonObject.getString("long");

                                latLong = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .title(locationName)
                                        .snippet("Click for more info...")
                                        .position(latLong);
                                Marker currMarker = mMap.addMarker(markerOptions);
                                currMarker.setTag(location_url);
                                trailMarkerOptions.add(markerOptions);
                                trailMarkers.add(currMarker);

                            }
                            if (pDialog.isShowing()) pDialog.dismiss();
                            LatLng latLngGC;
                            if (jsonArray.length() == 1) {
                                latLngGC = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngGC, 18));
                            } else {
                                LatLng latLngOAT = new LatLng(12.989281, 80.233585);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngOAT, 16));
//                                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Showing all related results...", Snackbar.LENGTH_LONG);
//                                snackbar.show();
                            }
                            if(trailCount==1){
                                mTrail1Markers =trailMarkers;
                                mTrail1MarkerOptions= trailMarkerOptions;
                            }else{
                                mTrail2Markers =trailMarkers;
                                mTrail2MarkerOptions= trailMarkerOptions;
                            }
                        } catch (JSONException e) {

                            if (pDialog.isShowing()) pDialog.dismiss();
                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "No result found!", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            //.makeText(MapsActivity.this,String.valueOf(e),Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

        int MY_SOCKET_TIMEOUT_MS = 10000;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void animateSearchOut() {
        if (!isVisible) return;
        searchField.animate()
                .translationX(searchBarPosX)
                .translationY(searchBarPosY)
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        searchField.setVisibility(View.GONE);
                    }
                });
        isVisible = false;
        MenuItem item = myMenu.findItem(R.id.search_go_btn);
        item.setIcon(R.drawable.ic_search_deselected);
        searchField.setText("");

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
    }

    private void animateSearchIn() {
        if (isVisible) return;
        searchField.animate()
                .translationYBy(myToolbar.getHeight())
                .setDuration(100)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        searchField.setVisibility(View.VISIBLE);
                    }
                });
        isVisible = true;
        MenuItem item = myMenu.findItem(R.id.search_go_btn);
        item.setIcon(R.drawable.ic_search_selected);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng testGC = new LatLng(12.991780, 80.233772);

        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(testGC, 17));

        mMap = googleMap;
        showCampusBoundary();
        // Set a listener for info window events.
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String url_link =(String)marker.getTag();
                //Toast.makeText(MapsActivity.this,url_link,Toast.LENGTH_LONG).show();
                Intent i = new Intent(MapsActivity.this, in.ac.iitm.students.heritagetrails.MySingleton.AboutActivity.class);
                i.putExtra("url",url_link);
                startActivity(i);
            }
        });

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(false);
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            mMap.setPadding(0, actionBarHeight, 0, 0);
        }

    }


    protected void showBusRoute(){

        Request request = new JsonRequest< List<List<HashMap<String, String>>>>(Request.Method.GET, url, null, new Response.Listener<List<List<HashMap<String, String>>>>() {

            @Override
            public void onResponse(List<List<HashMap<String, String>>> response) {
                ArrayList<LatLng> points;
                lineOptions = null;

                // Traversing through all the routes
                if (response != null) {
                    for (int i = 0; i < response.size(); i++) {
                        points = new ArrayList<>();
                        lineOptions = new PolylineOptions();
                        // Fetching i-th route
                        List<HashMap<String, String>> path = response.get(i);

                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                        lineOptions.width(4);
                        lineOptions.color(ContextCompat.getColor(context, R.color.polyline_blue));


                    }
                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    polyline = mMap.addPolyline(lineOptions);
                    isBusRouteShown = true;
                    MenuItem item = myMenu.findItem(R.id.bus_route);
                    item.setIcon(R.drawable.ic_bus_selected);
                    if (progress.isShowing()) progress.dismiss();
                } else {
                    if (progress.isShowing()) {
                        isBusRouteShown = false;
                        progress.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Error getting route, try again later.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
                // error.networkResponse.statusCode
                // error.networkResponse.data
                isBusRouteShown = false;
                Toast.makeText(MapsActivity.this, "Loading failed!", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Response<List<List<HashMap<String, String>>>> parseNetworkResponse(NetworkResponse response) {
                String jsonString = null;
                try {
                    jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;

                try {
                    jObject = new JSONObject(jsonString);
                    DataParser parser = new DataParser();

                    // Starts parsing data
                    routes = parser.parse(jObject);
                } catch (Exception e) {
                    if (progress.isShowing()) {
                        progress.dismiss();
                        isBusRouteShown = false;
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Error parsing data, try again later...", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    e.printStackTrace();
                }

                Response<List<List<HashMap<String, String>>>> result = Response.success(routes, HttpHeaderParser.parseCacheHeaders(response));
                return result;
            }
        };
        MySingleton.getInstance(MapsActivity.this).addToRequestQueue(request);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(12.9905663,80.2322976);
        mHeritageCenterMarkerOptions = new MarkerOptions();
        mHeritageCenterMarkerOptions.position(latLng);
        mHeritageCenterMarkerOptions.title("Heritage Center");
        mHeritageCenterMarkerOptions.snippet("Click for more info...");
        mHeritageCenterMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mCurrLocationMarker = mMap.addMarker(mHeritageCenterMarkerOptions);
        mCurrLocationMarker.showInfoWindow();
        mCurrLocationMarker.setTag("about.html");

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.

                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }



    private void setUpClusterer() {

        if(mClusterManager==null){
            // Initialize the manager with the context and the map.
            mClusterManager = new ClusterManager<>(this, mMap);


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GAJENDRA_CIRCLE_BUS_STOP, 14));

            // Add cluster items (markers) to the cluster manager.
            addItems();
        }

        // Point the map's listeners at the listeners implemented by the cluster
        // manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setRenderer(new OwnIconRendered(this, mMap, mClusterManager));

    }


    private void addItems() {

        ArrayList<ClusterMarkerLocation> items = new ArrayList<>();
        items.add(new ClusterMarkerLocation(MAIN_GATE));
        items.add(new ClusterMarkerLocation(GAJENDRA_CIRCLE_BUS_STOP));
        items.add(new ClusterMarkerLocation(HSB_BUS_STOP));
        items.add(new ClusterMarkerLocation(BT_BUS_STOP));
        items.add(new ClusterMarkerLocation(VELACHERY_GATE));
        items.add(new ClusterMarkerLocation(CRC_BUS_STOP));
        items.add(new ClusterMarkerLocation(TGH_BUS_STOP));
        items.add(new ClusterMarkerLocation(JAM_BUS_STOP));
        items.add(new ClusterMarkerLocation(NARMADA_BUS_STOP));
        items.add(new ClusterMarkerLocation(FOURTH_CROSS_STREET_BUS_STOP));
        items.add(new ClusterMarkerLocation(KV_BUS_STOP));
        items.add(new ClusterMarkerLocation(VANVANI_BUS_STOP));

        mClusterManager.addItems(items);

    }

    class OwnIconRendered extends DefaultClusterRenderer<ClusterMarkerLocation> {

        public OwnIconRendered(Context context, GoogleMap map,
                               ClusterManager<ClusterMarkerLocation> clusterManager) {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(ClusterMarkerLocation item, MarkerOptions markerOptions) {


            if (item.getPosition() == MAIN_GATE) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                        .alpha(0.6f)
                        .title("Main Gate");
            }
            if (item.getPosition() == JAM_BUS_STOP) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                        .alpha(0.6f)
                        .title("Jam Bus Stop");
            }
            if (item.getPosition() == GAJENDRA_CIRCLE_BUS_STOP) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                        .alpha(0.5f)
                        .title("Gajendra Circle Bus Stop");
            }
            if (item.getPosition() == HSB_BUS_STOP) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                        .alpha(0.5f)
                        .title("HSB Bus Stop");
            }
            if (item.getPosition() == VELACHERY_GATE) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                        .alpha(0.6f)
                        .title("Velachery Gate");
            }
            if (item.getPosition() == CRC_BUS_STOP) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                        .alpha(0.5f)
                        .title("CRC Bus Stop");
            }
            if (item.getPosition() == TGH_BUS_STOP) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                        .alpha(0.5f)
                        .title("TGH Bus Stop");
            }
            if (item.getPosition() == NARMADA_BUS_STOP) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                        .alpha(0.5f)
                        .title("Narmada Bus Stop");
            }
            if (item.getPosition() == BT_BUS_STOP) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                        .alpha(0.5f)
                        .title("BT Bus Stop");
            }
            if (item.getPosition() == FOURTH_CROSS_STREET_BUS_STOP) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                        .alpha(0.5f)
                        .title("4th Cross Street Bus Stop");
            }
            if (item.getPosition() == KV_BUS_STOP) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                        .alpha(0.5f)
                        .title("KV Bus Stop");
            }
            if (item.getPosition() == VANVANI_BUS_STOP) {
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bus_black_18dp))
                        .alpha(0.5f)
                        .title("Vanvani Bus Stop");
            }


            super.onBeforeClusterItemRendered(item, markerOptions);
        }
    }

}
