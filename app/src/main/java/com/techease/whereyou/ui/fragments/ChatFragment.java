package com.techease.whereyou.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techease.whereyou.R;
import com.techease.whereyou.ui.models.Message;

import java.sql.Time;

public class ChatFragment extends Fragment {

    RecyclerView mMessageList;
    private FirebaseUser mCurrentUser;
    DatabaseReference mDatabaseUser;
    DatabaseReference messageRef;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText editMessage;
    ImageView btnSend;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat, container, false);

        database=FirebaseDatabase.getInstance();
        mDatabaseUser=database.getReference("user");
        messageRef=database.getReference("Messages");
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Messages");
        btnSend=(ImageView)view.findViewById(R.id.btnSend);
        editMessage=(EditText)view.findViewById(R.id.editMessageE);
        mMessageList=(RecyclerView)view.findViewById(R.id.messageRec);
        mMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        mMessageList.setLayoutManager(linearLayoutManager);
        mAuth=FirebaseAuth.getInstance();


        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()<1)
                {
                    btnSend.setEnabled(false);

                }
                else {
                    btnSend.setEnabled(true);
                }

            }
        });

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null){

                    Fragment fragment=new RegisterFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_main,fragment).commit();
                }
            }
        };
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentUser=mAuth.getCurrentUser();
                mDatabaseUser=FirebaseDatabase.getInstance().getReference().child("user").child(mCurrentUser.getUid());
                final String messageValue=editMessage.getText().toString().trim();
                if (!TextUtils.isEmpty(messageValue))
                {
                    final DatabaseReference newPost=databaseReference.push();
                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("content").setValue(messageValue);
                            newPost.child("user").child("userId").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    mMessageList.scrollToPosition(mMessageList.getAdapter().getItemCount());
                    editMessage.setText("");
                    editMessage.setHint("Type new message");

                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        final Time time=new Time(System.currentTimeMillis());
        time.setTime(System.currentTimeMillis());

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Message,MessageViewHolder> FBRA=new FirebaseRecyclerAdapter<Message, MessageViewHolder> (
                Message.class,R.layout.singlmessagelayout,MessageViewHolder.class,databaseReference)
        {    @Override
        protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
            viewHolder.setContent(model.getContent());
            viewHolder.setUserName(model.getUsername());
            viewHolder.setTime(time);
        }
        };
        mMessageList.setAdapter(FBRA);
    }
    public  static class MessageViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setContent (String content)
        {
            TextView textView=(TextView)itemView.findViewById(R.id.messageText);
            textView.setText(content);
        }
        public void setUserName(String userName)
        {
            TextView textView=(TextView)itemView.findViewById(R.id.usernameText);
            textView.setText(userName);
        }
        public void setTime(Time time)
        {
            TextView textViewtime=(TextView)itemView.findViewById(R.id.timeText);
            textViewtime.setText(String.valueOf(time));
        }
    }
}
