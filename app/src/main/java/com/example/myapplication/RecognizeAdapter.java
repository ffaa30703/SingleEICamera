package com.example.myapplication;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Jiyf
 * @package:com.example.myapplication
 * @date on 2019-06-10   18:04
 * @email ffaa30703@icloud.com
 */
public class RecognizeAdapter extends RecyclerView.Adapter<RecognizeAdapter.RstHolder> {


    List<RstBean> rstBeans = new ArrayList<>();


    @NonNull
    @Override
    public RstHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recognize_rst, viewGroup, false);
        return new RstHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RstHolder rstHolder, int i) {
        RstBean rstBean = rstBeans.get(i);
        if (!TextUtils.isEmpty(rstBean.srcPath)) {
            File srcFile = new File(rstBean.srcPath);
            if (srcFile.exists())
                Glide.with(rstHolder.itemView.getContext()).load(srcFile).into(rstHolder.ivRecognize);
        }

        if (!TextUtils.isEmpty(rstBean.rstPath)) {
            File rstFile = new File(rstBean.rstPath);
            if (rstFile.exists())
                Glide.with(rstHolder.itemView.getContext()).load(rstFile).into(rstHolder.ivRst);
        }

        rstHolder.tvIndex.setText("------" + i + "------>");
        if (!TextUtils.isEmpty(rstBean.srcName))
            rstHolder.tvSrcName.setText(rstBean.srcName);
        if (!TextUtils.isEmpty(rstBean.rstName))
            rstHolder.tvRstName.setText(rstBean.rstName);
    }

    public void addBean(String srcPath, String srcName, String rstPath, String rstName) {
        rstBeans.add(new RstBean(srcPath, srcName, rstPath, rstName));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return rstBeans.size();
    }

    static class RstHolder extends RecyclerView.ViewHolder {
        ImageView ivRecognize;
        ImageView ivRst;
        TextView tvSrcName;
        TextView tvRstName;
        TextView tvIndex;


        public RstHolder(@NonNull View itemView) {
            super(itemView);
            ivRecognize = itemView.findViewById(R.id.iv_torecognize);
            ivRst = itemView.findViewById(R.id.iv_rst);
            tvSrcName = itemView.findViewById(R.id.tv_src_name);
            tvRstName = itemView.findViewById(R.id.tv_rst_name);
            tvIndex = itemView.findViewById(R.id.tv_index);
        }
    }


    static class RstBean {

        public RstBean(String srcPath, String srcName, String rstPath, String rstName) {
            this.srcName = srcName;
            this.srcPath = srcPath;
            this.rstPath = rstPath;
            this.rstName = rstName;
        }

        String srcName;
        String srcPath;
        String rstPath;
        String rstName;
    }
}
