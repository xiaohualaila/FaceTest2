package xiahohu.facetest.Util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import xiahohu.facetest.bean.CountryModel;
import xiahohu.facetest.model.WhiteList;
import xiaohu.facetest.greendaodemo.greendao.GreenDaoManager;
import xiaohu.facetest.greendaodemo.greendao.gen.WhiteListDao;

/**
 * Created by Administrator on 2017/11/20.
 */

public class GetDataUtil {

    /**
     * 获取 excel 表格中的数据,不能在主线程中调用
     *
     * @param xlsName excel 表格的名称
     * @param index   第几张表格中的数据
     */
    public static ArrayList<WhiteList> getXlsData(String xlsName, int index) {
        ArrayList<WhiteList> countryList = new ArrayList<>();
        countryList.clear();
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
            WhiteListDao whiteListDao = GreenDaoManager.getInstance().getSession().getWhiteListDao();
            whiteListDao.deleteAll();
            WhiteList whiteList =null;
            String code = "";
            String num = "";
            for (int i = 0; i < sheetRows; i++) {
                code = sheet.getCell(0, i).getContents();
                num = sheet.getCell(1, i).getContents();
                Log.i("xxx",code + "   " + num);

                if(TextUtils.isEmpty(code) && TextUtils.isEmpty(num)){
                    break;
                }
                whiteList = new WhiteList(null,num,code);
                     //whiteListDao.insert(whiteList);
                countryList.add(whiteList);
            }
            workbook.close();
        } catch (Exception e) {
     //       Log.e(TAG, "read error=" + e, e);
        }
        return countryList;
    }

    public static boolean getDataBooean (Context context){
//                WhiteListDao whiteListDao = GreenDaoManager.getInstance().getSession().getWhiteListDao();
//                WhiteList mmm =  whiteListDao.queryBuilder().where(WhiteListDao.Properties.Num.eq("100")).build().unique();
//                for (int i = 0; i < countryList.size() ; i++) {
//                    if(countryList.get(i).getNum().equals("10094")){
//                        WhiteList mmm =  countryList.get(i);
//                        if(mmm != null){
//                            Log.i("sss", "  " + mmm.getCode() + "  " +mmm.getNum());
//                        }
//                    }
//                }
        boolean flag = false;
        ArrayList<WhiteList> list = SharedPreferencesUtil.getDataList(context,"countryModels");
        for (int i = 0; i < list.size() ; i++) {
            if(list.get(i).getNum().equals("10155")){
                flag = true;
                return flag;
            }else {
                flag = false;
            }
        }
        return flag;
    }

}
