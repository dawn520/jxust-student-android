package cn.zhouchenxi.app.student.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.swipeitemlayout.BGASwipeItemLayout;
import cn.zhouchenxi.app.student.MainActivity;
import cn.zhouchenxi.app.student.R;
import cn.zhouchenxi.app.student.model.TaskModel;
import cn.zhouchenxi.app.student.setting.globalData;


/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/5/22 16:31
 * 描述:
 */
public class TaskAdapter extends BGARecyclerViewAdapter<TaskModel.returndata> {
    private BGASwipeItemLayout mOpenedSil;

    public TaskAdapter(Context context) {
        super(context, R.layout.item_swipe);
    }

    @Override
    public void setItemChildListener(BGAViewHolderHelper viewHolderHelper) {
        BGASwipeItemLayout swipeItemLayout = viewHolderHelper.getView(R.id.sil_item_swipe_root);
        swipeItemLayout.setDelegate(new BGASwipeItemLayout.BGASwipeItemLayoutDelegate() {
            @Override
            public void onBGASwipeItemLayoutOpened(BGASwipeItemLayout swipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim();
                mOpenedSil = swipeItemLayout;
            }

            @Override
            public void onBGASwipeItemLayoutClosed(BGASwipeItemLayout swipeItemLayout) {
                mOpenedSil = null;
            }
        });
        viewHolderHelper.setItemChildClickListener(R.id.tv_item_swipe_delete);
        viewHolderHelper.setItemChildLongClickListener(R.id.tv_item_swipe_delete);
    }

    @Override
    public void fillData(BGAViewHolderHelper viewHolderHelper, int position, TaskModel.returndata model) {
        closeOpenedSwipeItemLayoutWithAnim();
        viewHolderHelper
                .setText(R.id.tv_item_swipe_title, model.title)
                .setText(R.id.tv_item_swipe_detail, model.detail)
                .setText(R.id.tv_item_swipe_author, model.author)
                .setText(R.id.tv_item_swipe_time, model.time);
                //.setImageBitmap(R.id.tv_item_swipe_pic,model.face);
        ImageView imageView = viewHolderHelper.getView(R.id.tv_item_swipe_pic);

            //  imageView.setImageResource(R.drawable.profile);

        String siteurl = globalData.siteUrl;
        String faceImageUrl = siteurl + "/index.php/Home/Client/loadAvatar?uid="+model.fromuid+"&size=big";



        //显示图片的配置
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .showImageOnLoading(R.drawable.loading_spinner_blue)
                            .showImageOnFail(R.drawable.loading_spinner_blue)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
        MainActivity.mImageLoader.getInstance().displayImage(faceImageUrl, imageView, options);

//        String siteurl = globalData.siteUrl;
//
//
//                    String faceImageUrl = siteurl + "/index.php/Home/Client/loadAvatar?uid=49&size=big";
//                    //创建默认的ImageLoader配置参数
//                    if (!ImageLoader.getInstance().isInited()) {
//                        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
//                                .createDefault(mContext);
//                        ImageLoader.getInstance().init(configuration);
//                    }
//                    //显示图片的配置
//                    DisplayImageOptions options = new DisplayImageOptions.Builder()
//                            .showImageOnLoading(R.drawable.loading_spinner_blue)
//                            .showImageOnFail(R.drawable.loading_spinner_blue)
//                            .cacheInMemory(true)
//                            .cacheOnDisk(true)
//                            .bitmapConfig(Bitmap.Config.RGB_565)
//                            .build();
//        ImageLoader.getInstance().displayImage(faceImageUrl, imageView, options);


    }

    public void closeOpenedSwipeItemLayoutWithAnim() {
        if (mOpenedSil != null) {
            mOpenedSil.closeWithAnim();
            mOpenedSil = null;
        }
    }



}