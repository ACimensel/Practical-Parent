package ca.cmpt276.flame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.cmpt276.flame.model.Child;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Child> childrenQueue;

    public RecyclerViewAdapter(Context context, List<Child> childList) {
        this.context = context;
        this.childrenQueue = childList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_child, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vHolder, int position) {
        //vHolder.childImage.setImageResource(childrenQueue.get(position).getImage());
        vHolder.childName.setText(childrenQueue.get(position).getName());
        vHolder.childOrderInQ.setText(context.getString(R.string.child_pos_in_queue, position + 1));
    }

    @Override
    public int getItemCount() {
        return childrenQueue.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView childImage;
        private TextView childName;
        private TextView childOrderInQ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            childImage = itemView.findViewById(R.id.item_child_image);
            childName = itemView.findViewById(R.id.item_child_name);
            childOrderInQ = itemView.findViewById(R.id.item_child_order);
        }
    }
}
