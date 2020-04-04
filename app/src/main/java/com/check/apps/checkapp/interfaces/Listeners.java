package com.check.apps.checkapp.interfaces;


public interface Listeners {


    interface LoginListener {
        void checkDataLogin();
    }
    interface SkipListener
    {
        void skip();
    }
    interface BackListener
    {
        void back();
    }


    interface ShowCountryDialogListener
    {
        void showDialog();
    }


    interface SignUpListener {
        void checkDataSignUp();
    }



}
