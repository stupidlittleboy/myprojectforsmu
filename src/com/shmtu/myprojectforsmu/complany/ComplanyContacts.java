package com.shmtu.myprojectforsmu.complany;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.shmtu.myprojectforsmu.BaseActivity;
import com.shmtu.myprojectforsmu.R;
import com.shmtu.myprojectforsmu.adapter.CompanyContactsAdapter;
import com.shmtu.myprojectforsmu.commons.Constant;

public class ComplanyContacts extends BaseActivity {


	private final static String CONTACTS_URL = Constant.URL + "get_contacts.php";
	private final static String DEPARTMENT_URL = Constant.URL + "get_department.php";
	
	private ExpandableListView elvCompanyContacts;
	private RequestQueue mQueueSon = null;
	private RequestQueue mQueueFather = null;
	private CompanyContactsAdapter companyContactsAdapter;
	private ArrayList<HashMap<String,Object>> father_array = new ArrayList<HashMap<String,Object>>();
	private ArrayList<HashMap<String,Object>> son_array = new ArrayList<HashMap<String,Object>>();

	/**
	 * 启动Activity
	 * @param context
	 */
	public static void startComplanyContacts(Context context){
		Intent intent = new Intent(context, ComplanyContacts.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complany_contacts);
		init();
	}

	private void init() {
		elvCompanyContacts = (ExpandableListView) findViewById(R.id.elv_company_contacts);
	}

	@Override
	protected void onResume() {
		super.onResume();
		companyContactsAdapter = new CompanyContactsAdapter(this, father_array, son_array, elvCompanyContacts);
		getFatherArrry();
		getCompanyContacts();
		elvCompanyContacts.setAdapter(companyContactsAdapter);
	}


	//获取父节点的值
	private void getFatherArrry(){
		//				father_array = new ArrayList<HashMap<String,Object>>();

		//创建一个RequestQueue队列
		mQueueFather = Volley.newRequestQueue(getApplicationContext());
		//向服务端发送请求
		JsonArrayRequest jsonArrayRequestFather = new JsonArrayRequest(DEPARTMENT_URL, 
				new Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {
				for (int i = 0; i < response.length(); i++) {
					try {
						JSONObject jsonObj = response.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("contactsDepartment", jsonObj.getString("emp_department"));
						father_array.add(map);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				companyContactsAdapter.setItemFatherArray(father_array);
				companyContactsAdapter.notifyDataSetChanged();
				Log.e("father1", father_array.toString());
			}
		},  
		new Response.ErrorListener() {  
			@Override  
			public void onErrorResponse(VolleyError error) {  
				Log.e("TAG", error.getMessage(), error);  
				//				Toast.makeText(LoginActivity.this, "网络连接出错，请检查网络状况！", Toast.LENGTH_LONG).show();
			}  
		});  
		mQueueFather.add(jsonArrayRequestFather);
		Log.e("father2", father_array.toString());
	}

	//获取子节点的值
	private void getCompanyContacts(){
		//		son_array = new ArrayList<HashMap<String,Object>>();
		//son
		
		//创建一个RequestQueue队列
		mQueueSon= Volley.newRequestQueue(getApplicationContext());
		JsonArrayRequest jsonArrayRequestSon = new JsonArrayRequest(CONTACTS_URL,
				new Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {
				Log.e("sonson", response.toString());
				for (int i = 0; i < response.length(); i++) {
					try {
						JSONObject jsonObj = response.getJSONObject(i);
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("contactsChildName", jsonObj.getString("emp_name"));
						map.put("contactsChildEmpNo", jsonObj.getString("emp_no"));
						map.put("contactsChildPhoneNo", jsonObj.getString("emp_phone_no"));
						map.put("contactsDepartment", jsonObj.getString("emp_department"));
						son_array.add(map);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				companyContactsAdapter.setItemSonArray(son_array);
				companyContactsAdapter.notifyDataSetChanged();
				Log.e("son", son_array.toString());
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		mQueueSon.add(jsonArrayRequestSon);
	}

}