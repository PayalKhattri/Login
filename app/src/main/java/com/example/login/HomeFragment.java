package com.example.login;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
private RecyclerView bloglistview;
private List<blogpost> bloglist;
private FirebaseFirestore firebaseFirestore;
private BlogRecyclerAdapter blogRecyclerAdapter;
private DocumentSnapshot lastVisible;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        bloglist=new ArrayList<>();
        bloglistview=view.findViewById(R.id.bloglistview);
blogRecyclerAdapter=new BlogRecyclerAdapter(bloglist);
bloglistview.setLayoutManager(new LinearLayoutManager(getActivity()));
bloglistview.setAdapter(blogRecyclerAdapter);
firebaseFirestore=FirebaseFirestore.getInstance();
bloglistview.addOnScrollListener(new RecyclerView.OnScrollListener() {
    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        Boolean reachedBottom=!recyclerView.canScrollVertically(1);
    if(reachedBottom){
        String desc=lastVisible.getString("desc");
        Toast.makeText(container.getContext(),"Reached "+desc,Toast.LENGTH_SHORT).show();
    loadMorePost();
    }
    }
});
        Query firstQuery=firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING).limit(3);
firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        lastVisible=queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-1);
for(DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
    if(doc.getType()==DocumentChange.Type.ADDED){
        String blogPostId=doc.getDocument().getId();
        blogpost bp=doc.getDocument().toObject(blogpost.class).withId(blogPostId);
        bloglist.add(bp);
blogRecyclerAdapter.notifyDataSetChanged();
    }
}
    }
});
        // Inflate the layout for this fragment

    return view;
    }
public void loadMorePost()
{
    Query nextQuery=firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING).startAfter(lastVisible).limit(3);
    nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
            if (!queryDocumentSnapshots.isEmpty()){
                lastVisible = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
            for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                if (doc.getType() == DocumentChange.Type.ADDED) {
                    String blogPostId=doc.getDocument().getId();
                    blogpost bp = doc.getDocument().toObject(blogpost.class).withId(blogPostId);
                    bloglist.add(bp);
                    blogRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }
        }
    });
}
}
