package com.gaolei.mvvm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gaolei.mvvm.activity.BaseActivity;
import com.gaolei.mvvm.databinding.ActivityMainBinding;
import com.gaolei.mvvm.fragment.BaseFragment;
import com.gaolei.mvvm.fragment.HomeFragment;
import com.gaolei.mvvm.fragment.KnowledgeFragment;
import com.gaolei.mvvm.fragment.NavigationFragment;
import com.gaolei.mvvm.fragment.ProjectFragment;
import com.gaolei.mvvm.model.PageTitle;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class MainActivity extends BaseActivity {

    private ArrayList<BaseFragment> mFragments;
    private int mLastFgIndex = 0;
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;

    String[] permissionArray = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    ActivityMainBinding binding;
    private PageTitle titleData;

    @Override
    protected void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        titleData = new PageTitle();
        titleData.setTitle(getString(R.string.home_pager));
        binding.setTitle(titleData);
    }

    @Override
    protected void initData(Bundle bundle) {

        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());
        mFragments.add(new KnowledgeFragment());
        mFragments.add(new NavigationFragment());
        mFragments.add(new ProjectFragment());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //如果versionCode>=23 则需要动态授权
            checkPermission();
        } else {
            switchFragment(0);
        }
        binding.bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.tab_main_pager:
                    titleData.setTitle(getString(R.string.home_pager));
                    binding.setTitle(titleData);
                    switchFragment(0);

                    break;
                case R.id.tab_knowledge_hierarchy:
                    titleData.setTitle(getString(R.string.knowledge_hierarchy));
                    binding.setTitle(titleData);
                    switchFragment(1);

                    break;
                case R.id.tab_navigation:
                    titleData.setTitle(getString(R.string.navigation));
                    binding.setTitle(titleData);
                    switchFragment(2);

                    break;
                case R.id.tab_project:
                    titleData.setTitle(getString(R.string.project));
                    binding.setTitle(titleData);
                    switchFragment(3);
                    break;
            }
            return true;
        });
    }

    /**
     * 切换fragment
     *
     * @param position 要显示的fragment的下标
     */
    private void switchFragment(int position) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment targetFg = mFragments.get(position);
        Fragment lastFg = mFragments.get(mLastFgIndex);
        mLastFgIndex = position;
        ft.hide(lastFg);
        if (!targetFg.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(targetFg).commit();
            ft.add(R.id.fragment_group, targetFg);
        }
        ft.show(targetFg);
        ft.commitAllowingStateLoss();
    }

    public void checkPermission() {

        boolean isAllGranted = checkPermissionAllGranted(
                permissionArray
        );
        // 如果这权限全都拥有, 则显示HomeFragment
        if (isAllGranted) {
            switchFragment(0);
            return;
        }

        ActivityCompat.requestPermissions(
                this,
                permissionArray,
                MY_PERMISSION_REQUEST_CODE
        );
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了, 则显示HomeFragment
                switchFragment(0);
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                getAppDetailSettingIntent(this);
                toast("App正常使用需要授权");

            }
        }
    }

    public void toast(String content) {
        Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
    }

    private void getAppDetailSettingIntent(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        startActivity(intent);
    }
}
