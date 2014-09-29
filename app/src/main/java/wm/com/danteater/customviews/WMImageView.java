package wm.com.danteater.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.AvoidXfermode;
import android.graphics.Color;
import android.graphics.ColorFilter;

import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;

import wm.com.danteater.R;

/**
 * Created by dhruvil on 28-08-2014.
 */
public class WMImageView extends ImageView{

    private int src = 0;
    private int selected_src = 0;

    public WMImageView(Context context) {
        super(context);
    }


    public WMImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.customImageView, 0, 0);


         src = a.getResourceId(R.styleable.customImageView_normal_src,R.drawable.ic_share);
         selected_src = a.getResourceId(R.styleable.customImageView_selected_src,R.drawable.ic_launcher);




        a.recycle();

        setImageResource(src);
        setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
    }


    public void normal(){
        setImageResource(src);
        setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
    }

    public void selected(){
        setImageResource(selected_src);
        setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }



}
