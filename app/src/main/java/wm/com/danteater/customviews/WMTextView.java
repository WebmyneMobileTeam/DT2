package wm.com.danteater.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by dhruvil on 25-08-2014.
 */
public class WMTextView extends TextView{


    private Typeface typeFace;

    public WMTextView(Context context) {
        super(context);
        createAndSet(context);
    }

    private void createAndSet(Context ctx) {

        typeFace = Typeface.createFromAsset(ctx.getAssets(),"helvetica.ttf");
        setTypeface(typeFace);


    }

    public void setBold(){
        setTypeface(typeFace,Typeface.BOLD);
    }

    public void setItalic(){
        setTypeface(typeFace,Typeface.ITALIC);
    }

    public WMTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        createAndSet(context);
    }

    public WMTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createAndSet(context);
    }
}
