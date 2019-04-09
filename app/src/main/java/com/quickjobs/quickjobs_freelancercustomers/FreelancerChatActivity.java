package com.quickjobs.quickjobs_freelancercustomers;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class FreelancerChatActivity extends AppCompatActivity {

    private ImageButton sendButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessage;
    private String currentUserId,currentUserName;
    private FirebaseAuth mAuth;
    private String currentGroupName,currentDate,currentTime;
    private Toolbar mToolbar;
    private DatabaseReference userRef,groupNameRef,messageKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_freelancer);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef = FirebaseDatabase.getInstance().getReference().child("Chats").child(currentUserId);
        InitializeFeild();
        getUserInfo();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMessageInfoToDatabase();
                userMessageInput.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });




    }

    private void saveMessageInfoToDatabase() {

        String message = userMessageInput.getText().toString();
        String messageKey = groupNameRef.push().getKey();
        if (TextUtils.isEmpty(message)){
            Toast.makeText(FreelancerChatActivity.this,"Empty feild",Toast.LENGTH_SHORT);
        }else{
            Calendar ccalForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MM dd, yyyy");
            currentDate = currentDateFormat.format(ccalForDate.getTime());


            Calendar ccalForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(ccalForTime.getTime());

            HashMap<String,Object> groupMessageKey = new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);

            messageKeyRef = groupNameRef.child(messageKey);

            HashMap<String,Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name",currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);

            messageKeyRef.updateChildren(messageInfoMap);


        }
    }

    private void getUserInfo() {

        userRef.child("Freelancers").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFeild() {

        sendButton = (ImageButton)findViewById(R.id.send_messgae_btn);
        userMessageInput = (EditText)findViewById(R.id.input_group_message);
        displayTextMessage = (TextView)findViewById(R.id.group_chat_text_display);
        mScrollView = (ScrollView)findViewById(R.id.my_Scroll_view);

    }

    @Override
    protected void onStart() {
        super.onStart();
        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    DisplayMessage(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists()){
                    DisplayMessage(dataSnapshot);
                }
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

    private void DisplayMessage(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext()){
            String chatDate = (String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String)((DataSnapshot)iterator.next()).getValue();
            String chatName  = (String)((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String)((DataSnapshot)iterator.next()).getValue();

            displayTextMessage.append(chatName + " :\n" + chatMessage + " \n"+ chatTime + "    "+ chatDate + "\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }
}