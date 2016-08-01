package com.rubykus.realmvtwo.ui.adapters;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rubykus.realmvtwo.R;
import com.rubykus.realmvtwo.model.Good;
import com.rubykus.realmvtwo.ui.activities.GoodsActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Rubykus on 29.07.2016.
 */
public class GoodsRecyclerViewAdapter extends RealmRecyclerViewAdapter<Good, GoodsRecyclerViewAdapter.MyHolder> {

    private final GoodsActivity activity;

    public GoodsRecyclerViewAdapter(GoodsActivity activity, OrderedRealmCollection<Good> data) {
        super(activity, data, true);
        this.activity = activity;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_good, parent, false);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        Good obj = getData().get(position);
        holder.data = obj;
        holder.name.setText(obj.getName());
        holder.quantity.setText(String.valueOf(obj.getQuantity()));
        holder.price.setText(String.valueOf(obj.getPrice())+" UAH");
        Uri path = Uri.parse(Environment.getExternalStorageDirectory().toString()+"/"+obj.getImg());
        File file = new File(String.valueOf(path));
        if (file.exists()){
            holder.img.setImageURI(path);
        }else{
            holder.img.setImageResource(R.drawable.not_img);
        }
    }


    class MyHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener{

        @BindView(R.id.tvNameGood) TextView name;
        @BindView(R.id.tvQuantityGood) TextView quantity;
        @BindView(R.id.tvPriceGood) TextView price;
        @BindView(R.id.ivImageGood) ImageView img;
        public Good data;

        public MyHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            view.setOnCreateContextMenuListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem Edit = contextMenu.add(Menu.NONE, 1, 1, "Edit");
            MenuItem Delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1:
                        activity.editGood(data);
                        break;

                    case 2:
                        activity.deleteGood(data);
                        break;
                }
                return true;
            }
        };


        @Override
        public void onClick(View view) {
            activity.openSingleGood(data);
        }
    }

}
