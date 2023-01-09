package com.example.mydesign;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HolderDesign extends RecyclerView.Adapter<HolderDesign.ViewHolder> {
    private ArrayList<String> imageList;
    private FirebaseFirestore store;
    private ArrayList<String[]> info;
    public Context context;

    public HolderDesign(ArrayList<String> imageList, ArrayList<String[]> info, Context context) {
        this.imageList = imageList;
        this.info = info;
        this.context = context;
        this.store = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public HolderDesign.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_show_design,parent,false);
        return new HolderDesign.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderDesign.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.user_name.setText(info.get(position)[0]);
        holder.email.setText(info.get(position)[1]);
        holder.bid.setText(info.get(position)[2]);
        holder.file_name.setText(info.get(position)[3]);
        holder.description.setText(info.get(position)[4]);
        holder.checkBox.setChecked(info.get(position)[5].equals("true"));
        if (info.get(position)[5].equals("true")){
            holder.checkBox.setTextColor(Color.GREEN);
        }
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked()){
                    holder.checkBox.setTextColor(Color.GREEN);
                    store.collection("User Design").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (QueryDocumentSnapshot document : task.getResult()){
                                            if (document.get("Image URL").toString().equals(imageList.get(position))){
                                                store.collection("User Design").
                                                        document(document.getId()).update("Order State","true");
                                                CollectionReference new_order = store.collection("Admins").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .collection("Order");
                                                Map<String, Object> user_info = new HashMap<>();
                                                user_info.put("User Name",info.get(position)[0]);
                                                user_info.put("Email",info.get(position)[1]);
                                                user_info.put("URL",imageList.get(position));
                                                user_info.put("File Name",info.get(position)[3]);
                                                user_info.put("Description",info.get(position)[4]);
                                                user_info.put("Bid",info.get(position)[2]);
                                                user_info.put("Order State","true");
                                                new_order.add(user_info).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(context, "User special design is now in your order list", Toast.LENGTH_SHORT).show();
                                                        }else {
                                                            Toast.makeText(context, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            });
                }else {
                    store.collection("User Design").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if (document.get("Image URL").toString().equals(imageList.get(position))) {
                                                store.collection("User Design").document(document.getId()).update("Order State", "false");
                                            }
                                        }
                                    }
                                }
                            });
                }
            }
        });
        Glide.with(holder.itemView.getContext()).load(imageList.get(position)).into(holder.imageView);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//              //try commit
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView email;
        TextView description;
        TextView file_name;
        TextView user_name;
        TextView bid;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.file_name);
            imageView=itemView.findViewById(R.id.image);
            email=itemView.findViewById(R.id.email);
            description =itemView.findViewById(R.id.product_description);
            user_name=itemView.findViewById(R.id.username);
            bid =itemView.findViewById(R.id.bid);
            checkBox=itemView.findViewById(R.id.checkbox);
        }
    }
}
