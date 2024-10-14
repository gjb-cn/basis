package com.yft.zbase.router;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.chenenyu.router.RouteCallback;
import com.chenenyu.router.Router;
import com.yft.zbase.bean.TargetBean;
import com.yft.zbase.server.DynamicMarketManage;
import com.yft.zbase.server.IServerAgent;
import com.yft.zbase.server.IUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RouterFactory {
    public static final String ROUTER_TEST = "";

    private static RouterFactory instance;

    private final ConcurrentMap<String, String> routerPages = new ConcurrentHashMap<>();

    public synchronized  void initPages(ConcurrentMap<String, String> concurrentMap) {
        routerPages.putAll(concurrentMap);
    }

    public static synchronized RouterFactory getInstance() {
        if (instance == null) {
            synchronized (RouterFactory.class) {
                if (instance == null) {
                    instance = new RouterFactory();
                }
            }
        }
        return instance;
    }

    /**
     * 获取页面路径
     * @param pageKey
     * @return
     */
    public synchronized String getPage(String pageKey) {
        return routerPages.get(pageKey);
    }

    /**
     * 创建页面路由集合
     * @param routers
     * @return
     */
    public synchronized List<IRouter> factoryRouterPages(IRouter...routers) {
        if (routers == null) {
            throw new RuntimeException("routers is null");
        }

       return Arrays.asList(routers);
    }

    /*
       ---------------------------------首页跳转类型----------------------------
     不可用
     */
    public final static String JUMP_INNER_MODULE = "InnerModule";
    public final static String JUMP_LINK_MODULE = "link";
    public final static String JUMP_OUT_LINK_MODULE = "outLink";
    public final static String JUMP_GOODS_LIST_MODULE = "goodsList";
    public final static String JUMP_GOODS_DETAIL_MODULE = "videoDetail";
    public final static String JUMP_INVITE_FRIEND = "inviteFriend"; // 邀请好友

    /**
       不可用
     */
    public final static String TO_HOME_PAGE = "toHomePage";
    public final static String TO_HOME_APPRAISE = "toHomeAppraise";
    public final static String TO_HOME_NEWS = "toHomeNews";
    public final static String TO_HOME_SHOPCAR ="toHomeShopCar";
    public final static String TO_HOME_MINE = "toHomeMine";
    @Deprecated
    private static List<IToHomePageListener> mToHomePageListener;
    // app中全局只能设置一个地方集中跳转
    private static IRouterJumpPages iRouterJumpPages;

    public synchronized static void setRouterJumpPages(IRouterJumpPages iRouterJumpPages) {
        if (RouterFactory.iRouterJumpPages == null) {
            RouterFactory.iRouterJumpPages = iRouterJumpPages;
        }
    }

    @Deprecated
    public synchronized static void addToHomePageListener(IToHomePageListener iToHomePageListener) {
        if (mToHomePageListener == null) {
            mToHomePageListener = new ArrayList<>();
        }
        RouterFactory.mToHomePageListener.add(iToHomePageListener);
    }

    @Deprecated
    public synchronized static void removeToHomePageListener(IToHomePageListener iToHomePageListener) {
        if (RouterFactory.mToHomePageListener != null) {
            RouterFactory.mToHomePageListener.remove(iToHomePageListener);
        }
    }

    public synchronized void startRouterRequestActivity(Context context, String path, int requestCode, RouteCallback routerCallback) {
        Router.build(path)
                .callback(routerCallback)
                .requestCode(requestCode)
                .go(context);
    }

    public synchronized void startRouterRequestActivity(Context context, String path, int requestCode,
                                                  Bundle bundle, RouteCallback routerCallback) {
        Router.build(path)
                .with(bundle)
                .requestCode(requestCode)
                .go(context, routerCallback);
    }

    public synchronized void startRouterBundleActivity(Context context, String path, Bundle bundle) {
        Router.build(path)
                .with(bundle)
                .go(context);
    }

    public synchronized void startRouterRequestActivity(Fragment fragment, String path, int requestCode,
                                                  RouteCallback routerCallback) {
        Router.build(path)
                .callback(routerCallback)
                .requestCode(requestCode)
                .go(fragment);
    }

    public synchronized void startRouterRequestActivity(Fragment fragment, String path, int requestCode, Bundle bundle) {
        Router.build(path)
                .requestCode(requestCode)
                .with(bundle)
                .go(fragment);
    }

    public synchronized void startRouterActivity(Context context, String path) {
        Router.build(path).go(context);
    }

    /**
     * 获取Fragment并携带参数
     *
     * @param context
     * @param path
     * @return
     */
    public synchronized Fragment getFragment(Context context, String path, Bundle bundle) {
        return Router.build(path).with(bundle).getFragment(context);
    }

    /**
     * 获取Fragment
     *
     * @param context
     * @param path
     * @return
     */
    public synchronized Fragment getFragment(Context context, String path ) {
        return Router.build(path).getFragment(context);
    }

    public synchronized boolean jumpToActivity(Context context, TargetBean homeListBean) {
        if (RouterFactory.iRouterJumpPages != null) {
            // 将逻辑全部抛出去
            return RouterFactory.iRouterJumpPages.jumpToActivity(context, homeListBean);
        } else {
            throw new RuntimeException("iRouterJumpPages is null");
        }

        // 在外部实现跳转

//        if (homeListBean == null) {return false;}
//
//        if (RouterFactory.JUMP_INNER_MODULE.equals(homeListBean.getActionType())) {
//            IUser user = DynamicMarketManage.getInstance().getServer(IServerAgent.USER_SERVER);
//            if (!user.isLogin()) {
////                RouterFactory.startRouterActivity(context, RouterFactory.ACTIVITY_USER_LOGIN);
//                RouterFactory.startRouterActivity(context, getPage("LoginActivity"));
//                return false;
//            }
//        }
//
//        try {
//            switch (homeListBean.getActionType()) {
//                case JUMP_LINK_MODULE:
//                    // 内部跳转
//                    Bundle bundle = new Bundle();
//                    bundle.putString("title", "");
//                    bundle.putString("url", homeListBean.getTarget());
//                    //RouterFactory.startRouterBundleActivity(context, RouterFactory.ACTIVITY_WEB, bundle);
//                    RouterFactory.startRouterBundleActivity(context, getPage("WebYftActivity"), bundle);
//                    break;
//                case JUMP_OUT_LINK_MODULE:
//                    // Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
//                    Uri webdress = Uri.parse(homeListBean.getTarget());  // 解析网址
//                    Intent intent = new Intent(Intent.ACTION_VIEW, webdress); // 创建绑定
//                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent); // 开始活动
//                    break;
//                case JUMP_INNER_MODULE:
//                    return startInnerModule(context, homeListBean);
//                case JUMP_INVITE_FRIEND:
//                    bundle = new Bundle();
//                    bundle.putString("pmc", homeListBean.getPmc());
//                    RouterFactory.startRouterBundleActivity(context, getPage("LoginActivity"), bundle);
//                    break;
//                case JUMP_GOODS_DETAIL_MODULE:
//
//                    break;
//                default:{
//                    return false;
//                }
//            }
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
    }

    /**
     * toHomePage 跳转App首页Item
     * toHomeAppraise 跳转App品鉴Item
     * toHomeNews 跳转App消息Item
     * toHomeShopCar 跳转App购物车Item
     * toHomeMine 跳转App个人中心Item
     *
     * toAddressList 跳转收货地址列表
     * toUserIdentity 跳转实名认证页面
     * toOrderList 跳转订单列表
     * toCollectList 跳转收藏列表
     * toProfitPlanList 跳转返现计划列表
     * toInviteFriendList 跳转邀请好友列表
     *
     * toGoldcoinsList 跳转元宝列表
     * toBalanceList 跳转余额列表
     * toGiftVoucherList 跳转兑换券列表
     */
    @Deprecated
    private synchronized boolean startInnerModule(Context context, TargetBean homeListBean) {
        IUser iUser = DynamicMarketManage.getInstance().getServer(IServerAgent.USER_SERVER);
        try {
            switch (homeListBean.getTarget()) {
                case TO_HOME_PAGE:
                case TO_HOME_APPRAISE:
                case TO_HOME_NEWS:
                case TO_HOME_SHOPCAR:
                case TO_HOME_MINE:
                    if (mToHomePageListener != null) {
                        for (IToHomePageListener iToHomePageListener : mToHomePageListener) {
                            if (iToHomePageListener != null) {
                                iToHomePageListener.onToHomePage(homeListBean);
                            }
                        }
                    } else {
                        // 从广告页点击进入，
                        String pageStr = homeListBean.getTarget();
                        Bundle bundle = new Bundle();
                        bundle.putString("initPage", pageStr);
                        RouterFactory.getInstance().startRouterBundleActivity(context, getPage("MainActivity"), bundle);
                    }
                    break;

                default:{
                    return false;
                }
            }
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public interface IRouterJumpPages {
        boolean jumpToActivity(Context context, TargetBean homeListBean);
    }

    public static void exApp() {
        //Router.c();
        //Router.
    }
}
