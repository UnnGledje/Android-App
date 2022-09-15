
/* Detta är en app som visar tiden, genom en NTP server och genom system klockan. Genom att trycka på
"Connectivity" knappen så kan du stänga av och på internetåtkomsten. Det enda jag inte hann klart med varr
"Pause" and "Resume" funktionerna */


package com.example.dnk_project_unn_gledje.main;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dnk_project_unn_gledje.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


@SuppressLint("SimpleDateFormat")
public class MainActivity extends AppCompatActivity {

    NTP ntpServer = new NTP();

    private WifiManager wifiManager;

    public SimpleDateFormat simpleDateFormat;

    {
        simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final TextView textView = findViewById(R.id.timeField);
        final Button startButton = findViewById(R.id.button);
        final Button wiFiButton = findViewById(R.id.button2);


                        // When you press Start
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Button startButton = (Button) v;
            startButton.setText("Pause");
                            /* Change text on the former start button, the idea was
                                to toggle betwwen two dofferent states to paus and play the clock */

                String nptTime = null;
                try {
                    nptTime = simpleDateFormat.format(ntpServer.getNTPTime());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String systemTime;
                systemTime = simpleDateFormat.format(new Date());

                            /* Checking for internet connection to see wgether to update the clock
                            with NTPServer time or System time */
                if (findConnection()) {
                    try {
                        textView.setText(nptTime);
                        System.out.println("Time from " + ": " + ntpServer.getNTPTime());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    textView.setText(systemTime);
                    Log.d("system", "systemTime" + new Date());
                }
                updateTime();
            }
        });
    }

                //Checking whether the device has Internet connection by returning 'true' or 'false'
    public boolean findConnection() {
        Button wiFiButton = findViewById(R.id.button2);
        boolean b;
        b = wiFiButton.getText() != "Connect";
        return b;
    }
            // If connection is true, update with NTPTIOme, else with System time
            // Displaying time to th screen
    public void updateTextTimeView() throws IOException {
        TextView textView = findViewById(R.id.timeField);
        String ntpTime;
        String systemTime;

        if (findConnection()) {
            ntpTime = simpleDateFormat.format(ntpServer.getNTPTime());
            Log.d("utv", "ntp updateTimeView");
            textView.setText(ntpTime);
        } else {
            systemTime = simpleDateFormat.format(new Date());
            Log.d("utv", "system updateTimeView");
            textView.setText(systemTime);
        }
    }
                /*
                Utilizing a timer to schedule a task every time one minute
                has passed
                 */
        public void updateTime() {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                updateTextTimeView();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }, 0, 60000);//60000 is a refreshing Time one minute
        }
                /*
                The Java functions --> setWifiEnabled among others are deprecated.
                Instead i found a way to turn the internet connection on by calling an Intent.
                This seems to be a better way to handle this task than the deprecated functions
                since the setWifiEnabled function only was available to android's having Android Q and above.
                 */
        public void handleConnect(View v){
                    // If it is Android Q and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                startActivity(panelIntent);
                if (wifiManager.isWifiEnabled()) {
                    Button button = (Button) v;
                    button.setText("Connect");
                } else {
                    Button button = (Button) v;
                    button.setText("Disconnect");
                }
            }



        }
    }




