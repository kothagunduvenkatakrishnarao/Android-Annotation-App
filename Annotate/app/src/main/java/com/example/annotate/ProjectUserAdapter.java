package com.example.annotate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ProjectUserAdapter extends ArrayAdapter<Projects> {
    private Context context;
    private List<Projects> projects;
    public ProjectUserAdapter(Context context,List<Projects> list)
    {
        super(context,R.layout.row_admin,list);
        this.context=context;
        this.projects=list;
    }
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        convertView = inflater.inflate(R.layout.row_admin,parent,false);
        TextView tittle=convertView.findViewById(R.id.tittle);
        TextView description = convertView.findViewById(R.id.description);
        TextView noOfAnnotations = convertView.findViewById(R.id.noOfAnnotations);
        noOfAnnotations.setVisibility(View.GONE);
        tittle.setText(projects.get(position).getProjectName());
        description.setText(projects.get(position).getDescription());
        return convertView;
    }
}
