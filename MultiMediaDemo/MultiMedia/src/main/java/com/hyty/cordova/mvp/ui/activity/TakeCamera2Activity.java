package com.hyty.cordova.mvp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.hyty.cordova.di.component.DaggerTakeCamera2Component;
import com.hyty.cordova.di.module.TakeCamera2Module;
import com.hyty.cordova.mvp.contract.TakeCamera2Contract;
import com.hyty.cordova.mvp.presenter.TakeCamera2Presenter;

import timber.log.Timber;

import com.hyty.cordova.R;


import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * ================================================================
 * 创建时间：2017-12-18 10:16:52
 * 创建人：赵文贇
 * 文件描述：
 * 看淡身边的虚伪，静心宁神做好自己。路那么长，无愧走好每一步。
 * ================================================================
 */
public class TakeCamera2Activity extends BaseActivity<TakeCamera2Presenter> implements TakeCamera2Contract.View {


    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerTakeCamera2Component //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .takeCamera2Module(new TakeCamera2Module(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_take_camera2; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }


    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }


    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }


}
