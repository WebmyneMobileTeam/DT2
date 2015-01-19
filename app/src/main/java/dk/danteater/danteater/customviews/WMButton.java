package dk.danteater.danteater.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by dhruvil on 25-08-2014.
 */
public class WMButton extends Button{


    private Typeface typeFace;

    public WMButton(Context context) {
        super(context);
        createAndSet(context);
    }

    private void createAndSet(Context ctx) {

        typeFace = Typeface.createFromAsset(ctx.getAssets(),"helvetica.ttf");
        setTypeface(typeFace);

    }

    public WMButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        createAndSet(context);
    }

    public WMButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        createAndSet(context);
    }
}
