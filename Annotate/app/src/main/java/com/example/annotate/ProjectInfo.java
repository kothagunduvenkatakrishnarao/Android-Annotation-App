package com.example.annotate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class ProjectInfo extends AppCompatActivity {
    TextView tvtittle,tvdescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_info);
        tvtittle = findViewById(R.id.tvtittle);
        tvdescription = findViewById(R.id.tvdescription);
        final int index=getIntent().getIntExtra("position",0);
        tvtittle.setText(ApplicationClass.adminpprojects.get(index).getProjectName());
        tvdescription.setText(ApplicationClass.adminpprojects.get(index).getDescription());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        menu.removeItem(R.id.showallprojects);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logout:
                Backendless.UserService.logout(new AsyncCallback<Void>()
                {
                    public void handleResponse( Void response )
                    {
                        Toast.makeText(ProjectInfo.this,"logout successfull",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProjectInfo.this,Login.class));
                        ProjectInfo.this.finish();
                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        Toast.makeText(ProjectInfo.this,"Error "+fault.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.showallprojects:
                startActivity(new Intent(ProjectInfo.this,DisplayAllProjects.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
