package dk.danteater.danteater.excercise;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import dk.danteater.danteater.R;
import dk.danteater.danteater.app.BaseActivity;


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
        int videoPath = i.getIntExtra("video_path",0);
        videoView=(VideoView)findViewById(R.id.video_view_full_screen);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                +videoPath);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);
        videoView.start();
    }
}
