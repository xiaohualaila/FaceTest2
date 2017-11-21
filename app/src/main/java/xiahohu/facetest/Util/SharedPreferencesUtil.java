package xiahohu.facetest.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import xiahohu.facetest.model.WhiteList;

public class SharedPreferencesUtil {

	public static String getStringByKey(String key, Context context){
		SharedPreferences sharedata = context.getSharedPreferences("data", 0);
		return sharedata.getString(key, null);
	}
	public static long getLongByKey(String key, Context context){
		SharedPreferences sharedata = context.getSharedPreferences("data", 0);
		return sharedata.getLong(key, 0);
	}
    public static int getIntByKey(String key, Context context){
		SharedPreferences sharedata = context.getSharedPreferences("data", 0);
		return sharedata.getInt(key, 0);
	}
	public static boolean getBooleanByKey(String key, Context context){
		SharedPreferences sharedata = context.getSharedPreferences("data", 0);
		return sharedata.getBoolean(key, false);
	}
	public static void save(String key, String value, Context context){
		Editor sharedata =context. getSharedPreferences("data", 0).edit();
		sharedata.putString(key,value);
		sharedata.commit();
		System.out.println("保存数据");
	}

	public static void save(String key, long value, Context context){
		Editor sharedata =context. getSharedPreferences("data", 0).edit();
		sharedata.putLong(key,value);
		sharedata.commit();
	}
    public static void save(String key, int value, Context context){
        Editor sharedata =context. getSharedPreferences("data", 0).edit();
        sharedata.putInt(key,value);
        sharedata.commit();
    }
	public static void save(String key, boolean value, Context context){
		Editor sharedata =context. getSharedPreferences("data", 0).edit();
		sharedata.putBoolean(key,value);
		sharedata.commit();
	}
	public static void clearData(Context context) {
		Editor sharedata =context. getSharedPreferences("data", 0).edit();
		sharedata.clear().commit();
	}
	private static String name = "config";
	/**
	 * 获取SharedPreferences实例对象
	 *
	 * @param context
	 * @return
	 */
	public static SharedPreferences getSharedPreference(Context context) {
		return context.getSharedPreferences(name, Context.MODE_PRIVATE);
	}/**
	 * 获取boolean的value
	 *
	 * @param context
	 * @param key
	 *            名字
	 * @param defValue
	 *            默认值
	 * @return
	 */
	public static boolean getBoolean(Context context, String key, Boolean defValue) {
		SharedPreferences sharedPreference = getSharedPreference(context);
		return sharedPreference.getBoolean(key, defValue);
	}
	/**
	 * 保存一个Boolean类型的值！
	 *
	 * @param context
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean putBoolean(Context context, String key, Boolean value) {
		SharedPreferences sharedPreference = getSharedPreference(context);
		Editor editor = sharedPreference.edit();
		editor.putBoolean(key, value);
		return editor.commit();
	}
	public static void removeKey(Context context, String key){
		Editor sharedata =context.getSharedPreferences("data", 0).edit();
		sharedata.remove(key);
		sharedata.commit();
	}

	/**
	 * 保存List
	 * @param tag
	 * @param datalist
	 */
	public static <T> void setDataList(Context context, String tag, List<T> datalist) {
		SharedPreferences sharedPreference = getSharedPreference(context);
		Editor editor = sharedPreference.edit();
		if (null == datalist || datalist.size() <= 0)
			return;

		Gson gson = new Gson();
		//转换成json数据，再保存
		String strJson = gson.toJson(datalist);
		editor.clear();
		editor.putString(tag, strJson);
		editor.commit();

	}

	/**
	 * 获取List
	 * @param tag
	 * @return
	 */
	public static<T> ArrayList<T> getDataList(Context context,String tag) {
		SharedPreferences preferences = getSharedPreference(context);
		ArrayList<T> datalist=new ArrayList<T>();
		String strJson = preferences.getString(tag, null);
		if (null == strJson) {
			return datalist;
		}
		Gson gson = new Gson();
		datalist = gson.fromJson(strJson, new TypeToken<ArrayList<WhiteList>>() {
		}.getType());
		return datalist;

	}

}
