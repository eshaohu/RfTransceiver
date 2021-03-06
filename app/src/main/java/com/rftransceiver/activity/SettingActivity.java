package com.rftransceiver.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rftransceiver.R;
import com.rftransceiver.db.DBManager;
import com.rftransceiver.fragments.SettingFragment;
import com.rftransceiver.fragments.SelfInfoFragment;
import com.rftransceiver.util.Constants;
import com.rftransceiver.util.PoolThreadUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by rantianhua on 15-6-26.
 */
public class SettingActivity extends Activity {
    @InjectView(R.id.img_top_left)
    ImageView imgBack;
    @InjectView(R.id.tv_title_left)
    TextView tvTitle;

    private String titleSetting;

    private SettingFragment settingFrag;
    private SelfInfoFragment selfInfoFragment;
    public static final int REQUEST_SETTING = 306;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        titleSetting = getResources().getString(R.string.setting);
        initView();
        initEvent();
    }

    private void initView() {
        ButterKnife.inject(this);
        tvTitle.setText("设置");
        imgBack.setImageResource(R.drawable.back);
        if(settingFrag == null) {
            settingFrag = new SettingFragment();
            settingFrag.setCallbackInSF(new SettingFragment.CallbackInSF() {

                @Override
                public void changeINfo(Intent data) {
                    setResult(MainActivity.REQUEST_SETTING,data);
                }

                @Override

                public void changeInfo() {
                    SharedPreferences sp = getSharedPreferences(Constants.SP_USER, 0);
                    String path = sp.getString(Constants.PHOTO_PATH, "");
                    String name = sp.getString(Constants.NICKNAME,"");
                    Intent intent = new Intent();
                    intent.putExtra(Constants.NICKNAME, name);
                    intent.putExtra(Constants.PHOTO_PATH, path);

                    setResult(REQUEST_SETTING, intent);
                }
            });
        }
        changeFragment(settingFrag, false);
    }

    private void initEvent() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    onBackPressed();
            }


        });
    }

    /**
     * change fragment to show
     * @param fragment
     * @param addToBack if true add the fragment to background
     */
    private void changeFragment(Fragment fragment,boolean addToBack) {
        FragmentTransaction transcation = getFragmentManager().beginTransaction();
        transcation.replace(R.id.frame_container_setting,
                fragment);
        if(addToBack) {
            transcation.addToBackStack(null);
        }
        transcation.commit();
    }


}