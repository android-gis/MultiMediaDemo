package com.hyty.cordova.di.component;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Component;

import com.jess.arms.di.component.AppComponent;

import com.hyty.cordova.di.module.TakeCamera2Module;

import com.hyty.cordova.mvp.ui.activity.TakeCamera2Activity;

/**
 * ================================================================
 * 创建时间：2017-12-18 10:16:52
 * 创建人：赵文贇
 * 文件描述：
 * 看淡身边的虚伪，静心宁神做好自己。路那么长，无愧走好每一步。
 * ================================================================
 */
@ActivityScope
@Component(modules = TakeCamera2Module.class, dependencies = AppComponent.class)
public interface TakeCamera2Component {
    void inject(TakeCamera2Activity activity);
}