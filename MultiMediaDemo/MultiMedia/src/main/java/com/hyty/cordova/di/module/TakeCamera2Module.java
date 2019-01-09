package com.hyty.cordova.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Module;
import dagger.Provides;

import com.hyty.cordova.mvp.contract.TakeCamera2Contract;
import com.hyty.cordova.mvp.model.TakeCamera2Model;

/**
 * ================================================================
 * 创建时间：2017-12-18 10:16:52
 * 创建人：赵文贇
 * 文件描述：
 * 看淡身边的虚伪，静心宁神做好自己。路那么长，无愧走好每一步。
 * ================================================================
 */
@Module
public class TakeCamera2Module {
    private TakeCamera2Contract.View view;

    /**
     * 构建TakeCamera2Module时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public TakeCamera2Module(TakeCamera2Contract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    TakeCamera2Contract.View provideTakeCamera2View() {
        return this.view;
    }

    @ActivityScope
    @Provides
    TakeCamera2Contract.Model provideTakeCamera2Model(TakeCamera2Model model) {
        return model;
    }
}