package com.hyty.cordova.mvp.presenter;

import android.app.Application;
import android.content.DialogInterface;

import com.jess.arms.integration.AppManager;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.http.imageloader.ImageLoader;

import me.jessyan.rxerrorhandler.core.RxErrorHandler;

import javax.inject.Inject;

import com.hyty.cordova.mvp.contract.TakeCamera2Contract;


/**
 * ================================================================
 * 创建时间：2017-12-18 10:16:52
 * 创建人：赵文贇
 * 文件描述：
 * 看淡身边的虚伪，静心宁神做好自己。路那么长，无愧走好每一步。
 * ================================================================
 */
@ActivityScope
public class TakeCamera2Presenter extends BasePresenter<TakeCamera2Contract.Model, TakeCamera2Contract.View> implements DialogInterface.OnCancelListener {
    private RxErrorHandler mErrorHandler;
    private Application mApplication;
    private ImageLoader mImageLoader;
    private AppManager mAppManager;

    @Inject
    public TakeCamera2Presenter(TakeCamera2Contract.Model model, TakeCamera2Contract.View rootView
            , RxErrorHandler handler, Application application
            , ImageLoader imageLoader, AppManager appManager) {
        super(model, rootView);
        this.mErrorHandler = handler;
        this.mApplication = application;
        this.mImageLoader = imageLoader;
        this.mAppManager = appManager;
    }


    public DialogInterface.OnCancelListener getCancleListener() {
        return this;
    }


    @Override
    public void onCancel(DialogInterface mDialogInterface) {
        unDispose();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }

}
