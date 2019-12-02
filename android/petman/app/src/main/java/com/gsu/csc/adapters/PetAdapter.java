package com.gsu.csc.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gsu.csc.models.PetModel;
import com.gsu.csc.petman.PetFragment;
import com.gsu.csc.petman.PetFragmentDetails;
import com.gsu.csc.petman.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder>{

    private Context context;
    private List<PetModel> petModel;

    public PetAdapter(Context context, List<PetModel> petModel) {
        this.context = context;
        this.petModel = petModel;
    }

    @NonNull
    @Override
    public PetAdapter.PetViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pet_recycleview_row, viewGroup, false);

        return new PetAdapter.PetViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PetAdapter.PetViewHolder petViewHolder, int i) {
        PetModel item = petModel.get(i);

        petViewHolder.txtName.setText(item.getName());
        petViewHolder.txtBreed.setText(item.getBreed());

        switch (item.getType().toLowerCase()) {
            case "dog":
                petViewHolder.imgPet.setImageResource(R.drawable.ic_dog);
                break;

            case "cat":
                petViewHolder.imgPet.setImageResource(R.drawable.ic_cat);
                break;

            case "fish":
                petViewHolder.imgPet.setImageResource(R.drawable.ic_fish);
                break;

            case "bird":
                petViewHolder.imgPet.setImageResource(R.drawable.ic_bird);
                break;

            case "turtle":
                petViewHolder.imgPet.setImageResource(R.drawable.ic_turtle);
                break;

        }

    }

    @Override
    public int getItemCount() {
        return petModel.size();
    }

    public class PetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtName;
        private TextView txtBreed;
        private ImageView imgPet;


        public PetViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            txtName = itemView.findViewById(R.id.txtName);
            txtBreed = itemView.findViewById(R.id.breed);
            imgPet = itemView.findViewById(R.id.img_pet);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            PetModel currentItem = petModel.get(position);

            Bundle bundle = new Bundle();

            bundle.putInt("id", currentItem.getId());
            bundle.putString("name", currentItem.getName());
            bundle.putString("sex", currentItem.getSex());
            bundle.putString("dob", new SimpleDateFormat("MMM dd, yyyy").format(currentItem.getDob()));
            bundle.putString("breed", currentItem.getBreed());
            bundle.putInt("owner_id", currentItem.getOwner_id());
            bundle.putString("type", currentItem.getType());
            bundle.putString("action", "change");
            bundle.putInt("position", position);

            PetFragmentDetails petFragmentDetails = new PetFragmentDetails();
            petFragmentDetails.setArguments(bundle);

            (((AppCompatActivity) context)).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, petFragmentDetails).commit();

        }

    }



}
