package com.suda.jzapp.ui.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.avos.avoscloud.AVException;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.suda.jzapp.BaseActivity;
import com.suda.jzapp.R;
import com.suda.jzapp.dao.local.record.RecordLocalDAO;
import com.suda.jzapp.manager.AccountManager;
import com.suda.jzapp.manager.RecordManager;
import com.suda.jzapp.manager.UserManager;
import com.suda.jzapp.misc.Constant;
import com.suda.jzapp.misc.IntentConstant;
import com.suda.jzapp.util.NetworkUtil;
import com.suda.jzapp.util.SPUtils;
import com.suda.jzapp.util.SnackBarUtil;
import com.suda.jzapp.util.ThemeUtil;
import com.suda.jzapp.util.ThreadPoolUtil;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.drakeet.materialdialog.MaterialDialog;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtil.getAppStyleId(this));
        setMyContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userManager = new UserManager(this);
        accountManager = new AccountManager(this);
        recordManager = new RecordManager(this);

        forgetGesture = getIntent().getBooleanExtra(IntentConstant.FORGET_GESTURE, false);
        orgUser = userManager.getCurUserName();

        initWidget();
    }

    @Override
    protected void initWidget() {
        mTitUserId = (TextInputEditText) findViewById(R.id.userid);
        mTitPassWord = (TextInputEditText) findViewById(R.id.pass);
        mTilUserId = (TextInputLayout) findViewById(R.id.til_userID);
        mTilPassWord = (TextInputLayout) findViewById(R.id.til_pass);
        mCircleProgressBar = (CircleProgressBar) findViewById(R.id.progressBar);
        mCircleProgressBar.setVisibility(View.INVISIBLE);
        mloginView = findViewById(R.id.login_view);

        loginBt = (Button) findViewById(R.id.login_bt);
        if (forgetGesture)
            loginBt.setText("清除手势");

        loginBt.setBackgroundColor(getColor(this, getMainTheme().getMainColorID()));

        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
                //thirdLogin();
            }
        });

        mTitUserId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTilUserId.setErrorEnabled(false);
            }
        });

        mTitPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTilPassWord.setErrorEnabled(false);
            }
        });
    }

    private void thirdLogin() {
//        try {
//            SNS.setupPlatform(SNSType.AVOSCloudSNSQQ, "https://leancloud.cn/1.1/sns/goto/4zex7dav3eo08wk5");
//        } catch (AVException e) {
//            e.printStackTrace();
//        }
//        SNS.loginWithCallback(this, SNSType.AVOSCloudSNSQQ, new SNSCallback() {
//            @Override
//            public void done(SNSBase base, SNSException e) {
//                if (e==null) {
//                    SNS.loginWithAuthData(base.userInfo(), new LogInCallback<AVUser>() {
//                        @Override
//                        public void done(final AVUser user, AVException e) {
//
//                        }
//                    });
//                }
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_FOR_REGISTER) {
                setResult(RESULT_OK);
                finish();
            }
        }
        //SNS.onActivityResult(requestCode, resultCode, data, SNSType.AVOSCloudSNSQQ);
    }

    private void doLogin() {
        if (!NetworkUtil.checkNetwork(this)) {
            SnackBarUtil.showSnackInfo(mTilUserId, LoginActivity.this, "请连接网络");
            return;
        }


        final String user = mTitUserId.getText().toString();
        final String password = mTitPassWord.getText().toString();
        if (TextUtils.isEmpty(user)) {
            mTilUserId.setError("请输入用户名或邮箱");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            mTilPassWord.setError("请输入密码");
            return;
        }

        hideKeyboard();
        loginBt.setClickable(false);
        if (forgetGesture) {
            login(user, password, false);
            return;
        }

        RecordLocalDAO recordLocalDAO = new RecordLocalDAO();
        if (recordLocalDAO.haveRecord(this)) {
            final MaterialDialog materialDialog = new MaterialDialog(this);
            materialDialog.setTitle("登陆提醒").setMessage("是否合并登陆前数据").
                    setPositiveButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            materialDialog.dismiss();
                            login(user, password, true);
                        }
                    }).setNegativeButton("确认", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    materialDialog.dismiss();
                    login(user, password, false);
                }
            });
            materialDialog.show();
        } else {
            login(user, password, true);
        }
    }

    private void login(final String user, final String password, final boolean clearData) {
        boolean isEmail = false;
        isEmail = isNameAddressFormat(user);
        userManager.login(isEmail ? null : user, password, isEmail ? user : null, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Constant.MSG_ERROR) {
                    loginBt.setClickable(true);
                    SnackBarUtil.showSnackInfo(mTilUserId, LoginActivity.this, msg.obj.toString());
                } else {
                    if (forgetGesture && user.equals(orgUser)) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        if (clearData) {
                            userManager.logOut(false, false);
                        }

                        SnackBarUtil.showSnackInfo(mTilUserId, LoginActivity.this, "登录成功");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mCircleProgressBar.setVisibility(View.VISIBLE);
                                YoYo.with(Techniques.SlideOutUp).playOn(mloginView);
                                YoYo.with(Techniques.SlideInUp).playOn(mCircleProgressBar);
                                SnackBarUtil.showSnackInfo(mTilUserId, LoginActivity.this, "正在恢复数据");
                                mSyncData = true;
                                canBack = false;
                                mCircleProgressBar.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light);
                                ThreadPoolUtil.getThreadPoolService().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            recordManager.initRecordTypeData();
                                            recordManager.initRecordData();
                                            accountManager.initAccountData();
                                            setResult(RESULT_OK);
                                            finish();
                                            SPUtils.put(LoginActivity.this, false, Constant.SP_LAST_SYNC_AT, Calendar.getInstance().getTimeInMillis());
                                        } catch (AVException e) {
                                            mSyncData = false;
                                            canBack = true;
                                            SnackBarUtil.showSnackInfo(mTilUserId, LoginActivity.this, "恢复出错");
                                            userManager.logOut();
                                            finish();
                                        }
                                    }
                                });

                            }
                        }, 600);
                    }
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSyncData)
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isNameAddressFormat(String email) {
        Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public void register(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REQUEST_FOR_REGISTER);
    }

    public void getPassBack(View view) {
        Intent intent = new Intent(this, UserGetPassBackActivity.class);
        startActivity(intent);
    }


    private TextInputEditText mTitUserId, mTitPassWord;
    private TextInputLayout mTilUserId, mTilPassWord;
    private Button loginBt;
    private UserManager userManager;
    private AccountManager accountManager;
    private RecordManager recordManager;
    private boolean forgetGesture = false;
    private String orgUser;
    private CircleProgressBar mCircleProgressBar;
    private View mloginView;
    private boolean mSyncData = false;
    private final static int REQUEST_FOR_REGISTER = 1;

}
