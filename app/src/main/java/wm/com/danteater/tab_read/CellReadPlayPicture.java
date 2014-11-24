package wm.com.danteater.tab_read;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.WMButton;
import wm.com.danteater.model.SingleMediaScanner;

/**
 * Created by dhruvil on 30-09-2014.
 */
public class CellReadPlayPicture implements View.OnClickListener{

    Context ctx;
    ImageView img;
    WMButton btnLoad;
    WMButton btnSave;
    ImageLoader loader;
    Bitmap bitmap;

    public CellReadPlayPicture(View view,Context ctx) {

        this.ctx = ctx;
        img = (ImageView)view.findViewById(R.id.imgItemPictureCell);
        btnLoad = (WMButton)view.findViewById(R.id.readPlayPictureCellLoadImage);
        btnSave = (WMButton)view.findViewById(R.id.readPlayPictureCellSaveImage);
        btnSave.setOnClickListener(this);
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(ctx).build();
        ImageLoader.getInstance().init(configuration);

    }

    public void setupForPlayLine(PlayLines playline){

        img.setVisibility(View.GONE);
        btnLoad.setVisibility(View.VISIBLE);

        String url = playline.textLinesList.get(0).LineText;

        if(bitmap == null){
            ImageLoader.getInstance().loadImage(url,new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    // Do whatever you want with Bitmap
                    bitmap = loadedImage;
                    setImageToCell(bitmap);
                }
            });

        }else{
            setImageToCell(bitmap);
        }

    }

    public void setImageToCell(Bitmap b){

        btnLoad.setVisibility(View.GONE);
        img.setVisibility(View.VISIBLE);
        img.setImageBitmap(b);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.readPlayPictureCellSaveImage:



                if(bitmap == null){
                    Toast.makeText(ctx, "Billedet er ikke indlæst", Toast.LENGTH_SHORT).show();
                }else{

                    String extr = Environment.getExternalStorageDirectory().toString();
                    File mFolder = new File(extr + "/danteater");

                    if (!mFolder.exists()) {
                        mFolder.mkdir();
                    }

                    String strF = mFolder.getAbsolutePath();
                    File mSubFolder = new File(strF + "/images");

                    if (!mSubFolder.exists()) {
                        mSubFolder.mkdir();
                    }

                  String s = System.currentTimeMillis()+".png";
                  File fToSave = new File(mSubFolder.getAbsolutePath(),s);

                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(fToSave);
                        bitmap.compress(Bitmap.CompressFormat.PNG,70, fos);

                        fos.flush();
                        fos.close();
                    }catch (FileNotFoundException e) {

                        e.printStackTrace();
                    } catch (Exception e) {

                        e.printStackTrace();
                    }

                    Toast.makeText(ctx, "gemt", Toast.LENGTH_SHORT).show();
                    try{
                        new SingleMediaScanner(ctx, fToSave);
                    }catch(Exception e){}



                }


                break;

        }

    }
}
