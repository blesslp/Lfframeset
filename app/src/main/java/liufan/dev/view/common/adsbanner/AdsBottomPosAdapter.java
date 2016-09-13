package liufan.dev.view.common.adsbanner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.gigamole.infinitecycleviewpager.OnInfiniteCyclePageTransformListener;
import com.liufan.utils.ImageUtils;
import com.liufan.utils.ScreenUtils;
import com.orhanobut.logger.Logger;

import liufan.dev.lfframeset.R;


/**
 * 说明：广告页的小圆点
 * <hr>
 *
 * @author 刘帆
 * @date 2015年6月18日
 */
public abstract class AdsBottomPosAdapter<Entry> extends PagerAdapter implements OnPageChangeListener{

    private WeakReference<Context> context;
    private LinearLayout bottomPos;
    private HorizontalInfiniteCycleViewPager mViewPager;
    private RelativeLayout container;
    private Handler mHandler;
    private int mCurrentItem;
    private long delayMillis;
    private List<Entry> list = Collections.EMPTY_LIST;

    public AdsBottomPosAdapter(Context context, View container) {
        this.context = new WeakReference<Context>(context);
        this.container = (RelativeLayout) container.findViewById(R.id.frame);
        this.bottomPos = (LinearLayout) container.findViewById(R.id.bottomPos);
        this.mViewPager = (HorizontalInfiniteCycleViewPager) container.findViewById(R.id.viewpager);
        initViewPager();
        this.mHandler = new Handler();
    }

    public View getViewPager() {
        return mViewPager;
    }

    public void setHeight(float ratioOfScreenWidth) {
        Context con = context.get();
        if (con == null) {
            stopSwitch();
            Logger.e(new NullPointerException("context已经被回收"), "所以没有反应");
            return;
        }
        int screenWidth = ScreenUtils.getScreenWidth(con.getApplicationContext());
        int containerHeight = (int) (screenWidth * ratioOfScreenWidth);
        container.getLayoutParams().height = containerHeight;
    }

    public void setDataSource(List<Entry> list) {
        this.list = list;
        if (mViewPager.getAdapter() == null) {
            this.mViewPager.setAdapter(this);
        }

        this.mViewPager.notifyDataSetChanged();
        initBannerPoses();
        if (list.size() > 0) {
            mCurrentItem = 0;
//			int halfPos = Integer.MAX_VALUE>>1;
//			mCurrentItem = halfPos - (halfPos % list.size());
            this.mViewPager.setCurrentItem(mCurrentItem);
        }
    }

    public List<Entry> getDataSource() {
        return this.list;
    }

    private void initViewPager() {
        this.mViewPager.setOnPageChangeListener(this);
        this.mViewPager.setOffscreenPageLimit(list.size());
        this.mViewPager.setMaxPageScale(1);
        this.mViewPager.setInterpolator(new LinearInterpolator());
        this.mViewPager.setScrollDuration(300);
    }

    private boolean isAutoScroll = false;

    public void startSwitch(int delayMillis) {
        this.delayMillis = delayMillis;
        isAutoScroll = true;
        mHandler.removeCallbacks(autoSwitchTask);
        mHandler.postDelayed(autoSwitchTask, delayMillis);

    }

    public void pauseSwitch() {
//		this.mViewPager.stopAutoScroll();
        isAutoScroll = false;
        mHandler.removeCallbacks(autoSwitchTask);
    }

    public void stopSwitch() {
//        this.mViewPager.stopAutoScroll();
        isAutoScroll = false;
        if (mHandler != null) {
            mHandler.removeCallbacks(autoSwitchTask);
        }
        if (this.context != null) {
            this.context.clear();
        }
        this.context = null;
        mHandler = null;
        autoSwitchTask = null;
        bottomPos.removeAllViews();
        list.clear();
    }

    private Runnable autoSwitchTask = new Runnable() {

        public void run() {
            if (getCount() > 0 && context.get() != null) {
                mViewPager.setCurrentItem((mCurrentItem++) % getCount(), true);
                mHandler.postDelayed(this, delayMillis);
            }
        }
    };

    private void initBannerPoses() {
        Context context = this.context.get();
        if (context == null) {
            stopSwitch();
            Logger.e(new NullPointerException("context已经回收了"), "小圆点没有反应");
            return;
        }
        bottomPos.removeAllViews();
        for (int i = 0; i < list.size(); i++) {
            ImageView img = new ImageView(context);
            img.setScaleType(ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ImageUtils.dip2px(context, 8), ImageUtils.dip2px(context, 8));
            if (i == 0) {
                img.setImageResource(R.drawable.banner_on);
            } else {
                img.setImageResource(R.drawable.banner_off);
            }
            params.leftMargin = 10;
            params.bottomMargin = 10;
            bottomPos.addView(img, params);
        }
    }


    public interface OnScroll {
        public void onScroll(int pos);
    }

    private OnScroll o;

    public void setOnScroll(OnScroll o) {
        this.o = o;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//		((ViewPager)container).removeView((View)object);
    }

    private SparseArrayCompat<ImageView> viewCache = new SparseArrayCompat<>();

    public Object instantiateItem(View container, int position) {
        ImageView img = null;
        Context con = context.get();
        if (con == null) {
            stopSwitch();
            return null;
        }
        img = viewCache.get(position);
        if (img == null) {
            img = new ImageView(con);
            img.setScaleType(ScaleType.FIT_XY);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            img.setLayoutParams(params);
//			img.setTag(position);
            viewCache.put(position, img);
        }
        if (img.getParent() != null) {
            ((ViewGroup) img.getParent()).removeView(img);
        }
        bindData(position, img);
        ((ViewPager) container).addView(img);
        return img;
    }

    public int getDelayMillis() {
        return (int) delayMillis;
    }

    private Runnable delayAutoRunningTask = new Runnable() {
        @Override
        public void run() {
            if (isAutoScroll) {
                startSwitch(getDelayMillis());
            }
        }
    };

    public Entry getItem(int position) {
        return list.get(position);
    }

    public abstract void bindData(int position, ImageView img);


    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
//		if (position == 0) {
//			mCurrentItem = list.size();
//		} else if (position == list.size()+1) {
//			mCurrentItem = 1;
//		}
        mCurrentItem = position % list.size();
        for (int i = 0; i < bottomPos.getChildCount(); i++) {
            ImageView img = (ImageView) bottomPos.getChildAt(i);
            if ((position % list.size()) == i) {
                img.setImageResource(R.drawable.banner_on);
            } else {
                img.setImageResource(R.drawable.banner_off);
            }
        }
        if (o != null) {
            o.onScroll(position % list.size());
        }

    }
}
