package com.rubykus.realmvtwo.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rubykus.realmvtwo.R;
import com.rubykus.realmvtwo.model.Sale;
import com.rubykus.realmvtwo.ui.activities.SaleActivity;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Rubykus on 31.07.2016.
 */
public class SaleRecyclerViewAdapter extends RealmRecyclerViewAdapter<Sale, SaleRecyclerViewAdapter.MyViewHolder> {

    private final SaleActivity activity;

    public SaleRecyclerViewAdapter(SaleActivity activity, OrderedRealmCollection<Sale> data) {
        super(activity, data, true);
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_sale, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Sale obj = getData().get(position);
        holder.data = obj;
        holder.id.setText(String.valueOf(obj.getId()));
        holder.date.setText(obj.getDate());
        holder.info.setText(obj.getInfo());
        holder.sum.setText(String.valueOf(obj.getSum()));

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        @BindView(R.id.textIDSale) TextView id;
        @BindView(R.id.textIDGood) TextView date;
        @BindView(R.id.textDate) TextView info;
        @BindView(R.id.textIDCheck) TextView sum;
        public Sale data;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem CreateCheck = contextMenu.add(Menu.NONE, 1, 1, "Create check");
            MenuItem Delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");
            CreateCheck.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1:
                        activity.createCheck(data);
                        break;

                    case 2:
                        activity.deleteSale(data);
                        break;
                }
                return true;
            }
        };
    }
}