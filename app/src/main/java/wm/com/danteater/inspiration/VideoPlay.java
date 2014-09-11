package wm.com.danteater.inspiration;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;

/**
 * Created by nirav on 09-09-2014.
 */
public class VideoPlay extends BaseActivity {
    private VideoView videoView;
    private MediaController mc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        getActionBar().hide();
        Intent i=getIntent();
        String videoPath=i.getStringExtra("video_path");

        videoView=(VideoView)findViewById(R.id.video_view_full_screen);
//        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
//                + Integer.parseInt(videoPath));
//        Log.e("video path: ", video + "");
//        videoView.setVideoURI(video);
//        mc = new MediaController(this);
//        videoView.setMediaController(mc);
//        videoView.start();

      MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
// Set video link (mp4 format )
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                +R.raw.intro);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);
        videoView.start();
    }
}
