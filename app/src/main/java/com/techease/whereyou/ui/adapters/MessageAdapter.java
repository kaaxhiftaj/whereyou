package com.techease.whereyou.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.techease.whereyou.R;
import com.techease.whereyou.ui.activities.ChatActivity;
import com.techease.whereyou.ui.activities.FullScreenImageActivity;
import com.techease.whereyou.ui.models.ChatMessage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by k.zahid on 4/22/18.
 */

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<ChatMessage> chat;


    public MessageAdapter(Context context, List<ChatMessage> chat) {
        this.context = context;
        this.chat = chat;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatActivity.VIEW_TYPE_FRIEND_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
            return new ItemMessageFriendHolder(view);
        } else if (viewType == ChatActivity.VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
            return new ItemMessageUserHolder(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ConstraintSet constraintSet = new ConstraintSet();

        if (holder instanceof ItemMessageFriendHolder) {
            if (chat.get(position).isLink()) {
                constraintSet.clone(((ItemMessageFriendHolder) holder).constraintParentLayout);
                ((ItemMessageFriendHolder) holder).textMessageBody.setVisibility(View.GONE);
                ((ItemMessageFriendHolder) holder).imageUploaded.setVisibility(View.VISIBLE);
                constraintSet.connect(((ItemMessageFriendHolder) holder).textMessageTime.getId(), ConstraintSet.LEFT, ((ItemMessageFriendHolder) holder).frameLayout.getId(), ConstraintSet.RIGHT);
                constraintSet.connect(((ItemMessageFriendHolder) holder).textMessageTime.getId(), ConstraintSet.BOTTOM, ((ItemMessageFriendHolder) holder).frameLayout.getId(), ConstraintSet.BOTTOM);
                constraintSet.applyTo(((ItemMessageFriendHolder) holder).constraintParentLayout);
                ((ItemMessageFriendHolder) holder).textMessageBody.setVisibility(View.GONE);
                Glide.with(context).load(chat.get(position).getMessageText()).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ((ItemMessageFriendHolder) holder).progressBar.setVisibility(View.GONE);
                        ((ItemMessageFriendHolder) holder).imageUploaded.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Toast.makeText(context, "Load Fail", Toast.LENGTH_LONG).show();
                        ((ItemMessageFriendHolder) holder).progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        ((ItemMessageFriendHolder) holder).progressBar.setVisibility(View.VISIBLE);
                    }
                });;

            } else {
                ((ItemMessageFriendHolder) holder).textMessageBody.setVisibility(View.VISIBLE);
                ((ItemMessageFriendHolder) holder).textMessageBody.setText(chat.get(position).getMessageText());
                ((ItemMessageFriendHolder) holder).imageUploaded.setVisibility(View.GONE);
            }
            ((ItemMessageFriendHolder) holder).textMessageName.setText(chat.get(position).getMessageUser());
            ((ItemMessageFriendHolder) holder).textMessageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", chat.get(position).getMessageTime()));

            ((ItemMessageFriendHolder) holder).imageUploaded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FullScreenImageActivity.class);
                    intent.putExtra("nameUser", chat.get(position).getMessageUser());
                    intent.putExtra("urlPhotoUser", chat.get(position).getMessageText());
                    intent.putExtra("urlPhotoClick", chat.get(position).getMessageText());
                    context.startActivity(intent);
                }
            });

        } else if (holder instanceof ItemMessageUserHolder) {
            if (chat.get(position).isLink()) {
                constraintSet.clone(((ItemMessageUserHolder) holder).constraintParentLayout);
                ((ItemMessageUserHolder) holder).textMessageBody.setVisibility(View.GONE);
                ((ItemMessageUserHolder) holder).imageUploaded.setVisibility(View.VISIBLE);
                constraintSet.connect(((ItemMessageUserHolder) holder).textMessageTime.getId(), ConstraintSet.RIGHT, ((ItemMessageUserHolder) holder).frameLayout.getId(), ConstraintSet.LEFT);
                constraintSet.connect(((ItemMessageUserHolder) holder).textMessageTime.getId(), ConstraintSet.BOTTOM, ((ItemMessageUserHolder) holder).frameLayout.getId(), ConstraintSet.BOTTOM);
                constraintSet.applyTo(((ItemMessageUserHolder) holder).constraintParentLayout);
                ((ItemMessageUserHolder) holder).textMessageBody.setVisibility(View.GONE);
                Glide.with(context).load(chat.get(position).getMessageText()).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        ((ItemMessageUserHolder) holder).progressBar.setVisibility(View.GONE);
                        ((ItemMessageUserHolder) holder).imageUploaded.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Toast.makeText(context, "Load Fail", Toast.LENGTH_LONG).show();
                        ((ItemMessageUserHolder) holder).progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        ((ItemMessageUserHolder) holder).progressBar.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                ((ItemMessageUserHolder) holder).textMessageBody.setVisibility(View.VISIBLE);
                ((ItemMessageUserHolder) holder).textMessageBody.setText(chat.get(position).getMessageText());
                ((ItemMessageUserHolder) holder).imageUploaded.setVisibility(View.GONE);
            }
            ((ItemMessageUserHolder) holder).imageUploaded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, FullScreenImageActivity.class);
                    intent.putExtra("nameUser", chat.get(position).getMessageUser());
                    intent.putExtra("urlPhotoUser", chat.get(position).getMessageText());
                    intent.putExtra("urlPhotoClick", chat.get(position).getMessageText());
                    context.startActivity(intent);
                }
            });
            ((ItemMessageUserHolder) holder).textMessageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", chat.get(position).getMessageTime()));

        }
    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    @Override
    public int getItemViewType(int position) {
        return chat.get(position).getId().equals(FirebaseAuth.getInstance().getUid()) ? ChatActivity.VIEW_TYPE_USER_MESSAGE : ChatActivity.VIEW_TYPE_FRIEND_MESSAGE;
    }

    class ItemMessageUserHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_message_body)
        TextView textMessageBody;
        @BindView(R.id.text_message_time)
        TextView textMessageTime;
        @BindView(R.id.image_uploaded)
        ImageView imageUploaded;
        @BindView(R.id.constraint_parent_layout)
        ConstraintLayout constraintParentLayout;
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;
        @BindView(R.id.frame_layout)
        FrameLayout frameLayout;

        public ItemMessageUserHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_message_profile)
        ImageView imageMessageProfile;
        @BindView(R.id.text_message_name)
        TextView textMessageName;
        @BindView(R.id.text_message_body)
        TextView textMessageBody;
        @BindView(R.id.text_message_time)
        TextView textMessageTime;
        @BindView(R.id.image_uploaded)
        ImageView imageUploaded;
        @BindView(R.id.constraint_parent_layout)
        ConstraintLayout constraintParentLayout;
        @BindView(R.id.progress_bar)
        ProgressBar progressBar;
        @BindView(R.id.frame_layout)
        FrameLayout frameLayout;

        public ItemMessageFriendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
