package com.rubykus.realmvtwo.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rubykus.realmvtwo.model.Category;
import com.rubykus.realmvtwo.ui.activities.CategoriesActivity;
import com.rubykus.realmvtwo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Rubykus on 29.07.2016.
 */
public class CategoriesRecyclerViewAdapter extends RealmRecyclerViewAdapter<Category, CategoriesRecyclerViewAdapter.MyViewHolder> {

    private final CategoriesActivity activity;

    public CategoriesRecyclerViewAdapter(CategoriesActivity activity, OrderedRealmCollection<Category> data) {
        super(activity ,data, true);
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_category, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Category obj = getData().get(position);
        holder.data = obj;
        holder.name.setText(obj.getName());
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        @BindView(R.id.tvNameCat) TextView name;
        public Category data;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnCreateContextMenuListener(this);
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
                        activity.editCategory(data);
                        break;

                    case 2:
                        activity.deleteCategory(data);
                        break;
                }
                return true;
            }
        };
    }
}