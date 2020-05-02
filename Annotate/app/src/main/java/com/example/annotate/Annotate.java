package com.example.annotate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class Annotate extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private View mProgressView;
    private View mLoginFormView;
    private TextView tvLoad;

    ArrayList<ArrayList<Float>> result;
    Button button,btnsubmit,uploadimage,annotate;
    ImageView imview;
    EditText getnumber;
    String pathToFile=null;
    int numberOfAnnotations=0;
    final int RESULT_LOAD_IMAGE=2;
    float downx = 0,downy = 0,upx = 0,upy = 0;
    Canvas canvas;
    Paint paint;
    float projectedX,projectedY;
    float width,height;
    Spinner spinner;
    List<String> Items=new ArrayList<>();
    List<String> itemsSelected=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotate);
        if(Build.VERSION.SDK_INT>=23)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
        button = findViewById(R.id.button);
        imview =findViewById(R.id.imview);
        btnsubmit = findViewById(R.id.btnsubmit);
        getnumber = findViewById(R.id.getnumber);
        uploadimage = findViewById(R.id.uploadimage);
        annotate = findViewById(R.id.annotate);
        mLoginFormView= findViewById(R.id.login_form);
        mProgressView= findViewById(R.id.login_progress);
        tvLoad= findViewById(R.id.tvLoad);
        annotate.setVisibility(View.GONE);
        spinner =findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        final int index = getIntent().getIntExtra("position",0);
        String itemstoshow=ApplicationClass.projects.get(index).getDataToShow();
        String[] itemstodropdown = itemstoshow.split(",");
        List<String> Items= Arrays.asList(itemstodropdown);
//        Items.add(0,"----");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Items);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        annotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Annotate.this,"Please wait for a while to upload!",Toast.LENGTH_LONG).show();
                button.setVisibility(View.VISIBLE);
                uploadimage.setVisibility(View.VISIBLE);
                annotate.setVisibility(View.GONE);
                showProgress(true);
                if(itemsSelected.size()!=0 ) {
                    JSONObject jsonObject = new JSONObject();
                    String file = filename();
                    File myDir = new File(Environment.getExternalStorageDirectory() + "/Download/" + file);
                    myDir.mkdir();
                    try {
                        jsonObject.put("name", "" + file);
                        JSONObject wid_hei = new JSONObject();
                        wid_hei.put("width", width);
                        wid_hei.put("height", height);
                        jsonObject.put("size", wid_hei);
                        ArrayList<JSONObject> obj = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            JSONObject temp = new JSONObject();
                            temp.put("bitmap", null);
                            temp.put("classTitle", itemsSelected.get(i));
                            JSONObject points = new JSONObject();
                            ArrayList<ArrayList<Float>> left_top_right_bottom = new ArrayList<>();
                            for (int j = 0; j < 4; j += 2) {
                                ArrayList<Float> t = new ArrayList<>();
                                t.add(result.get(i).get(j));
                                t.add(result.get(i).get(j + 1));
                                left_top_right_bottom.add(t);
                            }
                            points.put("exterior", left_top_right_bottom);
                            points.put("interior", new ArrayList<>());
                            temp.put("points", points);
                            obj.add(temp);
                        }
                        jsonObject.put("objects", obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    File f = new File(myDir + "/" + file + ".json");
                    Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                    File f2 = new File(myDir + "/" + file + ".png");
                    Writer output = null;
                    try {
                        f.createNewFile();
                        f2.createNewFile();
                        output = new BufferedWriter(new FileWriter(f));
                        output.write(jsonObject.toString());
                        output.close();
                        OutputStream stream = new FileOutputStream(f2);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        stream.flush();
                        stream.close();
                        Toast.makeText(Annotate.this, "json file created", Toast.LENGTH_SHORT).show();
                        ApplicationClass.userprojects.get(index).setRemainingCount(ApplicationClass.userprojects.get(index).getRemainingCount()-1);
                        Backendless.Persistence.of(Projects.class).save(ApplicationClass.userprojects.get(index), new AsyncCallback<Projects>() {
                            @Override
                            public void handleResponse(Projects response) {
                                Toast.makeText(Annotate.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(Annotate.this, "Error " + fault.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        ApplicationClass.userData.get(0).setScore(ApplicationClass.userData.get(0).getScore()+0.2);
                        Backendless.Persistence.of(UserScore.class).save(ApplicationClass.userData.get(0), new AsyncCallback<UserScore>() {
                            @Override
                            public void handleResponse(UserScore response) {
                                Toast.makeText(Annotate.this,"updated Succesfully!",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void handleFault(BackendlessFault fault) {
                                Toast.makeText(Annotate.this,"Error "+fault.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                        fetchFullData();
                        showProgress(false);
                    } catch (IOException e) {
                        Log.i("file creation",e.toString());
                        Toast.makeText(Annotate.this, "unable to create a json file", Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }
                    imview.setImageBitmap(null);
                    itemsSelected=new ArrayList<>();
                }
                else
                {
                    Toast.makeText(Annotate.this,"select atleast a single annotaion or pick or capture annother image",Toast.LENGTH_LONG).show();
                    showProgress(false);
                }
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(getnumber.getText().toString().trim());
                if(value<=0 || value> numberOfAnnotations)
                {
                    Toast.makeText(Annotate.this,"Enter a Valid Annotation number!",Toast.LENGTH_LONG).show();
                }
                else
                {
                    numberOfAnnotations--;
                    Toast.makeText(Annotate.this,"Annotation deleted!",Toast.LENGTH_SHORT).show();
                    result.remove(value-1);
                    itemsSelected.remove(value-1);
                    Bitmap bitmap= BitmapFactory.decodeFile(pathToFile);
                    Bitmap alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                    canvas = new Canvas(alteredBitmap);
                    Matrix matrix = new Matrix();
                    canvas.drawBitmap(bitmap, matrix, paint);
                    for(int i=0;i<numberOfAnnotations;i++)
                    {
                        redraw(i);
                        canvas.drawText(""+(i+1),(result.get(i).get(0)+result.get(i).get(2))/2,result.get(i).get(1),paint);
                        canvas.drawText(itemsSelected.get(i),(result.get(i).get(0)+result.get(i).get(2))/2,result.get(i).get(1)+100,paint);
                        imview.invalidate();
                    }
                    imview.setImageBitmap(alteredBitmap);
                }

            }
        });
        uploadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(View.GONE);
                numberOfAnnotations=0;
                result=new ArrayList<>();
                itemsSelected =new ArrayList<>();
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadimage.setVisibility(View.GONE);
                result=new ArrayList<>();
                numberOfAnnotations=0;
                itemsSelected =new ArrayList<>();
                try {
                    dispatchPictureTakerAction();
                } catch (IOException e) {
                    Log.d("error","err"+e.toString());
                }
            }
        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent,View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if(!item.equals("-----") && itemsSelected.size() < numberOfAnnotations) {
            itemsSelected.add(item);
            canvas.drawText(item,(result.get(numberOfAnnotations-1).get(0)+result.get(numberOfAnnotations-1).get(2))/2,result.get(numberOfAnnotations-1).get(1)+100,paint);
            imview.invalidate();
        }
        else
        {
            Toast.makeText(Annotate.this, "Please Annotate and then select !", Toast.LENGTH_LONG).show();
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==1 && numberOfAnnotations==itemsSelected.size())
            {
                final Bitmap bitmap= BitmapFactory.decodeFile(pathToFile);
                width=bitmap.getWidth();
                height=bitmap.getHeight();
                Bitmap alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                canvas = new Canvas(alteredBitmap);
                paint = new Paint();
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.GREEN);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10);
                paint.setTextSize(100f);
                Matrix matrix = new Matrix();
                canvas.drawBitmap(bitmap, matrix, paint);
                imview.setImageBitmap(alteredBitmap);

                imview.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = event.getAction();
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                annotate.setVisibility(View.VISIBLE);
                                numberOfAnnotations++;
                                downx = event.getX();
                                downy = event.getY();
                                downx = (float) ((double)downx * ((double)bitmap.getWidth()/(double)imview.getWidth()));
                                downy = (float) ((double)downy * ((double)bitmap.getHeight()/(double)imview.getHeight()));
                                break;
                            case MotionEvent.ACTION_UP:
                                upx = event.getX();
                                upy = event.getY();
                                if(upx<0) upx=0;
                                if(upx>imview.getWidth()) upx = imview.getWidth();
                                if(upy < 0) upy =0;
                                if(upy >imview.getHeight() ) upy=imview.getHeight();
                                projectedX = (float)((double)upx * ((double)bitmap.getWidth()/(double)imview.getWidth()));
                                projectedY = (float)((double)upy * ((double)bitmap.getHeight()/(double)imview.getHeight()));if(numberOfAnnotations-itemsSelected.size() == 1) onDrawRect(downx,downy,projectedX,projectedY,paint);
                            else numberOfAnnotations --;
                                imview.invalidate();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });

            }
            if (requestCode == RESULT_LOAD_IMAGE && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                pathToFile = cursor.getString(columnIndex);
                cursor.close();
                final Bitmap bitmap= BitmapFactory.decodeFile(pathToFile);
                width=bitmap.getWidth();
                height=bitmap.getHeight();
                Bitmap alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                canvas = new Canvas(alteredBitmap);
                paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setTextSize(100f);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10);
                Matrix matrix = new Matrix();
                canvas.drawBitmap(bitmap, matrix, paint);
                imview.setImageBitmap(alteredBitmap);
                imview.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = event.getAction();
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                annotate.setVisibility(View.VISIBLE);
                                numberOfAnnotations++;
                                downx = event.getX();
                                downy = event.getY();
                                downx = (float) ((double) downx * ((double) bitmap.getWidth() / (double) imview.getWidth()));
                                downy = (float) ((double) downy * ((double) bitmap.getHeight() / (double) imview.getHeight()));
                                break;
                            case MotionEvent.ACTION_UP:
                                upx = event.getX();
                                upy = event.getY();
                                if (upx < 0) upx = 0;
                                if (upx > imview.getWidth()) upx = imview.getWidth();
                                if (upy < 0) upy = 0;
                                if (upy > imview.getHeight()) upy = imview.getHeight();
                                projectedX = (float) ((double) upx * ((double) bitmap.getWidth() / (double) imview.getWidth()));
                                projectedY = (float) ((double) upy * ((double) bitmap.getHeight() / (double) imview.getHeight()));
                                if(numberOfAnnotations-itemsSelected.size() == 1) onDrawRect(downx, downy, projectedX, projectedY, paint);
                                else numberOfAnnotations--;
                                imview.invalidate();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
            }
        }
    }

    public void redraw(int i)
    {
        canvas.drawRect(result.get(i).get(0), result.get(i).get(1), result.get(i).get(2), result.get(i).get(3),paint);
    }
    public String filename()
    {
        String answer="";
        for(int i=pathToFile.length()-1;i>=0;i--)
        {
            if(pathToFile.charAt(i)=='/') {
                answer = pathToFile.substring(i+1,pathToFile.length()-4);
                break;
            }
        }
        return answer;
    }
    public void onDrawRect(float x,float y, float x1,float y1 ,Paint paint)
    {
        ArrayList<Float> ans=new ArrayList<>();
        ans.add(x);
        ans.add(y);
        ans.add(x1);
        ans.add(y1);
        canvas.drawRect(x, y, x1, y1,paint);
        canvas.drawText(""+numberOfAnnotations,(x+x1)/2,y,paint);
        imview.invalidate();
        result.add(ans);
    }
    private void dispatchPictureTakerAction() throws IOException {
        Intent takepic= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takepic.resolveActivity(getPackageManager())!=null)
        {
            File photofile=null;
            photofile=createPhotoFile();
            if(photofile!=null)
            {
                pathToFile = photofile.getAbsolutePath();
                Uri photoURI= FileProvider.getUriForFile(Annotate.this,"com.example.annotate.fileprovider",photofile);
                takepic.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takepic, 1);
            }
        }
    }

    private File createPhotoFile() throws IOException {
        String name=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File storageDir= getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image=null;
        try {
            image= File.createTempFile(name,".jpg",storageDir);
        }
        catch (IOException e)
        {
            Log.d("mylog","Exep"+e.toString());
        }
        return image;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    public void fetchFullData( )
    {
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setPageSize(100);
        queryBuilder.addAllProperties();
        queryBuilder.setSortBy("created DESC");
        Backendless.Persistence.of(Projects.class).find(queryBuilder, new AsyncCallback<List<Projects>>() {
            @Override
            public void handleResponse(List<Projects> response) {
                ApplicationClass.projects=response;
            }
            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(Annotate.this, "Error "+fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        queryBuilder = DataQueryBuilder.create();
        String whereClause =  "userEmail = '"+ ApplicationClass.user.getEmail() +"'";
        queryBuilder.setWhereClause(whereClause);
        Backendless.Persistence.of(UserScore.class).find(queryBuilder, new AsyncCallback<List<UserScore>>() {
            @Override
            public void handleResponse(List<UserScore> response) {
                ApplicationClass.userData=response;
                if(response.size()!=0){
                    Log.i("User Data ",""+response.get(0).getEnrolledProjects());
                    HashSet<Long> set=new HashSet<>();
                    String[] ids=ApplicationClass.userData.get(0).getEnrolledProjects().split(",");
                    for(int i=0;i<ids.length;i++)
                    {
                        set.add(Long.parseLong(ids[i]));
                    }
                    ApplicationClass.userenrolledprojectids=set;
                    Log.i("set",""+set);
                }
                else Log.i("User Data ","unable to retrive");
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Toast.makeText(Annotate.this,"Unable to fetch Data",Toast.LENGTH_LONG).show();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE: View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE: View.GONE);
                }
            });

            tvLoad.setVisibility(show ? View.VISIBLE: View.GONE);
            tvLoad.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tvLoad.setVisibility(show ? View.VISIBLE: View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE: View.GONE);
            tvLoad.setVisibility(show ? View.VISIBLE: View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE: View.VISIBLE);
        }
    }
}
