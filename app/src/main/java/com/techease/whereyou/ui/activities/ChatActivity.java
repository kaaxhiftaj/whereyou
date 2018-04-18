package com.techease.whereyou.ui.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.techease.whereyou.R;
import com.techease.whereyou.ui.models.ChatMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.list_of_messages)
    ListView listOfMessages;
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;


    private FirebaseListAdapter<ChatMessage> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        displayChatMessages();
        if (getIntent().hasExtra("comment")) {
            input.setText(getIntent().getStringExtra("comment"));
        }
    }

    @OnClick(R.id.fab)
    public void onViewClicked() {
        if (input.length() > 0) {
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("ReviewLocation")
                    .child(getIntent().getStringExtra("place_id"))
                    .child("chat")
                    .push()
                    .setValue(new ChatMessage(input.getText().toString(),
                            FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getDisplayName())
                    );
            input.setText("");
        }
    }


    private void displayChatMessages() {

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference()
                .child("ReviewLocation")
                .child(getIntent().getStringExtra("place_id"))
                .child("chat")) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
    }
}
