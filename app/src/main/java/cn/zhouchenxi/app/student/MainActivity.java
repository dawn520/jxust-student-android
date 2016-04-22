package cn.zhouchenxi.app.student;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.igexin.sdk.PushManager;
import com.mikepenz.fastadapter.utils.RecyclerViewCacheUtil;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.zhouchenxi.app.student.method.mapRead;
import cn.zhouchenxi.app.student.setting.globalData;


public class MainActivity extends AppCompatActivity  {
    public static MaterialDialog connectdialog;
    public String siteurl = globalData.siteUrl;;
    public String uid = null;
    public static Activity mainActivity = null;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    private static final int PROFILE_SETTING = 1;
    private ViewPager pager;
    public static ImageLoader mImageLoader;
    // private ViewPagerAdapter adapter;


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://cn.zhouchenxi.app.student/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://cn.zhouchenxi.app.student/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main);
        mainActivity = this;
        HashMap<String, Object> map = (HashMap<String, Object>) mapRead.getMsg("login",this.getApplicationContext());
        String uid = (String) map.get("uid");
        String user_account = (String) map.get("user_account");
        String user_name = (String) map.get("user_name");
        String group_id = (String) map.get("group_id");

        mImageLoader=ImageLoader.getInstance();
        //初始化auil
        //创建默认的ImageLoader配置参数
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                    .createDefault(this);
            ImageLoader.getInstance().init(configuration);
        }


        // 创建 Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("任务中心");
       // setSupportActionBar(toolbar);
       // toolbar.setNavigationIcon(R.menu.menu_main);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        // 创建 TabHost
     //   tabHost = (MaterialTabHost) this.findViewById(R.id.materialTabHost);
     //   pager = (ViewPager) this.findViewById(R.id.pager);
        // init view pager
     //   ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
     //   pager.setAdapter(adapter);
     //   pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      //      @Override
      //      public void onPageSelected(int position) {
                // when user do a swipe the selected tab change
      //          tabHost.setSelectedNavigationItem(position);

     //       }
     // });

//        // insert all tabs from pagerAdapter data
//        for (int i = 0; i < adapter.getCount(); i++) {
//            tabHost.addTab(
//                    tabHost.newTab()
//                            .setText(adapter.getPageTitle(i))
//                            .setTabListener(this)
//            );
//
//        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



        // Create a few sample profile
        // NOTE you have to define the loader logic too. See the CustomApplication for more details
        final IProfile profile = new ProfileDrawerItem().withName(user_name).withEmail(user_account).withIcon(Uri.parse(siteurl + "/index.php/Home/Client/loadAvatar?uid="+uid+"&size=big")).withIdentifier(100);
        final IProfile profile2 = new ProfileDrawerItem().withName("Bernat Borras").withEmail("alorma@github.com").withIcon(Uri.parse("https://avatars3.githubusercontent.com/u/887462?v=3&s=460")).withIdentifier(101);
        final IProfile profile3 = new ProfileDrawerItem().withName("Max Muster").withEmail("max.mustermann@gmail.com").withIcon(R.drawable.profile2).withIdentifier(102);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile,
                        profile2,
                        profile3,
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName("添加账户").withDescription("Add new GitHub Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_account_add).actionBar().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_SETTING),
                        new ProfileSettingDrawerItem().withName("管理账户").withIcon(GoogleMaterial.Icon.gmd_settings)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                            int count = 100 + headerResult.getProfiles().size() + 1;
                            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman" + count).withEmail("batman" + count + "@gmail.com").withIcon(R.drawable.profile5).withIdentifier(count);
                            if (headerResult.getProfiles() != null) {
                                //we know that there are 2 setting elements. set the new profile above them ;)
                                headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
                            } else {
                                headerResult.addProfiles(newProfile);
                            }
                        }

                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("任务中心").withIcon(FontAwesome.Icon.faw_tasks).withIdentifier(1).withSelectable(false).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_blue_100)),
                        new PrimaryDrawerItem().withName("通知中心").withIcon(GoogleMaterial.Icon.gmd_notifications).withIdentifier(2).withSelectable(false),
                        new PrimaryDrawerItem().withName("教务中心").withIcon(FontAwesome.Icon.faw_building).withIdentifier(3).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName("课表查询").withLevel(2).withIcon(FontAwesome.Icon.faw_table).withIdentifier(2000),
                                new SecondaryDrawerItem().withName("成绩查询").withLevel(2).withIcon(FontAwesome.Icon.faw_th_list).withIdentifier(2001),
                                new SecondaryDrawerItem().withName("考试查询").withLevel(2).withIcon(FontAwesome.Icon.faw_file_text).withIdentifier(2002)
                        ),
                        new PrimaryDrawerItem().withName("理工新闻").withIcon(FontAwesome.Icon.faw_globe).withIdentifier(4).withSelectable(false).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.md_red_700)),
                        new PrimaryDrawerItem().withName("图书馆/一卡通").withIcon(GoogleMaterial.Icon.gmd_library).withIdentifier(5).withSelectable(false),
                        new PrimaryDrawerItem().withName("微博").withIcon(FontAwesome.Icon.faw_comments).withIdentifier(6).withSelectable(false),
                        new SectionDrawerItem().withName("系统"),
                        new SecondaryDrawerItem().withName("个人中心").withIcon(FontAwesome.Icon.faw_user).withIdentifier(20).withSelectable(false),
                        new SecondaryDrawerItem().withName("设置中心").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(21).withSelectable(false),
                        new SecondaryDrawerItem().withName("帮助").withIcon(GoogleMaterial.Icon.gmd_help_outline).withIdentifier(22).withTag("Bullhorn")

                ) // add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 4) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 5) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 7) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 8) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 9) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 10) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 11) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 12) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 13) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 14) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 15) {
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            } else if (drawerItem.getIdentifier() == 20) {
//                                intent = new LibsBuilder()
//                                        .withFields(R.string.class.getFields())
//                                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
//                                        .intent(MainActivity.this);
                                intent = new Intent(MainActivity.this, NoticeActivity.class);
                            }
                            if (intent != null) {
                                MainActivity.this.startActivity(intent);
                            }
                        }

                        return false;
                    }
                })

                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        //if you have many different types of DrawerItems you can magically pre-cache those items to get a better scroll performance
        //make sure to init the cache after the DrawerBuilder was created as this will first clear the cache to make sure no old elements are in
        //RecyclerViewCacheUtil.getInstance().withCacheSize(2).init(result);
        new RecyclerViewCacheUtil<IDrawerItem>().withCacheSize(2).apply(result.getRecyclerView(), result.getDrawerItems());

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(1, false);

            //set the active profile
            headerResult.setActiveProfile(profile);
        }

        result.updateBadge(4, new StringHolder(10 + ""));


        //读取任务进度条
        MainActivity.connectdialog = new MaterialDialog.Builder(this)
                .content("正在读取任务数据,请稍后")
                .progress(true,0)
                .canceledOnTouchOutside(false)
                .show();



        //个推初始化
        PushManager.getInstance().initialize(this.getApplicationContext());
        Toast.makeText(MainActivity.this, "个推推送服务已启动", Toast.LENGTH_SHORT).show();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }



    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
            if (drawerItem instanceof Nameable) {
                Log.i("material-drawer", "DrawerItem: " + ((Nameable) drawerItem).getName() + " - toggleChecked: " + isChecked);
            } else {
                Log.i("material-drawer", "toggleChecked: " + isChecked);
            }
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);
        super.onSaveInstanceState(outState);

    }

//    @Override
//    public void onBackPressed() {
//        //handle the back press :D close the drawer first and if the drawer is closed close the activity
//        if (result != null && result.isDrawerOpen()) {
//            result.closeDrawer();
//        } else {
//            super.onBackPressed();
//        }
//    }

//    //tab相关
//
//    @Override
//    public void onTabSelected(MaterialTab tab) {
//        pager.setCurrentItem(tab.getPosition());
//    }
//
//    @Override
//    public void onTabReselected(MaterialTab tab) {
//
//    }
//
//    @Override
//    public void onTabUnselected(MaterialTab tab) {
//
//    }
//
//    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
//
//        public ViewPagerAdapter(FragmentManager fm) {
//            super(fm);
//
//        }
//
//        //获取tab页面
//        public Fragment getItem(int num) {
////            if(num == 0){ System.out.println("AAAAAAAAAA____1"); return new FragmentNewtask();}
////            else if (num == 1){System.out.println("AAAAAAAAAA____2"); return new FragmentAccepttask();}
////            else if (num == 2){System.out.println("AAAAAAAAAA____3"); return new FragmentSubmittask();}
////            else if (num == 3){ System.out.println("AAAAAAAAAA____4");return new FragmentFinshtask();}
////            else {
//            Log.e("zcx", "zcx");
//            return new FragmentNewtask();
//            //  }
//
//
//        }
//
//        //tab页面的个数
//        @Override
//        public int getCount() {
//            return 4;
//        }
//
//        //初始化页面标题
//        @Override
//        public CharSequence getPageTitle(int position) {
//            String pagetitle[] = {"最新任务", "已接受任务", "已提交任务", "已审核任务"};
//            return pagetitle[position];
//        }
//
//    }

   // 检测返回键按一次后台运行不退出
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //toolbar菜单选项监听
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            switch (menuItem.getItemId()) {
                case R.id.tb_switch:
                    msg += "Click edit";
                    break;
                case R.id.tb_theme:
                    msg += "Click share";
                    break;
                case R.id.tb_done:
                    msg += "Click setting";
                    break;
            }

            if(!msg.equals("")) {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    };


    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentNewtask(), "新任务");
        adapter.addFragment(new FragmentNewtask(), "已接受");
        adapter.addFragment(new FragmentNewtask(), "已提交");
        adapter.addFragment(new FragmentNewtask(), "已完成");
//        adapter.addFragment(new FragmentAccepttask(), "Category 2");
//        adapter.addFragment(new FragmentSubmittask(), "Category 3");
//        adapter.addFragment(new FragmentFinishtask(), "Category 4");
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
    }
    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

    }


}
