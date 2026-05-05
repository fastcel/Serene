package com.example.serene;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardFragment extends Fragment {
    private RecyclerView recyclerUsers;
    private Button btnRefresh, btnAddUser, btnLogout;
    private DatabaseReference usersRef;
    private FirebaseAuth auth;
    private UserAdapter adapter;
    private List<User> userList;
    private List<String> userIds;
    private TextView tvLoading, tvEmpty;

    public AdminDashboardFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        tvLoading = view.findViewById(R.id.tvLoading);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        recyclerUsers = view.findViewById(R.id.recyclerUsers);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        btnAddUser = view.findViewById(R.id.btnAddUser);
        btnLogout = view.findViewById(R.id.btnLogout);
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        userList = new ArrayList<>();
        userIds = new ArrayList<>();
        adapter = new UserAdapter(userList, userIds, uid -> {
            UserDetailsFragment fragment = UserDetailsFragment.newInstance(uid);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.admin_fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerUsers.setAdapter(adapter);
        loadUsers();
        btnRefresh.setOnClickListener(v -> loadUsers());
        btnAddUser.setOnClickListener(v -> showAddUserDialog());
        btnLogout.setOnClickListener(v->logout());
        return view;
    }
    private void loadUsers() {
        tvLoading.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        recyclerUsers.setVisibility(View.GONE);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();
                userIds.clear();

                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    tvLoading.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    recyclerUsers.setVisibility(View.GONE);

                    return;
                }

                for (DataSnapshot userSnap : snapshot.getChildren()) {

                    String uid = userSnap.getKey();
                    String username = userSnap.child("username").getValue(String.class);
                    String email = userSnap.child("email").getValue(String.class);

                    if (email == null) continue;

                    userList.add(new User(username, email));
                    userIds.add(uid);
                }

                adapter.notifyDataSetChanged();

                tvLoading.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.GONE);
                recyclerUsers.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                tvLoading.setVisibility(View.GONE);
                tvEmpty.setText("Error loading users");
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Create User");
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_add_user, null);
        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etEmail = view.findViewById(R.id.etEmail);
        EditText etPassword = view.findViewById(R.id.etPassword);
        builder.setView(view);
        builder.setPositiveButton("Create", (dialog, which) -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) return;
            createUser(username, email, password);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void createUser(String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) return;
                    String uid = firebaseUser.getUid();
                    User user = new User(username, email);
                    usersRef.child(uid).setValue(user);
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}