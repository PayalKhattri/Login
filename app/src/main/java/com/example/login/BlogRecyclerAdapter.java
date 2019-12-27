package com.example.login;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.FieldNamingStrategy;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

   public List<blogpost> bloglist;
   public Context context;
   private FirebaseFirestore firebaseFirestore;
   private FirebaseAuth firebaseAuth;
    public BlogRecyclerAdapter(List<blogpost> bloglist){
       this.bloglist=bloglist;
   }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.bloglistitem,parent,false);
       context=parent.getContext();
       firebaseFirestore=FirebaseFirestore.getInstance();
       firebaseAuth=FirebaseAuth.getInstance();
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
    holder.setIsRecyclable(false);
     final String blogPostId=bloglist.get(position).BlogPostId;
     final String currentUserId=firebaseAuth.getCurrentUser().getUid();
      String descdata=bloglist.get(position).getDesc();
      holder.setDescText(descdata);
      String url=bloglist.get(position).getImage();
      holder.setBlogImage(url);
      String id=bloglist.get(position).getId();
firebaseFirestore.collection("Users").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
   if(task.isSuccessful()){
       String username=task.getResult().getString("name");
       String userimage=task.getResult().getString("image");
       holder.setUserData(username,userimage);
   }
   else{

   }
    }
});

      long milliseconds=bloglist.get(position).getTimestamp().getTime();
        java.util.Date dateObj = new java.util.Date(milliseconds);

        SimpleDateFormat dateformatMMDDYYYY = new SimpleDateFormat("MM/dd/yyyy");


        StringBuilder dateString = new StringBuilder( dateformatMMDDYYYY.format( dateObj ) );

    holder.setTime(dateString.toString());
        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
             if(!queryDocumentSnapshots.isEmpty()){
                 int count=queryDocumentSnapshots.size();
                 holder.updateLikesCount(count);
             }
             else{
holder.updateLikesCount(0);
             }
            }
        });
        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if(documentSnapshot.exists()){
                    holder.bloglikebtn.setImageDrawable(context.getDrawable(R.drawable.favoritenext));
                }
                else{
                    holder.bloglikebtn.setImageDrawable(context.getDrawable(R.drawable.favorite));
                }

            }
        });


    holder.bloglikebtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(!task.getResult().exists()){
                        Map<String,Object> likesMap=new HashMap<>();
                        likesMap.put("timestamp", FieldValue.serverTimestamp());
                        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).set(likesMap);
                    }else{
                        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).delete();
                    }
                }
            });

        }
    });
    }

    @Override
    public int getItemCount() {
        return bloglist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView descView;
private ImageView blogImageView;
private TextView blogdate;
private TextView blogusername;
private CircleImageView bloguserimage;
private ImageView bloglikebtn;
private TextView bloglikecount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
         mView=itemView;
         bloglikebtn=mView.findViewById(R.id.bloglikebtn);
        }
        public void setDescText(String text)
        {
            descView=mView.findViewById(R.id.blogdesc);
            descView.setText(text);
        }
        public void setBlogImage(String downloadUri){
blogImageView=mView.findViewById(R.id.blogimage);
Glide.with(context).load(downloadUri).into(blogImageView);
        }

        public void setTime(String date){
            blogdate=mView.findViewById(R.id.blogdate);
            blogdate.setText(date);
        }
        public void setUserData(String name,String img)
        {
            bloguserimage=mView.findViewById(R.id.bloguserimage);
            blogusername=mView.findViewById(R.id.blogusername);
            blogusername.setText(name);
            RequestOptions placeholderOption=new RequestOptions();
            placeholderOption.placeholder(R.drawable.ellipse);
            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(img).into(bloguserimage);
        }
        public void updateLikesCount(int count){
            bloglikecount=mView.findViewById(R.id.bloglikecount);
            bloglikecount.setText(count+" Likes");
        }
    }
}
