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
}
