package xiahohu.facetest.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;


@Entity
public class WhiteList {

    @Id
    private Long _id;
    private String num;//票号
    private String code;//二维码

    @Keep
    @Generated(hash = 2050529862)
    public WhiteList(Long _id, String num,String code) {
        this._id = _id;
        this.num = num;
        this.code = code;
    }
    @Keep
    @Generated(hash = 73662917)
    public WhiteList() {
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}