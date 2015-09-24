package me.nootify.users;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.nootify.users.Data.User;

/**
 * Created by davide on 24/09/15.
 */
public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    /**
     * A callback interface for the ListFragment. This mechanism allows
     * the fragment to be notified of item selections.
     */
    public interface ActionCallback {
        void onPostExecute(int index);
    }

    private Context context;
    private int layout;
    private List<User> items;
    private ActionCallback actionButtonCallback;

    public UserRecyclerViewAdapter(Context context, int layout, ActionCallback actionButtonCallback) {
        this.context = context;
        this.layout = layout;
        this.actionButtonCallback = actionButtonCallback;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final User user = items.get(position);

        Glide.with(holder.avatar.getContext())
                .load("http://lorempixel.com/512/512/people/" + Integer.valueOf((position % 10) + 1))
                .fitCenter()
                .crossFade()
                .into(holder.avatar);

        holder.name.setText(user.getName());
        holder.email.setText(user.getEmail());
        holder.infos.setText(user.getInfos());
        holder.id = user.getId();

        holder.action.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                actionButtonCallback.onPostExecute(holder.id);
            }
        });

        if (items.get(position).isSelected()) {

            holder.name.setTextColor(context.getResources().getColor(R.color.customPrimaryDarkColor));
            holder.name.setTypeface(null, Typeface.BOLD);
            holder.infos.setVisibility(View.VISIBLE);

        } else {

            holder.name.setTextColor(Color.BLACK);
            holder.name.setTypeface(null, Typeface.NORMAL);
            holder.infos.setVisibility(View.GONE);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void setItems(List<User> users) {
        this.items = users;
    }

    @Override
    public int getItemCount() {
        int l = 0;

        if (items != null) {
            l = items.size();
        }

        return l;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private int id;
        private final CircleImageView avatar;
        private final TextView name;
        private final TextView email;
        private final TextView infos;
        private final FloatingActionButton action;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (CircleImageView) itemView.findViewById(R.id.avatar);
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);
            infos = (TextView) itemView.findViewById(R.id.infos);
            action = (FloatingActionButton) itemView.findViewById(R.id.action_button);

            itemView.setTag(itemView);
        }

    }
}
