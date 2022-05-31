package com.lovense.sdkdemo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lovense.sdklibrary.LovenseToy;

import java.util.List;

/**
 *  Created by Lovense on 2019/5/14
 *
 *  Copyright © 2019 Hytto. All rights reserved.
 */
public class ToyAdapter extends RecyclerView.Adapter<ToyAdapter.ViewHolder> {

    Context context;
    List<LovenseToy> lovenseToyList;

    public ToyAdapter(Context context, List<LovenseToy> lovenseToyList) {
        this.context = context;
        this.lovenseToyList = lovenseToyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = LayoutInflater.from(context).inflate(R.layout.item_toy , null ,false);
        return new ViewHolder(item);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final LovenseToy lovenseToy = lovenseToyList.get(i);
        viewHolder.name.setText("name："+ lovenseToy.getDeviceName());
        viewHolder.id.setText("ID："+ lovenseToy.getUuid());
        viewHolder.rssi.setText("RSSI："+ lovenseToy.getRssi());
        // -1 lovenseToy disconnected 1:lovenseToy connected
        viewHolder.status.setText("status："+(lovenseToy.getStatus()==1?"connected":"not connected"));

        viewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ToyActivity.class);
                intent.putExtra("toyId", lovenseToy.getToyId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return lovenseToyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView id;
        TextView name;
        TextView rssi;
        TextView status;
        LinearLayout ll;
        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_id);
            name = itemView.findViewById(R.id.tv_name);
            rssi = itemView.findViewById(R.id.tv_rssi);
            status = itemView.findViewById(R.id.tv_statu);
            ll = itemView.findViewById(R.id.ll_toy);
        }
    }


}
