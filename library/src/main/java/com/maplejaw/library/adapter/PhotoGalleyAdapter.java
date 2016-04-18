package com.maplejaw.library.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.maplejaw.library.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PhotoGalleyAdapter extends BaseAdapter {
    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    public static List<String> mSelectedImage = new LinkedList<String>();


    private static final int ITME_VIEW = 1;
    private static final int ITME_CAMERA = 0;
    protected Context mContext;
    protected List<String> mList;
    protected LayoutInflater mLayoutInflater;
    private boolean isShowCamera;
    private int maxCount;

    public PhotoGalleyAdapter(Context context) {
        this(context, null);
    }

    public PhotoGalleyAdapter(Context context, List<String> models) {
        this(context, models, false,9);
    }

    public PhotoGalleyAdapter(Context context, List<String> models, boolean isShowCamera,int maxCount) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        if (models == null) {
            models = new ArrayList<>();
        }
        this.mList = models;
        this.isShowCamera = isShowCamera;
        this.maxCount=maxCount;
    }






    @Override
    public int getCount() {
        return isShowCamera ? mList.size() + 1 : mList.size();
    }

    @Override
    public String getItem(int position) {
        if (isShowCamera && position == 0) {
            return null;
        }
        return isShowCamera ? mList.get(position - 1) : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        int type = getItemViewType(position);
        String entity = getItem(position);
        if(convertView==null){
            if(type==ITME_CAMERA){
                convertView=mLayoutInflater.inflate(R.layout.grid_camera_item,parent,false);
            }else{
                convertView=mLayoutInflater.inflate(R.layout.grid_photo_item,parent,false);
                holder=new ViewHolder();
                holder.photo= (ImageView) convertView.findViewById(R.id.ic_image_pick);
                holder.selectFlag= (ImageView) convertView.findViewById(R.id.ic_check_box);
                convertView.setTag(holder);
                bindViewHolder(holder,entity);
            }
        }else{
            if(type==ITME_CAMERA){

            }else{
                holder= (ViewHolder) convertView.getTag();
                bindViewHolder(holder,entity);
            }
        }




        return convertView;
    }



    @Override
    public int getViewTypeCount() {
        return isShowCamera ? 2 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera && position == 0) {
            return ITME_CAMERA;
        } else {
            return ITME_VIEW;
        }

    }



    class ViewHolder {
        ImageView photo;
        ImageView selectFlag;
    }


    private void bindViewHolder(final ViewHolder holder,final String path){
        if(mDisplayAdapter==null){
           return;
        }


        mDisplayAdapter.onDisplayImage(mContext,holder.photo,path);
        if(maxCount>1){
            holder.photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDisplayAdapter.onItemImageClick(mContext, mList.indexOf(path),null);
                }
            });
            holder.selectFlag.setVisibility(View.VISIBLE);
        }else{
            holder.selectFlag.setVisibility(View.GONE);
        }

        holder.selectFlag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked=mSelectedImage.contains(path);

                if(isChecked){
                    mSelectedImage.remove(path);
                    holder.selectFlag.setSelected(false);
                    holder.photo.setColorFilter(null);
                }else{
                    if(mSelectedImage.size()>=maxCount){
                        Toast.makeText(mContext.getApplicationContext(),"不能超过"+maxCount+"张",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mSelectedImage.add(path);
                    holder.selectFlag.setSelected(true);
                    holder.photo.setColorFilter(0x80000000);
                }
                mDisplayAdapter.onImageCheckL(path,!isChecked);
            }
        });

        if(mSelectedImage.contains(path)){
            holder.selectFlag.setSelected(true);
            holder.photo.setColorFilter(0x80000000);
        }else {
            holder.selectFlag.setSelected(false);
            holder.photo.setColorFilter(null);
        }

    }




    /** 更新数据 */
    public void notifyDataSetChanged(List<String> models,boolean isRefresh) {
        if(isRefresh){
            this.mList.clear();
        }
        if(models==null||models.size()==0){
            this.notifyDataSetChanged();
            return;
        }

        this.mList.addAll(models);
        this.notifyDataSetChanged();
    }


    private DisplayImageViewAdapter<String> mDisplayAdapter;
    public void setOnDisplayImageAdapter(DisplayImageViewAdapter<String> adapter){
        this.mDisplayAdapter=adapter;
    }

}
