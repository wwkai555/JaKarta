package com.example.kevin.jakarta;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.dxm.rxbinder.annotations.RxBind;
import com.example.kevin.jakarta.databinding.ActivityMusicConfigureBinding;
import com.example.kevin.jakarta.dummy.MusicContent;
import com.example.kevin.jakarta.dummy.VideoInfo;
import com.example.kevin.jakarta.common.Constance;
import com.example.kevin.jakarta.type.MusicTypeFragment;

public class MusicConfigureActivity extends FragmentActivity {
    ActivityMusicConfigureBinding configureBinding;
    MusicControl musicControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_configure);
        configureBinding = DataBindingUtil.setContentView(this, R.layout.activity_music_configure);
        VideoInfo videoInfo = new VideoInfo(15000);
        musicControl = new MusicControl(videoInfo, configureBinding.musicContainer.getId());
        musicControl.showMusicTypeFragment(this);
        musicControl.musicSubject.subscribe(MusicConfigureActivityBindings.showResult(this));
    }

    @RxBind void showResult(MusicControl.ClipMusicInfo clipMusicInfo) {
        configureBinding.setAction(clipMusicInfo.toString());
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MusicConfigureActivity", "onActivityResult requestCode = " + requestCode);
        if (requestCode == MusicTypeFragment.REQUEST_CODE_ONLINE && resultCode == Activity.RESULT_OK) {
            MusicContent.MusicItem item = data.getParcelableExtra(Constance.CLICK_MUSIC);
            musicControl.filterMusicAction(item, this);
        }
    }
}
