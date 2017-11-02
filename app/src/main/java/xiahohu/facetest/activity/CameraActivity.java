package xiahohu.facetest.activity;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.cmm.rkadcreader.adcNative;
import com.cmm.rkgpiocontrol.rkGpioControlNative;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import xiahohu.facetest.R;
import xiahohu.facetest.Util.FileUtil;
import xiahohu.facetest.Util.UtilToast;
import xiahohu.retrofit.Api;
import xiahohu.retrofit.ConnectUrl;


/**
 * Created by dhht on 16/9/29.
 */

public class CameraActivity extends Activity implements SurfaceHolder.Callback {

    @Bind(R.id.camera_sf)
    SurfaceView camera_sf;
    @Bind(R.id.text_card)
    TextView text_card;

    private Camera camera;
    private String filePath;
    private SurfaceHolder holder;
    private boolean isFrontCamera = true;
    private boolean safephoto  =true;
    private int width = 640;
    private int height = 480;
    Handler handler=new Handler();

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            int val = rkGpioControlNative.ReadGpio(4);
            if(val==0){
                if(safephoto){
                    safephoto = false;
                    text_card.setText("拍摄人脸照片！");
                    deleteFile();
                    camera.takePicture(null, null, jpeg);
                }else {
                    handler.postDelayed(this, 500);
                }
            }else {
                text_card.setText("");
                handler.postDelayed(this, 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera2);
        ButterKnife.bind(this);
        permiss();
        holder = camera_sf.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        initYingjian();
    }

    private void initYingjian() {
        rkGpioControlNative.init();
        handler.postDelayed(runnable, 1000);
    }

    private void deleteFile(){
        if(!TextUtils.isEmpty(filePath)){
            File file = new File(filePath);
            if(file!=null){
                if(file.exists()){
                    file.delete();
                }
            }
        }
    }


    private void uploadPhoto() {
        safephoto = true;
        camera.startPreview();
        handler.postDelayed( runnable, 1000);
        File  file = new File(filePath);
        if(!file.exists()){
            UtilToast.showToast(CameraActivity.this,"文件不存在！");
            handler.postDelayed(runnable, 1000);
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("photoImgFiles", file.getName(), requestBody);
        Api.getBaseApiWithOutFormat(ConnectUrl.URL)
                .uploadPhoto(builder.build().parts())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {


                    }

                    @Override
                    public void onError(Throwable e) {
                        text_card.setText("人脸检测成功！");
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                       // Log.i("sss",jsonObject.toString());
                        if(jsonObject!=null){
                            try {
                                JSONObject result = jsonObject.optJSONObject("result");
                                boolean isSuccess=  result.optBoolean("success");
                                if(isSuccess){
                                    text_card.setText("人脸检测成功！");
                                }else {
                                    text_card.setText("人脸检测失败！");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
    }


    private void permiss(){
        PermissionGen.needPermission(this, 200, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        });
    }

    @PermissionSuccess(requestCode = 200)
    public void toLocation() {

    }

    @PermissionFail(requestCode = 200)
    public void toLocationFail() {
        Toast.makeText(this, "请打开相机权限！", Toast.LENGTH_LONG).show();
    }



    public long getTime() {
        return Calendar.getInstance().getTimeInMillis();
    }


    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            filePath = FileUtil.getPath() + File.separator + getTime() + ".jpeg";
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.postRotate(270);
            BitmapFactory.Options factory = new BitmapFactory.Options();
            factory = setOptions(factory);
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length,factory);
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                bm1.compress(Bitmap.CompressFormat.JPEG,30, bos);
                bos.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null){
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                bm.recycle();
                bm1.recycle();
                stopPreview();
                uploadPhoto();
            }
        }
    };

    public static BitmapFactory.Options setOptions(BitmapFactory.Options opts) {
        opts.inJustDecodeBounds = false;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inSampleSize = 1;
        return opts;
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = openCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCamera();
        handler.removeCallbacks(runnable);
        FileUtil.deleteDir(FileUtil.getPath());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
    }

    private Camera openCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
            } catch (Exception e) {
                camera = null;
                e.printStackTrace();
            }
        }
        return camera;
    }
    private void startPreview() {
        Camera.Parameters para;
        if (null != camera) {
            para = camera.getParameters();
        } else {
            return;
        }

        // 选择合适的预览尺寸
//        List<Camera.Size> sizeList = para.getSupportedPreviewSizes();
//        if (sizeList.size() > 1) {
//            Iterator<Camera.Size> itor = sizeList.iterator();
//            while (itor.hasNext()) {
//                Camera.Size cur = itor.next();
//                Log.i("xxx",cur.width+"--"+cur.height);
//            }
//        }

        para.setPreviewSize(width, height);
        setPictureSize(para,640 , 480);
        para.setPictureFormat(ImageFormat.JPEG);//设置图片格式
        setCameraDisplayOrientation(isFrontCamera ? 0 : 1, camera);
        camera.setParameters(para);
        camera.startPreview();
    }

    private void stopPreview() {
        if (null != camera) {
            camera.stopPreview();
        }
    }

    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        rotation = 1;
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private void setPictureSize(Camera.Parameters para, int width, int height) {
        int absWidth = 0;
        int absHeight = 0;
        List<Camera.Size> supportedPictureSizes = para.getSupportedPictureSizes();
        for (Camera.Size size : supportedPictureSizes) {
            if (Math.abs(width - size.width) < Math.abs(width - absWidth)) {
                absWidth = size.width;
            }
            if (Math.abs(height - size.height) < Math.abs(height - absHeight)) {
                absHeight = size.height;
            }
        }
    }

    private void closeCamera() {
        if (null != camera) {
            try {
                camera.setPreviewDisplay(null);
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        adcNative.close(0);
        adcNative.close(2);
        rkGpioControlNative.close();
    }
}
