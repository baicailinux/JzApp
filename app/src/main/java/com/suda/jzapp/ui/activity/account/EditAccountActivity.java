package com.suda.jzapp.ui.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.suda.jzapp.BaseActivity;
import com.suda.jzapp.R;
import com.suda.jzapp.ui.adapter.AccountTypeAdapter;
import com.suda.jzapp.dao.greendao.AccountType;
import com.suda.jzapp.manager.AccountManager;
import com.suda.jzapp.misc.Constant;
import com.suda.jzapp.misc.IntentConstant;

import java.util.ArrayList;
import java.util.List;

public class EditAccountActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_prop);

        accountManager = new AccountManager(this);

        initParam();
        initWidget();
    }

    @Override
    protected void initWidget() {
        mEtProp = (EditText) findViewById(R.id.prop_et);
        mBtProp = (Button) findViewById(R.id.prop_bt);
        mLvAccountType = (ListView) findViewById(R.id.account_type);

        findViewById(mEditType == PROP_TYPE_ACCOUNT_TYPE ? R.id.account_other_param : R.id.account_type_param).setVisibility(View.GONE);

        switch (mEditType) {
            case PROP_TYPE_ACCOUNT_NAME:
                getSupportActionBar().setTitle("修改账户名");
                mEtProp.setText(mParam);
                break;
            case PROP_TYPE_ACCOUNT_MONEY:
                getSupportActionBar().setTitle("修改账户余额");
                mEtProp.setText(String.valueOf(mMoney));
                mEtProp.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case PROP_TYPE_ACCOUNT_REMARK:
                getSupportActionBar().setTitle("修改账户备注");
                mEtProp.setText(mParam);
                break;
            case PROP_TYPE_ACCOUNT_TYPE:
                getSupportActionBar().setTitle("修改账户类型");
                initListView();
                break;
            default:
                break;
        }

        mEtProp.setSelection(mEtProp.getText().toString().length());


        mBtProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProp(mEtProp.getText().toString());
            }
        });

    }

    private void initListView() {
        accountManager.getAllAccountType(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Constant.MSG_ERROR)
                    return;

                accountTypes.addAll((List<AccountType>) msg.obj);
                mAccountTypeAdapter = new AccountTypeAdapter(accountTypes, EditAccountActivity.this);
                mLvAccountType.setAdapter(mAccountTypeAdapter);

            }
        });

        mLvAccountType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(IntentConstant.EDIT_ACCOUNT_TYPE, accountTypes.get(position).getAccountTypeID());
                if (mAccountID > 0) {
                    accountManager.updateAccountTypeID(mAccountID, accountTypes.get(position).getAccountTypeID(), null);
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void saveProp(String param) {

        Intent intent = new Intent();

        //账户号>0 实时保存
        switch (mEditType) {
            case PROP_TYPE_ACCOUNT_NAME:
                intent.putExtra(IntentConstant.EDIT_ACCOUNT_NAME, param);
                if (mAccountID > 0) {
                    accountManager.updateAccountName(mAccountID, param, null);
                }
                //
                break;
            case PROP_TYPE_ACCOUNT_MONEY:
                double money = Double.parseDouble(param);
                if (mAccountID > 0) {
                    accountManager.updateAccountMoney(mAccountID, money - mMoney, null);
                }
                intent.putExtra(IntentConstant.EDIT_ACCOUNT_MONEY, money);
                //
                break;
            case PROP_TYPE_ACCOUNT_REMARK:
                intent.putExtra(IntentConstant.EDIT_ACCOUNT_REMARK, param);
                if (mAccountID > 0) {
                    accountManager.updateAccountRemark(mAccountID, param, null);
                }
                //
                break;
            default:
                break;
        }

        setResult(RESULT_OK, intent);
        finish();
    }


    private void initParam() {
        mEditType = getIntent().getIntExtra(IntentConstant.EDIT_TYPE, -1);

        if (mEditType == PROP_TYPE_ACCOUNT_MONEY) {
            mMoney = getIntent().getDoubleExtra(IntentConstant.EDIT_ACCOUNT_MONEY, 0);
        } else if (mEditType == PROP_TYPE_ACCOUNT_NAME) {
            mParam = getIntent().getStringExtra(IntentConstant.EDIT_ACCOUNT_NAME);
        } else if (mEditType == PROP_TYPE_ACCOUNT_REMARK) {
            mParam = getIntent().getStringExtra(IntentConstant.EDIT_ACCOUNT_REMARK);
        }

        mAccountID = getIntent().getLongExtra(IntentConstant.ACCOUNT_ID, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private EditText mEtProp;
    private Button mBtProp;
    private ListView mLvAccountType;

    private String mParam;
    private double mMoney;
    private int mEditType = -1;
    private long mAccountID = 0;
    private AccountManager accountManager;
    private AccountTypeAdapter mAccountTypeAdapter;
    private List<AccountType> accountTypes = new ArrayList<>();

    ///////////////////////////////////////////////////////////
    public static final int PROP_TYPE_ACCOUNT_NAME = 0;
    public static final int PROP_TYPE_ACCOUNT_MONEY = 1;
    public static final int PROP_TYPE_ACCOUNT_REMARK = 2;
    public static final int PROP_TYPE_ACCOUNT_TYPE = 3;

}