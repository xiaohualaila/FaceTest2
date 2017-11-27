package xiahohu.facetest.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/10.
 */

public class MyMessage implements Serializable{
    private String num;

    public MyMessage(String num) {
        this.num = num;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
