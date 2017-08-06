package com.druger.aboutwork.fragments;


import android.Manifest;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.arellomobile.mvp.MvpFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.druger.aboutwork.App;
import com.druger.aboutwork.R;
import com.druger.aboutwork.activities.LoginActivity;
import com.druger.aboutwork.activities.MainActivity;
import com.druger.aboutwork.interfaces.view.AccountView;
import com.druger.aboutwork.presenters.AccountPresenter;
import com.druger.aboutwork.utils.PreferencesHelper;
import com.druger.aboutwork.utils.Utils;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import com.squareup.leakcanary.RefWatcher;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.theartofdev.edmodo.cropper.CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE;
import static com.theartofdev.edmodo.cropper.CropImage.getPickImageChooserIntent;


public class AccountFragment extends MvpFragment implements View.OnClickListener, AccountView{

    @InjectPresenter
    AccountPresenter accountPresenter;
    @Inject
    PreferencesHelper preferencesHelper;

    private TextView tvName;
    private ImageView ivEditName;
    private TextView tvMyReviews;
    private TextView tvSettings;
    private TextView tvLogout;
    private CircleImageView civAvatar;
    private TextView tvEmail;

    public AccountFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        setupUI(view);
        accountPresenter.setupAuth();
        accountPresenter.setupStorage();
        setupListeners();
        setupToolbar();

        return view;
    }

    private void setupListeners() {
        tvSettings.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        ivEditName.setOnClickListener(this);
        tvMyReviews.setOnClickListener(this);
        civAvatar.setOnClickListener(this);
    }

    private void setupToolbar() {
        ((MainActivity) getActivity()).setActionBarTitle(R.string.app_name);
        ((MainActivity) getActivity()).resetBackArrowActionBar();
    }

    private void setupUI(View view) {
        tvName = (TextView) view.findViewById(R.id.tvName);
        ivEditName = (ImageView) view.findViewById(R.id.ivEditName);
        tvMyReviews = (TextView) view.findViewById(R.id.tvMyReviews);
        tvSettings = (TextView) view.findViewById(R.id.tvSettings);
        tvLogout = (TextView) view.findViewById(R.id.tvLogout);
        civAvatar = (CircleImageView) view.findViewById(R.id.civAvatar);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);

        tvName.setText(preferencesHelper.getUserName());
    }

    @Override
    public void onStart() {
        super.onStart();
        accountPresenter.addAuthListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        accountPresenter.removeAuthListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivEditName:
                accountPresenter.clickChangeName();
                break;
            case R.id.tvSettings:
                accountPresenter.clickOpenSettings();
                break;
            case R.id.tvLogout:
                accountPresenter.logout();
                break;
            case R.id.tvMyReviews:
                accountPresenter.clickOpenMyReviews();
                break;
            case R.id.civAvatar:
                showPhotoPicker();
                break;
        }
    }

    private void showPhotoPicker() {
        startActivityForResult(getPickImageChooserIntent(getActivity()), PICK_IMAGE_CHOOSER_REQUEST_CODE);
    }

    @Override
    public void openMyReviews(String userId) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, MyReviewsFragment.newInstance(userId));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void showLoginActivity() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    @Override
    public void showEmail(String email) {
        tvEmail.setText(email);
    }

    @Override
    public void checkPermissionReadExternal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void startCropImageActivity(Uri imgUri) {
        CropImage.activity(imgUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setActivityTitle(getString(R.string.photo_crop_name))
                .setScaleType(CropImageView.ScaleType.CENTER)
                .setAspectRatio(1, 1)
                .start(getActivity(), this);
    }

    @Override
    public void setupPhoto(Uri imgUri) {
        civAvatar.setImageURI(imgUri);
    }

    @Override
    public void showPhoto(StorageReference storageRef) {
        Glide.with(getActivity())
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .crossFade()
                .error(R.drawable.ic_account_circle_black)
                .into(civAvatar);
    }

    @Override
    public void openSettings() {
        SettingsFragment settings = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.main_container, settings)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void changeName(final String userId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_name, null);
        final EditText etName = (EditText) view.findViewById(R.id.etUserName);
        etName.setText(tvName.getText());

        builder.setTitle(R.string.name);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userName = etName.getText().toString();
                if (!userName.trim().isEmpty()) {
                    tvName.setText(userName);
                    Utils.hideKeyboard(getActivity(), etName);
                    preferencesHelper.saveUserName(userName);
                    accountPresenter.changeUserName(userName, userId);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.hideKeyboard(getActivity(), etName);
                dialog.cancel();
            }
        });
        builder.show();
        Utils.showKeyboard(getActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        accountPresenter.checkActivityResult(getActivity(), requestCode, resultCode, data);
    }
}
