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
    }

    public void addBean(String srcPath,String rstPath){
        rstBeans.add(new RstBean(srcPath,rstPath));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return rstBeans.size();
    }

    static class RstHolder extends RecyclerView.ViewHolder {
        ImageView ivRecognize;
        ImageView ivRst;

        public RstHolder(@NonNull View itemView) {
            super(itemView);
            ivRecognize = itemView.findViewById(R.id.iv_torecognize);
            ivRst = itemView.findViewById(R.id.iv_rst);
        }
    }



    static class RstBean {

        public RstBean(String srcPath, String rstPath) {
            this.srcPath = srcPath;
            this.rstPath = rstPath;
        }

        String srcPath;
        String rstPath;
    }
}
