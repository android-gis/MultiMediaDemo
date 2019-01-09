package com.hyty.cordova.imagepicker.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.hyty.cordova.MultiMediaConfig;
import com.hyty.cordova.bean.DataBean;
import com.hyty.cordova.bean.Key;
import com.hyty.cordova.imagepicker.DataHolder;
import com.hyty.cordova.imagepicker.ImageDataSource;
import com.hyty.cordova.imagepicker.ImagePicker;
import com.hyty.cordova.R;
import com.hyty.cordova.imagepicker.adapter.ImageFolderAdapter;
import com.hyty.cordova.imagepicker.adapter.ImageRecyclerAdapter;
import com.hyty.cordova.imagepicker.adapter.ImageRecyclerAdapter.OnImageItemClickListener;
import com.hyty.cordova.imagepicker.bean.ImageFolder;
import com.hyty.cordova.imagepicker.bean.ImageItem;
import com.hyty.cordova.imagepicker.util.Utils;
import com.hyty.cordova.imagepicker.view.FolderPopUpWindow;
import com.hyty.cordova.imagepicker.view.GridSpacingItemDecoration;
import com.hyty.cordova.mvp.impl.CopyFilesListener;
import com.jess.arms.utils.ArmsUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * ================================================
 * 作    者：赵文贇
 * 版    本：1.0
 * 创建日期：2016/5/19
 * 描    述：
 * 修订历史：
 * 2017-03-17
 *
 * @author nanchen
 *         新增可直接传递是否裁剪参数，以及直接拍照
 *         ================================================
 */
public class ImageGridActivity extends ImageBaseActivity implements ImageDataSource.OnImagesLoadedListener, OnImageItemClickListener, ImagePicker.OnImageSelectedListener, View.OnClickListener {

    public static final int REQUEST_PERMISSION_STORAGE = 0x01;
    public static final int REQUEST_PERMISSION_CAMERA = 0x02;
    public static final String EXTRAS_TAKE_PICKERS = "TAKE";
    public static final String EXTRAS_IMAGES = "IMAGES";

    private ImagePicker imagePicker;

    private boolean isOrigin = false;  //是否选中原图
    private View mFooterBar;     //底部栏
    private TextView mFooterAllBar; //底部所有选择项
    private Button mBtnOk;       //确定按钮
    private View mllDir; //文件夹切换按钮
    private TextView mtvDir; //显示当前文件夹
    private TextView mBtnPre;      //预览按钮
    private ImageFolderAdapter mImageFolderAdapter;    //图片文件夹的适配器
    private FolderPopUpWindow mFolderPopupWindow;  //ImageSet的PopupWindow
    private List<ImageFolder> mImageFolders;   //所有的图片文件夹
    //    private ImageGridAdapter mImageGridAdapter;  //图片九宫格展示的适配器
    private boolean directPhoto = false; // 默认不是直接调取相机
    private RecyclerView mRecyclerView;
    private ImageRecyclerAdapter mRecyclerAdapter;
    private int resultCode = 0;
    private MultiMediaConfig mMultiMediaConfig;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        directPhoto = savedInstanceState.getBoolean(EXTRAS_TAKE_PICKERS, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRAS_TAKE_PICKERS, directPhoto);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);
        resultCode = getIntent().getIntExtra(Key.REQUEST_CODE, 0);
        //ArmsUtils.showToast("resultCode="+resultCode);
        imagePicker = ImagePicker.getInstance();
        mMultiMediaConfig = MultiMediaConfig.getInstance();
        imagePicker.clear();
        imagePicker.addOnImageSelectedListener(this);
        if (mMultiMediaConfig.getDoType() == 3 || mMultiMediaConfig.getDoType() == 1) {
            imagePicker.setSelectLimit(mMultiMediaConfig.getPreViewData().size());
        } else {
            imagePicker.setSelectLimit(mMultiMediaConfig.getMaxOptionalNum());
        }
        Intent data = getIntent();
        // 新增可直接拍照
        if (data != null && data.getExtras() != null && mMultiMediaConfig.getDoType() != 3) {
            directPhoto = data.getBooleanExtra(EXTRAS_TAKE_PICKERS, false); // 默认不是直接打开相机
            if (directPhoto) {
                if (!(checkPermission(Manifest.permission.CAMERA))) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ImageGridActivity.REQUEST_PERMISSION_CAMERA);
                } else {
                    imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
                }
            }
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(EXTRAS_IMAGES);
            imagePicker.setSelectedImages(images);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);

        findViewById(R.id.btn_back).setOnClickListener(this);
        mFooterBar = findViewById(R.id.footer_bar);
        mFooterAllBar = findViewById(R.id.tv_dir);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        if (mMultiMediaConfig.getDoType() == 3 && mMultiMediaConfig.isCanDelete()) {
            mBtnOk.setVisibility(View.VISIBLE);
            mFooterBar.setVisibility(View.VISIBLE);
        } else if (mMultiMediaConfig.getDoType() == 3 && !mMultiMediaConfig.isCanDelete()) {
            mBtnOk.setVisibility(View.GONE);
            mFooterBar.setVisibility(View.GONE);
        } else {
            mBtnOk.setVisibility(View.VISIBLE);
            mFooterBar.setVisibility(View.VISIBLE);
        }
        mBtnPre = (TextView) findViewById(R.id.btn_preview);
        mBtnPre.setOnClickListener(this);

        mllDir = findViewById(R.id.ll_dir);
        mllDir.setOnClickListener(this);
        if (resultCode==5)mllDir.setVisibility(View.GONE); //如果是从拍照页面进入，测屏蔽掉选择文件夹按钮
        mtvDir = (TextView) findViewById(R.id.tv_dir);
        if (imagePicker.isMultiMode() && mMultiMediaConfig.isCanDelete()) {
            mBtnOk.setVisibility(View.VISIBLE);
            mBtnPre.setVisibility(View.VISIBLE);
        } else if (imagePicker.isMultiMode() && !mMultiMediaConfig.isCanDelete()) {
            if (mMultiMediaConfig.getDoType() == 3) {
                mBtnOk.setVisibility(View.GONE);
                mBtnPre.setVisibility(View.GONE);
            } else {
                mBtnOk.setVisibility(View.VISIBLE);
                mBtnPre.setVisibility(View.VISIBLE);
            }
        }
//        if (imagePicker.isMultiMode()){
//            mBtnOk.setVisibility(View.VISIBLE);
//            mBtnPre.setVisibility(View.VISIBLE);
//
//        }else {
//            mBtnOk.setVisibility(View.GONE);
//            mBtnPre.setVisibility(View.GONE);
//        }

//        mImageGridAdapter = new ImageGridAdapter(this, null);
        mImageFolderAdapter = new ImageFolderAdapter(this, null);
        mRecyclerAdapter = new ImageRecyclerAdapter(this, null);

        onImageSelected(0, null, false);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if(mMultiMediaConfig.getDoType() == 3){
                    mFooterBar.setVisibility(View.GONE);
                    new ImageDataSource(this, MultiMediaConfig.CAMERA_FILE_PATH, this);
                }else{
                    mFooterBar.setVisibility(View.VISIBLE);
                    new ImageDataSource(this, null, this);
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
            }
        } else {
            new ImageDataSource(this, MultiMediaConfig.CAMERA_FILE_PATH, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ImageDataSource(this, MultiMediaConfig.CAMERA_FILE_PATH, this);
            } else {
                showToast("权限被禁止，无法选择本地图片");
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
            } else {
                showToast("权限被禁止，无法打开相机");
            }
        }
    }

    @Override
    protected void onDestroy() {
        imagePicker.removeOnImageSelectedListener(this);
        imagePicker.clear();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            ArrayList<String> paths = new ArrayList<>();
            for (ImageItem item : imagePicker.getSelectedImages()) {
                paths.add(item.path);
            }

            if (mMultiMediaConfig.getDoType() == 3 || mMultiMediaConfig.getDoType() == 1) {
                if (mMultiMediaConfig.getDoType()==3&& !mMultiMediaConfig.isCanDelete()){
                    setResult(resultCode);
                    finish();
                    return;
                }
                intent.putExtra(Key.RESULT_INTENT, paths);
                setResult(resultCode, intent);
                finish();
                return;
            }
//            if (mMultiMediaConfig.getDoType() == 1) {
////                mMultiMediaConfig.setMaxOptionalNum(mMultiMediaConfig.getMaxOptionalNum() + imagePicker.getSelectImageCount());
//                List<DataBean> mDataBeanList = mMultiMediaConfig.getPreViewData();
//                ArrayList<String> backFilesPath = new ArrayList<>();
//                if (mDataBeanList.size() != imagePicker.getSelectImageCount()) {
//                    for (ImageItem item : imagePicker.getSelectedImages()) {//已经选中的图片 123.jpg
//                        for (DataBean mDataBean : mDataBeanList) {//全部预览的图片 123.jpg 456.jpb
//                            if (!item.name.trim().equals(mDataBean.getFileName().trim())) {
//                                backFilesPath.add(MultiMediaConfig.CAMERA_FILE_PATH + "/" + mDataBean.getFileName());
//                            }
//                        }
//                    }
//                }
//
//                intent.putExtra(Key.RESULT_INTENT, backFilesPath);
//                setResult(resultCode, intent);
//                finish();
//                return;
//            }

//            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());

            ArmsUtils.showLoading("处理中，请稍后...", false, null);
            mMultiMediaConfig.commpImages(paths, getApplication(), new CopyFilesListener() {
                @Override
                public void onSucc(ArrayList<String> toFilesPath) {
                    ArmsUtils.dissMissLoading();
                    intent.putExtra(Key.RESULT_INTENT, toFilesPath);
                    setResult(resultCode, intent);  //多选不允许裁剪裁剪，返回数据
                    finish();
                }

                @Override
                public void onError(String errorMsg) {
                    ArmsUtils.dissMissLoading();
                    ArmsUtils.showToast(errorMsg);
                }
            });
        } else if (id == R.id.ll_dir) {
            if (mImageFolders == null) {
                Log.i("ImageGridActivity", "您的手机没有图片");
                return;
            }
            //点击文件夹按钮
            createPopupFolderList();
            mImageFolderAdapter.refreshData(mImageFolders);  //刷新数据
            //让按钮文件夹默认选择系统相册文件夹
            if(resultCode != 4){
                for (int i=0;i<mImageFolders.size();i++){
                    if (mImageFolders.get(i).name!=null&&mImageFolders.get(i).name.equals("Camera")){
                        mImageFolderAdapter.setSelectIndex(i);
                    }
                }
            }

            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.showAtLocation(mFooterBar, Gravity.NO_GRAVITY, 0, 0);
                //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                int index = mImageFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                mFolderPopupWindow.setSelection(index);
            }
        } else if (id == R.id.btn_preview) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imagePicker.getSelectedImages());
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            intent.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);
        } else if (id == R.id.btn_back) {
            //点击返回按钮
            finish();
        }
    }

    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList() {
        mFolderPopupWindow = new FolderPopUpWindow(this, mImageFolderAdapter);
        mFolderPopupWindow.setOnItemClickListener(new FolderPopUpWindow.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mImageFolderAdapter.setSelectIndex(position);
                imagePicker.setCurrentImageFolderPosition(position);
                mFolderPopupWindow.dismiss();
                ImageFolder imageFolder = (ImageFolder) adapterView.getAdapter().getItem(position);
                if (null != imageFolder) {
//                    mImageGridAdapter.refreshData(imageFolder.images);
                    mRecyclerAdapter.refreshData(imageFolder.images);
                    mtvDir.setText(imageFolder.name);
                }
            }
        });
        mFolderPopupWindow.setMargin(mFooterBar.getHeight());
    }

    @Override
    public void onImagesLoaded(List<ImageFolder> imageFolders) {
        this.mImageFolders = imageFolders;
        imagePicker.setImageFolders(imageFolders);
        if (imageFolders.size() == 0) {
            mRecyclerAdapter.refreshData(null);
        } else {
            if (resultCode==5 || resultCode==4) {
            mRecyclerAdapter.refreshData(imageFolders.get(0).images);
            }else {
                //让页面初始化是只显示系统相册
                mRecyclerAdapter.refreshData(null);
                for (int i = 0; i < imageFolders.size(); i++) {
                    if (imageFolders.get(i).name != null && imageFolders.get(i).name.equals("Camera")) {
                        mRecyclerAdapter.refreshData(imageFolders.get(i).images);
                        mtvDir.setText(imageFolders.get(i).name);

                        mImageFolderAdapter.setSelectIndex(i);
                        imagePicker.setCurrentImageFolderPosition(i);
                    }
                }
            }

        }
//        mImageGridAdapter.setOnImageItemClickListener(this);
        mRecyclerAdapter.setOnImageItemClickListener(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, Utils.dp2px(this, 2), false));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mImageFolderAdapter.refreshData(imageFolders);
    }

    @Override
    public void onImageItemClick(View view, ImageItem imageItem, int position) {
        //根据是否有相机按钮确定位置
        position = imagePicker.isShowCamera() ? position - 1 : position;
        if (imagePicker.isMultiMode()) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);

            /**
             * 2017-03-20
             *
             * 依然采用弱引用进行解决，采用单例加锁方式处理
             */

            // 据说这样会导致大量图片的时候崩溃
//            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imagePicker.getCurrentImageFolderItems());

            // 但采用弱引用会导致预览弱引用直接返回空指针
            DataHolder.getInstance().save(DataHolder.DH_CURRENT_IMAGE_FOLDER_ITEMS, imagePicker.getCurrentImageFolderItems());
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);  //如果是多选，点击图片进入预览界面
        } else {
            imagePicker.clearSelectedImages();
            imagePicker.addSelectedImageItem(position, imagePicker.getCurrentImageFolderItems().get(position), true);
            if (imagePicker.isCrop()) {
                Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
            } else {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                finish();
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (imagePicker.getSelectImageCount() > 0) {
            if (mMultiMediaConfig.getDoType() == 3 || mMultiMediaConfig.getDoType()==1) {
                mBtnOk.setText(getString(R.string.ip_select_delete, imagePicker.getSelectImageCount(), imagePicker.getSelectLimit()));
                mBtnPre.setText(getResources().getString(R.string.ip_preview_delete, imagePicker.getSelectImageCount()));
            } else {
                mBtnOk.setText(getString(R.string.ip_select_complete, imagePicker.getSelectImageCount(), imagePicker.getSelectLimit()));
                mBtnPre.setText(getResources().getString(R.string.ip_preview_count, imagePicker.getSelectImageCount()));
            }
            mBtnOk.setEnabled(true);
            mBtnPre.setEnabled(true);
            mBtnPre.setTextColor(ContextCompat.getColor(this, R.color.ip_text_primary_inverted));
            mBtnOk.setTextColor(ContextCompat.getColor(this, R.color.ip_text_primary_inverted));
        } else {
            if (mMultiMediaConfig.getDoType() == 3 || mMultiMediaConfig.getDoType()==1) {
                mBtnOk.setText(getString(R.string.ip_complete_delete));
                mBtnPre.setText(getResources().getString(R.string.ip_preview_delete1));
            } else {
                mBtnOk.setText(getString(R.string.ip_complete));
                mBtnPre.setText(getResources().getString(R.string.ip_preview));
            }

            mBtnOk.setEnabled(false);
            mBtnPre.setEnabled(false);

            mBtnPre.setTextColor(ContextCompat.getColor(this, R.color.ip_text_secondary_inverted));
            mBtnOk.setTextColor(ContextCompat.getColor(this, R.color.ip_text_secondary_inverted));
        }
//        mImageGridAdapter.notifyDataSetChanged();
//        mRecyclerAdapter.notifyItemChanged(position); // 17/4/21 fix the position while click img to preview
//        mRecyclerAdapter.notifyItemChanged(position + (imagePicker.isShowCamera() ? 1 : 0));// 17/4/24  fix the position while click right bottom preview button
        for (int i = imagePicker.isShowCamera() ? 1 : 0; i < mRecyclerAdapter.getItemCount(); i++) {
            if (mRecyclerAdapter.getItem(i).path != null && mRecyclerAdapter.getItem(i).path.equals(item.path)) {
                mRecyclerAdapter.notifyItemChanged(i);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("回到多图选择页面:requestCode:" + requestCode + ",resultCode:" + resultCode + ",intent ==null?" + (data == null));
        if (data == null) return;
//        super.onActivityResult(requestCode, resultCode, data);
//        if (data != null && data.getExtras() != null) {
//            if (resultCode == ImagePicker.RESULT_CODE_BACK) {
//                isOrigin = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
//            } else {
//                //从拍照界面返回
//                //点击 X , 没有选择照片
//                if (data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) == null) {
//                    //什么都不做 直接调起相机
//                } else {
//                    //说明是从裁剪页面过来的数据，直接返回就可以
//                    setResult(ImagePicker.RESULT_CODE_ITEMS, data);
//                }
//                finish();
//            }
//        } else {
//            //如果是裁剪，因为裁剪指定了存储的Uri，所以返回的data一定为null
//            if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_TAKE) {
//                //发送广播通知图片增加了
//                ImagePicker.galleryAddPic(this, imagePicker.getTakeImageFile());
//
//                /**
//                 * 2017-03-21 对机型做旋转处理
//                 */
//                String path = imagePicker.getTakeImageFile().getAbsolutePath();
////                int degree = BitmapUtil.getBitmapDegree(path);
////                if (degree != 0){
////                    Bitmap bitmap = BitmapUtil.rotateBitmapByDegree(path,degree);
////                    if (bitmap != null){
////                        File file = new File(path);
////                        try {
////                            FileOutputStream bos = new FileOutputStream(file);
////                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
////                            bos.flush();
////                            bos.close();
////                        } catch (IOException e) {
////                            e.printStackTrace();
////                        }
////                    }
////                }
//
//                ImageItem imageItem = new ImageItem();
//                imageItem.path = path;
//                imagePicker.clearSelectedImages();
//                imagePicker.addSelectedImageItem(0, imageItem, true);
//                if (imagePicker.isCrop()) {
//                    Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
//                    startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
//                } else {
//                    Intent intent = new Intent();
//                    intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
//                    setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
//                    finish();
//                }
//            } else if (directPhoto) {
//                finish();
//            }
//        }
    }

}