package kaiqi.cn.httpx;

import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Properties;

import callbacks.ResultCallbackAdapt;
import http.ApiException;
import http.HttpRequestFactory;
import http.HttpRequestManager;
import http.Tst;
import kaiqi.cn.httpx.req.HomeArticleReq;
import kaiqi.cn.httpx.req.RegisterReq;
import kaiqi.cn.httpx.req.UploadReq;
import kaiqi.cn.httpx.resp.HomeArticleResp;
import kaiqi.cn.httpx.resp.RegisterResp;
import loading.IProgressDialog;
import resp.HttpCommObjResp;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTst1;
    private TextView mTst2;
    private TextView mTst3;
    private TextView mTst4;
    private TextView mContent;
    private HomeArticleReq home;
    private HomeArticleReq homeII;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTst1 = (TextView) findViewById(R.id.tst1);
        mTst2 = (TextView) findViewById(R.id.tst2);
        mTst3 = (TextView) findViewById(R.id.tst3);
        mTst4 = (TextView) findViewById(R.id.tst4);
        mContent = (TextView) findViewById(R.id.content);
        mTst1.setOnClickListener(this);
        mTst2.setOnClickListener(this);
        mTst3.setOnClickListener(this);
        mTst4.setOnClickListener(this);
        mTst1.setText("注册并取消请求");
        mTst2.setText("注册请求");
        mTst3.setText("首页文章请求");
        mTst4.setText("首页分类请求");
        UploadReq req = new UploadReq();
        req.fileData = "哈哈";
        String json = HttpRequestManager.GSON.toJson(req);
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> map = HttpRequestManager.GSON.fromJson(json, type);
        mContent.setText(map + "" + Tst.A);
        System.out.println(map);

        try {
            InputStream open = getResources().getAssets().open("http");
            // open = Resources.getSystem().getAssets().open("http"); //获取失败
            // InputStream open = ClassLoader.getSystemResourceAsStream("http");
            Properties properties = new Properties();
            properties.load(open);//is是通过上面获得的输入流
            System.out.println("======================");
            System.out.println(properties);
            System.out.println("======================");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e + "======================");
        }
    }

    @Override
    public void onClick(View v) {
        final RegisterReq json = new RegisterReq("12132322333", "123456", "123456");
        //step2
        final IProgressDialog iLoading = createLoading();
        if (v == mTst1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    registerReq(json, iLoading);
                    HttpRequestFactory.cancel("注册");
                }
            }, 3000);

            // stepII(json);
        } else if (v == mTst2) {
            registerReq(json, iLoading);
        } else if (v == mTst3) {
            if (home == null) {
                home = new HomeArticleReq();
            } else {
                home.page += 1;
            }
            home.page = 1; // 缓存测试
            home.skipParams = true;
            optHomeArticleReq(home, iLoading);
        } else if (v == mTst4) {
            if (homeII == null) {
                homeII = new HomeArticleReq();
            }
            homeII.cid = 60;
            optHomeArticleReq(homeII, iLoading);
        }
    }

    @NonNull
    private IProgressDialog createLoading() {
        final IProgressDialog iLoading = new IProgressDialog(this);
        ClipDrawable d = new ClipDrawable(new ColorDrawable(Color.YELLOW), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        iLoading.setProgressDrawable(d);
        iLoading.setTitle("加载zhong....");
        iLoading.setCancelable(false);
        iLoading.setCanceledOnTouchOutside(false);
        iLoading.beginShow(); // 可以不调用这个,已经为你调用了,当然你调用也没事
        return iLoading;
    }

    private void registerReq(RegisterReq json, IProgressDialog iLoading) {
        HttpRequestFactory.doPost(json, new ResultCallbackAdapt<HttpCommObjResp<RegisterResp>>() {
            @Override
            public void doOnResponse(HttpCommObjResp<RegisterResp> response) {
                System.out.println(response + "");
                mContent.setText("\n方式一返回:" + response);
            }

            @Override
            public void doOnError(ApiException ex) {
                System.out.println(ex + "");
                mContent.setText("\n方式一返回:" + ex);
            }
        }, iLoading);
    }

    //方式II,返回string
    public void stepII(RegisterReq json) {

        HttpRequestFactory.doPost(json, new ResultCallbackAdapt<String>() {
            @Override
            public void doOnResponse(String response) {
                System.out.println(response + "");
                mContent.setText("\n方式二返回:" + response);
            }

            @Override
            public void doOnError(ApiException ex) {
                System.out.println(ex + "");
                mContent.setText("\n方式二返回:" + ex);
            }
        });
    }

    public void optHomeArticleReq(HomeArticleReq json, IProgressDialog iLoading) {
        HttpRequestFactory.doGet(json, new ResultCallbackAdapt<HttpCommObjResp<HomeArticleResp>>() {
            @Override
            public void doOnResponse(HttpCommObjResp<HomeArticleResp> response) {
                System.out.println(response + "");
                mContent.setText("\n方式一返回:" + response);
            }

            @Override
            public void doOnError(ApiException ex) {
                System.out.println(ex + "");
                mContent.setText("\n方式一返回:" + ex);
            }
        }, iLoading);
    }
}
