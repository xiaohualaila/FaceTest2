package xiahohu.facetest.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

import jxl.Sheet;
import jxl.Workbook;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import xiahohu.facetest.R;
import xiahohu.facetest.Util.FileUtil;
import xiahohu.facetest.Util.SharedPreferencesUtil;
import xiahohu.facetest.Util.UtilToast;
import xiahohu.facetest.activity.base.BaseAppCompatActivity;
import xiahohu.facetest.bean.CountryModel;
import xiahohu.retrofit.Api;
import xiahohu.view.ClearEditTextWhite;


/**
 * Created by ThinkPad on 2017/10/17.
 */

public class SetUpActivity extends BaseAppCompatActivity  {
    @Bind(R.id.btn_back)
    ImageView btn_back;
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.tv_finish)
    TextView tv_finish;
    @Bind(R.id.state)
    TextView state;
    @Bind(R.id.ip_address)
    ClearEditTextWhite ip_address;
    @Bind(R.id.duankou)
    ClearEditTextWhite ct_duankou;
    @Bind(R.id.rb_lian)
    RadioButton rb_lian;
    @Bind(R.id.rb_lian_not)
    RadioButton rb_lian_not;
    @Bind(R.id.ll_tuoji)
    LinearLayout ll_tuoji;
    @Bind(R.id.ll_service)
    LinearLayout ll_service;
    @Bind(R.id.excel_state)
    TextView excel_state;
    @Bind(R.id.add_excel)
    TextView add_excel;
    private String ipAddress;
    private String duankou;
    private AlertDialog alertDialog;
    public static SetUpActivity instance = null;
    private static final String TAG = "xxxxx";
    private String  str_ip;

    @Bind(R.id.switch1)
    Switch switch1;
    @Bind(R.id.switch2)
    Switch switch2;
    @Bind(R.id.switch3)
    Switch switch3;
    @Bind(R.id.switch4)
    Switch switch4;

    private boolean code = false;
    private boolean xin = false;
    private boolean idCard = false;
    private boolean photo = false;
    private String path = "";
    @Override
    protected void init() {
        tv_title.setText("设置");
        tv_finish.setText("完成");
        tv_finish.setVisibility(View.VISIBLE);
        instance = this;
        ll_service.setVisibility(View.GONE);
        ll_tuoji.setVisibility(View.VISIBLE);
        initSwitch();
        initPath();

    }



    private void initPath() {
        String mkdirPath = FileUtil.getPath();
        File file = new File(mkdirPath);
        if(!file.isDirectory()){
            file.mkdir();
            excel_state.setText("文件不存在！");
        }else {
            getExcel();
        }

    }

    private void initSwitch() {
        code = SharedPreferencesUtil.getBooleanByKey("code",this);
        xin = SharedPreferencesUtil.getBooleanByKey("xin",this);
        idCard = SharedPreferencesUtil.getBooleanByKey("idCard",this);
        photo = SharedPreferencesUtil.getBooleanByKey("photo",this);

        switch1.setChecked(code);
        switch2.setChecked(xin);
        switch3.setChecked(idCard);
        switch4.setChecked(photo);
        switch1.setOnCheckedChangeListener(listener);
        switch2.setOnCheckedChangeListener(listener);
        switch3.setOnCheckedChangeListener(listener);
        switch4.setOnCheckedChangeListener(listener);
    }

    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.switch1:
                    setBoolean(code, isChecked,"code");
                    break;
                case R.id.switch2:
                    setBoolean(xin, isChecked,"xin");
                    break;
                case R.id.switch3:
                    setBoolean(idCard, isChecked,"idCard");
                    break;
                case R.id.switch4:
                    photo = isChecked;
                    setBoolean(photo, isChecked,"photo");
                    break;
            }
        }
    };

    private void setBoolean(boolean flag,boolean isChecked,String saveTag){
        flag = isChecked;
        SharedPreferencesUtil.save(saveTag,flag,this);
//        if (isChecked) {
//            flag = isChecked;
//            SharedPreferencesUtil.save(saveShared,flag,this);
//        } else {
//            flag = isChecked;
//            SharedPreferencesUtil.save(saveShared,flag,this);
//        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_setup;
    }

    @OnClick({R.id.tv_finish,R.id.btn_back,R.id.rb_lian,R.id.rb_lian_not,R.id.check_ip,R.id.download_video,R.id.local_video,R.id.reset,R.id.check_excel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_ip:
                try {
                    checkIp();
                }catch(Exception e){
                    showDialog("ip地址错误！");
                }
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.rb_lian:
                ll_service.setVisibility(View.VISIBLE);
                ll_tuoji.setVisibility(View.GONE);
                break;
            case R.id.rb_lian_not:
                ll_service.setVisibility(View.GONE);
                ll_tuoji.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_finish:
                if(photo){
                    startActivity(new Intent(this, CameraActivity.class));
                }else {
                   startActivity(new Intent(this, MainActivity.class));
                }
                break;
            case R.id.download_video:
               // startActivity(new Intent(this, VideoActivity.class));
                break;
            case R.id.local_video:
            //    startActivity(new Intent(this, VideoLocalActivity.class));
                break;
            case R.id.reset:
                SharedPreferencesUtil.save("code",false,this);
                SharedPreferencesUtil.save("xin",false,this);
                SharedPreferencesUtil.save("idCard",false,this);
                SharedPreferencesUtil.save("photo",false,this);
                code = false;
                xin = false;
                idCard = false;
                photo = false;
                switch1.setChecked(false);
                switch2.setChecked(false);
                switch3.setChecked(false);
                switch4.setChecked(false);
                break;
            case R.id.check_excel:
                getExcel();
                break;
            case R.id.add_excel:
                if(!TextUtils.isEmpty(path)){
                    new ExcelDataLoader().execute(path);
                }
                break;

        }
    }

    private void getExcel() {
        path = FileUtil.getPath()+ File.separator +"a.xls";
        File file = new File(path);
        if(!file.exists()){
            UtilToast.showToast(this,"文件不存在,请将文件放入设备存储face2文件下！");
            excel_state.setText("文件不存在！");
            return;
        }else {
            add_excel.setVisibility(View.VISIBLE);
        }

    }

    //在异步方法中 调用
    private class ExcelDataLoader extends AsyncTask<String, Void, ArrayList<CountryModel>> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList<CountryModel> doInBackground(String... params) {
            return getXlsData(params[0], 0);
        }

        @Override
        protected void onPostExecute(ArrayList<CountryModel> countryModels) {

            if(countryModels != null && countryModels.size()>0){
                //存在数据
                Log.i(TAG,"count++"+ countryModels.size());
                excel_state.setText("存在");
            }else {
                //加载失败

            }
        }
    }

    /**
     * 获取 excel 表格中的数据,不能在主线程中调用
     *
     * @param xlsName excel 表格的名称
     * @param index   第几张表格中的数据
     */
    private ArrayList<CountryModel> getXlsData(String xlsName, int index) {
        ArrayList<CountryModel> countryList = new ArrayList<CountryModel>();
        try {
            File file =new File(xlsName);
            InputStream in=new FileInputStream(file);
            Workbook workbook = Workbook.getWorkbook(in);
            Sheet sheet = workbook.getSheet(index);
            int sheetNum = workbook.getNumberOfSheets();
            int sheetRows = sheet.getRows();
            int sheetColumns = sheet.getColumns();
//            Log.d(TAG, "the num of sheets is " + sheetNum);
//            Log.d(TAG, "the name of sheet is  " + sheet.getName());
//            Log.d(TAG, "total rows is 行=" + sheetRows);
//            Log.d(TAG, "total cols is 列=" + sheetColumns);
            for (int i = 0; i < sheetRows; i++) {
                CountryModel countryModel = new CountryModel();
                countryModel.setNum(sheet.getCell(0, i).getContents());
                countryList.add(countryModel);
            }
            workbook.close();
        } catch (Exception e) {
            Log.e(TAG, "read error=" + e, e);
        }
        return countryList;
    }

   //检测IP
    private void checkIp() {
        ipAddress= ip_address.getText().toString();
        duankou = ct_duankou.getText().toString();
        if(TextUtils.isEmpty(ipAddress)){
            UtilToast.showToast(this,"ip地址不能为空！");
            return;
        }else if(TextUtils.isEmpty(duankou)){
            UtilToast.showToast(this,"端口不能为空！");
            return;
        }
         str_ip = "http://" + ipAddress + ":" + duankou+"/";
        Api.getBaseApiWithOutFormat(str_ip)
                .checkIp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        showDialog("ip地址错误！");
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                        if(jsonObject != null){
                            Log.i("sss",jsonObject.toString());
                            if(jsonObject.optBoolean("success")){
                                UtilToast.showToast(SetUpActivity.this,jsonObject.optString("resultMSG"));
                                  SharedPreferencesUtil.save("ip",str_ip,SetUpActivity.this);
                                state.setText("有效");
                            }
                        }else{
                            showDialog("ip地址错误！");
                        }
                    }
                });
    }

    private void showDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 获取布局
        LayoutInflater inflater=LayoutInflater.from(this);
        View view2 = inflater.inflate(R.layout.dialog_view, null);
        // 获取布局中的控件
        TextView tip = (TextView) view2.findViewById(R.id.tip);
        TextView sure = (TextView) view2.findViewById(R.id.sure);
        tip.setText(msg);
        // 设置参数
        builder.setView(view2);
        // 创建对话框
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();// 对话框消失
                state.setText("无效");
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }
}