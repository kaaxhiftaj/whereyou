package com.techease.whereyou.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.techease.whereyou.R;
import com.techease.whereyou.ui.adapters.MessageAdapter;
import com.techease.whereyou.ui.models.ChatMessage;
import com.techease.whereyou.utils.GeneralUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.list_of_messages)
    RecyclerView listOfMessages;
    @BindView(R.id.editWriteMessage)
    EditText editWriteMessage;


    public static final int VIEW_TYPE_USER_MESSAGE = 0;
    public static final int VIEW_TYPE_FRIEND_MESSAGE = 1;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.btnSend)
    ImageButton btnSend;
    @BindView(R.id.btnAttach)
    ImageButton btnAttach;

    private MessageAdapter adapter;
    List<ChatMessage> chat;
    String picturePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        chat = new ArrayList<>();
        adapter = new MessageAdapter(ChatActivity.this, chat);
        listOfMessages.setAdapter(adapter);
        displayChatMessages();
        if (getIntent().hasExtra("comment")) {
            editWriteMessage.setText(getIntent().getStringExtra("comment"));
        }
    }


    private void displayChatMessages() {
        FirebaseDatabase.getInstance().getReference()
                .child("ReviewLocation")
                .child(getIntent().getStringExtra("place_id"))
                .child("chat")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.getValue() != null) {
                            HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                            ChatMessage chatMessage = new ChatMessage();
                            chatMessage.setId((String) mapMessage.get("id"));
                            chatMessage.setLink((Boolean) mapMessage.get("link"));
                            chatMessage.setMessageText((String) mapMessage.get("messageText"));
                            chatMessage.setMessageTime((Long) mapMessage.get("messageTime"));
                            chatMessage.setMessageUser((String) mapMessage.get("messageUser"));
                            chat.add(chatMessage);
                            adapter.notifyDataSetChanged();
                            listOfMessages.scrollToPosition(chat.size() - 1);
                        }

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

    @OnClick({R.id.btnSend, R.id.btnAttach})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSend:
                if (editWriteMessage.length() > 0) {
                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("ReviewLocation")
                            .child(getIntent().getStringExtra("place_id"))
                            .child("chat")
                            .push()
                            .setValue(new ChatMessage(editWriteMessage.getText().toString(),
                                    FirebaseAuth.getInstance()
                                            .getCurrentUser()
                                            .getDisplayName(), FirebaseAuth.getInstance().getUid(), false)
                            );
                    editWriteMessage.setText("");
                }
                break;
            case R.id.btnAttach:
                showPictureSelectionOptionDialog();
                break;
        }
    }

    private void showPictureSelectionOptionDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(ChatActivity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select Photo from Gallery",
                "Capture using Camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                checkGalleryPermission();
                                break;
                            case 1:
                                checkCameraPermission();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void checkGalleryPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        openGalleryForPictureSelection();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ChatActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openGalleryForPictureSelection() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), 1);
    }


    private void checkCameraPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            openCameraForPicture();
                        } else {
                            Toast.makeText(ChatActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openCameraForPicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = GeneralUtils.createImageFile(this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.techease.whereyou.fileprovider", photoFile);
                picturePath = Uri.fromFile(photoFile).toString();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
//                        Bundle extras = data.getExtras();
//                        Bitmap imageBitmap = (Bitmap) extras.get("data");
//                        ivProfileImage.setImageBitmap(imageBitmap);
                    } else {
//                        ivProfileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                    }
//                    ivUploadPic.setVisibility(View.GONE);
//                    ivProfileImage.setVisibility(View.VISIBLE);
//                    btnCancel.setVisibility(View.VISIBLE);
                    uploadPicture();
                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    picturePath = selectedImage.toString();
                    uploadPicture();
//                    ivProfileImage.setImageURI(selectedImage);
//                    UserModel.getInstance().setFilePath(selectedImage.toString());
//                    ivUploadPic.setVisibility(View.GONE);
//                    ivProfileImage.setVisibility(View.VISIBLE);
//                    btnCancel.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void uploadPicture() {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference userRef = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.child("chatPicture").putFile(Uri.parse(picturePath)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                FirebaseDatabase.getInstance()
                        .getReference()
                        .child("ReviewLocation")
                        .child(getIntent().getStringExtra("place_id"))
                        .child("chat")
                        .push()
                        .setValue(new ChatMessage(taskSnapshot.getDownloadUrl().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName(), FirebaseAuth.getInstance().getUid(), true));

            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }

}
