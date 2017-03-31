package kuldeep.mourya.com.smartcollege.FragmentProfessor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import kuldeep.mourya.com.smartcollege.Activity.NavigationDrawerForStudent;
import kuldeep.mourya.com.smartcollege.Adapter.CollegeNewsRecyclerAdapter;
import kuldeep.mourya.com.smartcollege.Models.Topics;
import kuldeep.mourya.com.smartcollege.R;

/**
 * Created by kulde on 10/1/2016.
 */
public class CollegeNewsFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    CollegeNewsRecyclerAdapter collegeNewsRecyclerAdapter;
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;
    boolean refreshing = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_college_news, container, false);

        context = getActivity();

        

        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swiperefresh_for_college_news);

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView_college_news);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                CollegeNewsRecyclerAdapter.topicslist.clear();
                new JsonTask().execute();
                swipeRefreshLayout.setRefreshing(true);
                refreshing = true;
                swipeRefreshLayout.requestDisallowInterceptTouchEvent(true);

            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (refreshing)
                    return true;
                else {
                    return false;
                }


            }
        });

        new JsonTask().execute();


    }

    class JsonTask extends AsyncTask<Void, Void, String> {
        HttpURLConnection connection = null;
        boolean flag = true;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();/*
            LectureTakenByProfessorInClassesFragment.pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            LectureTakenByProfessorInClassesFragment.pDialog.getProgressHelper().setBarColor(Color.parseColor("#00BCD4"));
            LectureTakenByProfessorInClassesFragment.pDialog.setTitleText("Fetching Data....");
            LectureTakenByProfessorInClassesFragment.pDialog.setCancelable(false);
            LectureTakenByProfessorInClassesFragment.pDialog.show();
            */
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.d("MainActivity", "doInBackground");
            String str = "";
            String temp = "";


            
            URL url = null;// this api link
            try {
                url = new URL("put your url here");
                Log.d("cccc", "url" + url.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                connection = (HttpURLConnection) url.openConnection();
                Log.d("cccc", "connection" + connection.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                connection.setRequestMethod("POST");
                Log.d("cccc", "connection set request" + connection.toString());
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            try {
                connection.connect();
                Log.d("cccc", "connect" + connection.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("cccc", "connect exception" + connection.toString());
                flag = false;
            }


            try {
                if (connection.getResponseCode() == 200) {
                    //Toast.makeText(getBaseContext(),"Everything is right",Toast.LENGTH_SHORT).show();
                    InputStream stream = connection.getInputStream(); //here getting response
                    BufferedReader br = new BufferedReader(new InputStreamReader(stream));
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        // buffer.append(line);
                        str = str + line;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return str;
        }

        @Override
        protected void onPostExecute(String result) {

            if (!flag) {
                Toast.makeText(getContext(), "Check Your Internet Connection",
                        Toast.LENGTH_SHORT).show();
            }

            String json_string = result;
            Log.d("ssss_news", json_string);
            jsonMethod(json_string);
            refreshing = false;
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    public void jsonMethod(String json) {
        try {

            Topics topics;
            ArrayList<Topics> topicsArrayList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("LOGIN");
            JSONObject jsonObject1;
            String id = "", article = "", content = "", givenby = "";

            for (int i = 0; i < jsonArray.length(); i++) {
                topics = new Topics();
                jsonObject1 = jsonArray.getJSONObject(i);
                id = jsonObject1.getString("timestamp");
                topics.setId(id);
                Log.d("ssss_news", "" + id);
                article = jsonObject1.getString("article");
                topics.setArticle(article);
                Log.d("ssss_aricle", "" + article);
                content = jsonObject1.getString("content");
                topics.setContent(content);
                givenby = jsonObject1.getString("givenby");
                topics.setGivenby(givenby);
                topicsArrayList.add(topics);
            }
            collegeNewsRecyclerAdapter = new CollegeNewsRecyclerAdapter(getActivity(), topicsArrayList);
            recyclerView.setAdapter(collegeNewsRecyclerAdapter);
            // LectureTakenByProfessorInClassesFragment.pDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    
}


