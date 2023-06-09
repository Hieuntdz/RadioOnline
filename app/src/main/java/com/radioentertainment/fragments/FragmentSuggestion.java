package com.radioentertainment.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.makeramen.roundedimageview.RoundedImageView;
import com.radioentertainment.asyncTasks.LoadSuggestion;
import com.radioentertainment.interfaces.SuccessListener;
import com.radioentertainment.radio.LoginActivity;
import com.radioentertainment.radio.R;
import com.radioentertainment.utils.Constants;
import com.radioentertainment.utils.Methods;
import com.radioentertainment.utils.SharedPref;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class FragmentSuggestion extends Fragment {

    Methods methods;
    SharedPref sharedPref;
    public static Button button_submit;
    private RoundedImageView imageView;
    private EditText editText_title, editText_desc;
    private ProgressDialog progressDialog;
    private String imagePath = "";
    private int PICK_IMAGE_REQUEST = 1;
    private final static int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_suggest, container, false);

        methods = new Methods(getActivity());
        sharedPref = new SharedPref(getActivity());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.loading));

        button_submit = rootView.findViewById(R.id.button_sug);
        imageView = rootView.findViewById(R.id.imageView_sug);
        editText_title = rootView.findViewById(R.id.editText_sug_title);
        editText_desc = rootView.findViewById(R.id.editText_sug_desc);

        button_submit.setBackground(methods.getRoundDrawableRadis(sharedPref.getFirstColor(), 500));

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPer()) {
                    pickImage();
                }
            }
        });

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (methods.isConnectingToInternet()) {
                    if (TextUtils.isEmpty(editText_title.getText())) {
                        methods.showToast(getString(R.string.enter_song_title));
                    } else if (TextUtils.isEmpty(editText_desc.getText())) {
                        methods.showToast(getString(R.string.enter_song_desc));
                    } else if (imagePath.equals("")) {
                        methods.showToast(getString(R.string.select_image));
                    } else if (!Constants.isLogged) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.putExtra("from", "app");
                        startActivity(intent);
                    } else {
//                        Toast.makeText(getActivity(), "Suggestion is disabled in demo app", Toast.LENGTH_SHORT).show();
                        loadSuggestion();
                    }
                } else {
                    methods.showToast(getString(R.string.internet_not_connected));
                }
            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void loadSuggestion() {
        LoadSuggestion loadSuggestion = new LoadSuggestion(new SuccessListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                if (getActivity() != null) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        switch (registerSuccess) {
                            case "1":
                                imageView.setImageResource(R.drawable.placeholder_upload);
                                editText_title.setText("");
                                editText_desc.setText("");
                                imagePath = "";
                                uploadDialog(message);
                                break;
                            case "-1":
                                methods.showToast(message);
                                break;
                            default:
                                methods.showToast(getString(R.string.error_server));
                                break;
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server));
                    }
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_SUGGESTION, 0, "", "", editText_desc.getText().toString(), "", "", "", "", "", editText_title.getText().toString(), "", Constants.itemUser.getId(), "", new File(imagePath)));
        loadSuggestion.execute();
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_image)), PICK_IMAGE_REQUEST);
    }

    private void uploadDialog(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        alertDialog.setTitle(getString(R.string.upload_success));
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            imagePath = methods.getPathImage(uri);

            try {
                Bitmap bitmap_upload = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                imageView.setImageBitmap(bitmap_upload);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Boolean checkPer() {
        if ((ContextCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            }
            return true;
        } else {
            return true;
        }
    }
}
