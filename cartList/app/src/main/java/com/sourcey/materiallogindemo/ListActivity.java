package com.sourcey.materiallogindemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;


import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;




public class ListActivity extends Activity{

    String myJSON;
    private static final String IP = "http://envchaesampledevelopment-env.iwquxapdmw.ap-northeast-2.elasticbeanstalk.com/";
    private static final String TAG_RESULTS = "result";

    private static final String TAG_EMAIL= "user_email";
    private static final String TAG_NUMBER= "bc_number";
    private static final String TAG_NAME= "bc_name";
    private static final String TAG_PRICE= "bc_price";
    private static final String TAG_CONTENT= "bc_content";
    private static final String TAG_TIME = "reg_time";
    private static final String TAG_COUNT= "count";

    static String number;
    static String name;
    static String price;
    static String content;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Button btn_back;
    private Button btn_buy;
    private TextView textTotal;
    private int totalCost = 0;
    JSONArray JSData = null;

    ArrayList<HashMap<String, String>> JSDatas;

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        list = (ListView)findViewById(R.id.listViews);
        Thread th = new Thread(new Runnable() {
            @Override
            public synchronized void run() {
                while (true) {
                    try {
                        JSDatas = new ArrayList<HashMap<String, String>>();
                        getData(IP + "controller/listView.php");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        th.start();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        textTotal = (TextView)findViewById(R.id.totalCost);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public synchronized boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                SendPost post = new SendPost(JSDatas.get(i).get(TAG_NUMBER).toString(),IP +"controller/listRemove.php");
                post.execute();
                Toast.makeText(getApplicationContext(), JSDatas.get(i).get(TAG_NUMBER).toString() + " Delete success", Toast.LENGTH_LONG).show();
                JSDatas = new ArrayList<HashMap<String, String>>();
                getData(IP +"controller/listView.php");
                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public synchronized void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                number = JSDatas.get(i).get(TAG_NUMBER).toString();
                name = JSDatas.get(i).get(TAG_NAME).toString();
                price = JSDatas.get(i).get(TAG_PRICE).toString();
                content = JSDatas.get(i).get(TAG_CONTENT).toString();

                Intent intent = new Intent(getApplicationContext(), ContentActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_buy = (Button)findViewById(R.id.btn_buy);
        btn_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public void onBackPressed() {

        // Alert을 이용해 종료시키기
        AlertDialog.Builder dialog = new AlertDialog.Builder(ListActivity.this);
        dialog  .setTitle("구매 알림")
                .setMessage("구매하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SendPost post = new SendPost("null",IP +"controller/listBuy.php");
                        Toast.makeText(getApplicationContext(), "구매 완료", Toast.LENGTH_LONG).show();
                        post.execute();
                        JSDatas = new ArrayList<HashMap<String, String>>();
                        getData(IP +"controller/listView.php");
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ListActivity.this, "종료하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
    }

    protected void showList() {
        try {
            totalCost = 0;
            JSONObject jsonObj = new JSONObject(myJSON);
            JSData = jsonObj.getJSONArray(TAG_RESULTS);
            if (JSData != null){
                for (int i = 0; i < JSData.length(); i++) {
                    JSONObject c = JSData.getJSONObject(i);
                    String email = c.getString(TAG_EMAIL);
                    String number = c.getString(TAG_NUMBER);
                    String name = c.getString(TAG_NAME);
                    String price = c.getString(TAG_PRICE);
                    String content = c.getString(TAG_CONTENT);
                    String time = c.getString(TAG_TIME);
                    String count = c.getString(TAG_COUNT);

                    HashMap<String, String> datas = new HashMap<String, String>();

                    datas.put(TAG_EMAIL, email);
                    datas.put(TAG_NUMBER, number);
                    datas.put(TAG_NAME, name);
                    datas.put(TAG_PRICE, price);
                    datas.put(TAG_CONTENT, content);
                    datas.put(TAG_TIME, time);
                    datas.put(TAG_COUNT, count);
                    totalCost += Integer.parseInt(price) * Integer.parseInt(count);
                    JSDatas.add(datas);
                }
            }

            ListAdapter adapter = new SimpleAdapter(
                    ListActivity.this, JSDatas, R.layout.activity_list_item,
                    new String[]{TAG_NAME, TAG_PRICE, TAG_COUNT},
                    new int[]{R.id.num, R.id.title, R.id.content}
            );
            list.setAdapter(adapter);
            textTotal.setText("Total : " + totalCost + "WON");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public synchronized void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }

                    return sb.toString().trim();

                } catch (Exception e) {
                    return null;
                }


            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

}