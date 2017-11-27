package xiahohu.facetest.activity;

import android.app.Activity;
import android.content.Intent;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cmm.rkadcreader.adcNative;
import com.cmm.rkgpiocontrol.rkGpioControlNative;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import xiahohu.facetest.R;
import xiahohu.facetest.Util.FileUtil;
import xiahohu.facetest.Util.MyUtil;
import xiahohu.facetest.bean.MyMessage;
import xiahohu.facetest.rx.RxBus;
import xiahohu.facetest.service.MyService;
import xiahohu.facetest.service.ScreenService;
import xiahohu.retrofit.Api;
import xiahohu.retrofit.ConnectUrl;


/**
 * Created by dhht on 16/9/29.
 */

public class ScreenCameraActivity extends Activity implements SurfaceHolder.Callback {

    @Bind(R.id.camera_sf)
    SurfaceView camera_sf;
    @Bind(R.id.text_card)
    TextView text_card;
    @Bind(R.id.img1)
    ImageView img1;
    @Bind(R.id.img_server)
    ImageView img_server;
    @Bind(R.id.card_no)
    TextView card_no;
    @Bind(R.id.flag_tag)
    ImageView flag_tag;
    private Camera camera;
    private String filePath;
    private SurfaceHolder holder;
    private boolean isFrontCamera = true;
    private boolean safephoto  =true;
    private int width = 640;
    private int height = 480;
    private String device_id;
    private boolean isOpenDoor = false;
    private String str;
    private boolean oo =true;


    /**
     * 测试用按钮
     * @param view
     */
    @OnClick({R.id.take_photo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_photo:
//                if(oo){
//                    rkGpioControlNative.ControlGpio(1, 0);//开门
//                    oo = false;
//                }else {
//                    rkGpioControlNative.ControlGpio(1, 1);//关门
//                    oo = true;
//                }
                takePhoto();
                break;
        }
    }

    private Handler handler = new Handler();

////////////////////////////////////屏保部分代码
private Handler mHandler01 = new Handler();
    private Handler mHandler02 = new Handler();

    /* 上一次User有动作的Time Stamp */
    private Date lastUpdateTime;
    /* 计算User有几秒没有动作的 */
    private long timePeriod;

    /* 静止超过N秒将自动进入屏保 */
    private float mHoldStillTime = 10;
    /*标识当前是否进入了屏保*/
    private boolean isRunScreenSaver;

    /*时间间隔*/
    private long intervalScreenSaver = 1000;
    private long intervalKeypadeSaver = 1000;
    //////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_camera2);
        ButterKnife.bind(this);
        holder = camera_sf.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        startService(new Intent(this, ScreenService.class));
        RxBus.getDefault().toObserverable(MyMessage.class).subscribe(myMessage -> {
            str = myMessage.getNum();
            card_no.setText(str);
            updateUserActionTime();
            takePhoto();
        });
        device_id = MyUtil.getDeviceID(this);//获取设备号
        rkGpioControlNative.init();

          /* 初始取得User可触碰屏幕的时间 */
        lastUpdateTime = new Date(System.currentTimeMillis());
    }

    private void takePhoto(){
           if(!isOpenDoor){
                if (safephoto) {
                    safephoto = false;
                    FileUtil.deleteFile(filePath);
                    camera.takePicture(null, null, jpeg);
                    text_card.setText("拍摄人脸照片");
                }
           }
    }

    private Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            stopPreview();
            filePath = FileUtil.getPath() + File.separator + FileUtil.getTime() + ".jpeg";

            Matrix matrix = new Matrix();
            matrix.reset();
            matrix.postRotate(180);
            BitmapFactory.Options factory = new BitmapFactory.Options();
            factory = setOptions(factory);
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length,factory);
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            BufferedOutputStream bos = null;
            try {
                File file = new File(filePath);
                if(!file.exists()){
                    file.createNewFile();
                }
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
                camera.startPreview();//开始预览
                uploadPhoto();
            }
        }
    };

    /**
     * 上传信息
     */
    private void uploadPhoto() {
        File  file = new File(filePath);
        if(!file.exists()){
            uploadFinish();
            return;
        }
        Glide.with(ScreenCameraActivity.this).load(filePath).error(R.drawable.img_bg).into(img1);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        builder.addFormDataPart("photoImgFiles", file.getName(), requestBody);
        Api.getBaseApiWithOutFormat(ConnectUrl.URL)
                .uploadPhotoBase("1",str,builder.build().parts())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("xxx",e.toString());
                        text_card.setText("数据请求失败！");
                        flag_tag.setImageResource(R.drawable.flag_red);
                        uploadFinish();
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        Log.i("xxx",jsonObject.toString());
                            try {
                                if(jsonObject != null) {
                                    String Face_path = jsonObject.optString("Face_path");
                                    if(!TextUtils.isEmpty(Face_path)){
                                        Glide.with(ScreenCameraActivity.this).load(Face_path).error(R.drawable.img_bg).into(img_server);
                                    }
                                    String result = jsonObject.optString("Result");
                                    if (result.equals("1")||result.equals("6")) {
                                        if(result.equals("1")){
                                            text_card.setText(R.string.open_door_1);
                                        }else {
                                            text_card.setText(R.string.open_door_6);
                                        }
                                        isOpenDoor = true;
                                        rkGpioControlNative.ControlGpio(1, 0);//开门
                                        flag_tag.setImageResource(R.drawable.flag_green);
                                    } else if (result.equals("2")) {
                                        text_card.setText(R.string.open_door_2);
                                        flag_tag.setImageResource(R.drawable.flag_red);
                                    }else if (result.equals("3")){
                                        text_card.setText(R.string.open_door_3);
                                        flag_tag.setImageResource(R.drawable.flag_red);
                                    }else if (result.equals("4")){
                                        text_card.setText(R.string.open_door_4);
                                        flag_tag.setImageResource(R.drawable.flag_red);
                                    }else if(result.equals("5")){
                                        text_card.setText(R.string.open_door_5);
                                        flag_tag.setImageResource(R.drawable.flag_red);
                                    } else if(result.equals("99")){
                                        text_card.setText(R.string.open_door_99);
                                        flag_tag.setImageResource(R.drawable.flag_red);
                                    } else{
                                        text_card.setText(R.string.open_door_other);
                                        flag_tag.setImageResource(R.drawable.flag_red);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }finally {
                                uploadFinish();
                            }
                    }
                });
    }

    /**
     * 0.5秒关门
     */
    private void uploadFinish() {
        safephoto = true;
        handler.postDelayed(runnable,500);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(isOpenDoor){
                rkGpioControlNative.ControlGpio(1, 1);//关门
                isOpenDoor = false;
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
        ///////////////////////////////////
        mHandler01.postAtTime(mTask01, intervalKeypadeSaver);
    }

    @Override
    protected void onPause() {
        /*activity不可见的时候取消线程*/
        mHandler01.removeCallbacks(mTask01);
        mHandler02.removeCallbacks(mTask02);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeCamera();
        FileUtil.deleteDir(FileUtil.getPath());
        stopService( new Intent(this, ScreenService.class));
        adcNative.close(0);
        adcNative.close(2);
        rkGpioControlNative.close();
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

    public void setCameraDisplayOrientation(int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
         rotation = 2;
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
        para.setPictureSize(absWidth,absHeight);
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

/////////////////////////////////////////////////////////屏保部分代码

    /**
     * 计时线程
     */
    private Runnable mTask01 = new Runnable() {

        @Override
        public void run() {
            Date timeNow = new Date(System.currentTimeMillis());
            /* 计算User静止不动作的时间间距 */
            /**当前的系统时间 - 上次触摸屏幕的时间 = 静止不动的时间**/
            timePeriod = (long) timeNow.getTime() - (long) lastUpdateTime.getTime();

            /*将静止时间毫秒换算成秒*/
            float timePeriodSecond = ((float) timePeriod / 1000);

            if(timePeriodSecond > mHoldStillTime){
                if(isRunScreenSaver == false){  //说明没有进入屏保
                    /* 启动线程去显示屏保 */
                    mHandler02.postAtTime(mTask02, intervalScreenSaver);
                    /*显示屏保置为true*/
                    isRunScreenSaver = true;
                }else{
                    /*屏保正在显示中*/
                }
            }else{
                /*说明静止之间没有超过规定时长*/
                isRunScreenSaver = false;
            }
            /*反复调用自己进行检查*/
            mHandler01.postDelayed(mTask01, intervalKeypadeSaver);
        }
    };

    /**
     * 持续屏保显示线程
     */
    private Runnable mTask02 = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (isRunScreenSaver == true) {  //如果屏保正在显示，就计算不断持续显示
//              hideOriginalLayout();
                showScreenSaver();
                mHandler02.postDelayed(mTask02, intervalScreenSaver);
            } else {
                mHandler02.removeCallbacks(mTask02);  //如果屏保没有显示则移除线程
            }
        }
    };

    /**
     * 显示屏保
     */
    private void showScreenSaver(){
        Log.d("danxx", "显示屏保------>");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    /*用户有操作的时候不断重置静止时间和上次操作的时间*/
    public void updateUserActionTime() {
        Date timeNow = new Date(System.currentTimeMillis());
        timePeriod = timeNow.getTime() - lastUpdateTime.getTime();
        lastUpdateTime.setTime(timeNow.getTime());
    }

}
