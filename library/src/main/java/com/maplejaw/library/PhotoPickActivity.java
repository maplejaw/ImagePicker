package com.maplejaw.library;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maplejaw.library.adapter.DisplayImageViewAdapter;
import com.maplejaw.library.adapter.PhotoGalleyAdapter;
import com.maplejaw.library.util.AlbumController;
import com.maplejaw.library.util.ImageLoader;
import com.maplejaw.library.util.PhotoSelectorHelper;
import com.maplejaw.library.util.PictureUtil;
import com.maplejaw.library.util.UriUtil;

import java.util.ArrayList;
import java.util.List;

public class PhotoPickActivity extends AppCompatActivity implements PhotoSelectorHelper.OnLoadPhotoListener, AdapterView.OnItemClickListener {
    private PhotoSelectorHelper mHelper;
    private GridView mGridView;
    private PhotoGalleyAdapter mGalleyAdapter;
    private View mPickAlbumView;
    private TextView mCountText;
    public static final String MAX_PICK_COUNT="max_pick_count";
    public static final String IS_SHOW_CAMERA="is_show_camera";
    public static final String SELECT_PHOTO_LIST="select_photo_list";
    private static final int TO_PICK_ALBUM=1;
    private static final int TO_PRIVIEW_PHOTO=2;
    private static final int TO_TAKE_PHOTO=3;

    private boolean isShowCamera;
    private int maxPickCount;
    private String mLastAlbumName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pick);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("最近照片");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        List<String> list=getIntent().getStringArrayListExtra(SELECT_PHOTO_LIST);
        if(list!=null){
            for(String s:list){
                PhotoGalleyAdapter.mSelectedImage.add(s);
            }
        }
        maxPickCount=getIntent().getIntExtra(MAX_PICK_COUNT,1);
        isShowCamera=getIntent().getBooleanExtra(IS_SHOW_CAMERA,false);
        mGridView= (GridView) this.findViewById(R.id.mp_galley_gridView);
        mGridView.setOnItemClickListener(this);
        mCountText= (TextView) this.findViewById(R.id.tv_to_confirm);
        mPickAlbumView=this.findViewById(R.id.tv_to_album);
        mLastAlbumName= AlbumController.RECENT_PHOTO;
        mHelper=new PhotoSelectorHelper(this);
        mHelper.getReccentPhotoList(this);
        mGridView.setAdapter(mGalleyAdapter=new PhotoGalleyAdapter(this,null,isShowCamera,maxPickCount));

        if(maxPickCount>1){
            mCountText.setVisibility(View.VISIBLE);
        }else{
            mCountText.setVisibility(View.GONE);
        }

        mPickAlbumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=  new Intent(PhotoPickActivity.this,AlbumPickActivity.class);
                startActivityForResult(intent,TO_PICK_ALBUM);
            }
        });


        mGalleyAdapter.setOnDisplayImageAdapter(new DisplayImageViewAdapter<String>() {
            @Override
            public void onDisplayImage(Context context, ImageView imageView, String path) {
                ImageLoader.getInstance().loadImage(path,imageView);
         }

            @Override
            public void onItemImageClick(Context context, int index, List<String> list) {
                Intent intent=new Intent(PhotoPickActivity.this,PhotoPreviewActivity.class);
                intent.putExtra(PhotoPreviewActivity.PHOTO_INDEX_IN_ALBUM,index);
                intent.putExtra(MAX_PICK_COUNT,maxPickCount);
                intent.putExtra(AlbumPickActivity.ALBUM_NAME,mLastAlbumName);
                startActivityForResult(intent,TO_PRIVIEW_PHOTO);
            }

            @Override
            public void onImageCheckL(String path, boolean isChecked) {
                updateCountView();
            }
        });


        mCountText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDone();
            }
        });
        updateCountView();


    }

    /**
     * 图片加载完成
     * @param photos
     */
    @Override
    public void onPhotoLoaded(List<String> photos) {
        mGalleyAdapter.notifyDataSetChanged(photos,true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TO_PRIVIEW_PHOTO){
            updateCountView();
            mGalleyAdapter.notifyDataSetChanged();
        }
        if(resultCode!=RESULT_OK){
            return;
        }

        switch (requestCode){
            case TO_PICK_ALBUM:
                String name=data.getStringExtra(AlbumPickActivity.ALBUM_NAME);
                if(mLastAlbumName.equals(name)){
                    return;
                }
                if(getSupportActionBar()!=null){
                    getSupportActionBar().setTitle(name);
                }
                mLastAlbumName=name;
                if(name.equals(AlbumController.RECENT_PHOTO)){
                    mHelper.getReccentPhotoList(this);
                }else{
                    mHelper.getAlbumPhotoList(name,this);
                }
                break;

            case TO_PRIVIEW_PHOTO:
                selectDone();
                break;

            case TO_TAKE_PHOTO:
                String url=UriUtil.getAbsolutePathFromUri(this,mUri);
                PictureUtil.notifyGallery(this,url);
                PhotoGalleyAdapter.mSelectedImage.add(url);
                selectDone();
                break;
        }
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

    private Uri mUri;
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String path=mGalleyAdapter.getItem(position);
        if(PhotoGalleyAdapter.mSelectedImage.size()>=maxPickCount){
            Toast.makeText(getApplicationContext(),"已经选满"+maxPickCount+"张",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(path)){

            mUri=PictureUtil.takePhoto(this,TO_TAKE_PHOTO);
        }else{
            PhotoGalleyAdapter.mSelectedImage.add(path);
            selectDone();
        }
    }


    private void selectDone(){
        ArrayList<String> list=new ArrayList<String>();
        for(String s:PhotoGalleyAdapter.mSelectedImage){
            list.add(s);
        }
        PhotoGalleyAdapter.mSelectedImage.clear();
        Intent intent=new Intent();
        intent.putStringArrayListExtra(SELECT_PHOTO_LIST,list);
        setResult(RESULT_OK,intent);
        finish();
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
    protected void onDestroy() {
        super.onDestroy();
        PhotoGalleyAdapter.mSelectedImage.clear();
    }
}
