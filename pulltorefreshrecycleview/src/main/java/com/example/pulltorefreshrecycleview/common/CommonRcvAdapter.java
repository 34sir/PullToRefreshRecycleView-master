package com.example.pulltorefreshrecycleview.common;

import android.content.Context;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.pulltorefreshrecycleview.impl.AdapterItem;
import com.example.pulltorefreshrecycleview.impl.IAdapter;
import com.example.pulltorefreshrecycleview.impl.OnItemClickListener;
import com.example.pulltorefreshrecycleview.util.DataBindingJudgement;
import com.example.pulltorefreshrecycleview.util.ItemTypeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by admin on 2017/4/12.
 */

public abstract class CommonRcvAdapter<T> extends RecyclerView.Adapter<CommonRcvAdapter.RcvAdapterItem> implements IAdapter<T> {

    private List<T> mDataList;
    private Object mType;
    private ItemTypeUtil mUtil;
    private int currentPos;
    public int itemHeight;

    private RecycleViewFooter recyclerViewFooter;
    private RecycleViewHeader recyclerViewHeader;
    private boolean mPullRefreshing;//是否正在刷新
    private boolean mPullLoading;//是否正在加载更多
    private RelativeLayout mHeaderViewContent;
    private int mHeaderViewHeight;
    /*添加头*/
    private View headerView;
    private RecyclerView.Adapter adapter;
    private boolean isLoadMore;   //是否上拉加载
    private boolean isRefresh;   //是否下拉刷新
    //模拟数据
    protected Context mContext;
    public int mHeaderCount = 1;//头部View个数
    public int mBottomCount = 1;//底部View个数
    private OnItemClickListener itemListener;

    //item类型
    public static final int ITEM_TYPE_HEADER = -10;
    public static final int ITEM_TYPE_CONTENT = 1;
    public static final int ITEM_TYPE_BOTTOM = -11;
    public static final int ITEM_TYPE_HEADERVIEW = 3;

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public void setOnItemClickListener(OnItemClickListener itemListener) {
        this.itemListener = itemListener;
    }

    public void setRecyclerViewFooter(RecycleViewFooter recyclerViewFooter) {
        this.recyclerViewFooter = recyclerViewFooter;
    }

    public void setRecyclerViewHeader(RecycleViewHeader recyclerViewHeader) {
        this.recyclerViewHeader = recyclerViewHeader;
    }

    public void setRefresh(boolean refresh) {
        isRefresh = refresh;
        if (refresh) {
            mHeaderCount = 1;
        } else {
            mHeaderCount = 0;
        }
    }

    public void setLoadMore(boolean loadMore) {
        isLoadMore = loadMore;
    }

    //判断当前item是否是HeadView
    public boolean isHeaderView(int position) {
        return mHeaderCount != 0 && position < mBottomCount;
    }

    //判断当前item是否是FooterView
    public boolean isBottomView(int position) {
        return mBottomCount != 0 && position >= adapter.getItemCount()-1;
    }

    //判断当前是否是自定义头部View
    public boolean isCustomHeaderView(int position) {
        return headerView != null && position == mHeaderCount;
    }

    public int getHFCount() {
        int count = getheaderViewCount();
        if (isLoadMore) {
            count += mBottomCount;  //一般情况下设置为上拉加载
        }
        return count;
    }

    public int getheaderViewCount() {
        int count = 0;
        if (isRefresh) {
            count += 1;
        }
        if (headerView != null) {
            count += 1;
        }
        return count;
    }

    public CommonRcvAdapter(@Nullable List<T> data) {
        if (data == null) {
            data = new ArrayList<>();
        }

        if (DataBindingJudgement.SUPPORT_DATABINDING && data instanceof ObservableList) {
            ((ObservableList<T>) data).addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableList<T>>() {
                @Override
                public void onChanged(ObservableList<T> sender) {
                    notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
                    notifyItemRangeChanged(positionStart, itemCount);
                }

                @Override
                public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
                    notifyItemRangeInserted(positionStart, itemCount);
                    notifyChange(sender, positionStart);
                }

                @Override
                public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
                    notifyItemRangeRemoved(positionStart, itemCount);
                    notifyChange(sender, positionStart);
                }

                @Override
                public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {
                    notifyChange(sender, Math.min(fromPosition, toPosition));
                }

                private void notifyChange(ObservableList<T> sender, int start) {
                    onItemRangeChanged(sender, start, getItemCount() - start);
                }

            });
        }
        mDataList = data;
        mUtil = new ItemTypeUtil();
        adapter = this;
    }


    /**
     * 配合RecyclerView的pool来设置TypePool
     *
     * @param typePool
     */
    public void setTypePool(HashMap<Object, Integer> typePool) {
        mUtil.setTypePool(typePool);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size() + getHFCount();  //加上头和尾
    }

    @Override
    public void setData(@NonNull List<T> data) {
        mDataList = data;
    }

    @Override
    public List<T> getData() {
        return mDataList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * instead by{@link #getItemType(Object)}
     * <p>
     * 通过数据得到obj的类型的type
     * 然后，通过{@link ItemTypeUtil}来转换位int类型的type
     */
    @Deprecated
    @Override
    public int getItemViewType(int position) {
        this.currentPos = position;
        if (isHeaderView(position) && isRefresh) {
            //头部View
            return ITEM_TYPE_HEADER;
        } else if (isBottomView(position)) {
            //底部View
            return ITEM_TYPE_BOTTOM;
        }
        if(position-getheaderViewCount()<mDataList.size())
            mType = getItemType(mDataList.get(position-getheaderViewCount()));
        return mUtil.getIntType(mType);
    }

    @Override
    public Object getItemType(T t) {
        return -1; // default
    }

    @Override
    public RcvAdapterItem onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_HEADER) {
            return new RcvAdapterItem(recyclerViewHeader);
        } else if (viewType == ITEM_TYPE_BOTTOM) {
            return new RcvAdapterItem(recyclerViewFooter);
        }else {
            return new RcvAdapterItem(LayoutInflater.from(parent.getContext()).inflate(createItem(mType).getLayoutResId(), parent, false), parent, createItem(mType));
        }
//        return new RcvAdapterItem(parent.getContext(), parent, createItem(mType));
    }


    @Override
    public void onBindViewHolder(RcvAdapterItem holder, int position) {
//        debug(holder);
        if (isHeaderView(position) || isBottomView(position) || isCustomHeaderView(position)) {
            return;
        }
        if (itemHeight == 0) {
            itemHeight = holder.itemView.getHeight();
        }

        if( holder.item!=null&&position-getheaderViewCount()<mDataList.size())  //避免数组溢出
            holder.item.handleData(getConvertedData(mDataList.get(position-getheaderViewCount()), mType), position);
    }

    @NonNull
    @Override
    public Object getConvertedData(T data, Object type) {
        return data;
    }

    @Override
    public int getCurrentPosition() {
        return currentPos;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 内部用到的viewHold
    ///////////////////////////////////////////////////////////////////////////

    static class RcvAdapterItem extends RecyclerView.ViewHolder {

        protected AdapterItem item;

        boolean isNew = true; // debug中才用到

        RcvAdapterItem(View view) {
            super(view);
        }

        RcvAdapterItem(View view, ViewGroup parent, AdapterItem item) {
//            super(LayoutInflater.from(context).inflate(item.getLayoutResId(), parent, false));
            super(view);
            this.item = item;
            this.item.bindViews(itemView);
            this.item.setViews();
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // For debug
    ///////////////////////////////////////////////////////////////////////////

    private void debug(RcvAdapterItem holder) {
        boolean debug = false;
        if (debug) {
            holder.itemView.setBackgroundColor(holder.isNew ? 0xffff0000 : 0xff00ff00);
            holder.isNew = false;
        }
    }

}
