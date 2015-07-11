package bkmi.de.hftl_app.help;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import bkmi.de.hftl_app.Fragmente.NewsFragment;
import bkmi.de.hftl_app.NewsActivity;
import bkmi.de.hftl_app.R;

public class CustomAdapterNews extends BaseAdapter{
    String [] date;
    String [] headline;
    String [] content;
    Context context;
    private static LayoutInflater inflater=null;
    public CustomAdapterNews(Activity mainActivity, String[] dateList, String[] headlineList, String[] contentList) {
        // TODO Auto-generated constructor stub
        date=dateList;
        content=contentList;
        headline=headlineList;
        context=mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return date.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv_date;
        TextView tv_headline;
        TextView tv_content;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.news_list, null);
        holder.tv_date=(TextView) rowView.findViewById(R.id.newslist_date);
        holder.tv_date.setText(date[position]);
        holder.tv_headline=(TextView) rowView.findViewById(R.id.newslist_headline);
        holder.tv_headline.setText(headline[position]);
        holder.tv_content=(TextView) rowView.findViewById(R.id.newslist_content);
        holder.tv_content.setText(content[position]);


        return rowView;
    }

}