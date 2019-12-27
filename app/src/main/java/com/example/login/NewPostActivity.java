package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {
    DatabaseReference databaseUser;





String description;
    Map<String, Object> userMap;
    FirebaseAuth mAuth;
    String id;
    String desc;
    FirebaseFirestore firebaseFirestore;
    private ImageView newPostImage;
    private EditText newPostDesc;
    private Button newpostBtn;
    Uri uriProfileImage;
    String profileImageUrl;
    private ProgressBar progressBar;
    private static final int CHOOSE_IMAGE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = findViewById(R.id.newPostToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Post");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        id=mAuth.getCurrentUser().getUid();
        newPostImage = findViewById(R.id.newPostImage);
        newPostDesc = findViewById(R.id.newPostDesc);
        newpostBtn = findViewById(R.id.postBtn);
        progressBar = findViewById(R.id.progressBar);
        databaseUser= FirebaseDatabase.getInstance().getReference("user");





        progressBar=findViewById(R.id.progressbar);

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });



        findViewById(R.id.postBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
                startActivity(new Intent(NewPostActivity.this,UserInfo.class));

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }

    }


    private void saveUserInformation() {
        description=newPostDesc.getText().toString();

        if(description.isEmpty())
        {
            newPostDesc.setError("Description required");
            newPostDesc.requestFocus();
            return;
        }

        userMap=new HashMap<>();
        userMap.put("desc",newPostDesc.getText().toString());
        userMap.put("image",profileImageUrl);
        userMap.put("id",id);
        userMap.put("timestamp",FieldValue.serverTimestamp());

        firebaseFirestore.collection("Posts").add(userMap);




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CHOOSE_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            uriProfileImage=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                newPostImage.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {

        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("posts/" + System.currentTimeMillis() + ".jpg");
        if(uriProfileImage!=null){
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImageUrl=uri.toString();
                                }
                            });
                           /* userMap=new HashMap<>();
                            userMap.put("desc",newPostDesc.getText().toString());
                            userMap.put("image",profileImageUrl);
                            userMap.put("id",id);
                            userMap.put("timestamp",FieldValue.serverTimestamp());

                            firebaseFirestore.collection("Posts").add(userMap);
*/
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(NewPostActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menuLogout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this,MainActivity.class));
                break;

            case R.id.blog:
                startActivity(new Intent(NewPostActivity.this,UserInfo.class));
                break;
        }
        return true;
    }

    private  void showImageChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Post Image"),CHOOSE_IMAGE);
    }

}
