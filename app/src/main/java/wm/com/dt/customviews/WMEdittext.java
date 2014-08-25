package wm.com.dt.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by dhruvil on 25-08-2014.
 */
public class WMEdittext extends EditText{


    private Typeface typeFace;

    public WMEdittext(Context context) {
        super(context);
        createAndSet(context);
    }

    private void createAndSet(Context ctx) {

        typeFace = Typeface.createFromAsset(ctx.getAssets(),"helvetica.ttf");
        setTypeface(typeFace);

    }

    public WMEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        createAndSet(context);
    }

    public WMEdittext(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createAndSet(context);
    }
}
