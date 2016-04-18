package com.maplejaw.library;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.maplejaw.library.adapter.AlbumListAdapter;
import com.maplejaw.library.util.AlbumModel;
import com.maplejaw.library.util.PhotoSelectorHelper;

import java.util.List;

public class AlbumPickActivity extends AppCompatActivity implements PhotoSelectorHelper.OnLoadAlbumListener, AdapterView.OnItemClickListener {
    private PhotoSelectorHelper mHelper;
    private ListView mListView;
    private AlbumListAdapter mAdapter;
    public static final String ALBUM_NAME="album_name";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_pick);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("选择相册");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mListView= (ListView) this.findViewById(R.id.lv_show_album);
        mListView.setAdapter(mAdapter=new AlbumListAdapter(this) );
        mListView.setOnItemClickListener(this);
        mHelper=new PhotoSelectorHelper(this);
        mHelper.getAlbumList(this);
    }

    @Override
    public void onAlbumLoaded(List<AlbumModel> albums) {
        mAdapter.notifyDataSetChanged(albums,true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent();
        intent.putExtra(ALBUM_NAME,mAdapter.getItem(position).getName());
        setResult(RESULT_OK,intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
