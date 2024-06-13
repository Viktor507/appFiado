package com.example.appfiado;

import android.content.Intent;

public interface OnActivityResultListener {
    void onActivityResult(int requestCode, int resultCode, Intent data, String img);
}
