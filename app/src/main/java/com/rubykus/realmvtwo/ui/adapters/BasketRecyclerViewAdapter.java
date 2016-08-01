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
import com.rubykus.realmvtwo.model.Basket;
import com.rubykus.realmvtwo.ui.RoundedImageView;
import com.rubykus.realmvtwo.ui.activities.BasketActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Rubykus on 31.07.2016.
 */
public class BasketRecyclerViewAdapter extends RealmRecyclerViewAdapter<Basket, BasketRecyclerViewAdapter.MyViewHolder> {

    private final BasketActivity activity;

    public BasketRecyclerViewAdapter(BasketActivity activity, OrderedRealmCollection<Basket> data) {
        super(activity ,data, true);
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_basket, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Basket obj = getData().get(position);
        holder.data = obj;
        holder.info.setText(obj.getName()+" "+obj.getCount()+"X"+obj.getPrice());
        holder.cost.setText(String.valueOf(obj.getSum()));
        Uri path = Uri.parse(Environment.getExternalStorageDirectory().toString()+"/"+obj.getImg());
        File file = new File(String.valueOf(path));
        if (file.exists()){
            holder.img.setImageURI(path);
        }else{
            holder.img.setImageResource(R.drawable.not_img);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        @BindView(R.id.basketInfo) TextView info;
        @BindView(R.id.cost) TextView cost;
        @BindView(R.id.imageGood) RoundedImageView img;
        public Basket data;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem Delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1:

                        break;

                    case 2:
                        activity.deleteBasketItem(data);
                        break;
                }
                return true;
            }
        };
    }
}