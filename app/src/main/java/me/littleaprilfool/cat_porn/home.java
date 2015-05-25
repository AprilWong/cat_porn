package me.littleaprilfool.cat_porn;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.hardware.Camera;
import android.os.StrictMode;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class home extends Activity implements Camera.PreviewCallback{

    public boolean if_connect;
    public boolean if_record;


    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Button cButton;
    private Button rButton;
    private int wHeight;
    private int wWidth;
    private int vFormat;
    private int vQuality;
    private String sIp = "192.168.19.111";
    private int sPort = 8787;
    private Socket socket;

    private int picnum = 0;

    private void InitCamera(){
        Log.d("linc","start camera");
        try{
            mCamera = Camera.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    class SendFile extends Thread {
        private ByteArrayOutputStream outstream;
        public SendFile(ByteArrayOutputStream outstream){
            this.outstream = outstream;
            try {
                outstream.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }

        public void send(){
            try {
                Log.d("socket","send imagefrane");
                socket = new Socket(sIp,sPort);
                Log.d("socket","finish create");
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                String data = "hello,world";
                out.println(data);
                out.flush();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera){
        if (!if_record) return;

        try {
            YuvImage image = new YuvImage(data,vFormat,wWidth,wHeight,null);
            ByteArrayOutputStream tmpstream = new ByteArrayOutputStream();
            ByteArrayOutputStream outstream = new ByteArrayOutputStream();

            image.compressToJpeg(new Rect(0,0,wWidth,wHeight),vQuality,tmpstream);
            tmpstream.flush();
            byte[] tdata = tmpstream.toByteArray();

            Bitmap bitmap = BitmapFactory.decodeByteArray(tdata,0,tdata.length);

            bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outstream);

            outstream.flush();

        } catch (IOException e){
            e.printStackTrace();
        }

//        if (picnum == 0){
//            picnum++;
//            Log.d("camera","start to write in file");
//            try {
//                String path = Environment.getExternalStorageDirectory().getPath();
//                File f = new File(path,"test.webp");
//                f.createNewFile();
//                FileOutputStream fos = new FileOutputStream(f);
//
//                YuvImage image = new YuvImage(data,vFormat,wWidth,wHeight,null);
//                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
//
//                image.compressToJpeg(new Rect(0,0,wWidth,wHeight),vQuality,outstream);
//                outstream.flush();
//                byte[] tdata = outstream.toByteArray();
//
//                Bitmap bitmap = BitmapFactory.decodeByteArray(tdata,0,tdata.length);
//
//                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, fos);
//                fos.close();
//
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void onStart(){
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Log.d("linc", "start");
        super.onStart();
        InitCamera();
        try{
            if (mCamera != null){
                Log.d("linc","setdisplay");
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewCallback(this);

                wHeight = mCamera.getParameters().getPreviewSize().height;
                wWidth = mCamera.getParameters().getPreviewSize().width;
                vFormat = mCamera.getParameters().getPreviewFormat();
                vQuality = 100;

                Log.d("camera-height", Integer.toString(wHeight));
                Log.d("camera-width",Integer.toString(wWidth));



                mCamera.startPreview();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        try {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mSurfaceView = (SurfaceView)findViewById(R.id.surface);
        cButton = (Button)findViewById(R.id.connect_button);
        rButton = (Button)findViewById(R.id.record_button);
        if_connect = false;
        if_record = false;
        rButton.setEnabled(false);

        Log.d("linc", "oncreate");

        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(if_record){
                    if_record = false;
                    rButton.setText("start");
                }
                else{
                    if_record = true;
                    rButton.setText("stop");
                }
            }
        });

        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (if_connect){
                    if_connect = false;
                    if_record = false;
                    rButton.setEnabled(false);
                    rButton.setText("start");
                    cButton.setText("connect");
                }
                else{
                    try {
                        Log.d("socket","connecting server");
                        socket = new Socket(sIp,sPort);
                        Log.d("socket","finish create");
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                        String data = "hello,world";
                        out.println(data);
                        out.flush();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    if_connect = true;
                    rButton.setEnabled(true);
                    cButton.setText("disconnect");
                }
            }
        });
    }
}
