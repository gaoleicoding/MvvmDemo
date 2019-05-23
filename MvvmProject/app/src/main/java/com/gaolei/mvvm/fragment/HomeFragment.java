package com.gaolei.mvvm.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gaolei.mvvm.R;
import com.gaolei.mvvm.activity.ArticleDetailActivity;
import com.gaolei.mvvm.adapter.DividerItemDecoration;
import com.gaolei.mvvm.adapter.ProjectAdapter;
import com.gaolei.mvvm.databinding.FragmentHomeBinding;
import com.gaolei.mvvm.mmodel.BannerListData;
import com.gaolei.mvvm.mmodel.ProjectListData;
import com.gaolei.mvvm.viewmodel.BannerViewModel;
import com.gaolei.mvvm.viewmodel.ProjectViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;


public class HomeFragment extends BaseFragment {

    private ProjectAdapter projectAdapter;
    private FragmentHomeBinding binding;
    private ProjectViewModel viewModel;
    private int mCurrentPage = 1;
    private List<ProjectListData.FeedArticleData> articleDataList = new ArrayList<>();

    @Override
    public View getContentLayout(LayoutInflater inflater, ViewGroup container) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    @Override
    public void reload() {
        articleDataList.clear();
        mCurrentPage = 1;
        initData(null);
    }

    @Override
    public void initView() {
        initSmartRefreshLayout();
        projectAdapter = new ProjectAdapter(getActivity(), articleDataList);

        binding.projectRecyclerview.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        binding.projectRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.projectRecyclerview.setAdapter(projectAdapter);
        projectAdapter.setOnItemClickListener(new ProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", articleDataList.get(position).getLink());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData(Bundle bundle) {
        viewModel = ViewModelProviders.of(this)
                .get(ProjectViewModel.class);
        observeViewModel(viewModel);
        viewModel.setProjectParams(new ProjectViewModel.ProjectParams(mCurrentPage, 294));

        final BannerViewModel bannerViewModel = ViewModelProviders.of(this)
                .get(BannerViewModel.class);
        observeBannerViewModel(bannerViewModel);
        bannerViewModel.getBanner();
    }


    //初始化下拉刷新控件
    private void initSmartRefreshLayout() {
        binding.smartRefreshLayout.setEnableLoadMore(true);
        binding.smartRefreshLayout.setEnableRefresh(false);
        binding.smartRefreshLayout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
        binding.smartRefreshLayout.setEnableFooterFollowWhenLoadFinished(true);
        binding.smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                ++mCurrentPage;
                viewModel.setProjectParams(new ProjectViewModel.ProjectParams(mCurrentPage, 294));
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                binding.smartRefreshLayout.finishRefresh(1000);
            }
        });
    }

    private void observeViewModel(final ProjectViewModel viewModel) {
        // Observe project data
        viewModel.getObservableProject().observe(this, new Observer<ProjectListData>() {
            @Override
            public void onChanged(@Nullable ProjectListData listData) {
                if (listData != null) {
                    articleDataList.addAll(listData.data.getDatas());
                    projectAdapter.notifyDataSetChanged();
                    binding.smartRefreshLayout.finishLoadMore();
                }
            }
        });
    }

    private void observeBannerViewModel(final BannerViewModel bannerViewModel) {
        // Observe banner data
        bannerViewModel.getObservableBanner().observe(this, new Observer<BannerListData>() {
            @Override
            public void onChanged(@Nullable BannerListData listData) {
                if (listData != null) {
                    requstBannerList(listData);
                }
            }
        });
    }

    public void requstBannerList(BannerListData itemBeans) {

        final List<String> linkList = new ArrayList<String>();
        List imageList = new ArrayList();
        List titleList = new ArrayList();
        int size = itemBeans.data.size();
        for (int i = 0; i < size; i++) {
            imageList.add(itemBeans.data.get(i).getImagePath());
            titleList.add(itemBeans.data.get(i).getTitle());
            linkList.add(itemBeans.data.get(i).getUrl());
        }
        binding.banner.setImageLoader(new com.youth.banner.loader.ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(getActivity()).load(path).into(imageView);
            }
        });

        binding.banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);//设置圆形指示器与标题

        binding.banner.setBannerAnimation(Transformer.FlipHorizontal);
        binding.banner.setIndicatorGravity(BannerConfig.CENTER);//设置指示器位置
        binding.banner.setDelayTime(3000);//设置轮播时间
        binding.banner.setImages(imageList);//设置图片源
        binding.banner.setBannerTitles(titleList);//设置标题源
        binding.banner.start();
        binding.banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", linkList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


}