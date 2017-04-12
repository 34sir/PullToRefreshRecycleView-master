package com.example.admin.pulltorefreshrecycleview_master;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.admin.pulltorefreshrecycleview_master.item.ButtonItem;
import com.example.admin.pulltorefreshrecycleview_master.item.ImageItem;
import com.example.admin.pulltorefreshrecycleview_master.item.TextItem;
import com.example.admin.pulltorefreshrecycleview_master.model.DemoModel;
import com.example.admin.pulltorefreshrecycleview_master.util.DataManager;
import com.example.admin.pulltorefreshrecycleview_master.util.LayoutUtil;
import com.example.pulltorefreshrecycleview.common.CommonRcvAdapter;
import com.example.pulltorefreshrecycleview.common.PullToRefreshRecycleView;
import com.example.pulltorefreshrecycleview.impl.AdapterItem;
import com.example.pulltorefreshrecycleview.impl.IAdapter;
import com.example.pulltorefreshrecycleview.impl.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2017/4/12.
 */

public class RecyclerViewActivity extends AppCompatActivity implements OnItemClickListener, PullToRefreshRecycleView.LFRecyclerViewListener, PullToRefreshRecycleView.LFRecyclerViewScrollChange {

    private static final String TAG = "RecyclerViewActivity";

    private PullToRefreshRecycleView mRecyclerView;

    private List<DemoModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = new PullToRefreshRecycleView(this);
        LayoutUtil.setContentView(this, mRecyclerView);

        mRecyclerView.setLoadMore(true);
        mRecyclerView.setRefresh(true);
        mRecyclerView.setNoDateShow();
        mRecyclerView.setAutoLoadMore(true);
        mRecyclerView.setOnItemClickListener(this);
        mRecyclerView.setLFRecyclerViewListener(this);
        mRecyclerView.setScrollChangeListener(this);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setRecycleChildrenOnDetach(true);
        mRecyclerView.setLayoutManager(layoutManager);

        // 放一个默认空数据
        mRecyclerView.setAdapter(getAdapter(null));

        // 现在得到数据
        final List<DemoModel> data = DataManager.loadData(getBaseContext());
        ((IAdapter<DemoModel>) mRecyclerView.getAdapter()).setData(data); // 设置新的数据
//        mRecyclerView.getAdapter().notifyDataSetChanged(); // 通知数据刷新

//        loadNewData(data);
    }

    private void loadNewData(final List<DemoModel> data) {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                data.clear();
                data.addAll(DataManager.loadData(getBaseContext())); // 对data进行操作

//                mRecyclerView.getAdapter().notifyDataSetChanged(); // 通知数据刷新

                Toast.makeText(RecyclerViewActivity.this, "refresh completed", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }

    private void loadData(final List<DemoModel> data) {
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                data.addAll(DataManager.loadData(getBaseContext())); // 对data进行操作

//                mRecyclerView.getAdapter().notifyDataSetChanged(); // 通知数据刷新

                Toast.makeText(RecyclerViewActivity.this, "refresh completed", Toast.LENGTH_SHORT).show();
            }
        }, 1000);
    }

    /**
     * CommonAdapter的类型和item的类型是一致的
     * 这里的都是{@link DemoModel}
     * <p>
     * 多种类型的type
     */
    private CommonRcvAdapter<DemoModel> getAdapter(List<DemoModel> data) {
        return new CommonRcvAdapter<DemoModel>(data) {

            @Override
            public Object getItemType(DemoModel demoModel) {
                return demoModel.type;
            }

            @NonNull
            @Override
            public AdapterItem createItem(Object type) {
                Log.d(TAG, "createItem " + type + " view");
                switch (((String) type)) {
                    case "text":
                        return new TextItem();
                    case "button":
                        return new ButtonItem();
                    case "image":
                        return new ImageItem();
                    default:
                        throw new IllegalArgumentException("不合法的type");
                }
            }
        };
    }

    private boolean b;
    public static String LOG = "recycle";

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                b = !b;
                DataManager.flag = "----0";
                List<DemoModel> data = DataManager.loadData(getBaseContext());
                list.clear();
                list.addAll(data);
                ((IAdapter<DemoModel>) mRecyclerView.getAdapter()).setData(data); // 设置新的数据
                mRecyclerView.stopRefresh(b);
            }
        }, 2000);
    }
int flag=0;
    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.stopLoadMore();
                flag++;
                DataManager.flag = "----"+flag;
                List<DemoModel> data = DataManager.loadData(getBaseContext());
                list.addAll(data);
                ((IAdapter<DemoModel>) mRecyclerView.getAdapter()).setData(list); // 设置新的数据

            }
        }, 2000);
    }

    @Override
    public void onRecyclerViewScrollChange(View view, int i, int i1) {

    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void onLongClick(int po) {

    }
}
