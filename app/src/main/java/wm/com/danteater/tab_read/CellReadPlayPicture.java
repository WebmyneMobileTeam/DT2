package wm.com.danteater.tab_read;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.WMButton;

/**
 * Created by dhruvil on 30-09-2014.
 */
public class CellReadPlayPicture {

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
}
