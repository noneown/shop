package com.nshop.nshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

import com.nshop.nshop.Contants;
import com.nshop.nshop.R;
import com.nshop.nshop.WareDetailActivity;
import com.nshop.nshop.adapter.HWAdatper;
import com.nshop.nshop.bean.Page;
import com.nshop.nshop.bean.Wares;
import com.nshop.nshop.utils.Pager;
import com.nshop.nshop.adapter.BaseAdapter.OnItemClickListener;


public class HotFragment extends BaseFragment implements Pager.OnPageListener<Wares> {




    private HWAdatper mAdatper;

    @ViewInject(R.id.recyclerview)
    private RecyclerView mRecyclerView;

    @ViewInject(R.id.refresh_view)
    private MaterialRefreshLayout mRefreshLaout;



    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hot,container,false);
    }

    @Override
    public void init() {

        Pager pager = Pager.newBuilder()
                .setUrl(Contants.API.WARES_HOT)
                .setLoadMore(true)
                .setOnPageListener(this)
                .setPageSize(20)
                .setRefreshLayout(mRefreshLaout)
                .build(getContext(), new TypeToken<Page<Wares>>() {}.getType());


        pager.request();

    }


    @Override
    public void load(List<Wares> datas, int totalPage, int totalCount) {

        mAdatper = new HWAdatper(getContext(),datas);

        mAdatper.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Wares wares = mAdatper.getItem(position);

                Intent intent = new Intent(getActivity(), WareDetailActivity.class);

                intent.putExtra(Contants.WARE,wares);
                startActivity(intent);


            }
        });


        mRecyclerView.setAdapter(mAdatper);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    public void refresh(List<Wares> datas, int totalPage, int totalCount) {
        mAdatper.refreshData(datas);

        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void loadMore(List<Wares> datas, int totalPage, int totalCount) {

       mAdatper.loadMoreData(datas);
        mRecyclerView.scrollToPosition(mAdatper.getDatas().size());
    }
}
