package com.h5200006_burak_oztas_yonlendirilmiscalisma.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.h5200006_burak_oztas_yonlendirilmiscalisma.R;
import com.h5200006_burak_oztas_yonlendirilmiscalisma.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsFragment extends Fragment {

    private View contactsView;
    private RecyclerView contactsrycview;

    private DatabaseReference contactsRef, usersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactsView = inflater.inflate(R.layout.fragment_friends, container, false);

        contactsrycview = contactsView.findViewById(R.id.contacts_list);
        contactsrycview.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return contactsView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(contactsRef, User.class)
                        .build();

        final FirebaseRecyclerAdapter<User, contactsViewHolder> adapter =
                new FirebaseRecyclerAdapter<User, contactsViewHolder>(options) {


                    @Override
                    protected void onBindViewHolder(@NonNull final contactsViewHolder holder, int position, @NonNull User model) {

                        String userIDs = getRef(position).getKey();

                        usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {

                                    if (snapshot.child("userState").hasChild("state")) {
                                        String state = snapshot.child("userState").child("state").getValue().toString();
                                        String date = snapshot.child("userState").child("date").getValue().toString();
                                        String time = snapshot.child("userState").child("time").getValue().toString();

                                        if (state.equals("Online")) {
                                            holder.onlineIcon.setVisibility(View.VISIBLE);
                                        } else if (state.equals("Offline")) {
                                            holder.onlineIcon.setVisibility(View.INVISIBLE);
                                        }
                                    } else {
                                        holder.onlineIcon.setVisibility(View.INVISIBLE);
                                    }

                                    if (snapshot.hasChild("userImage")) {
                                        String userProfileImage = snapshot.child("userImage").getValue().toString();
                                        String userProfileName = snapshot.child("username").getValue().toString();
                                        String userProfileStatus = snapshot.child("status").getValue().toString();

                                        holder.userName.setText(userProfileName);
                                        holder.userStatus.setText(userProfileStatus);
                                        Picasso.get().load(userProfileImage).placeholder(R.drawable.user_proile_icon).into(holder.profileImage);
                                    } else {
                                        String userProfileName = snapshot.child("username").getValue().toString();
                                        String userProfileStatus = snapshot.child("status").getValue().toString();

                                        holder.userName.setText(userProfileName);
                                        holder.userStatus.setText(userProfileStatus);

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public contactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        contactsViewHolder viewHolder = new contactsViewHolder(view);
                        return viewHolder;
                    }
                };

        contactsrycview.setAdapter(adapter);
        adapter.startListening();
    }


    public static class contactsViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;

        public contactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
            onlineIcon = itemView.findViewById(R.id.user_online_status);
        }
    }
}