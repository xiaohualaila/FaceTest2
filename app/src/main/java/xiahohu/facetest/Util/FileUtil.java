package xiahohu.facetest.Util;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

/**
 * Created by xyuxiao on 2016/9/23.
 */
public class FileUtil {
    /**
     * 获取图片保存路径
     */
    public static String getPath(){
        String path = FileUtil.getSDPath() + File.separator + "door";
        File pathFile = new File(path);
        if(!pathFile.exists() || !pathFile.isDirectory()){
            pathFile.mkdir();
        }
        return path;
    }

    /**
     * 获取路径
     * @return
     */
    public static String getSDPath() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return null;
    }

    /**
     * 删除文件夹下所有文件
     */
    public static void deleteDir(String dir) {
        File fileDir = new File(dir);
        if (fileDir.isDirectory()) {
            String[] children = fileDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(fileDir, children[i]).delete();
            }
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String
     * @param newPath String
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在�?
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                fs.flush();
                fs.close();
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }

    public static void deleteFile(String filePath){
        if(!TextUtils.isEmpty(filePath)){
            File file = new File(filePath);
            if(file!=null){
                if(file.exists()){
                    file.delete();
                }
            }
        }
    }


    public static long getTime() {
        return Calendar.getInstance().getTimeInMillis();
    }
}
