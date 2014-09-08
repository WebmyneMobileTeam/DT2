package wm.com.danteater.customviews;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidanimations.library.Techniques;
import com.androidanimations.library.Webmyne;

import wm.com.danteater.R;

/**
 * Created by Dhruvil
 *
 * Custom dialog class like HUD in iOS
 *
 * This class has status functionality
 *
 * 1. Success with custom image
 * 2. Failure with custom image
 *
 */
public class HUD extends Dialog{


    private View convertView;
    private Context ctx;
    private ProgressBar pb;
    private ImageView imgStatus;
    private TextView txtTitle;
    private FrameLayout frameLayout;

    public HUD(Context context, int theme) {
        super(context, theme);
        this.ctx = context;
        init();

    }

    private void init() {

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        convertView = inflater.inflate(R.layout.ui_hud,null);
        setContentView(convertView);
        pb = (ProgressBar)convertView.findViewById(R.id.pb);
        imgStatus = (ImageView)convertView.findViewById(R.id.imgStatus);
        txtTitle = (TextView)convertView.findViewById(R.id.txttitle);
        frameLayout = (FrameLayout)convertView.findViewById(R.id.frame);


    }


    /*

    This method helps to show custom text and image in dialog and dismiss it
    automatically after 2 seconds

     */
    public void dismissWithStatus(int resource,String successMessage){

        pb.setVisibility(View.INVISIBLE);
        imgStatus.setVisibility(View.VISIBLE);
        imgStatus.setImageResource(resource);
        txtTitle.setText(successMessage);

        Webmyne.get(Techniques.BounceIn).duration(500).startOn(frameLayout);

        new CountDownTimer(2000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                dismiss();
            }
        }.start();

    }

    public void title(String title){

        txtTitle.setText(title);
    }

}
