package com.example.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    DatabaseReference databaseUser;
String id,name;
    private static final int CHOOSE_IMAGE =101;
    TextView textView;
    ImageView imageView;
    EditText editText;
    EditText gender,age,dob,mobile,add,mh;
    Uri uriProfileImage;
    ProgressBar progressBar;
    String profileImageUrl;
    Map<String,String> userMap;
    FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        Toolbar toolbar=findViewById(R.id.toolbar);



        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Personal Information");
        id=mAuth.getCurrentUser().getUid();
        databaseUser= FirebaseDatabase.getInstance().getReference("user");
        textView=(TextView)findViewById(R.id.textViewVerified);
        editText=(EditText)findViewById(R.id.name);
        gender=(EditText)findViewById(R.id.gender);
        age=(EditText)findViewById(R.id.age);
        dob=(EditText)findViewById(R.id.dob);
        mobile=(EditText)findViewById(R.id.mobile);
        add=(EditText)findViewById(R.id.add);
        mh=(EditText)findViewById(R.id.mh);
        imageView=(ImageView)findViewById(R.id.imageView);




        firebaseFirestore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){

                    if(task.getResult().exists()){
                       String na=task.getResult().getString("name");
                       String img=task.getResult().getString("image");
                       uriProfileImage=Uri.parse(img);
                       Log.d("image",img);
                       editText.setText(na);
                        gender.setText(task.getResult().getString("gender"));
                        age.setText(task.getResult().getString("age"));
                        dob.setText(task.getResult().getString("dob"));
                        mobile.setText(task.getResult().getString("mobile"));
                        add.setText(task.getResult().getString("address"));
                        mh.setText(task.getResult().getString("medical"));
                        RequestOptions placeholderRequest=new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.logo);
                       Glide.with(ProfileActivity.this).setDefaultRequestOptions(placeholderRequest).load(img).into(imageView);
findViewById(R.id.submit).setVisibility(View.GONE);
                    }

                }else{
                    Toast.makeText(ProfileActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });


        progressBar=findViewById(R.id.progressbar);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showImageChooser();
            }
        });

        loadUserInformation();

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             saveUserInformation();


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
    private void loadUserInformation() {
        final FirebaseUser user=mAuth.getCurrentUser();


   if(user!=null)
   {

       if(user.isEmailVerified())
       {
           textView.setText("Email Verified");
       }
       else{

           textView.setText("Email Not Verified(Click to Verify)");
           textView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           Toast.makeText(ProfileActivity.this,"Verification Email Sent",Toast.LENGTH_SHORT).show();
                       }
                   });
               }
           });
       }
   }
    }

    private void saveUserInformation() {
        name=editText.getText().toString();
        String gen=gender.getText().toString();
        String ag=age.getText().toString();
        String date=dob.getText().toString();
        String mob=mobile.getText().toString();
        String address=add.getText().toString();
        String medical=mh.getText().toString();
        if(name.isEmpty())
        {
            editText.setError("Name required");
            editText.requestFocus();
            return;
        }
        if(gen.isEmpty())
        {
            gender.setError("Gender required");
            gender.requestFocus();
            return;
        }
        if(ag.isEmpty())
        {
            age.setError("Age required");
            age.requestFocus();
            return;
        }
        if(date.isEmpty())
        {
            dob.setError("DOB required");
            editText.requestFocus();
            return;
        }
        if(mob.isEmpty())
        {
            mobile.setError("Mobile number required");
            mobile.requestFocus();
            return;
        }
        if(address.isEmpty())
        {
            add.setError("Address required");
            add.requestFocus();
            return;
        }
        if(medical.isEmpty())
        {
            mh.setError("Medical history required");
            mh.requestFocus();
            return;
        }

        userMap=new HashMap<>();
        userMap.put("name",editText.getText().toString());
        userMap.put("image",profileImageUrl);
        userMap.put("gender",gen);
        userMap.put("age",ag);
        userMap.put("dob",date);
        userMap.put("mobile",mob);
        userMap.put("address",address);
        userMap.put("medical",medical);
        firebaseFirestore.collection("Users").document(id).set(userMap);



        User us=new User(id,name,gen,ag,date,mob,address,medical);
         databaseUser.child(id).setValue(us);

        FirebaseUser user=mAuth.getCurrentUser();
        if(user!=null && profileImageUrl!=null){
            UserProfileChangeRequest profile=new
                    UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build()
                    ;
            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this,"Profile updated",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CHOOSE_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null){

           uriProfileImage=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                imageView.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {

        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("prfilepics/" + System.currentTimeMillis() + ".jpg");
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
                       userMap=new HashMap<>();
                      userMap.put("name",editText.getText().toString());
                      userMap.put("image",profileImageUrl);
                      userMap.put("gender",gender.getText().toString());
                      userMap.put("age",age.getText().toString());
                      userMap.put("dob",dob.getText().toString());
                      userMap.put("mobile",mobile.getText().toString());
                      userMap.put("address",add.getText().toString());
                      userMap.put("medical",mh.getText().toString());
                  firebaseFirestore.collection("Users").document(id).set(userMap);

                  }
              })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                   progressBar.setVisibility(View.GONE);
                   Toast.makeText(ProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
               startActivity(new Intent(ProfileActivity.this,UserInfo.class));
               break;
       }
        return true;
    }

    private  void showImageChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Image"),CHOOSE_IMAGE);
    }
}
