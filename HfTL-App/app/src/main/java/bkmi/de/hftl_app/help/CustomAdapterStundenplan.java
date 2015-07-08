package bkmi.de.hftl_app.help;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import bkmi.de.hftl_app.Fragmente.StundenplanFragment;
import bkmi.de.hftl_app.R;

public class CustomAdapterStundenplan extends BaseAdapter{
    String [] date;
    String [] subject;
    String [] category;
    String [] room;
    String [] time;
    Context context;
    private static LayoutInflater inflater=null;
    public CustomAdapterStundenplan(Activity StundenplanFragment, String[] dateList, String[] subjectList,String[] timeList, String[] roomList, String[] categoryList) {
        // TODO Auto-generated constructor stub
        date = dateList;
        subject = subjectList;
        category = categoryList;
        time = timeList;
        room = roomList;
        context = StundenplanFragment;
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

    public class NotenHolder
    {
        TextView tv_date;
        TextView tv_subject;
        TextView tv_time;
        TextView tv_room;
        TextView tv_category;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        NotenHolder holder=new NotenHolder();
        View rowView;
        rowView = inflater.inflate(R.layout.stundenplan_list, null);
        holder.tv_date=(TextView) rowView.findViewById(R.id.stundenplan_tag);
       /* if(semester[position]==null){
            holder.tv_semester.setVisibility(TextView.GONE);
        }
        else
            holder.tv_semester.setText(semester[position]);*/
        holder.tv_subject=(TextView) rowView.findViewById(R.id.stundenplan_fach);
        holder.tv_subject.setText(subject[position]);
        holder.tv_time=(TextView) rowView.findViewById(R.id.stundenplan_zeit);
        holder.tv_time.setText(time[position]);
        holder.tv_room=(TextView) rowView.findViewById(R.id.stundenplan_raum);
        holder.tv_room.setText(room[position]);
        holder.tv_category=(TextView) rowView.findViewById(R.id.notenlist_versuch);
        if (category[position].equals("Prüfung"))
            holder.tv_category.setBackgroundColor(context.getResources().getColor(R.color.magenta));
        else if (category[position].equals("Praktikum"))
            holder.tv_category.setBackgroundColor(context.getResources().getColor(R.color.dunkelblau));
        else
            holder.tv_category.setBackgroundColor(context.getResources().getColor(R.color.grau01));

        return rowView;
    }

}