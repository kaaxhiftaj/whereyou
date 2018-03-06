package com.techease.whereyou.ui.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.UploadTask;
import com.techease.whereyou.R;
import com.techease.whereyou.ui.adapters.MessageAdapter;
import com.techease.whereyou.ui.models.Messages;
import com.techease.whereyou.utils.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

import static android.app.Activity.RESULT_OK;


public class Chatting extends Fragment {

    private String mChatUser;
    private Toolbar mChatToolbar;

    private DatabaseReference mRootRef;

    private TextView mGroupTitle;
    private TextView mLastSeenView;
    private TextView mTyingText;
    private CircleImageView mGroupImage;
    private String mCurrentUserId;
    View rootView;
    private EmojiconEditText mChatMessageView;
    private ProgressDialog mprogressDialog;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mMessagesList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private EmojIconActions emojIcon;
    private MessageAdapter mAdapter;
    private DatabaseReference mUserRef,mMembersDatabase;
    private FirebaseAuth mAuth;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    //keep track of camera capture intent
    static final int CAMERA_CAPTURE = 1;
    //keep track of cropping intent
    final int PIC_CROP = 3;
    //keep track of gallery intent
    final int PICK_IMAGE_REQUEST = 2;
    //captured picture uri
    private Uri picUri;
    private Bitmap bitmap;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    private StorageReference imageStorage;
    LinearLayout mContainerImg;
    private String userName;
    private DatabaseReference mNotificationDatabase;
    ImageView mAddImg, mEmojiBtn, mDeleteImg, mPreviewImg;
    TextView mSendMessage;
    String rewiveCreater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_chatting, container, false);


        //Firebase Init Views
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mChatUser = getActivity().getIntent().getStringExtra("from_user_id");
        mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("ReviewNotifications");
        imageStorage = FirebaseStorage.getInstance().getReference();
        mMembersDatabase = FirebaseDatabase.getInstance().getReference().child("Reviews").child(mChatUser).child("group_members");
        mMembersDatabase.keepSynced(true);

        LayoutInflater inflater2 = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater2.inflate(R.layout.chat_app_bar, null);
       // actionbar.setCustomView(action_bar_view);
        //App Bar Init Views
        mGroupTitle = (TextView)view.findViewById(R.id.groupName);
        mPreviewImg = (CircleImageView)view.findViewById(R.id.groupImage);

        //Init Views
        mAddImg = (ImageView)view.findViewById(R.id.addImg);
        mContainerImg = (LinearLayout)view. findViewById(R.id.container_img);
        mDeleteImg = (ImageView)view. findViewById(R.id.deleteImg);
        mEmojiBtn = (ImageView)view. findViewById(R.id.emojiBtn);
        mContainerImg.setVisibility(View.GONE);
        mPreviewImg = (ImageView)view.findViewById(R.id.previewImg);
        mRefreshLayout = (SwipeRefreshLayout)view. findViewById(R.id.message_layout_swip);
        //Progress Dialoge
        mprogressDialog = new ProgressDialog(getActivity());
        mprogressDialog.setTitle("Sending...");
        mprogressDialog.setMessage("Please Wait.....");
        mprogressDialog.setCanceledOnTouchOutside(false);

//Emoje SetUp
        rootView =view.findViewById(R.id.root_view);
        mSendMessage = (TextView)view. findViewById(R.id.sendMessage);
        mChatMessageView = (EmojiconEditText)view.findViewById(R.id.TextMessage);
        emojIcon = new EmojIconActions(getActivity(), rootView, mChatMessageView, mEmojiBtn);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                mChatMessageView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }

            @Override
            public void onKeyboardClose() {
            }
        });

        //DeSelect Image
        mDeleteImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picUri = null;
                mContainerImg.setVisibility(View.GONE);
            }
        });
        //Select Image
        mAddImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });

        //Send Message
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();

            }
        });
        //Refresh Messages
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                itemPos = 0;
                loadMoreMessages();
            }
        });
        loadMessages();

        mAdapter = new MessageAdapter(messagesList);
        mMessagesList = (RecyclerView)view.findViewById(R.id.listView);
        mLinearLayout = new LinearLayoutManager(getActivity());

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);
        mGroupTitle.setText(userName);

        mRootRef.child("Reviews").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("reviewname")&& dataSnapshot.hasChild("uid_created_by")){
                    String groupName = dataSnapshot.child("reviewname").getValue().toString();
                    userName=groupName;
                    mGroupTitle.setText(groupName);
                    rewiveCreater=dataSnapshot.child("uid_created_by").getValue().toString();
                }else {
                    getActivity().finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void SelectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(getActivity());
                if (options[item].equals("Take Photo")) {
                    if (true) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);


                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    if (true) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);
                    }

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    sendImageMessage(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    sendImageMessage(selectedImage);
                }
                break;
        }
    }
    private void sendImageMessage(Uri uri) {
        final String current_user_ref = "Reviews/"+ mChatUser+"/messages";
        // final String chat_user_ref = "Chat/" + mChatUser + "/" + mCurrentUserId + "/messages";

        DatabaseReference user_message_push = mRootRef.child("Reviews").child(mChatUser).child("messages").push();

        final String push_id = user_message_push.getKey();

        final StorageReference filapath = imageStorage.child("message_images").child(push_id + ".jpg");
        filapath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    filapath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            Map messageMap = new HashMap();
                            messageMap.put("message", uri.toString());
                            messageMap.put("seen", false);
                            messageMap.put("type", "image");
                            messageMap.put("time", ServerValue.TIMESTAMP);
                            messageMap.put("from", mCurrentUserId);

                            Map messageUserMap = new HashMap();
                            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                            // messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

                            // mChatMessageView.setText("");

                            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                    if (databaseError != null) {

                                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                                        //  mprogressDialog2.dismiss();
                                        Toast.makeText(getActivity(), "Message Not Sent..", Toast.LENGTH_SHORT).show();

                                    } else {
                                        mRootRef.child("Reviews").child(mChatUser).child("last_message").setValue("Photo!");
                                        mRootRef.child("Reviews").child(mChatUser).child("last_message_time").setValue(ServerValue.TIMESTAMP);
                                        mRootRef.child("Reviews").child(mChatUser).child("name").setValue(userName);
                                        final Map notification = new HashMap<>();
                                        notification.put("from", mCurrentUserId);
                                        notification.put("type", "Single_message");
                                        notification.put("message", "Photo!");
                                        mNotificationDatabase.child(rewiveCreater).push().setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("CHAT_LOG", "Done");
                                                //  mprogressDialog2.dismiss();
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                } else {
                    // mprogressDialog2.dismiss();
                    Toast.makeText(getActivity(), "Error In Uploading Image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void loadMoreMessages() {
        DatabaseReference messageRef = mRootRef.child("Reviews").child(mChatUser).child("messages");
        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);


                String messageKey=dataSnapshot.getKey();
                if (!mPrevKey.equals(messageKey)){
                    messagesList.add(itemPos++,message);
                }else {
                    mPrevKey=mLastKey;
                }

                if (itemPos==1){

                    mLastKey=messageKey;
                }


                mAdapter.notifyDataSetChanged();
                // mMessagesList.scrollToPosition(messagesList.size() - 1);
                mRefreshLayout.setRefreshing(false);
                mLinearLayout.scrollToPositionWithOffset(10,0);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage() {
        final String message = mChatMessageView.getText().toString();

        if (!TextUtils.isEmpty(message)) {

            String current_user_ref = "Reviews/"+ mChatUser+"/messages";
            //String chat_user_ref = "Chat/" + mGroupId + "/" + mCurrentUserId+"/messages";

            DatabaseReference user_message_push = mRootRef.child("Reviews").child(mChatUser).child("messages").push();

            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", mCurrentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            //messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

            mChatMessageView.setText("");

            mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    if (databaseError != null) {

                        Log.d("CHAT_LOG", databaseError.getMessage().toString());

                    }else {
                        mRootRef.child("Reviews").child(mChatUser).child("last_message").setValue(message);
                        mRootRef.child("Reviews").child(mChatUser).child("name").setValue(userName);
                        mRootRef.child("Reviews").child(mChatUser).child("last_message_time").setValue(ServerValue.TIMESTAMP);
                        final Map notification = new HashMap<>();
                        notification.put("from",mCurrentUserId );
                        notification.put("fromGroup",userName );
                        notification.put("type","Single_message");
                        notification.put("message", message);
                        mNotificationDatabase.child(mCurrentUserId).push().setValue(notification).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("CHAT_LOG","Done");
                            }
                        });
                    }
                }
            });
        }


    }
    private void loadMessages() {
        DatabaseReference messageRef = mRootRef.child("Reviews").child(mChatUser).child("messages");
        Query messageQuery = messageRef.limitToLast(mCurrentPage*TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;
                if (itemPos==1){
                    String messageKey=dataSnapshot.getKey();
                    mLastKey=messageKey;
                    mPrevKey=messageKey;
                }

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();
                mMessagesList.scrollToPosition(messagesList.size() - 1);
                mRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


}
