package com.techease.whereyou.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.techease.whereyou.R;
import com.techease.whereyou.ui.activities.ChatActivity;
import com.techease.whereyou.ui.models.GroupsModel;

import java.util.List;

/**
 * Created by Adam Noor on 14-Feb-18.
 */

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {

    Context context;
    List<GroupsModel> models;

    public GroupsAdapter(Context context, List<GroupsModel> models) {
        this.context = context;
        this.models = models;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_group, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final GroupsModel model = models.get(position);
        holder.tvName.setText(model.getGroupName());
        holder.ratingBar.setRating(Float.parseFloat(String.valueOf(model.getRatingValue())));
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("place_id", model.getGroupId());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return models.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        LinearLayout linearLayout;
        RatingBar ratingBar;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.llParent);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratting_bar);
        }
    }
}
