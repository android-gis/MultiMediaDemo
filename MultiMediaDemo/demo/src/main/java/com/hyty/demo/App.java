package com.hyty.demo;

import com.hyty.cordova.BaseApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * ================================================================
 * 创建时间：2017/12/14 17:22
 * 创建人：赵文贇
 * 文件描述：
 * 看淡身边的虚伪，静心宁神做好自己。路那么长，无愧走好每一步。
 * ================================================================
 */
public class App extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
//        MobclickAgent.setDebugMode(true);
//        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
//        MobclickAgent. startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this,"5a321f698f4a9d5c0200007d","luxun"));
    }
}
