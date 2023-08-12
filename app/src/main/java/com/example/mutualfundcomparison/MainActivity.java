package com.example.mutualfundcomparison;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String cat;
    private String date1;
    private String date2;
    private int sYearnum;
    private int eYearnum;

    AutoCompleteTextView categoryName;
    ArrayList<String> categoryTypes;
    ArrayAdapter<String> arrayAdapter;

    AutoCompleteTextView startYear;
    ArrayList<String> yearList;
    ArrayAdapter<String> startYearAdapter;

    AutoCompleteTextView endYear;
    ArrayAdapter<String> endYearAdapter;

    private ProgressBar progressBar;
    //private static String file_url = "https://ce0c-122-200-18-91.in.ngrok.io/exportCSV/Large Cap/01-01-2016/30-12-2022";
    private static String file_url = "https://4517-122-200-18-91.ngrok-free.app/exportCSV/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progress_bar);

        HashMap<String, ArrayList<String>> dates = new HashMap<>();
        dates.put("2014", new ArrayList<>(Arrays.asList("01-01-2014", "31-12-2014")));
        dates.put("2015", new ArrayList<>(Arrays.asList("01-01-2015", "31-12-2015")));
        dates.put("2016", new ArrayList<>(Arrays.asList("01-01-2016", "30-12-2016")));
        dates.put("2017", new ArrayList<>(Arrays.asList("02-01-2017", "29-12-2017")));
        dates.put("2018", new ArrayList<>(Arrays.asList("01-01-2018", "31-12-2018")));
        dates.put("2019", new ArrayList<>(Arrays.asList("01-01-2019", "31-12-2019")));
        dates.put("2020", new ArrayList<>(Arrays.asList("01-01-2020", "31-12-2020")));
        dates.put("2021", new ArrayList<>(Arrays.asList("01-01-2021", "31-12-2021")));
        dates.put("2022", new ArrayList<>(Arrays.asList("03-01-2022", "30-12-2022")));

        categoryName = findViewById(R.id.autoCompleteTextView);
        categoryTypes = new ArrayList<>(Arrays.asList("Large Cap","Mid Cap","Small Cap","ELSS"));
        arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, categoryTypes);
        categoryName.setAdapter(arrayAdapter);
        categoryName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cat = parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, "Item: " + cat, Toast.LENGTH_SHORT).show();
            }
        });
        startYear = findViewById(R.id.startYear);
        yearList = new ArrayList<>(Arrays.asList("2014","2015","2016","2017","2018","2019","2020","2021","2022"));
        startYearAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, yearList);
        startYear.setAdapter(startYearAdapter);
        startYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sYear = parent.getItemAtPosition(position).toString();
                sYearnum = Integer.parseInt(sYear);
                date1 = dates.get(sYear).get(0).toString();
                Toast.makeText(MainActivity.this, "Item: " + date1, Toast.LENGTH_SHORT).show();
            }
        });

        endYear = findViewById(R.id.endYear);
        endYearAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item, yearList);
        endYear.setAdapter(endYearAdapter);
        endYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String eYear = parent.getItemAtPosition(position).toString();
                eYearnum = Integer.parseInt(eYear);
                if(sYearnum > eYearnum)
                {
                    Toast.makeText(MainActivity.this, "Start Year cannot be greater than End Year", Toast.LENGTH_SHORT).show();
                }
                date2 = dates.get(eYear).get(1).toString();
                Toast.makeText(MainActivity.this, "Item: " + date2, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void startAsyncTask(View v) {

        ExampleAsyncTask task = new ExampleAsyncTask(this);
        task.execute(file_url + cat + "/" + date1 + "/" + date2);
    }

    private class ExampleAsyncTask extends AsyncTask<String, Integer, String> {

        private WeakReference<MainActivity> activityWeakReference;

        ExampleAsyncTask(MainActivity activity) {
            activityWeakReference = new WeakReference<MainActivity>(activity);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity activity = activityWeakReference.get();
            if(activity == null || activity.isFinishing())
            {
                return;
            }
            activity.progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(activity, "Downloading ...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try  {
                //Your code goes here
                StringBuilder data = new StringBuilder();
                Log.d("debug","Reached here 1");
                try{
                    URL url = new URL(file_url+ cat + "/" + date1 + "/" + date2);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();

                    URLConnection urlConnection = url.openConnection();
                    urlConnection.connect();
                    int file_size = urlConnection.getContentLength();
                    Log.d("sasa", "file_size = " + file_size);
                    //Long lengthOfFile = httpURLConnection.getContentLengthLong();
                    //Toast.makeText(MainActivity.this, String.valueOf(lengthOfFile), Toast.LENGTH_SHORT).show();
                    //Log.d("debug","total size of file is " + String.valueOf(lengthOfFile));
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    Log.d("debug","Reached here 2");
                    while((line = bufferedReader.readLine()) != null)
                    {
                        data.append(line + "\n");
                        int downloadedSize = data.length();
                        Log.d("debug","File Size is "+ file_size);
                        Log.d("debug","Downloaded Size is " + downloadedSize);
                        publishProgress((downloadedSize * 100)/file_size);
//                            Log.d("debug",data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("debug","Reached here");
                try {
                    FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
//            out.write((sub.toString()).getBytes());
                    out.write((data.toString()).getBytes());
                    out.close();
                    Log.d("debug","Completed part 1");
                    Context context = getApplicationContext();
                    File filelocation = new File(getFilesDir(),"data.csv");
                    Uri path = FileProvider.getUriForFile(context,"com.example.mutualfundcomparison.fileprovider",filelocation);
                    Intent fileIntent = new Intent(Intent.ACTION_SEND);
                    Log.d("debug","Completed part 2");
                    fileIntent.setType("text/csv");
                    fileIntent.putExtra(Intent.EXTRA_SUBJECT,"Data");
                    fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    fileIntent.putExtra(Intent.EXTRA_STREAM,path);
                    startActivity(Intent.createChooser((fileIntent),"Send mail"));
                    Log.d("debug","Completed part 3");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Downloaded!";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            MainActivity activity = activityWeakReference.get();
            if(activity == null || activity.isFinishing())
            {
                return;
            }
            activity.progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            MainActivity activity = activityWeakReference.get();
            if(activity == null || activity.isFinishing())
            {
                return;
            }
            Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
            activity.progressBar.setProgress(0);
            activity.progressBar.setVisibility(View.GONE);
        }
    }
}