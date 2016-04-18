package com.maplejaw.library.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maplejaw.library.util.ImageLoader;
import com.maplejaw.library.R;
import com.maplejaw.library.util.AlbumModel;

import java.util.ArrayList;
import java.util.List;

public class AlbumListAdapter extends BaseAdapter {
    private List<AlbumModel> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public AlbumListAdapter(List<AlbumModel> list, Context context) {
        if(list==null){
            list=new ArrayList<>();
        }
        mList = list;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public AlbumListAdapter( Context context) {
        this(null,context);

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public AlbumModel getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=mLayoutInflater.inflate(R.layout.list__albums_item,parent,false);
            holder=new ViewHolder();
            holder.albumImage= (ImageView) convertView.findViewById(R.id.iv_album);
            holder.albumName= (TextView) convertView.findViewById(R.id.tv_album_name);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        AlbumModel model=getItem(position);
        holder.albumName.setText(model.getName() + "(" + model.getCount() + ")");
        ImageLoader.getInstance().loadImage(model.getRecent(),holder.albumImage);
        return convertView;
    }

    class ViewHolder{
        ImageView albumImage;
        TextView albumName;
    }


    /** 更新数据 */
    public void notifyDataSetChanged(List<AlbumModel> models,boolean isRefresh) {
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

}
