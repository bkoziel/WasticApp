package com.example.wastic.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.wastic.CommentAdapter;
import com.example.wastic.R;
import com.example.wastic.Requesthandler;
import com.example.wastic.SharedPrefManager;
import com.example.wastic.URLs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductActivity extends AppCompatActivity {
    private RecyclerView mList;

    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Comments> commentsList;
    private RecyclerView.Adapter adapter;
    private RatingBar ratingBar, averageRatingBar;
   private TextView barCodeTextView, rateCount;
    private TextView nameTextView,userTextView,userRating,loginForMore,commentTextView,addCommentButton;
   private ImageView photoImageView;
    private Button addProductButton;
    private String commentExist="false";
    LinearLayout LL;
    String code;
    String productName,barcode,photoURL,addedByUser,ratingValue;
    static int productID;
   private String addOpinionURL = "https://wasticelo.000webhostapp.com/addOpinion.php";
    private String addCommentReportURL = "https://wasticelo.000webhostapp.com/addCommentReport.php";
    private String addProductReportURL = "https://wasticelo.000webhostapp.com/addProductReport.php";

    private RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        barCodeTextView = findViewById(R.id.textViewCode);
        nameTextView = findViewById(R.id.textViewName);
        photoImageView = findViewById(R.id.imageViewPhoto);
        addProductButton = findViewById(R.id.buttonAddProduct);
        userTextView= findViewById(R.id.textViewUser);
        userRating= findViewById(R.id.textViewUserRate);
        averageRatingBar = findViewById(R.id.averageRating);
        loginForMore = findViewById(R.id.textViewPleaseLogIn);
       commentTextView = findViewById(R.id.editTextComment);
     addCommentButton = findViewById(R.id.buttonAddComment);
     ratingBar = findViewById(R.id.ratingBars);
       LL = findViewById(R.id.LL);
     rateCount = findViewById(R.id.ratingBarText);
        code = getIntent().getStringExtra("code");
        barCodeTextView.setText(code);
        requestQueue = Volley.newRequestQueue(this);
        mList = findViewById(R.id.RecycleViewComments);


        commentsList = new ArrayList<>();
        adapter = new CommentAdapter(getApplicationContext(), commentsList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(mList.getContext(), linearLayoutManager.getOrientation());

        mList.setHasFixedSize(false);
        mList.setLayoutManager(linearLayoutManager);
        mList.addItemDecoration(dividerItemDecoration);
        mList.setAdapter(adapter);
        jsonParse();
        checkCode();

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(getApplicationContext(), AddProductActivity.class);
                i.putExtra("code", code);
                startActivity(i);
            }
        });

        findViewById(R.id.buttonReport).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final String commentText = commentTextView.getText().toString().trim();
                //if(TextUtils.isEmpty(commentText)){

                 //   commentTextView.setError("Wprowadź komentarz");
                 //   commentTextView.requestFocus();
                 //   return;

                //}
                StringRequest request = new StringRequest(Request.Method.POST, addProductReportURL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),"Sent report",Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Error occured",Toast.LENGTH_LONG).show();
                    }
                }){

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();

                        //params.put("description", commentTextView.getText().toString());
                        params.put("product_id",Integer.toString(productID));
                        params.put("user_id", Integer.toString(SharedPrefManager.getInstance(ProductActivity.this).currentUser()));
                        //params.put("ratingValue",String.valueOf(ratingBar.getRating()));


                        return params;
                    }
                };
                requestQueue.add(request);
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        if (rating < 1.0f)
                        {       ratingBar.setRating(1.0f);
                                rating = 1.0f;
                        }
                        rateCount.setText("Your packaging rating: " + (int)rating + "/5");
                    }

                }
                );



        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String commentText = commentTextView.getText().toString().trim();
                if(TextUtils.isEmpty(commentText)){

                    commentTextView.setError("enter a comment");
                    commentTextView.requestFocus();
                    return;

                }
StringRequest request = new StringRequest(Request.Method.POST, addOpinionURL, new Response.Listener<String>() {
    @Override
    public void onResponse(String response) {
        Toast.makeText(getApplicationContext(),"Product rated",Toast.LENGTH_LONG).show();
       finish();
            Intent x = new Intent(getApplicationContext() , ProductActivity.class);
            x.putExtra("code",code);
            startActivity(x);

    }
}, new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(),"Error occured",Toast.LENGTH_LONG).show();
    }
}){

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();

        params.put("description", commentTextView.getText().toString());
        params.put("product_id",Integer.toString(productID));
        params.put("user_id", Integer.toString(SharedPrefManager.getInstance(ProductActivity.this).currentUser()));
        params.put("ratingValue",String.valueOf(ratingBar.getRating()));


        return params;
    }
};
requestQueue.add(request);
            }
        });

    }
    private void getRatings() {
        String prod_id=Integer.toString(productID);
        String url = "https://wasticelo.000webhostapp.com/averageRating.php?product_id="+prod_id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject product = response.getJSONObject("data");

                    ratingValue = product.getString("ratingValue");
                    userRating.setText("Packaging average rating: "+ ratingValue);
                    double d=0;
                    try
                    {
                        d = Double.parseDouble(ratingValue);

                    }
                    catch(NumberFormatException e)
                    {
                        d = 0;
                    }
                    averageRatingBar.setRating((float)d);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }



    private void jsonParse() {
        String url = "https://wasticelo.000webhostapp.com/testing.php?bar_code="+code;
        System.out.println(url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject product = response.getJSONObject("data");

                        productID = product.getInt("id");
                        productName = product.getString("name");
                        barcode = product.getString("bar_code");
                        photoURL = product.getString("photo");
                        addedByUser = product.getString("username");
                         getRatings();
                        nameTextView.setText(productName.toUpperCase());
                        barCodeTextView.setText(barcode);
                        userTextView.setText("Added by: " + addedByUser);
                        seeComments();
                        seeAlternativeProduct();
                        checkIfExsistComment();
                        Picasso.get().load("https://wasticelo.000webhostapp.com/"+ photoURL).into(photoImageView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    private void checkIfExsistComment() {
       String prod_id=Integer.toString(productID);
       String user_id=Integer.toString(SharedPrefManager.getInstance(ProductActivity.this).currentUser());
        System.out.println("user: "+user_id + " product: "+prod_id);
String url="https://wasticelo.000webhostapp.com/checkIfCommentExsist.php?user_id="+user_id+"&product_id="+prod_id;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONObject comment = response.getJSONObject("data");
                    commentExist=comment.getString("exist");

                    if(commentExist.equals("false")) {
                        addCommentButton.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(addCommentButton.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                        addCommentButton.setPadding(5,5,5,5);
                        addCommentButton.setLayoutParams(lp);

                        ratingBar.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ratingBar.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp2.gravity = 1;
                        ratingBar.setLayoutParams(lp2);

                        commentTextView.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(commentTextView.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                        commentTextView.setLayoutParams(lp3);

                        rateCount.setVisibility(View.VISIBLE);
                        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(rateCount.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp4.gravity = 1;
                        rateCount.setLayoutParams(lp4);

                    }else {
                        addCommentButton.setVisibility(View.INVISIBLE);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(addCommentButton.getLayoutParams().width, 0);
                        addCommentButton.setLayoutParams(lp);

                        ratingBar.setVisibility(View.INVISIBLE);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ratingBar.getLayoutParams().width, 0);
                        ratingBar.setLayoutParams(lp2);

                        commentTextView.setVisibility(View.INVISIBLE);
                        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(commentTextView.getLayoutParams().width, 0);
                        commentTextView.setLayoutParams(lp3);

                        rateCount.setVisibility(View.INVISIBLE);
                        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(rateCount.getLayoutParams().width, 0);
                        rateCount.setLayoutParams(lp4);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);

    }

    private void seeComments() {
        String url = "https://wasticelo.000webhostapp.com/addedComments.php?product_id="+productID;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray comments = response.getJSONArray("comments");

                    TextView tv = new TextView(findViewById(R.id.commentsLL).getContext());
                    tv.setText("Comments: ");
                    tv.setTextSize(30);

                    //findViewById(R.id.commentsLL).addView(tv);


                    int comment_id;
                    for(int i = 0; i < (comments).length(); i++) {
                        final JSONObject comment = comments.getJSONObject(i);

                        Comments comments1 = new Comments();
                        //comment_id = comment.getInt("comment_id");
                        comments1.setUserId(comment.getInt(("id")));
                        comments1.setDescription(comment.getString("description"));
                        comments1.setRating("Rating: " + comment.getString("ratingValue"));
                        comments1.setDate(comment.getString("comment_date"));
                        comments1.setImageUser("https://wasticelo.000webhostapp.com/"+ comment.getString("avatar"));
                        comments1.setUsername(comment.getString("username"));



                        commentsList.add(comments1);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    private void seeAlternativeProduct(){

        String url = "https://wasticelo.000webhostapp.com/alternative.php?name="+ productName + "&code=" + code;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray products = response.getJSONArray("products");
                    int size = (products).length();

                    final String[] s = new String[size];

                    if(size==0){
                        findViewById(R.id.AlternativeCV).setVisibility(View.INVISIBLE);
                    }else{
                        findViewById(R.id.AlternativeCV).setVisibility(View.VISIBLE);
                    }

                    for(int i = 0; i < size; i++) {
                        final JSONObject product = products.getJSONObject(i);


                        //nameTextView.append("\n" + product.getString("name"));
                        CardView cv = new CardView(LL.getContext());
                        cv.setCardElevation(10);
                        cv.setRadius(0.555f);
                        cv.setPadding(5,5,5,5);
                        cv.setBackgroundColor(Color.parseColor("#00ffffff"));
                        LinearLayout l = new LinearLayout(cv.getContext());
                        l.setOrientation(LinearLayout.VERTICAL);
                        l.setGravity(View.TEXT_ALIGNMENT_CENTER);
                        ImageView iv = new ImageView(l.getContext());
                        Picasso.get().load("https://wasticelo.000webhostapp.com/"+ product.getString("photo")).into(iv);

                        TextView tv = new TextView(l.getContext());
                        tv.setGravity(View.TEXT_ALIGNMENT_CENTER);

                        tv.setTextSize(10);
                        //tv.setTextColor(Color.parseColor("#ffffff"));
                        tv.setText(product.getString("name"));

                        TextView tv2 = new TextView(l.getContext());
                        tv2.setGravity(Gravity.CENTER);
                        tv2.setTextSize(10);
                        //tv2.setTextColor(Color.parseColor("#ffffff"));

                        View viewDivider = new View(LL.getContext());
                        LinearLayout.LayoutParams vdlp = new LinearLayout.LayoutParams (1, ViewGroup.LayoutParams.MATCH_PARENT);
                        vdlp.setMargins(0,15,0,15);
                        viewDivider.setLayoutParams(vdlp);
                        viewDivider.setBackgroundColor(Color.parseColor("#555555"));

                        String temp= product.getString("rating");
                        if(temp.equals("null")) {
                            tv2.setText("No rating");
                        }
                        else {
                            tv2.setText("Rating: " + product.getString("rating"));
                        }
                        LinearLayout.LayoutParams cvlp = new LinearLayout.LayoutParams(400, ViewGroup.LayoutParams.MATCH_PARENT);
                        LinearLayout.LayoutParams ivlp = new LinearLayout.LayoutParams(300,300 );
                        LinearLayout.LayoutParams tvlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 75);
                        LinearLayout.LayoutParams tv2lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 40);

                        tv2lp.gravity= Gravity.CENTER;
                        tvlp.gravity=Gravity.CENTER;


                        cvlp.setMargins(10,20,10,20);
                        ivlp.setMargins(50,10,50,10);
                        //tvlp.setMargins(5,5,5,5);
                        tv2lp.setMargins(5,0,5,5);


                      iv.setLayoutParams(ivlp);
                      cv.setLayoutParams(cvlp);
                      tv.setLayoutParams(tvlp);
                        tv2.setLayoutParams(tv2lp);

                        final int id=i;
                        s[i] = product.getString("bar_code");

                        cv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent x = new Intent(getApplicationContext() , ProductActivity.class);
                                x.putExtra("code",s[id]);
                                startActivity(x);
                            }

                        });
                         l.addView(tv);
                         l.addView(iv);
                         l.addView(tv2);
                        cv.addView(l);
                        LL.addView(cv);
                        LL.addView(viewDivider);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //nameTextView.setVisibility(View.INVISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                //nameTextView.setVisibility(View.INVISIBLE);
            }
        });
        requestQueue.add(request);
    }

    private void checkCode() {
        final String barcode = code;


        class Products extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                Requesthandler requestHandler = new Requesthandler();

                HashMap<String, String> params = new HashMap<>();
                params.put("bar_code", barcode);

                return requestHandler.sendPostRequest(URLs.URL_PRODUCT, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressBar = findViewById(R.id.progressBar2);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                progressBar.setVisibility(View.GONE);

                try {
                    JSONObject obj = new JSONObject(s);

                    if (!obj.getBoolean("error")) {

                        nameTextView.setText("Product doesn't exist in the database");

                        averageRatingBar.setVisibility(View.INVISIBLE);
                        addCommentButton.setVisibility(View.INVISIBLE);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(addCommentButton.getLayoutParams().width, 0);
                        lp.setMargins(10,10,10,10);
                        addCommentButton.setLayoutParams(lp);

                        ratingBar.setVisibility(View.INVISIBLE);
                        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ratingBar.getLayoutParams().width, 0);
                        lp2.setMargins(10,10,10,10);
                        ratingBar.setLayoutParams(lp2);

                        commentTextView.setVisibility(View.INVISIBLE);
                        LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(commentTextView.getLayoutParams().width, 0);
                        lp3.setMargins(10,10,10,10);
                        commentTextView.setLayoutParams(lp3);

                        rateCount.setVisibility(View.INVISIBLE);
                        LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(rateCount.getLayoutParams().width, 0);
                        lp4.setMargins(10,10,10,10);
                        rateCount.setLayoutParams(lp4);


                        if (!SharedPrefManager.getInstance(ProductActivity.this).isLoggedIn()) {
                            nameTextView.setText("Product doesn't exist in the database");
                            loginForMore.setVisibility(View.VISIBLE);
                            LinearLayout.LayoutParams lp5 = new LinearLayout.LayoutParams(loginForMore.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                            loginForMore.setGravity(Gravity.CENTER);
                            lp5.gravity = Gravity.CENTER;
                            loginForMore.setLayoutParams(lp5);
                            loginForMore.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {

                                    Intent x = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(x);
                                    return false;
                                }
                            });
                            addProductButton.setVisibility(View.INVISIBLE);
                            findViewById(R.id.CV).setVisibility(View.INVISIBLE);
                            LinearLayout.LayoutParams lp6 = new LinearLayout.LayoutParams(addProductButton.getLayoutParams().width, 0);
                            lp6.setMargins(20,10,20,20);
                            addProductButton.setLayoutParams(lp6);
                        } else {
                            averageRatingBar.setVisibility(View.INVISIBLE);
                            addProductButton.setVisibility(View.VISIBLE);
                            findViewById(R.id.CV).setVisibility(View.INVISIBLE);
                            LinearLayout.LayoutParams lp6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            lp6.setMargins(40,-70,40,50);
                            addProductButton.setLayoutParams(lp6);
                        }
                    }
                } catch (JSONException e) {
                    findViewById(R.id.CV).setVisibility(View.VISIBLE);
                    e.printStackTrace();
                    averageRatingBar.setVisibility(View.VISIBLE);
                    addProductButton.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams lp6 = new LinearLayout.LayoutParams(addProductButton.getLayoutParams().width, 0);
                    lp6.setMargins(20,10,20,20);
                    addProductButton.setLayoutParams(lp6);

                    if (!SharedPrefManager.getInstance(ProductActivity.this).isLoggedIn()) {
                            addCommentButton.setVisibility(View.INVISIBLE);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(addCommentButton.getLayoutParams().width, 0);
                        lp.setMargins(10,10,10,10);
                            addCommentButton.setLayoutParams(lp);

                            ratingBar.setVisibility(View.INVISIBLE);
                            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ratingBar.getLayoutParams().width, 0);
                        lp2.setMargins(10,10,10,10);
                            ratingBar.setLayoutParams(lp2);

                            commentTextView.setVisibility(View.INVISIBLE);
                            LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(commentTextView.getLayoutParams().width, 0);
                        lp3.setMargins(10,10,10,10);
                            commentTextView.setLayoutParams(lp3);

                            rateCount.setVisibility(View.INVISIBLE);
                            LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(rateCount.getLayoutParams().width, 0);
                        lp4.setMargins(10,10,10,10);
                            rateCount.setLayoutParams(lp4);

                            loginForMore.setVisibility(View.VISIBLE);
                            LinearLayout.LayoutParams lp5 = new LinearLayout.LayoutParams(loginForMore.getLayoutParams().width, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp5.setMargins(10,10,10,10);

                        lp5.gravity = Gravity.CENTER;
                    loginForMore.setLayoutParams(lp5);

                            loginForMore.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {

                                    Intent x = new Intent(getApplicationContext() , LoginActivity.class);
                                    startActivity(x);
                                    return false;
                                }
                            });
                        }
                }
            }
        }
        Products ru = new Products();
        ru.execute();
    }

}



