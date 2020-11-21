package ca.cmpt276.flame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ca.cmpt276.flame.model.Child;
import ca.cmpt276.flame.model.FlipManager;

/**
 *  A recycler view adapter class responsible for making a view for each item in the data set
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private final FlipManager flipManager = FlipManager.getInstance();
    private final Context context;
    private final List<Child> childList;

    public RecyclerViewAdapter(Context context, List<Child> childList) {
        this.context = context;
        this.childList = childList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_child, parent, false);
        ViewHolder vHolder = new ViewHolder(v);

        vHolder.childItem.setOnClickListener(v1 -> {
            flipManager.overrideTurnChild(vHolder.childObj);
            ((FlipCoinActivity) context).updateUI();
            ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();
        });
        
        return vHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vHolder, int position) {
        vHolder.childObj = childList.get(position);
        vHolder.childImage.setImageBitmap(vHolder.childObj.getImageBitmap(context));
        vHolder.childName.setText(vHolder.childObj.getName());
        vHolder.childOrderInQ.setText(context.getString(R.string.child_pos_in_queue, position + 1));
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    /**
     *  A view holder class responsible for filling the view of each item in the data set
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout childItem;
        private final ImageView childImage;
        private final TextView childName;
        private final TextView childOrderInQ;
        private Child childObj;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            childItem = itemView.findViewById(R.id.item_child);
            childImage = itemView.findViewById(R.id.item_child_image);
            childName = itemView.findViewById(R.id.item_child_name);
            childOrderInQ = itemView.findViewById(R.id.item_child_order);
        }
    }
}
