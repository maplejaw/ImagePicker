package com.maplejaw.library;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maplejaw.library.adapter.DisplayImageViewAdapter;
import com.maplejaw.library.adapter.PhotoGalleyAdapter;
import com.maplejaw.library.util.AlbumController;
import com.maplejaw.library.util.ImageLoader;
import com.maplejaw.library.util.PhotoSelectorHelper;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class PhotoPreviewActivity extends AppCompatActivity implements PhotoSelectorHelper.OnLoadPhotoListener, ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private ImageView mCheckBox;
    private TextView mCountText;
    private TextView mPreviewNum;
    public  static final String PHOTO_INDEX_IN_ALBUM="photo_index_in_album";
    private int index,maxPickCount;
    private String albumName;
    private List<String> mList;
    private PhotoAdapter mPhotoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);

        index=getIntent().getIntExtra(PHOTO_INDEX_IN_ALBUM,0);
        maxPickCount=getIntent().getIntExtra(PhotoPickActivity.MAX_PICK_COUNT,1);
        albumName=getIntent().getStringExtra(AlbumPickActivity.ALBUM_NAME);
        ActionBar mActionBar=getSupportActionBar();
        if(mActionBar!=null){
            mActionBar.setTitle(albumName);
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mViewPager= (ViewPager) this.findViewById(R.id.viewpager_preview_photo);
        mCheckBox= (ImageView) this.findViewById(R.id.checkbox_sel_flag);
        mPreviewNum= (TextView) this.findViewById(R.id.tv_preview_num);
        mCountText= (TextView) this.findViewById(R.id.tv_to_confirm);
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setAdapter(mPhotoAdapter=new PhotoAdapter(mList=new ArrayList<String>()));
        mPhotoAdapter.setOnDisplayImageAdapter(new DisplayImageViewAdapter<String>() {
            @Override
            public void onDisplayImage(Context context, ImageView imageView, String path) {
                ImageLoader.getInstance().loadImage(path,imageView);
            }
        });
        if(albumName!=null&&!albumName.equals(AlbumController.RECENT_PHOTO)){
            new PhotoSelectorHelper(this).getAlbumPhotoList(albumName,this);
        }else {
            new PhotoSelectorHelper(this).getReccentPhotoList(this);
        }

        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selCount= PhotoGalleyAdapter.mSelectedImage.size();
                if(selCount>=maxPickCount){
                    Toast.makeText(getApplicationContext(),"已经选满"+selCount+"张",Toast.LENGTH_SHORT).show();
                    return;
                }
                int index=mViewPager.getCurrentItem();
                boolean selFlag=PhotoGalleyAdapter.mSelectedImage.contains(mList.get(index));
                mCheckBox.setSelected(!selFlag);
                if(selFlag){
                    PhotoGalleyAdapter.mSelectedImage.remove(mList.get(index));
                }else{
                    PhotoGalleyAdapter.mSelectedImage.add(mList.get(index));
                }
                updateCountView();
            }
        });

        updateCountView();

        mCountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }

    @Override
    public void onPhotoLoaded(List<String> photos) {
        mList.clear();
        mList.addAll(photos);
        mPhotoAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(index,false);
        mPreviewNum.setText(index+1+"/"+mList.size());
        mCheckBox.setSelected(PhotoGalleyAdapter.mSelectedImage.contains(mList.get(index)));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCheckBox.setSelected(PhotoGalleyAdapter.mSelectedImage.contains(mList.get(position)));
        mPreviewNum.setText(position+1+"/"+mList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class  PhotoAdapter extends PagerAdapter {
        private List<String> imgList;

        public PhotoAdapter(List<String> imgList) {
            this.imgList = imgList;
        }

        @Override
        public int getCount() {
            return imgList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final PhotoView photoView = new PhotoView(container.getContext());

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            if(mDisplayAdapter!=null){
                mDisplayAdapter.onDisplayImage(PhotoPreviewActivity.this,photoView,imgList.get(position));
            }


            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private DisplayImageViewAdapter<String> mDisplayAdapter;
        public void setOnDisplayImageAdapter(DisplayImageViewAdapter<String> adapter){
            this.mDisplayAdapter=adapter;
        }
    }

    private void updateCountView(){
        if(PhotoGalleyAdapter.mSelectedImage.size()==0){
            mCountText.setEnabled(false);
        }else{
            mCountText.setEnabled(true);
        }
        mCountText.setText("确定("+PhotoGalleyAdapter.mSelectedImage.size()+"/"+maxPickCount+")");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id ==android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
