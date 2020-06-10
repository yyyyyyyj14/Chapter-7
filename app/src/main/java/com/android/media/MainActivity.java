package com.android.media;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout rl;
    private MyVideoView videoView;
    public static final int PICK_VIDEO = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.my_video_view);
        rl = findViewById(R.id.video_rl);

        if (getIntent().getData() == null) {
            return;
        }
        playVideo(getIntent().getData());
    }

    private void playVideo(Uri uri) {
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setMediaPlayer(videoView);
        videoView.setVideoURI(uri);
        videoView.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ADD_VIDEO://添加文件管理内的视频
                PickVideo();
                break;
            case R.id.LOAD_VIDEO://加载内置的示例视频
                Uri uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + R.raw.bytedance);
                playVideo(uri);
                break;
        }
        return true;
    }

    private void PickVideo() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, MainActivity.PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO && data != null) {
            playVideo(data.getData());
        }
    }

    /**
     * 切换横竖屏的关键代码，需要预先修改AndroidManifest.xml添加相关横竖屏属性
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , dip2px(this, 235f));
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            videoView.setLayoutParams(params);
            getSupportActionBar().show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            videoView.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , LinearLayout.LayoutParams.MATCH_PARENT));
            getSupportActionBar().hide();
        }
    }

    /**
     * 根据手机分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
