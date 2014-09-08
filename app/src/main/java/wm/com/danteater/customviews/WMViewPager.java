package wm.com.danteater.customviews;


import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;

import android.view.MotionEvent;

public class WMViewPager extends ViewPager {

    float mStartDragX;
    OnSwipeOutListener mListener;

    public WMViewPager(Context context) {
        super(context);
    }

    public void setOnSwipeOutListener(OnSwipeOutListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getCurrentItem() == getAdapter().getCount() - 1) {
            final int action = ev.getAction();
            float x = ev.getX();
            switch (action & MotionEventCompat.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mStartDragX = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (x < mStartDragX) {
                        mListener.onSwipeOutAtEnd();
                    } else {
                        mStartDragX = 0;
                    }
                    break;
            }
        } else {
            mStartDragX = 0;
        }
        return super.onTouchEvent(ev);
    }

    public interface OnSwipeOutListener {
        public void onSwipeOutAtEnd();
    }
}