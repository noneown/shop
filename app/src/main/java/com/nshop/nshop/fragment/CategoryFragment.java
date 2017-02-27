package com.nshop.nshop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nshop.nshop.Contants;
import com.nshop.nshop.R;
import com.nshop.nshop.adapter.BaseAdapter.OnItemClickListener;
import com.nshop.nshop.bean.Wares;
import com.nshop.nshop.http.BaseCallback;
import com.nshop.nshop.http.OkHttpHelper;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.List;

import com.nshop.nshop.WareDetailActivity;
import com.nshop.nshop.adapter.CategoryAdapter;
import com.nshop.nshop.adapter.WaresAdapter;
import com.nshop.nshop.adapter.decoration.DividerItemDecoration;
import com.nshop.nshop.bean.Banner;
import com.nshop.nshop.bean.Category;
import com.nshop.nshop.bean.Page;
import com.nshop.nshop.http.SpotsCallBack;


public class CategoryFragment extends BaseFragment {



    @ViewInject(R.id.recyclerview_category)
    private RecyclerView mRecyclerView;


    @ViewInject(R.id.recyclerview_wares)
    private RecyclerView mRecyclerviewWares;

    @ViewInject(R.id.refresh_layout)
    private MaterialRefreshLayout mRefreshLaout;

    @ViewInject(R.id.slider)
    private  SliderLayout mSliderLayout;

    private CategoryAdapter mCategoryAdapter;
    private WaresAdapter mWaresAdatper;


    private OkHttpHelper mHttpHelper = OkHttpHelper.getInstance();


    private int currPage=1;
    private int totalPage=1;
    private int pageSize=10;
    private long category_id=0;


    private  static final int STATE_NORMAL=0;
    private  static final int STATE_REFREH=1;
    private  static final int STATE_MORE=2;

    private int state=STATE_NORMAL;






    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category,container,false);
    }

    @Override
    public void init() {

        requestCategoryData();
        requestBannerData();

        initRefreshLayout();
    }


    private  void initRefreshLayout(){

        mRefreshLaout.setLoadMore(true);
        mRefreshLaout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {

                refreshData();

            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {

                if(currPage <=totalPage)
                    loadMoreData();
                else{
//                    Toast.makeText()
                    mRefreshLaout.finishRefreshLoadMore();
                }
            }
        });
    }


    private  void refreshData(){

        currPage =1;

        state=STATE_REFREH;
        requestWares(category_id);

    }

    private void loadMoreData(){

        currPage = ++currPage;
        state = STATE_MORE;
        requestWares(category_id);

    }


    private  void requestCategoryData(){



        mHttpHelper.get(Contants.API.CATEGORY_LIST, new SpotsCallBack<List<Category>>(getContext()) {


            @Override
            public void onSuccess(Response response, List<Category> categories) {

                showCategoryData(categories);

                if(categories !=null && categories.size()>0)
                    category_id = categories.get(0).getId();
                    requestWares(category_id);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

    }

    private  void showCategoryData(List<Category> categories){


        mCategoryAdapter = new CategoryAdapter(getContext(),categories);

        mCategoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Category category = mCategoryAdapter.getItem(position);

                category_id = category.getId();
                currPage=1;
                state=STATE_NORMAL;

                requestWares(category_id);


            }
        });

        mRecyclerView.setAdapter(mCategoryAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));


    }





    private void requestBannerData( ) {



       String url = Contants.API.BANNER+"?type=1";

        mHttpHelper.get(url, new SpotsCallBack<List<Banner>>(getContext()){


            @Override
            public void onSuccess(Response response, List<Banner> banners) {

                showSliderViews(banners);
            }

            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

    }



    private void showSliderViews(List<Banner> banners){




        if(banners !=null){

            for (Banner banner : banners){


                DefaultSliderView sliderView = new DefaultSliderView(this.getActivity());
                sliderView.image(banner.getImgUrl());
                sliderView.description(banner.getName());
                sliderView.setScaleType(BaseSliderView.ScaleType.Fit);
                mSliderLayout.addSlider(sliderView);

            }
        }



        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);

        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        mSliderLayout.setDuration(3000);




    }



    private void requestWares(long categoryId){

        String url = Contants.API.WARES_LIST+"?categoryId="+categoryId+"&curPage="+currPage+"&pageSize="+pageSize;

        mHttpHelper.get(url, new BaseCallback<Page<Wares>>() {
            @Override
            public void onBeforeRequest(Request request) {

            }

            @Override
            public void onFailure(Request request, Exception e) {

            }

            @Override
            public void onResponse(Response response) {

            }

            @Override
            public void onSuccess(Response response, Page<Wares> waresPage) {


                currPage = waresPage.getCurrentPage();
                totalPage =waresPage.getTotalPage();

                showWaresData(waresPage.getList());


            }


            @Override
            public void onError(Response response, int code, Exception e) {

            }
        });

    }



    private  void showWaresData(List<Wares> wares){



        switch (state){

            case  STATE_NORMAL:

                if(mWaresAdatper ==null) {
                    mWaresAdatper = new WaresAdapter(getContext(), wares);
                    mWaresAdatper.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Wares wares = mWaresAdatper.getItem(position);

                            Intent intent = new Intent(getActivity(), WareDetailActivity.class);

                            intent.putExtra(Contants.WARE,wares);
                            startActivity(intent);

                        }
                    });

                    mRecyclerviewWares.setAdapter(mWaresAdatper);

                    mRecyclerviewWares.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    mRecyclerviewWares.setItemAnimator(new DefaultItemAnimator());
//                    mRecyclerviewWares.addItemDecoration(new DividerGridItemDecoration(getContext()));
                }
                else{
                    mWaresAdatper.clear();
                    mWaresAdatper.addData(wares);
                }




                break;

            case STATE_REFREH:
                mWaresAdatper.clear();
                mWaresAdatper.addData(wares);

                mRecyclerviewWares.scrollToPosition(0);
                mRefreshLaout.finishRefresh();
                break;

            case STATE_MORE:
                mWaresAdatper.addData(mWaresAdatper.getDatas().size(),wares);
                mRecyclerviewWares.scrollToPosition(mWaresAdatper.getDatas().size());
                mRefreshLaout.finishRefreshLoadMore();
                break;


        }



    }


}



