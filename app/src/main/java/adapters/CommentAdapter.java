package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mobilancer.com.ulutekyemek.R;
import pojo.Comment;

/**
 * Created by ersinyildiz on 17/11/16.
 */

public class CommentAdapter extends BaseAdapter {

    List<Comment> data;
    Context context;


    public CommentAdapter(Context context, List<Comment> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Comment getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = layoutInflater.inflate(R.layout.single_comment,null);
        }

        TextView commentText = (TextView) vi.findViewById(R.id.single_comment_text);
        TextView commentNick = (TextView) vi.findViewById(R.id.single_comment_nick);

        commentText.setText(getItem(position).getCommentText());
        commentNick.setText(getItem(position).getNickname());

        return vi;
    }
}
