package ca.cmpt276.flame;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.cmpt276.flame.model.Child;

/**
 *  A recycler view adapter class responsible for making a view for each item in the data set
 */
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
        ViewHolder vHolder = new ViewHolder(v);



        vHolder.childItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "" + vHolder.childObj.getName(), Toast.LENGTH_SHORT).show();
                FlipCoinActivity.customChild = vHolder.childObj;

                // TODO Close fragment from here
            }
        });






        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vHolder, int position) {
        vHolder.childObj = childrenQueue.get(position);
        vHolder.childImage.setImageBitmap(vHolder.childObj.getImageBitmap(context));
        vHolder.childName.setText(vHolder.childObj.getName());
        vHolder.childOrderInQ.setText(context.getString(R.string.child_pos_in_queue, position + 1));
    }

    @Override
    public int getItemCount() {
        return childrenQueue.size();
    }

    /**
     *  A view holder class responsible for filling the view of each item in the data set
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private Child childObj;
        private LinearLayout childItem;
        private ImageView childImage;
        private TextView childName;
        private TextView childOrderInQ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            childItem = itemView.findViewById(R.id.item_child);
            childImage = itemView.findViewById(R.id.item_child_image);
            childName = itemView.findViewById(R.id.item_child_name);
            childOrderInQ = itemView.findViewById(R.id.item_child_order);
        }
    }
}
