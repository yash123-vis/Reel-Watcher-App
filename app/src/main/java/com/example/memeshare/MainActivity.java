package com.example.memeshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
 String currenturl = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadmeme();

    }
    public void shareMeme(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Hey Checkout This cool meme: " + currenturl);

        // Create a chooser to display the available sharing apps
        Intent chooser = Intent.createChooser(intent, "Share this meme via");

        // Verify that the intent will resolve to an activity
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        } else {
            // Handle the case where no activity can handle the share intent
            // (optional: show a message to the user)
            Toast.makeText(this, "No app found to handle sharing", Toast.LENGTH_SHORT).show();
        }
    }


    public void nextmeme(View view) {
        loadmeme();
    }
    public void loadmeme() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        final String[] url = {"https://meme-api.com/gimme"};

        // Assuming you have a ProgressBar with the id 'progressBar' in your layout
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE); // Show progress bar

        // Request a string response from the provided URL.
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, url[0], null,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        if (response instanceof JSONObject) {
                            try {
                                JSONObject jsonResponse = (JSONObject) response;
                                currenturl = jsonResponse.getString("url");

                                // Assuming you have an ImageView with the id 'imageView' in your layout
                                ImageView imageView = findViewById(R.id.imageView);

                                // Use Glide to load the image into the ImageView
                                Glide.with(getApplicationContext()).load(currenturl).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE); // Hide progress bar on load failure
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        progressBar.setVisibility(View.GONE); // Hide progress bar when the image is ready
                                        return false;
                                    }
                                }).into(imageView);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE); // Hide progress bar on error
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}

