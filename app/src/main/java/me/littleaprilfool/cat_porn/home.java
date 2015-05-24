package me.littleaprilfool.cat_porn;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.hardware.Camera;

import java.io.IOException;

public class home extends Activity {

    public boolean if_connect;
    public boolean if_record;


    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Button cButton;
    private Button rButton;

    private void InitCamera(){
        Log.d("linc","start camera");
        try{
            mCamera = Camera.open();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStart(){
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Log.d("linc","start");
        InitCamera();
        super.onStart();
        try{
            if (mCamera != null){
                Log.d("linc","setdisplay");
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        super.onStart();
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
                    if_connect = true;
                    rButton.setEnabled(true);
                    cButton.setText("disconnect");
                }
            }
        });
    }
}
