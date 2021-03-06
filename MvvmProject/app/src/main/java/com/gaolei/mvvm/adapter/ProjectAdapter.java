package com.gaolei.mvvm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.gaolei.mvvm.R;
import com.gaolei.mvvm.databinding.ItemProjectListBinding;
import com.gaolei.mvvm.model.ProjectListData.FeedArticleData;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import static com.gaolei.mvvm.application.CustomApplication.options;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.MyViewHolder> {

    public Context context;
    private OnItemClickListener listener;
    private List<FeedArticleData> list;

    public ProjectAdapter(Context context, List<FeedArticleData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProjectListBinding bindView = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_project_list, parent, false);

        MyViewHolder holder = new MyViewHolder(bindView);
        bindView.getRoot().setOnClickListener(view -> {

            int position = (int) view.getTag();
            if (listener != null) {
                listener.onItemClick(view, position);
            }
        });
        return holder;
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        FeedArticleData projectInfo = list.get(position);
        holder.bindView.itemProjectListTitleTv.setText(projectInfo.getTitle());
        holder.bindView.itemProjectListContentTv.setText(projectInfo.getDesc());
        holder.bindView.itemProjectListTimeTv.setText(projectInfo.getNiceDate());
        holder.bindView.itemProjectListAuthorTv.setText(projectInfo.getAuthor());
        Glide.with(context)
                .load(projectInfo.getEnvelopePic()) // 图片地址
                .apply(options) // 参数
                .into(holder.bindView.itemProjectListIv); // 需要显示的ImageView控件
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ItemProjectListBinding bindView;

        MyViewHolder(ItemProjectListBinding bindView) {
            super(bindView.getRoot());
            this.bindView = bindView;

        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}