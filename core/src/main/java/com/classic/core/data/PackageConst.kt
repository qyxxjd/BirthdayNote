@file:Suppress("SpellCheckingInspection", "MemberVisibilityCanBePrivate", "unused")

package com.classic.core.data

/**
 * 包名常量
 *
 * @author LiuBin
 * @date 2021/11/25 19:25
 */
object PackageConst {
    // 淘宝
    const val TB = "com.taobao.taobao"
    // 天猫
    const val TM = "com.tmall.wireless"
    // 支付宝
    const val ALIPAY = "com.eg.android.AlipayGphone"
    // 京东
    const val JD = "com.jingdong.app.mall"
    // QQ
    const val QQ = "com.tencent.mobileqq"
    const val QQ_LAUNCHER_UI = "com.tencent.mobileqq.activity.SplashActivity"
    // 微信
    const val WX = "com.tencent.mm"
    const val WX_LAUNCHER_UI = "com.tencent.mm.ui.LauncherUI"
    // 拼多多
    const val PDD = "com.xunmeng.pinduoduo"

    fun findAppName(packageName: String): String = when (packageName) {
        TB -> "淘宝"
        TM -> "天猫"
        ALIPAY -> "支付宝"
        JD -> "京东"
        WX -> "微信"
        PDD -> "拼多多"
        else -> ""
    }
}

// 应用商店	应用包名
// 腾讯应用宝	com.tencent.android.qqdownloader
// 360手机助手	com.qihoo.appstore
// 百度手机助手	com.baidu.appsearch
// 小米应用商店	com.xiaomi.market
// 华为应用商店	com.huawei.appmarket
// Google Play Store	com.android.vending
// 魅族应用市场	com.meizu.mstore
// 豌豆荚	com.wandoujia.phoenix2
// 91手机助手	com.dragon.android.pandaspace
// PP手机助手	com.pp.assistant
// OPPO应用商店	com.oppo.market
// VIVO应用商店	com.bbk.appstore
// 搜狗应用市场	com.sogou.androidtool
// 三星应用商店	com.sec.android.app.samsungapps
// 联想应用商店	com.lenovo.leos.appstore
// 中兴应用商店	zte.com.market
// 安智应用商店	com.hiapk.marketpho
// 应用汇	com.yingyonghui.market
// 机锋应用市场	com.mappn.gfan
// 安卓市场	com.hiapk.marketpho
// GO商店	cn.goapk.market
// 酷派应用商店	com.yulong.android.coolmart
// 酷市场	com.coolapk.market
// 金立软件商店	com.gionee.aora.market
// 更多应用市场，请移驾 Android应用发布平台大汇总
//
// 第三方应用
// 微信
// 应用类别	应用软件	应用包名
// 社交	QQ	com.tencent.mobileqq
// QQ空间	com.qzone
// 微信	com.tencent.mm
// 探探	com.p1.mobile.putong
// 陌陌	com.immomo.momo
// 购物	淘宝	com.taobao.taobao
// 京东	com.jingdong.app.mall
// 拼多多	com.xunmeng.pinduoduo
// 美团	com.sankuai.meituan
// 苏宁易购	com.suning.mobile.ebuy
// 咸鱼	com.taobao.idlefish
// 每日优鲜	cn.missfresh.application
// 阅读	QQ阅读	com.qq.reader
// 微信读书	com.tencent.weread
// 掌阅	com.chaozh.iReaderFree
// 新闻	今日头条	com.ss.android.article.news
// 新浪微博	com.sina.weibo
// 网易新闻	com.netease.newsreader.activity
// 搜狐新闻	com.sohu.newsclient
// 视频	抖音短视频	com.ss.android.ugc.aweme
// 快手	com.smile.gifmaker
// 火山小视频	com.ss.android.ugc.live
// 秒拍	com.yixia.videoeditor
// 优酷	com.youku.phone
// 爱奇艺	com.qiyi.video
// 腾讯视频	com.tencent.qqlive
// 斗鱼直播	air.tv.douyu.android
// 熊猫直播	com.panda.videoliveplatform
// 旅游	携程	ctrip.android.view
// 去哪儿旅行	com.Qunar
// 飞猪	com.taobao.trip
// 艺龙旅行	com.dp.android.elong
// 途牛旅游	com.tuniu.app.ui
// 拍照	美图秀秀	com.mt.mtxx.mtxx
// 美颜相机	com.meitu.meiyancamera
// 美拍	com.meitu.meipaimv
// 金融	支付宝	com.eg.android.AlipayGphone
// 百度钱包	com.baidu.wallet
// 京东钱包	com.wangyin.payment
// 美食	美团外卖	com.sankuai.meituan.takeoutnew
// 饿了么	me.ele
// 大众点评	com.dianping.v1
// 输入法	讯飞输入法	com.iflytek.inputmethod
// 百度输入法	com.baidu.input
// 搜狗输入法	com.sohu.inputmethod.sogou
// 浏览器	QQ浏览器	com.tencent.mtt
// UC浏览器	com.UCMobile
// 搜狗浏览器	sogou.mobile.explorer
// 百度浏览器	com.baidu.browser.apps
// 360浏览器	com.qihoo.browser
// 猎豹浏览器	com.ijinshan.browser_fast
// 谷歌浏览器	com.android.chrome
// 火狐浏览器	org.mozilla.firefox
// 地图	百度地图	com.baidu.BaiduMap
// 高德地图	com.autonavi.minimap
// 谷歌地图	com.google.android.apps.maps
// 出行	摩拜单车	com.mobike.mobikeapp
// OFO单车	so.ofo.labofo
// 滴滴出行	com.sdu.didi.psnger
// 12306	battymole.trainticket
// 航班管家	com.flightmanager.view
// 论坛	简书	com.jianshu.haruki
// 知乎	com.zhihu.android
// 得到	com.luojilab.player
// 音乐	QQ音乐	com.tencent.qqmusic
// 酷狗	com.kugou.android
// 全民K歌	com.tencent.karaoke
// 酷我	cn.kuwo.player
// 虾米	fm.xiami.main
// 唱吧	com.changba
// 网易云音乐	com.netease.cloudmusic
// 喜马拉雅	com.ximalaya.ting.android