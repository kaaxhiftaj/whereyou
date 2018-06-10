package com.techease.whereyou.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.techease.whereyou.R;
import com.techease.whereyou.ui.models.ReviewLocation;

import java.util.ArrayList;

/**
 * Created by kaxhiftaj on 2/8/18.
 */

public class MyReviewsAdapter extends RecyclerView.Adapter<MyReviewsAdapter.MyViewHolder> {


    ArrayList<ReviewLocation> myReviewsList;
    Context context;
    String strBlogId;

    public MyReviewsAdapter(Context context, ArrayList<ReviewLocation> myReviews) {
        this.context = context;
        this.myReviewsList = myReviews;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_reviews, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ReviewLocation model = myReviewsList.get(position);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        holder.locationName.setText(model.getLocationName());
        holder.comment.setText(model.getComment());
        holder.ratingBar.setRating((float) model.getRatValue());
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Review");
                builder.setMessage("Are you sure want to delete this review");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("ReviewLocation").child(model.getReviewId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                myReviewsList.remove(position);
                                MyReviewsAdapter.this.notifyItemRemoved(position);
                            }
                        });
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

            }
        });


    }

    @Override
    public int getItemCount() {
        return myReviewsList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewn;
        ImageView ivDelete;
        TextView locationName, comment;
        me.zhanghai.android.materialratingbar.MaterialRatingBar ratingBar;


        public MyViewHolder(View itemView) {
            super(itemView);
            locationName = (TextView) itemView.findViewById(R.id.review_location_name);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            comment = (TextView) itemView.findViewById(R.id.reviews_comment);
            ratingBar = (me.zhanghai.android.materialratingbar.MaterialRatingBar) itemView.findViewById(R.id.ratting_bar);
        }

    }

}
