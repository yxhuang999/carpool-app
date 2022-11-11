package edu.northeastern.fall22_team34.sticker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.sticker.models.Sticker;
import edu.northeastern.fall22_team34.sticker.models.User;

public class SendStickerActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private StorageReference mStorageRef;

    private String username;
    private List<String> validUsernames;

    private EditText recipientText;
    private String recipientUsername;

    private Button btnChooseImg;
    private ImageView selectedImgView;

    private Button btnSend;
    private Button btnImgReceived;
    private Button btnImgSent;

    private Uri imageUri;

    private Map<String, Integer> stickerSent = new HashMap<>();
    private Map<String, List<Sticker>> stickerReceived = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sticker);

        username = getIntent().getStringExtra("USERNAME");
        validUsernames = (List<String>) getIntent().getSerializableExtra("VALID_USERNAMES");

        recipientText = findViewById(R.id.recipient_text);

        btnChooseImg = findViewById(R.id.btnChooseImg);
        selectedImgView = findViewById(R.id.selectedImg);

        btnSend = findViewById(R.id.btnSend);
        btnImgReceived = findViewById(R.id.btnImgReceived);
        btnImgSent = findViewById(R.id.btnImgSent);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.getReference().child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                validUsernames.add(user.username);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                validUsernames.remove(user.username);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.getReference().child("users").child(username).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //List<Sticker> receivedList = (List<Sticker>) snapshot.getValue();
                //stickerReceived.put(username, receivedList);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validUsernames.contains(recipientText.getText().toString())) {
                    Toast.makeText(getApplicationContext(),
                            "Username Does not Exist", Toast.LENGTH_SHORT).show();
                } else if (recipientText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(),
                            "Recipient Username Cannot be Empty", Toast.LENGTH_SHORT).show();
                } else if (recipientText.getText().toString().equals(username)) {
                    Toast.makeText(getApplicationContext(),
                            "You Cannot Send Stickers to Yourself", Toast.LENGTH_SHORT).show();
                } else {
                    recipientUsername = recipientText.getText().toString();
                    openFileSelector();
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please Choose a Sticker First", Toast.LENGTH_SHORT).show();
                } else {
                    Date date = Calendar.getInstance().getTime();
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                    String timeSent = dateFormat.format(date);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            uploadSticker(timeSent);
                        }
                    }).start();
                }
            }
        });

        btnImgReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stickersReceivedActivity = new Intent(getApplicationContext(),
                        StickersReceivedActivity.class);
                stickersReceivedActivity.putExtra("USERNAME", username);
                stickersReceivedActivity.putExtra("RECEIVED", (Serializable) stickerReceived.get(username));
                startActivity(stickersReceivedActivity);
            }
        });

        /* btnImgSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stickersSentActivity = new Intent(getApplicationContext(),
                        StickersSentActivity.class);
                stickersSentActivity.putExtra("USERNAME", username);
                startActivity(stickersSentActivity);
            }
        }); */
    }

    private void openFileSelector() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        SelectImgResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> SelectImgResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUri = result.getData().getData();
                        Picasso.get().load(imageUri).into(selectedImgView);
                    }
                }
            });

    private void onSendSticker(DatabaseReference dbRef, String sender, Sticker sticker) {
        dbRef.child("users").child(sender).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                User user = currentData.getValue(User.class);

                if (user == null) {
                    return Transaction.success(currentData);
                }

                if (!stickerSent.containsKey(sticker.name)) {
                    stickerSent.put(sticker.name, 1);
                } else {
                    stickerSent.put(sticker.name, user.stickerSent.get(sticker.name) + 1);
                }
                user.stickerSent = stickerSent;

                currentData.setValue(user);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (!committed) {
                    Toast.makeText(SendStickerActivity.this,
                            "DBError: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void onReceiveSticker(DatabaseReference dbRef, String recipient, Sticker sticker) {
        dbRef.child("users").child(recipient).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                User user = currentData.getValue(User.class);

                if (user == null) {
                    return Transaction.success(currentData);
                }

                if (!stickerReceived.containsKey(recipient)) {
                    stickerReceived.put(recipient, new ArrayList<>());
                }
                stickerReceived.get(recipient).add(sticker);

                if (user.stickerReceived == null) {
                    user.stickerReceived = stickerReceived.get(recipient);
                } else {
                    user.stickerReceived.add(sticker);
                }

                currentData.setValue(user);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (!committed) {
                    Toast.makeText(SendStickerActivity.this,
                            "DBError: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void uploadSticker(String timeSent) {
        StorageReference stickerRef = mStorageRef.child(System.currentTimeMillis() + "." + getExtension(imageUri));

        stickerRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SendStickerActivity.this,
                        "Sticker Sent Successfully", Toast.LENGTH_SHORT).show();

                Task<Uri> stickerTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!stickerTask.isSuccessful());
                Uri downloadUri = stickerTask.getResult();
                Sticker sticker = new Sticker(downloadUri.toString(),
                        imageUri.toString().replaceAll("[^a-zA-Z0-9]", ""),
                        username, recipientUsername, timeSent);

                onSendSticker(mDatabase.getReference(), username, sticker);
                onReceiveSticker(mDatabase.getReference(), recipientUsername, sticker);
            }
        });
    }
}