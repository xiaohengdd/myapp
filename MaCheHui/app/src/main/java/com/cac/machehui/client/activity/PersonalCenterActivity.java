package com.cac.machehui.client.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.machehui.R;

public class PersonalCenterActivity extends BaseActivity implements
		OnClickListener {
	private AlertDialog dialog;
	private EditText et_enter_pwd;
	private Button bt_ok;
	private Button bt_cancel;
	private RadioButton mRadio1;
	private RadioButton mRadio2;
	private RadioGroup mRadioGroup1;
	private String tab = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.personal_center);

		tv_sex = (TextView) findViewById(R.id.tv_sex);
		ib_sex2 = (ImageButton) findViewById(R.id.ib_sex);
		ib_sex2.setOnClickListener(this);

	}

	public void isDialog() {

		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.person_dialog_enter, null);

		mRadioGroup1 = (RadioGroup) view.findViewById(R.id.gendergroup);

		mRadio1 = (RadioButton) view.findViewById(R.id.girl);
		mRadio2 = (RadioButton) findViewById(R.id.boy);
		bt_ok = (Button) view.findViewById(R.id.bt_ok);
		bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		mRadioGroup1.setOnCheckedChangeListener(radiogpchange);
		// 取消按钮
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (tab.equals("")) {
					Toast.makeText(getApplicationContext(), "请选择性别",
							Toast.LENGTH_SHORT).show();
				} else {
					tv_sex.setText(tab);
					dialog.dismiss();
				}
			}
		});
		builder.setView(view);
		dialog = builder.show();
	}

	private RadioGroup.OnCheckedChangeListener radiogpchange = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == mRadio1.getId()) {
				Toast.makeText(getApplicationContext(), "女", Toast.LENGTH_SHORT)
						.show();
				tab = "女";
			} else {
				Toast.makeText(getApplicationContext(), "男", Toast.LENGTH_SHORT)
						.show();
				tab = "男";
			}
		}
	};
	private TextView tv_sex;
	private ImageButton ib_sex;
	private ImageButton ib_sex2;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ib_sex:
				Toast.makeText(getApplicationContext(), "我被点击了", Toast.LENGTH_SHORT)
						.show();
				isDialog();

				break;

			default:
				break;
		}

	}

}
