package edu.northeastern.fall22_team34.sticker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.fall22_team34.R;
import edu.northeastern.fall22_team34.sticker.models.Sticker;
import edu.northeastern.fall22_team34.sticker.models.User;

public class SendStickerActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "STICKER_CHANNEL";
    private static final int NOTIFICATION_ID = 1;

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
    private String imageName;

    private Map<String, Integer> stickerSent = new HashMap<>();
    private List<Sticker> stickerList = new ArrayList<>();

    private Map<String, List<Sticker>> stickerReceived = new HashMap<>();

    private Date createTime = Calendar.getInstance().getTime();
    private String time;


    /* Begin of onCreate */
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

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        time = sdformat.format(createTime);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mDatabase = FirebaseDatabase.getInstance();

        // listen for new or deleted users
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

        // update local data
        mDatabase.getReference().child("users").child(username).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                User user = currentData.getValue(User.class);

                if (user == null) {
                    return Transaction.success(currentData);
                }

                if (user.stickerSent != null) {
                    stickerSent = user.stickerSent;
                }
                if (user.stickerList != null) {
                    stickerList = user.stickerList;
                }
                if (user.stickerReceived != null) {
                    stickerReceived.put(username, user.stickerReceived);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (!committed) {
                    Toast.makeText(SendStickerActivity.this,
                            "DBError: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // listen for updates of stickers received by the current user
        mDatabase.getReference().child("users").child(username).child("stickerReceived")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                        try {
                            if (sdformat.parse(snapshot.getValue(Sticker.class).timeSent).compareTo(sdformat.parse(time)) > 0) {
                                Sticker sticker = snapshot.getValue(Sticker.class);
                                sendNotification(sticker);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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

        // choose a sticker from local downloads
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
                    // select image from download files
                    openStickerSelector();
                }
            }
        });

        // send the sticker to another user
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
                            // start upload
                            uploadSticker(timeSent);
                        }
                    }).start();
                }
            }
        });

        // show stickers received
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

        // show the history of sent stickers
        btnImgSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stickersSentActivity = new Intent(getApplicationContext(),
                        StickersSentActivity.class);
                stickersSentActivity.putExtra("USERNAME", username);
                stickersSentActivity.putExtra("SENT", (Serializable) stickerSent);
                stickersSentActivity.putExtra("STICKERLIST", (Serializable) stickerList);
                startActivity(stickersSentActivity);
            }
        });
    }
    /* End of onCreate */


    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sticker Notification";
            String description = "New Sticker Received";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(Sticker sticker) {
        createNotificationChannel();

        Intent intent = new Intent(this, StickersReceivedActivity.class);
        intent.putExtra("USERNAME", username);
        intent.putExtra("RECEIVED", (Serializable) stickerReceived.get(username));

        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), getResources()
                .getIdentifier(sticker.name, "drawable", getPackageName()));

        NotificationCompat.Builder notifyBuild = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("New " + sticker.name)
                .setContentText("You received a new " + sticker.name + " from " + sticker.sender)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null))
                .setContentIntent(pIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, notifyBuild.build());

    }

    // choose an image by clicking on it
    private void openStickerSelector() {
        /*
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        SelectImgResultLauncher.launch(intent);
         */

        Intent intent = new Intent(this, DisplayLocalStickersActivity.class);
        SelectStickerResultLauncher.launch(intent);
    }

    // load selected sticker
    ActivityResultLauncher<Intent> SelectStickerResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUri = result.getData().getData();
                        Picasso.get().load(imageUri).into(selectedImgView);

                        imageName = result.getData().getStringExtra("NAME");
                    }
                }
            });

    // update sender in database
    private void onSendSticker(DatabaseReference dbRef, String sender, Sticker sticker) {
        dbRef.child("users").child(sender).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                User user = currentData.getValue(User.class);

                if (user == null) {
                    return Transaction.success(currentData);
                }

                if (user.stickerSent == null) {
                    user.stickerSent = stickerSent;
                    user.stickerList = stickerList;
                }

                if (!user.stickerSent.containsKey(sticker.name)) {
                    user.stickerSent.put(sticker.name, 1);
                    user.stickerList.add(sticker);
                } else {
                    user.stickerSent.put(sticker.name, user.stickerSent.get(sticker.name) + 1);
                }

                stickerSent = user.stickerSent;
                stickerList = user.stickerList;

                currentData.setValue(user);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (!committed) {
                    Toast.makeText(SendStickerActivity.this,
                            "DBError: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // update recipient in database
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

    // get file extension
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
                Task<Uri> stickerTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!stickerTask.isSuccessful()) ;
                Uri downloadUri = stickerTask.getResult();
                Sticker sticker = new Sticker(downloadUri.toString(), imageName,
                        username, recipientUsername, timeSent);

                // update sender and recipient in database
                onSendSticker(mDatabase.getReference(), username, sticker);
                onReceiveSticker(mDatabase.getReference(), recipientUsername, sticker);

                Toast.makeText(SendStickerActivity.this,
                        "Sticker Sent Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }
}