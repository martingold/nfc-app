package com.martingold.nfcreader;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.eftimoff.androipathview.PathView;
import com.nineoldandroids.animation.Animator;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "nfc-reader";
    private NfcAdapter mNfcAdapter;

    Runnable shake;
    Timer timer;
    PathView pathView;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected String getServiceName() {
        return "main";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pathView = (PathView) findViewById(R.id.pathView);
        pathView.useNaturalColors();





        YoYo.with(Techniques.SlideInUp)
                .duration(2000)
                .delay(200)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(pathView);

        YoYo.with(Techniques.ZoomIn)
                .duration(500)
                .delay(0)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(findViewById(R.id.main_info));

    }

    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer = new Timer();
        final Handler handler = new Handler();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(shake);
            }

        }, 3000, 3000);
        shake = new Runnable() {
            public void run() {
                YoYo.with(Techniques.Swing)
                        .duration(1500)
                        .delay(0)
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .playOn(pathView);
            }
        };
        pathView.getPathAnimator()
                .delay(200)
                .duration(2000)
                .interpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_write) {
            Intent i = new Intent(MainActivity.this, WriteActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
