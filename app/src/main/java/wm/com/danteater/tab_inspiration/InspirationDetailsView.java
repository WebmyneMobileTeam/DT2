package wm.com.danteater.tab_inspiration;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kbeanie.imagechooser.api.ChosenImage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.io.File;

import wm.com.danteater.R;

/**
 * Created by dhruvil on 11-11-2014.
 */
public class InspirationDetailsView extends Dialog{

    private setSelectedListner listner;
    private View convertView;
    private Context ctx;
    private FrameLayout parentDialog;
    private ImageView iv;
    private EditText tvComment;
    private TextView tvName;
    private TextView tvGem;
    private TextView txtCancelInspirationDialog;
    ImageLoader loader;

    public InspirationDetailsView(Context context, int theme) {

        super(context, theme);
        this.ctx = context;
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(ctx).build();
        ImageLoader.getInstance().init(configuration);
        init();

    }

    public void init() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        convertView = inflater.inflate(R.layout.inspiration_details_view,null);
        setContentView(convertView);
        txtCancelInspirationDialog=(TextView)convertView.findViewById(R.id.txtCancelInspirationDialog);
        parentDialog=(FrameLayout)convertView.findViewById(R.id.parentDialog);
        iv = (ImageView)convertView.findViewById(R.id.imgDialogInspiration);

        iv.getLayoutParams().height = (int)(ctx.getResources().getDisplayMetrics().widthPixels/3);
        iv.requestLayout();
        tvComment = (EditText)convertView.findViewById(R.id.txtDialogCommentInspiration);
        tvComment.setHint("Hjælp andre lærere med gode ideer fra jeres forestilling. Lav en kort beskrivelse her…");
        tvName = (TextView)convertView.findViewById(R.id.txtDialogUserDetailInspiration);
        tvGem = (TextView)convertView.findViewById(R.id.txtDialogGemInspiration);
        setCanceledOnTouchOutside(true);

        parentDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(tvComment.getWindowToken(), 0);
                dismiss();
            }
        });

        txtCancelInspirationDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(tvComment.getWindowToken(), 0);
                dismiss();
            }
        });

    }

    public void setTypeView(){
        tvGem.setVisibility(View.GONE);
    }

    public void setupExistingInspiration(Inspiration inspiration){
        tvComment.setHint("");
        tvComment.setText(inspiration.MessageText);
        tvComment.setEnabled(false);
        tvName.setText(inspiration.UserName+","+inspiration.SchoolName);


        if(inspiration.ImageUrlMedium != null && !inspiration.ImageUrlMedium.equalsIgnoreCase("")){

            ImageLoader.getInstance().loadImage(inspiration.ImageUrlMedium
                    ,new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    iv.setImageBitmap(loadedImage);
                }
            });


//            Picasso.with(ctx)
//                    .load(inspiration.ImageUrlMedium)
//                    .placeholder(R.drawable.camerax)   // optional
//
//                       // optional
//                            // optional
//                    .into(iv);
        }
    }

    public void setupAfterImageChoose(final ChosenImage image,String name){
        //imgProfilePic.setImageURI(Uri.parse(new File(image
        //        .getFileThumbnail()).toString()));
        iv.setImageURI(Uri.parse(new File(image.getFilePathOriginal()).toString()));
        tvName.setText(name);

        tvGem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = "";

                message = tvComment.getText().toString();

              //  Toast.makeText(ctx,image.getExtension(), Toast.LENGTH_SHORT).show();

               listner.selected(image.getFilePathOriginal(),message);
                dismiss();



            }
        });

    }


    public void setSelectedListner(setSelectedListner listner){
        this.listner = listner;
    }

    public static interface setSelectedListner{

        public void selected(String filePath,String message);
    }
}
