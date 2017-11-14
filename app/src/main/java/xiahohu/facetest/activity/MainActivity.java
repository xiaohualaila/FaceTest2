package xiahohu.facetest.activity;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import butterknife.Bind;
import xiahohu.facetest.R;
import xiahohu.facetest.activity.base.BaseAppCompatActivity;

/**
 * Created by Administrator on 2017/11/13.
 */

public class MainActivity extends BaseAppCompatActivity {
    @Bind(R.id.image)
    ImageView image;
    private AnimationDrawable frameAnimation;
    @Override
    protected void init() {
        image.setBackgroundResource(R.drawable.animation);
        frameAnimation = (AnimationDrawable) image.getBackground();
        frameAnimation.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_anima;
    }

    //数据库更新
//    private void updateItem(Long id) {
//        MessageDb message = GreenDaoManager.getInstance().getSession().getMessageDbDao().queryBuilder()
//                .where(MessageDbDao.Properties.Id.eq(id)).build().unique();
//        if (message != null) {
//            message.setIsRead(true);
//            GreenDaoManager.getInstance().getSession().getMessageDbDao().update(message);
//            getMessageList();
//        } else {
//            getMessageList();
//        }
//    }
//查询
//    private void getMessageList() {
//        list = GreenDaoManager.getInstance().getSession().getMessageDbDao().
//                queryBuilder().orderDesc(MessageDbDao.Properties.Time).list();//按照时间排列
//        notifyDataSetChanged();
//    }
//数据库保存
//    private void insertUser(MessageDb message) {
//        MessageDbDao messageDao = GreenDaoManager.getInstance().getSession().getMessageDbDao();
//        messageDao.save(message);
//
//
//    }
}
