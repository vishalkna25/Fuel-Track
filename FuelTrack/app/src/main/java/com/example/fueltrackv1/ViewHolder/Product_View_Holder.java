package com.example.fueltrackv1.ViewHolder;

import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fueltrackv1.R;


public class Product_View_Holder extends RecyclerView.ViewHolder
{
    public TextView pfName, plName, pNumber;
    public ImageView profile;

    public Product_View_Holder(@NonNull View itemView) {
        super(itemView);
        pfName = (TextView) itemView.findViewById(R.id.fname);
        plName = (TextView) itemView.findViewById(R.id.lname);
        pNumber = (TextView) itemView.findViewById(R.id.Number);
        profile = (ImageView) itemView.findViewById(R.id.prf);
    }

}
