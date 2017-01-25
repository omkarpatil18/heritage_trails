package in.ac.iitm.students.heritagetrails;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by admin on 25-12-2016.
 */

public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private MySingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

    }

    public static synchronized MySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Created by sai_praneeth7777 on 03-Dec-16.
     */

    public static class AboutActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.about_heritage);
            String url_link= getIntent().getStringExtra("url");
            //Toast.makeText(AboutActivity.this,url_link,Toast.LENGTH_SHORT).show();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            WebView myWebView = (WebView) findViewById(R.id.web_about);
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            String url = getString(R.string.about_url)+url_link;
            //Toast.makeText(AboutActivity.this,url,Toast.LENGTH_SHORT).show();
            myWebView.loadUrl(url);

        }
        @Override
        public boolean onOptionsItemSelected(MenuItem menuItem) {
            if (menuItem.getItemId() == android.R.id.home) {
                onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(menuItem);
        }
    }
}