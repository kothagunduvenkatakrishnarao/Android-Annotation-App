package com.example.annotate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class MainActivity extends AppCompatActivity {

    Button btnadmin,btnuser,btncreate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnadmin=findViewById(R.id.btnadmin);
        btnuser=findViewById(R.id.btnuser);
        btncreate=findViewById(R.id.btncreate);
        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CreateProject.class));

            }
        });
        btnadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,DisplayAdminProjects.class));
            }
        });
        btnuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,DisplayUserProjects.class));
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
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
                        Toast.makeText(MainActivity.this,"logout successfull",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,Login.class));
                        MainActivity.this.finish();
                    }

                    public void handleFault( BackendlessFault fault )
                    {
                        Toast.makeText(MainActivity.this,"Error "+fault.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.showallprojects:
                startActivity(new Intent(MainActivity.this,DisplayAllProjects.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
