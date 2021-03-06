package com.testapp.lostfound;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
/**
 * A simple {@link Fragment} subclass.
 */
public class FoundFragment extends Fragment {

    ArrayList<PostObject> mAllPostsList = new ArrayList<>();

    ArrayList<ProfileObject> mAllProfileList = new ArrayList<>();

    CollectionReference mAllPostsRef;

    CollectionReference mAllProfileRef;

    RecyclerViewAdapter mRecyclerViewAdapter;

    View mView;

    public FoundFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_found, container, false);

        initRecyclerView();

        initVariables(mView);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mAllProfileRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null)  {
                    return;
                }
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    ProfileObject temp = documentSnapshot.toObject(ProfileObject.class);
                    temp.setuID(documentSnapshot.getId());
                    mAllProfileList.add(temp);
                }
                notifyDataSetChanged();
            }
        });


        mAllPostsRef.whereEqualTo("mLostOrFound","Found").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null)  {
                    return;
                }
                for(DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType())
                    {
                        case ADDED:
                            mAllPostsList.add(dc.getDocument().toObject(PostObject.class));
                            notifyDataSetChanged();
                            break;
                        case MODIFIED:
                            int oldIndex = dc.getOldIndex();
                            int newIndex = dc.getNewIndex();
                            mAllPostsList.remove(oldIndex);
                            mAllPostsList.set(newIndex,dc.getDocument().toObject(PostObject.class));
                            notifyDataSetChanged();
                            break;
                        case REMOVED:
                            mAllPostsList.remove(dc.getOldIndex());
                            notifyDataSetChanged();
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        mAllPostsList.clear();
    }

    private void initRecyclerView()    {
        mRecyclerViewAdapter = new RecyclerViewAdapter(getContext(),mAllPostsList,mAllProfileList);
        RecyclerView recyclerView = mView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mRecyclerViewAdapter);
    }

    private void notifyDataSetChanged() {
        try {
            mRecyclerViewAdapter.notifyDataSetChanged();
        }
        catch (NullPointerException e)  {
            e.printStackTrace();
        }
    }

    private void initVariables(View view) {
        mAllPostsRef = FirebaseFirestore.getInstance().collection("all_posts");
        mAllProfileRef = FirebaseFirestore.getInstance().collection("profile_details");
    }

    /*private void loadPostsAdnProfiles()    {
     *//*mAllPostsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)    {
                    mAllPostsList.add(documentSnapshot.toObject(PostObject.class));
                }
            }
        });*//*
        mAllProfileRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)    {
                    ProfileObject temp = documentSnapshot.toObject(ProfileObject.class);
                    temp.setuID(documentSnapshot.getId());
                    mAllProfileList.add(temp);
                    Log.e("Document Id " , temp.getuID());
                }
            }
        });
    }*/

}
