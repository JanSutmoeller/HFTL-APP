package bkmi.de.hftl_app.help;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import bkmi.de.hftl_app.R;

import static android.R.layout.simple_list_item_1;

public class CustomAdapterStundenplan extends BaseAdapter{
    String[] datum;
    String[] fach;
    String[] zeit;
    String[] raum;
    String[] kategorie;

    Context context;
    private static LayoutInflater inflater=null;
    public CustomAdapterStundenplan(Activity StundenplanFragment, String [] dateList, String [] subjectList, String [] timeList, String [] roomList, String [] categorieList) {
        // TODO Auto-generated constructor stub
        datum = dateList;
        fach = subjectList;
        zeit = timeList;
        raum = roomList;
        kategorie = categorieList;

        context = StundenplanFragment;
        inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return datum.length;
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

    public class StundenplanHolder
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
        StundenplanHolder holder=new StundenplanHolder();
        View rowView;
        rowView = inflater.inflate(R.layout.stundenplan_list, null);


      /* if (event[0].isKeineDaten()){
            holder.tv_subject=(TextView) rowView.findViewById(R.id.stundenplan_fach);
            holder.tv_subject.setText("keine Daten");
            return rowView;
        }*/


        holder.tv_date=(TextView) rowView.findViewById(R.id.stundenplan_tag);

         if (datum[position]!=null)
             holder.tv_date.setText(datum[position]);
         else
             holder.tv_date.setVisibility(TextView.GONE);

        holder.tv_subject=(TextView) rowView.findViewById(R.id.stundenplan_fach);
      /*  if (fach[position].length()>36)
            holder.tv_subject.setLines(3);
        else
            holder.tv_subject.setLines(2);*/
        holder.tv_subject.setText(kategorie[position]+ "\n" + fach[position]);


        holder.tv_time=(TextView) rowView.findViewById(R.id.stundenplan_zeit);
        holder.tv_time.setText(zeit[position]);


        holder.tv_room=(TextView) rowView.findViewById(R.id.stundenplan_raum);
        if (raum[position].equals(""))
            holder.tv_room.setText("Raumplanung offen");
        else
            holder.tv_room.setText(raum[position]);


        holder.tv_category=(TextView) rowView.findViewById(R.id.stundenplan_kategorie);
        if (kategorie[position].equals("Prüfung"))
            holder.tv_category.setBackgroundColor(context.getResources().getColor(R.color.magenta));
        else if (kategorie[position].equals("Praktikum"))
            holder.tv_category.setBackgroundColor(context.getResources().getColor(R.color.dunkelblau));
        else
            holder.tv_category.setBackgroundColor(context.getResources().getColor(R.color.grau01));

        return rowView;
    }

}