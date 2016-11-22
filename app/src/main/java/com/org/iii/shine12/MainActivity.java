package com.org.iii.shine12;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // UDP Sender
    public void test1(View v) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                byte[] buf = "Hello, Shine".getBytes();
                try {
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(buf, buf.length,
                            InetAddress.getByName("10.0.3.2"), 8888);
                    socket.send(packet);
                    socket.close();
                    Log.v("shine", "UDP Send OK");
                } catch (Exception e) {
                    Log.v("shine", e.toString());
                }
            }
        }.start();
    }

}
