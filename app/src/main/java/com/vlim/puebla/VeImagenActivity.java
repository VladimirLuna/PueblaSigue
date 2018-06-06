package com.vlim.puebla;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

public class VeImagenActivity extends AppCompatActivity {

    String url_foto = null;
    String url_video = null;
    ImageView img_foto;
    VideoView videoView;
    String TAG = "VLIMDEV";
    // Toolbar
    TextView tv_titulo_toolbar;
    ImageView btn_back;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ve_imagen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/BoxedBook.otf");

        img_foto = (ImageView) findViewById(R.id.img_foto);
        videoView = (VideoView) findViewById(R.id.videoView);

        // Toolbar
        tv_titulo_toolbar = (TextView) findViewById(R.id.tv_titulo_toolbar);
        tv_titulo_toolbar.setTypeface(tf);
        btn_back = (ImageView) findViewById(R.id.btn_back);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            Log.i(TAG, "urlfotoRecibe: " + extras.getString("urlfoto"));

            url_foto = extras.getString("urlfoto");
            url_video = extras.getString("urlvideo");
            if(url_foto == null){
                Log.e(TAG, "No hay foto");
            }
            else if(url_video == null){
                Log.e(TAG, "No hay video");
            }
            else{
                Log.i(TAG, "foto: " + url_foto + ", vid: " + url_video);

                if(!url_foto.equals("0") ){
                    img_foto.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.INVISIBLE);
                    Picasso.with(getApplicationContext()).load(url_foto).into(img_foto);
                }
                else if(!url_video.equals("0")){
                    Log.d(TAG, "muestra video");
                    img_foto.setVisibility(View.INVISIBLE);
                    videoView.setVisibility(View.VISIBLE);

                   /* Uri uri = Uri.parse(url_video);
                    Uri.fromFile(new File(url_video));
                    videoView.setVideoURI(uri);
                        /*videoView.setVideoPath(url_video);
                        videoView.start();*/

                    /*videoView.setVideoURI(Uri.parse(url_video));
                    videoView.setMediaController(new MediaController(this));
                    videoView.bringToFront();
                    videoView.requestFocus();
                    videoView.start();*/

                    /*Uri uri = Uri.parse(url_video);  // tarda muuucho
                    videoView.setVideoURI(uri);
                    videoView.start();*/

                    pDialog = new ProgressDialog(this);

                    // Set progressbar message
                    pDialog.setMessage("Cargando video...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(true);
                    // Show progressbar
                    pDialog.show();

                    try {
                        // Start the MediaController
                        MediaController mediacontroller = new MediaController(this);
                        mediacontroller.setAnchorView(videoView);

                        Uri videoUri = Uri.parse(url_video);
                        videoView.setMediaController(mediacontroller);
                        videoView.setVideoURI(videoUri);

                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                    videoView.requestFocus();
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        // Close the progress bar and play the video
                        public void onPrepared(MediaPlayer mp) {
                            pDialog.dismiss();
                            videoView.start();
                        }
                    });
                    videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            if (pDialog.isShowing()) {
                                pDialog.dismiss();
                            }
                            finish();
                        }
                    });

                }
            }

        }else {
            url_foto = (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() { }
}
