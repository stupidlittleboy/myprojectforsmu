package com.shmtu.myprojectforsmu.login_resgester;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.shmtu.myprojectforsmu.BaseActivity;
import com.shmtu.myprojectforsmu.MainActivity;
import com.shmtu.myprojectforsmu.R;
import com.shmtu.myprojectforsmu.commons.Constant;
import com.shmtu.myprojectforsmu.ui.CircleDrawableImage;
import com.shmtu.myprojectforsmu.utils.ActivityCollector;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private final static String LOGIN_URL = Constant.URL + "login.php";
	
	private EditText tvUsername;
	private EditText tvPassword;
	private Button btn_login;
	private TextView tvBackpass;
	private TextView tvNewuser;
	private ImageView ivLoginLogo;
	private RequestQueue mQueue = null;
	private SharedPreferences sp;

	public static void startLoginActivity (Context context, 
			String username, String password) {
		Intent intent = new Intent(context, LoginActivity.class);
		intent.putExtra("username", username);
		intent.putExtra("password", password);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		/*JPushInterface.setDebugMode(true);
		JPushInterface.init(getApplicationContext());*/

		init();
		
		//将用户名，密码信息保存
		sp = getSharedPreferences("myProjectForSMU", MODE_PRIVATE);
		if (sp != null) {
			String username = sp.getString("userName", null);
			String password = sp.getString("passWord", null);
			JSONObject json = new JSONObject();
			try {
				json.put("UserName", username);
				json.put("PassWord", password);
				tvUsername.setText(username);
				tvPassword.setText(password);
				checkLogin(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	private void init() {
		tvUsername = (EditText) findViewById(R.id.username);
		tvPassword = (EditText) findViewById(R.id.password);
		tvBackpass = (TextView) findViewById(R.id.tv_backpass);
		tvNewuser = (TextView) findViewById(R.id.tv_newuser);
		btn_login = (Button) findViewById(R.id.btn_login);
		ivLoginLogo = (ImageView) findViewById(R.id.iv_login_logo);

		btn_login.setOnClickListener(this);
		tvBackpass.setOnClickListener(this);
		tvNewuser.setOnClickListener(this);
		
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
		ivLoginLogo.setImageDrawable(new CircleDrawableImage(bitmap));
		
		Intent intent = getIntent();
		String username = intent.getStringExtra("username");
		String password = intent.getStringExtra("password");
		if(username != null && password != null){
			tvUsername.setText(username);
			tvPassword.setText(password);
		}
	}

	public void onClick(View v){
		int id = v.getId();
		switch(id){
		//登陆按钮点击事件
		case R.id.btn_login:
			/*//test
			Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent1);*/
			//将数据封装成json格式
			if (tvUsername.getText() == null || "".equals(tvUsername.getText().toString().trim())) {
				Toast.makeText(LoginActivity.this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
				break;
			} else if (tvPassword.getText() == null || "".equals(tvPassword.getText().toString().trim())) {
				Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
				break;
			} else {
				String userName = tvUsername.getText().toString().trim();
				String passWord = tvPassword.getText().toString().trim();
				JSONObject json = new JSONObject();
				try {
					json.put("UserName", userName);
					json.put("PassWord", passWord);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
//				SharePreferenceUtil spu = new SharePreferenceUtil(this, "login");
//				spu.saveSharedPreferences("userName", userName);
//				spu.saveSharedPreferences("passWord", passWord);
				
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("userName", userName);
				editor.putString("passWord", passWord);
				editor.commit();
				
				checkLogin(json);
			}
			
			break;

			//找回密码
		case R.id.tv_backpass:
			Intent intent = new Intent(LoginActivity.this, BackPassword.class);
			startActivity(intent);
			break;

			//注册点击事件
		case R.id.tv_newuser:
			Intent intent1 = new Intent(LoginActivity.this, SMSRegisterActivity.class);
//			Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
			startActivity(intent1);
			break;
		}
	}

	private void checkLogin(JSONObject json) {
		//创建一个RequestQueue队列
		mQueue = Volley.newRequestQueue(getApplicationContext());
		//向服务端发送请求
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST, LOGIN_URL, json,  
				new Response.Listener<JSONObject>() {  
			@Override  
			public void onResponse(JSONObject response) {  
				Log.d("TAG", response.toString());  
//						Toast.makeText(LoginActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
				try {
					int success = Integer.parseInt(response.getString("success"));
					if(success == 1){
						progressDialog();
					}else{
						Toast.makeText(LoginActivity.this, "输入的用户名或密码有错", Toast.LENGTH_LONG).show();
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}  
		}, new Response.ErrorListener() {  
			@Override  
			public void onErrorResponse(VolleyError error) {  
				Log.e("TAG", error.getMessage(), error);  
				Toast.makeText(LoginActivity.this, "网络连接出错，请检查网络状况！", Toast.LENGTH_LONG).show();
			}  
		});  

		mQueue.add(jsonObjectRequest);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			//监听返回键
			ActivityCollector.finishAll();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void progressDialog () {
		final ProgressDialog proDialog = new ProgressDialog(this);
		proDialog.setTitle("验证中");
		proDialog.setMessage("正在登陆，请稍后…");
		proDialog.setIndeterminate(true);
		proDialog.setCancelable(false);
		proDialog.show();
		
		final Timer t = new Timer();
		t.schedule(new TimerTask() {
			
			@Override
			public void run() {
				proDialog.dismiss();
				t.cancel();
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
			}
		}, 3000);
	}
}
