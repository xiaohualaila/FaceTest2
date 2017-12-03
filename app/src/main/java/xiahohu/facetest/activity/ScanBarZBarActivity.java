package xiahohu.facetest.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import xiahohu.facetest.R;

/**
 * Created by admin on 2017/12/3.
 */

public class ScanBarZBarActivity extends Activity implements SurfaceHolder.Callback{
    private static String TAG = "ScanBarZBarActivity";
    private Camera mCamera;
    //拍照、保存、继续拍
    private Button mButton, mButton1, mButton2;
    private SurfaceView mSurfaceView;
    private SurfaceHolder holder;
    private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();
    private String path = "lanjingling";//图片所在文件夹名
    private String path1;
    private Bitmap bmp;
    private Calendar c;
  //  public native String getISBN(Bitmap bmp);
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            /* 隐藏状态栏 */
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
            /* 隐藏标题栏 */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
            /* 屏幕显示可转换 */
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_scan);
            /* SurfaceHolder设定 */
        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        holder = mSurfaceView.getHolder();
        holder.addCallback(ScanBarZBarActivity.this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        c = Calendar.getInstance();

            /* Button初始化 */
        mButton = (Button) findViewById(R.id.myButton);
        mButton1 = (Button) findViewById(R.id.myButton1);
        mButton2 = (Button) findViewById(R.id.myButton2);
            /* 拍照Button的事件处理 */
        mButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                            /* 告动对焦后拍照 */
                mCamera.autoFocus(mAutoFocusCallback);
            }
        });
            /* Button的事件处理 */
        mButton1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                            /* 保存文件 */
                Log.i(TAG,"click button2");
                if (bmp != null) {
                                    /* 检查SDCard是否存在 */
                    if (!Environment.MEDIA_MOUNTED.equals(Environment
                            .getExternalStorageState())) {
                                            /* SD卡不存在，显示Toast信息 */
                        Toast.makeText(ScanBarZBarActivity.this,
                                "SD卡不存在!无法保存相片,请插入SD卡。", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                                                    /* 文件不存在就创建 */
                            File f = new File(Environment
                                    .getExternalStorageDirectory(), path);
                            Log.i(TAG,"click button2:" + f.getAbsolutePath());
                            if (!f.exists()) {
                                f.mkdir();
                            }
                                                    /* 保存相片文件 */
                            path1 = String.valueOf(c.get(Calendar.MILLISECOND))
                                    + "camera.jpg";
                            File n = new File(f, path1);
                            FileOutputStream bos = new FileOutputStream(n
                                    .getAbsolutePath());
                                                    /* 文件转换 */
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            bos.flush();
                            bos.close();
                            Toast.makeText(ScanBarZBarActivity.this,
                                    path1 + "保存成功!", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                mButton.setVisibility(View.VISIBLE);
                mButton1.setVisibility(View.VISIBLE);
                mButton2.setVisibility(View.VISIBLE);

            }
        });
            /* 点击继续拍照 */
        mButton2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, mCamera.toString());
                initCamera();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceholder) {
        try {
                    /* 打开相机， */
            mCamera = Camera.open();
            mCamera.setPreviewDisplay(holder);
            Log.i(TAG,"create camera---");
        } catch (IOException exception) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w,
                               int h) {
            /* 相机初始化 */
        Log.i(TAG,"init camera");
        initCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        Log.i(TAG,"destoryed camera");
        stopCamera();
        mCamera.release();
        mCamera = null;
    }

    /* 拍照的method */
    private void takePicture() {
        if (mCamera != null) {
            Log.i(TAG,"takePicture");
            mCamera.takePicture(null, null, jpegCallback);
        }
    }


    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
                    /* 取得相仞 */
            try {
                            /* 设定Button可见性 */
                mButton.setVisibility(View.VISIBLE);
                mButton1.setVisibility(View.VISIBLE);
                mButton2.setVisibility(View.VISIBLE);
                            /* 取得Bitmap对象 */
                bmp = BitmapFactory.decodeByteArray(_data, 0, _data.length);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /* 告定义class AutoFocusCallback */
    public final class AutoFocusCallback implements
            android.hardware.Camera.AutoFocusCallback {
        public void onAutoFocus(boolean focused, Camera camera) {
                    /* 对到焦点拍照 */
            if (focused) {
                takePicture();
            }
        }
    };

    /* 相机初始化的method */
    private void initCamera() {
        if (mCamera != null) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();

                parameters.setPictureFormat(PixelFormat.JPEG);
                parameters.setPictureSize(1024, 768);
                mCamera.setParameters(parameters);
                            /* 开启预览画面 */
                mCamera.startPreview();
                Log.i(TAG, "init camera!!!!!!------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* 停止相机的method */
    private void stopCamera() {
        if (mCamera != null) {
            try {
                            /* 停止预览 */
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
