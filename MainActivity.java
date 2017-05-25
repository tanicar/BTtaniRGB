package com.rgbtani.andreafioroni.bttanirgb;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ToggleButton;
import android.widget.SeekBar;
import android.widget.TextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

// 2 TODO to be solved

public class MainActivity extends AppCompatActivity {

    // BT variables
    public UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter btAdapter = null;
    BluetoothSocket btSocket = null;
    BluetoothDevice btDevice = null;

    OutputStream outStream;

    // Widget variables
    private ToggleButton connectButton;
    private ToggleButton rButton;
    private ToggleButton gButton;
    private ToggleButton bButton;
    private SeekBar rSeek;
    private SeekBar gSeek;
    private SeekBar bSeek;
    private TextView rText;
    private TextView gText;
    private TextView bText;

    // Color variables (between 0 and 255)
    public int redValue = 255;
    public int greenValue = 100;
    public int blueValue = 20;
    public int redValueOld = 255;
    public int greenValueOld = 100;
    public int blueValueOld = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "App by Andrea Fioroni (andrifiore@gmail.com)", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        connectButton = (ToggleButton) findViewById(R.id.connectButton) ;
        rButton = (ToggleButton) findViewById(R.id.rButton) ;
        gButton = (ToggleButton) findViewById(R.id.gButton) ;
        bButton = (ToggleButton) findViewById(R.id.bButton) ;
        rSeek = (SeekBar) findViewById(R.id.rSeekBar);
        gSeek = (SeekBar) findViewById(R.id.gSeekBar);
        bSeek = (SeekBar) findViewById(R.id.bSeekBar);
        rText = (TextView) findViewById(R.id.rText);
        gText = (TextView) findViewById(R.id.gText);
        bText = (TextView) findViewById(R.id.bText);

        rButton.setChecked(true);
        gButton.setChecked(true);
        bButton.setChecked(true);

        rText.setText("" + redValue);
        gText.setText("" + greenValue);
        bText.setText("" + blueValue);

        reloadSliders();

        // BT CONNETCION
        addBTConnectionListener();

        // Color buttons
        addColorButtonAndSliderListener();

    }

    // there's a TODO inside (mandatory)
    private void addBTConnectionListener() {
        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (connectButton.isChecked()) { // the button is checked
                    btAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (btAdapter == null) { // BT not supported
                        connectButton.setChecked(false);
                    } else { // BT supported
                        if (!btAdapter.isEnabled()) { // BT is not enabled
                            connectButton.setChecked(false);
                        } else { // BT is enabled
                            btDevice = btAdapter.getRemoteDevice(""); // TODO ADD MAC ADDRESS HERE
                            try {
                                btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
                            } catch (IOException e) {
                                connectButton.setChecked(false);
                            }
                            try { // connect trough the socket
                                btSocket.connect();
                                outStream = btSocket.getOutputStream();
                            } catch (IOException e) {
                                connectButton.setChecked(false);
                                try {
                                    btSocket.close();
                                } catch (IOException ex) {
                                    // do nothing
                                }
                            }
                        } // BT is enabled
                    } // BT supported
                } // the button is checked
                else {
                    try {
                        outStream.close();
                        btSocket.close();
                    } catch (IOException e) {
                        // do nothing
                    }
                }
            } // onClick function
        });
    }

    // there's a TODO inside
    private void sendBTMessage() { // ENCODED STRING WILL BE: RRRGGGBBB
        if(outStream == null) { // something has gone wrong
            return;
        }
        String message = "";
        if(redValue<100) {
            message += "0";
            if(redValue<10) {
                message += "0";
            }
        }
        message += redValue;
        if(greenValue<100) {
            message += "0";
            if(greenValue<10) {
                message += "0";
            }
        }
        message += greenValue;
        if(blueValue<100) {
            message += "0";
            if(blueValue<10) {
                message += "0";
            }
        }
        message += blueValue;

        byte[] msgBuffer = message.getBytes();
        try{
            outStream.write(msgBuffer);
        } catch(IOException e) {
            // do nothing
        }
    }

    private void reloadSliders() {
        rSeek.setProgress(redValue);
        gSeek.setProgress(greenValue);
        bSeek.setProgress(blueValue);
    }

    private void addColorButtonAndSliderListener() {
        rButton.setOnClickListener(new View.OnClickListener(){ // RED BUTTON LISTENER
            public void onClick(View view) {
                if(!rButton.isChecked()) { // is not checked
                    rButton.setChecked(false);
                    rButton.setBackgroundColor(Color.WHITE);
                    redValueOld = redValue;
                    redValue = 0;
                    rSeek.setEnabled(false);
                } else { // is already checked
                    rButton.setChecked(true);
                    rButton.setBackgroundColor(Color.RED);
                    redValue = redValueOld;
                    rSeek.setEnabled(true);
                }
                sendBTMessage();
                reloadSliders();
            }
        });
        gButton.setOnClickListener(new View.OnClickListener(){ // GREEN BUTTON LISTENER
            public void onClick(View view) {
                if(!gButton.isChecked()) { // is not checked
                    gButton.setChecked(false);
                    gButton.setBackgroundColor(Color.WHITE);
                    greenValueOld = greenValue;
                    greenValue = 0;
                    gSeek.setEnabled(false);
                } else { // is already checked
                    gButton.setChecked(true);
                    gButton.setBackgroundColor(Color.GREEN);
                    greenValue = greenValueOld;
                    gSeek.setEnabled(true);
                }
                sendBTMessage();
                reloadSliders();
            }
        });
        bButton.setOnClickListener(new View.OnClickListener(){ // BLUE BUTTON LISTENER
            public void onClick(View view) {
                if(!bButton.isChecked()) { // is not checked
                    bButton.setChecked(false);
                    bButton.setBackgroundColor(Color.WHITE);
                    blueValueOld = blueValue;
                    blueValue = 0;
                    bSeek.setEnabled(false);
                } else { // is already checked
                    bButton.setChecked(true);
                    bButton.setBackgroundColor(Color.BLUE);
                    blueValue = blueValueOld;
                    bSeek.setEnabled(true);
                }
                sendBTMessage();
                reloadSliders();
            }
        });
        rSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (rSeek.isEnabled()) {
                    redValue = rSeek.getProgress();
                    rText.setText(""+redValue);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });
        gSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(gSeek.isEnabled()) {
                    greenValue = gSeek.getProgress();
                    gText.setText(""+greenValue);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });
        bSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(bSeek.isEnabled()) {
                    blueValue = bSeek.getProgress();
                    bText.setText(""+blueValue);
                }

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // do nothing
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
