package com.org.iii.shine12;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private UIHandler uiHandler;
    private ImageView imageView;
    private Bitmap bmp;
    private String urlDownload =  "http://www.iii.org.tw";

    private File sdroot;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiHandler = new UIHandler();
        textView = (TextView)findViewById(R.id.tv);
        imageView = (ImageView)findViewById(R.id.img);

        // 1. 權限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            // request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }

        // 2. 環境
        init();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        init();
    }

    private  void init(){
        sdroot = Environment.getExternalStorageDirectory(); //相對位置

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Download...");
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
    //TCP
    public void test2(View v){
        new Thread(){
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(InetAddress.getByName("10.0.3.2"), 9999);
                    socket.close();
                    Log.v("shine", "TCP Client OK");
                }catch (Exception e){
                    Log.v("shine", e.toString());
                }
            }
        }.start();
    }
    //網路原始碼
    public void test3(View v){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://www.iii.org.tw/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(conn.getInputStream()));
                    String line; StringBuilder sb = new StringBuilder();
                    while ( (line = reader.readLine()) != null){
                        sb.append(line + "\n");
                    }
                    reader.close();

                    Message mesg = new Message();
                    Bundle data = new Bundle();
                    data.putCharSequence("data", sb);
                    mesg.setData(data);
                    mesg.what=0;
                    uiHandler.sendMessage(mesg);


                } catch (Exception e) {
                    Log.v("shine", e.toString());
                }

            }
        }.start();
    }
    //網路抓圖
    public void test4(View v){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url=new URL("https://www.androidcentral.com/sites/androidcentral.com/files/styles/large/public/postimages/%5Buid%5D/podcast-ac-new_0.jpg?itok=GFz47App");
                    HttpURLConnection conn =(HttpURLConnection)url.openConnection();
                    conn.connect();

                    Bitmap bmp = BitmapFactory.decodeStream(conn.getInputStream());

                    Message mesg = new Message();
                    Bundle data = new Bundle();
                    data.putParcelable("data", bmp);
                    mesg.setData(data);
                    mesg.what = 1;
                    uiHandler.sendMessage(mesg);

                    //imageView.setImageBitmap(bmp);

                } catch (Exception e) {
                    Log.v("shine", e.toString());
                }

            }
        }.start();
    }
    //網路抓圖
    public void test5(View v){
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url=new URL("https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcTH6o94pXjlbgWnixbCp55w5psiV6tK3n0Q4Nj3utIignzKzGwN");
                    HttpURLConnection conn =(HttpURLConnection)url.openConnection();
                    conn.connect();

                    bmp = BitmapFactory.decodeStream(conn.getInputStream());
                    uiHandler.sendEmptyMessage(2);


                } catch (Exception e) {
                    Log.v("shine", e.toString());
                }

            }
        }.start();
    }
    
    //下載檔案
    public void test6(View v){
        new Thread(){
            @Override
            public void run() {
                try {
                    MultipartUtility mu = new MultipartUtility("http://10.0.3.2/add2.php", "UTF-8");
                    mu.addFormField("account", "mark");
                    mu.addFormField("passwd", "654321");
                    List<String> ret =  mu.finish();
                    Log.v("shine", ret.get(0));
                }catch (Exception e){
                    Log.v("shine", e.toString());
                }
            }
        }.start();
    }
    // Download save to SDCard
    public void test7(View v){
        pDialog.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL("http://pdfmyurl.com/?url=" + urlDownload);
                    HttpURLConnection conn =(HttpURLConnection) url.openConnection();

                    conn.connect();

                    InputStream in = conn.getInputStream();
                    File download = new File(sdroot,"shine.pdf");
                    FileOutputStream fout = new FileOutputStream(download);
                    byte[] buf = new byte[4096]; int len;
                    while ((len=in.read(buf)) != -1){
                        fout.write(buf,0,len);
                    }

                    fout.flush();
                    fout.close();
                    Log.v("shine", "Download OK");
                    uiHandler.sendEmptyMessage(3);

                } catch (Exception e) {
                    Log.v("brad", "Download :" + e.toString());
                    uiHandler.sendEmptyMessage(3);
                }
            }
        }.start();
    }

    // Upload to My Server => add2.php
    public void test8(View v){
        new Thread(){
            @Override
            public void run() {
                try {
                    MultipartUtility mu = new MultipartUtility("http://10.0.3.2/add2.php", "UTF-8");
                    mu.addFormField("account", "mark");
                    mu.addFormField("passwd", "654321");
                    mu.addFilePart("upload", new File(sdroot, "shine.pdf"));
                    List<String> ret =  mu.finish();
                    Log.v("shine", ret.get(0));
                }catch (Exception e){
                    Log.v("shine", e.toString());
                }

            }
        }.start();
    }

    private class UIHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        switch (msg.what){
            case 0:
                textView.setText(msg.getData().getCharSequence("data"));
                break;
            case 1:
                imageView.setImageBitmap((Bitmap)msg.getData().getParcelable("data"));
                break;
            case 2:
                imageView.setImageBitmap(bmp);
                break;
            case 3:
                pDialog.dismiss();
                break;
            }
        }
    }


}
