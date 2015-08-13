package bkmi.de.hftl_app.help;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bkmi.de.hftl_app.Fragmente.NotenFragment;
import bkmi.de.hftl_app.R;

public class CustomAdapterNoten extends BaseAdapter{
    String [] subject;
    String [] trys;
    String [] mark;
    String [] semester;
    Context context;
    private static LayoutInflater inflater=null;
    public CustomAdapterNoten(Activity NotenFragment, String[] semesterList ,String[] subjectList, String[] trysList, String[] markList) {
        // TODO Auto-generated constructor stub
        subject=subjectList;
        trys=trysList;
        mark=markList;
        semester=semesterList;
        context= NotenFragment;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return subject.length;
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


    public class NotenHolder
    {
        TextView tv_subject;
        TextView tv_trys;
        TextView tv_mark;
        TextView tv_semester;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        NotenHolder holder=new NotenHolder();
        View rowView;
        rowView = inflater.inflate(R.layout.noten_list, null);

        holder.tv_semester=(TextView) rowView.findViewById(R.id.notenlist_semester);
        if(semester[position]==null){
            holder.tv_semester.setVisibility(TextView.GONE);
        }
        else {
            holder.tv_semester.setText(semester[position]);
        }

        holder.tv_subject=(TextView) rowView.findViewById(R.id.notenlist_fach);
        holder.tv_subject.setText(subject[position]);

        holder.tv_trys=(TextView) rowView.findViewById(R.id.notenlist_versuch);
        holder.tv_trys.setText("Versuch: " + trys[position]);

        holder.tv_mark=(TextView) rowView.findViewById(R.id.notenlist_note);

        if (mark[position].equals("5,0" ))
            holder.tv_mark.setTextColor(context.getResources().getColor(R.color.magenta));
        else if (mark[position].equals("4,0" ) |
            mark[position].equals("3,9" ) |
            mark[position].equals("3,8" ) |
            mark[position].equals("3,7" ) |
            mark[position].equals("3,6" ) |
            mark[position].equals("3,5" ) )
                 holder.tv_mark.setTextColor(context.getResources().getColor(R.color.gelb));
            else
                holder.tv_mark.setTextColor(context.getResources().getColor(R.color.grün));
        holder.tv_mark.setText(mark[position]);

        return rowView;
    }

}