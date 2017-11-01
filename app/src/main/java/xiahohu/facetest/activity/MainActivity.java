package xiahohu.facetest.activity;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.faceplusplus.api.FaceDetecter;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import xiahohu.facetest.FaceMask;
import xiahohu.facetest.Util.FileUtil;
import xiahohu.facetest.R;
import xiahohu.facetest.Util.UtilToast;
import xiahohu.retrofit.Api;
import xiahohu.retrofit.ConnectUrl;
import xiahohu.view.ClearEditTextWhite;

public class MainActivity extends AppCompatActivity  implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private Camera camera;
    private String filePath;
    private SurfaceHolder holder;

    @Bind(R.id.camera_sf)
    SurfaceView camera_sf;
    @Bind(R.id.mask)
    FaceMask mask;
    @Bind(R.id.layout_wrap)
    FrameLayout layout_wrap;
    @Bind(R.id.text_no)
    ClearEditTextWhite text_no;

    @Bind(R.id.rephoto)
    TextView rephoto;
    @Bind(R.id.hand_photo)
    TextView hand_photo;

    HandlerThread handleThread = null;
    Handler detectHandler = null;
//    private int width = 320;
//    private int height = 240;
    private int width = 640;//640--480
    private int height = 480;
    FaceDetecter facedetecter = null;
    private DisplayMetrics metrics;
    private boolean isFrontCamera = true;
    private boolean safeToTakePicture = true;//防止拍照重复调用
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        permiss();
        metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.i("sss", String.valueOf(metrics.widthPixels));//1280
        Log.i("sss", String.valueOf(metrics.heightPixels));//752

        handleThread = new HandlerThread("dt");
        handleThread.start();
        detectHandler = new Handler(handleThread.getLooper());

        facedetecter = new FaceDetecter();
        if (!facedetecter.init(this, "d8ff9c739fc1b9127ea1549b7147c454")) {
            Log.e("diff", "有错误 ");
        }
        facedetecter.setTrackingMode(true);
        rephoto.setVisibility(View.GONE);
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

    private void uploadPhoto() {
        String num  = text_no.getText().toString();
        num = "123456";
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File  file = new File(filePath);
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
                    UtilToast.showToast(MainActivity.this,"请求失败！");
                }

                @Override
                public void onNext(JSONObject jsonObject) {
                    Log.i("sss",jsonObject.toString());

                    if(jsonObject!=null){
                        try {
                            JSONObject result = jsonObject.optJSONObject("result");
                            boolean isSuccess=  result.optBoolean("success");
                            if(isSuccess){
                                JSONObject image=jsonObject.optJSONObject("image");
                                String img=image.optString("img");
                                if(img.equals("none")){
                                    showDialog("没有可比对的图片,请再次拍照进行比对！");
                                }else if(img.equals("ok")){
                                    JSONObject compare = jsonObject.optJSONObject("compare");
                                    String config = compare.optString("confidence");
                                    int score = Integer.parseInt(config);
                                    if(score>70){
                                        showDialog("对比成功！");
                                    }else {
                                       showDialog("对比失败！");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
    }

    private void showDialog(String compare){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 获取布局
        LayoutInflater inflater=LayoutInflater.from(this);
        View view2 = inflater.inflate(R.layout.dialog_view, null);
        // 获取布局中的控件
        TextView tip = (TextView) view2.findViewById(R.id.tip);
        TextView sure = (TextView) view2.findViewById(R.id.sure);
        tip.setText(compare);
        // 设置参数
        builder.setView(view2);
        // 创建对话框
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               text_no.setText("");
                new File(filePath).delete();
                filePath= "";
               safeToTakePicture = true;
                try {
                    camera.startPreview();
                } catch (RuntimeException e) {
                    Log.e("error", "========>" + e.toString());
                }
                alertDialog.dismiss();// 对话框消失
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @OnClick({R.id.tv_switch,R.id.submit,R.id.rephoto,R.id.hand_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_switch:

                if (isFrontCamera) {
                    isFrontCamera = false;
                    stopPreview();
                    closeCamera();
                    openCamera();
                    if (null != camera) {
                        try {
                            camera.setPreviewDisplay(holder);
                        } catch (IOException ex) {
                            closeCamera();
                            ex.printStackTrace();
                        }
                        startPreview();
                    }
                } else {
                    isFrontCamera = true;
                    stopPreview();
                    closeCamera();
                    openCamera();
                    if (null != camera) {
                        try {
                            camera.setPreviewDisplay(holder);
                        } catch (IOException ex) {
                            closeCamera();
                            ex.printStackTrace();
                        }
                        startPreview();
                    }
                }
                break;
            case R.id.submit:
                if(TextUtils.isEmpty(filePath)){
                    UtilToast.showToast(this,"图片不能为空！");
                    return;
                }
//                if(TextUtils.isEmpty(text_no.getText().toString())){
//                    UtilToast.showToast(this,"输入框不能为空！");
//                    return;
//                }
                uploadPhoto();
                break;
            case R.id.rephoto:
                if(!TextUtils.isEmpty(filePath)){
                    new File(filePath).delete();
                }
                filePath= "";
                hand_photo.setVisibility(View.VISIBLE);
                try {
                    startPreview();
                } catch (RuntimeException e) {
                    Log.e("error", "========>" + e.toString());
                }
                break;
            case R.id.hand_photo:
                if(safeToTakePicture){
                    camera.takePicture(null, null, jpeg);
                    safeToTakePicture = false;
                }
                break;
        }
    }

    public long getTime() {
        return Calendar.getInstance().getTimeInMillis();
    }




    @Override
    public void onPreviewFrame(final byte[] data, final Camera mCamera) {
        detectHandler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                byte[] ori = new byte[width * height];
                int is = 0;
                if (!isFrontCamera) {
                    for (int x = width - 1; x >= 0; x--) {
                        for (int y = height - 1; y >= 0; y--) {
                            ori[is] = data[y * width + x];
                            is++;
                        }
                    }
                } else {
                  //  ori = rotateYUV420Degree90(data, width, height);
                }

                final FaceDetecter.Face[] faceinfo = facedetecter.findFaces(ori, height, width);//发现人脸
                if (faceinfo != null) {
                    if (safeToTakePicture) {
                        camera.takePicture(null, null, jpeg);
                        safeToTakePicture = false;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mask.setFaceInfo(faceinfo);
                    }
                });

            }
        });
    }

    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.postRotate(270);
            filePath = FileUtil.getPath() + File.separator + getTime() + ".jpeg";

            BitmapFactory.Options factory = new BitmapFactory.Options();
            factory = setOptions(factory);

            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length,factory);

//            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
//                    bm.getHeight(), matrix, true);

            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                bm.compress(Bitmap.CompressFormat.JPEG, 30, bos);
                bos.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                bm.recycle();
                //bm1.recycle();
                if(TextUtils.isEmpty(filePath)){
                    startPreview();
                    return;
                }else {
                    rephoto.setVisibility(View.VISIBLE);
                    hand_photo.setVisibility(View.GONE);

                }
                safeToTakePicture = true;
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
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
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
        closeCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        holder = camera_sf.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        facedetecter.release(this);
        handleThread.quit();
        FileUtil.deleteDir(FileUtil.getPath());
    }

    private void openCamera() {
        if (camera == null) {
            camera = Camera.open(isFrontCamera ? 0 : 1);
        }
    }

    private void startPreview() {
        Camera.Parameters para;
        if (null != camera) {
            para = camera.getParameters();
        } else {
            return;
        }

        // 选择合适的预览尺寸
        List<Camera.Size> sizeList = para.getSupportedPreviewSizes();
        if (sizeList.size() > 1) {
            Iterator<Camera.Size> itor = sizeList.iterator();
            while (itor.hasNext()) {
                Camera.Size cur = itor.next();
             Log.i("xxx",cur.width+"--"+cur.height);
            }
        }

//                if (cur.width >= width && cur.height >= height) {
//                    width = cur.width;
//                    height = cur.height;
//                    break;
//                }
        para.setPreviewSize(width, height);
        setPictureSize(para, 1080, 1920);
        para.setPictureFormat(ImageFormat.JPEG);//设置图片格式
        setCameraDisplayOrientation(isFrontCamera ? 0 : 1, camera);
        camera.setParameters(para);
        camera.startPreview();
        camera.setPreviewCallback(this);
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

    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = MainActivity.this.getWindowManager().getDefaultDisplay().getRotation();
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

    private void stopPreview() {
        if (null != camera) {
            camera.stopPreview();
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


    private byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // 旋转Y
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }


        }
        // 旋转U和V
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth)
                        + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

}
