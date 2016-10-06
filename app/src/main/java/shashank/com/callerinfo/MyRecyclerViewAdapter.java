package shashank.com.callerinfo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private List<CallerIdentity> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title ;
        public CircleImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.item_name);
            imageView = (CircleImageView) view.findViewById(R.id.item_image);

        }
    }


    public MyRecyclerViewAdapter(List<CallerIdentity> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_recycler, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        CallerIdentity callerIdentity = list.get(position);
        holder.title.setText(callerIdentity.getName());
        holder.imageView.setImageResource(R.drawable.dot);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}