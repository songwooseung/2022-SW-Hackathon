package com.example.hackerthon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class informationAdapter extends BaseAdapter
{
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<information> sample;

    /*public informationAdapter(Context context, ArrayList<information> data)
    {
        mContext = context;
        sample = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }*/

    @Override
    public int getCount()
    {
        return sample.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public information getItem(int position)
    {
        return sample.get(position);
    }

    public void addItem(information item)
    {
        sample.add(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final Context context = parent.getContext();
        final information i = sample.get(position);
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.right_listview, parent, false);

        } else {
            View view = new View(context);
            view = (View) convertView;
        }

        TextView address = (TextView) convertView.findViewById(R.id.address);
        TextView type = (TextView) convertView.findViewById(R.id.type);
        TextView name = (TextView) convertView.findViewById(R.id.name);

        address.setText(sample.get(position).address);
        type.setText(sample.get(position).subject);
        name.setText(sample.get(position).name);

        return convertView;
    }
}
