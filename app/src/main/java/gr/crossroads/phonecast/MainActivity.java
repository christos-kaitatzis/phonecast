package gr.crossroads.phonecast;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static gr.crossroads.phonecast.R.drawable.imgbutton;

public class MainActivity extends AppCompatActivity {

//    private final int duration = 1; // seconds
//    private final int sampleRate = 44100;
//    private final int numSamples = (int) duration * sampleRate;
//    private final double sample[] = new double[numSamples];
//    private final double freqOfTone = 3000; // hz
//
//     private final byte generatedSnd[] = new byte[2 * numSamples];
final int PERMISSION_MULTIPLE=123;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_CODE_WRITE = 1;

    int millis=100;

    BroadcastReceiver broadcastReceiver;

    TextView recName,button_save,button_clear;
    EditText nameSave, receiveNum;

        ImageView rotateLogo;
    ImageView snd,saveCnt,clrNum;
    ImageView refPercent;


    TextView yournum,receiveDes,moto,sendNumberTxt;
    ImageView bar;

    RelativeLayout relativetop;
    RelativeLayout shadow;
    RelativeLayout upcorner;
    RelativeLayout uprightrect;
    RelativeLayout relativetopup;

    RelativeLayout relS;

    ImageView sentCheck,receiveCheck;

    List<ScanResult> lsr;
    WifiConfiguration netConfig;
    WifiApManager sd;
    WifiApManager savedAp;
    WifiConfiguration SavedWiFi;

    Boolean sent=false,received=false;

    boolean permissionStatus= false;

    Handler handler = new Handler();
    TextView text;
    EditText textPhone;
    EditText textReceive;
    EditText name;
    ImageView iv;
    //Button bt;
    ImageView save;
    ImageView clearNum;
    String id;
    String cast="(@$+_";

    AlertDialog alert;

    public WifiManager wifiManager;//= (WifiManager)getBaseContext().getSystemService(Context.WIFI_SERVICE);

    public void task(){
        RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(10000);
        rotate.setInterpolator(new LinearInterpolator());

        //ImageView image= (ImageView) findViewById(R.id.logoRotate);

        rotateLogo.startAnimation(rotate);
    }

//    private TimerTask timerTask = new TimerTask() {
//
//        @Override
//        public void run() {
//           task();
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //new ExecuteAsRootBase().getCommandsToExecute();
//        try {
//            Runtime.getRuntime().exec("su");
//        }
//        catch (IOException e){}

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error" + Thread.currentThread().getStackTrace()[2], paramThrowable.getLocalizedMessage());
            }
        });



        setContentView(R.layout.design);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        //requestPermissionWrite();

        if ( (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS)!= PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS)!= PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_CONTACTS, Manifest.permission.WRITE_SETTINGS },
                    PERMISSION_MULTIPLE);
        }


        bar=(ImageView) findViewById(R.id.bar_sending);

        relativetop = (RelativeLayout) findViewById(R.id.toprelative);
        relativetop.setY(getStatusBarHeight() + 12);
        shadow = (RelativeLayout) findViewById(R.id.shadow);
        shadow.setY(getStatusBarHeight() + 12);
        upcorner = (RelativeLayout) findViewById(R.id.relativeuprightcorner);
        upcorner.setY(getStatusBarHeight() + 12);
        uprightrect = (RelativeLayout) findViewById(R.id.relativeupright);
        uprightrect.setY(getStatusBarHeight() + 12);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        params.height = getStatusBarHeight();

        relativetopup = (RelativeLayout) findViewById(R.id.toprelativeup);
        //params= relativetopup.getLayoutParams();
        relativetopup.setLayoutParams(params);



        sentCheck=(ImageView) findViewById(R.id.sentNumberCheckPercent);
        receiveCheck=(ImageView) findViewById(R.id.sentNumberCheckPercent2);
        //save=(ImageView) findViewById(R.id.saveContactImageView);
        clearNum=(ImageView) findViewById(R.id.clearImageView);
        saveCnt=(ImageView) findViewById(R.id.saveContactImageView);
        clrNum=(ImageView) findViewById(R.id.clearImageView);
        refPercent=(ImageView) findViewById(R.id.refreshPercent);

        textPhone = (EditText) findViewById(R.id.editTextDesign);
        textReceive= (EditText) findViewById(R.id.editText2);
        nameSave= (EditText) findViewById(R.id.Namesave);
        name=(EditText) findViewById(R.id.editText3);
        receiveNum=(EditText) findViewById(R.id.receiveNumber);

        textPhone.setText("");

       // bt = (Button) findViewById(R.id.button);





        iv=(ImageView) findViewById(R.id.newRotated);
//        iv.setVisibility(View.VISIBLE);
//        iv.setVisibility(View.INVISIBLE);

        rotateLogo=(ImageView) findViewById(R.id.logoRotate);

        recName=(TextView) findViewById(R.id.receiveNameDesign);
        button_save=(TextView) findViewById(R.id.saveContactButton);
        button_clear=(TextView) findViewById(R.id.clearContactButton);
        yournum=(TextView) findViewById(R.id.yournumber);
        receiveDes=(TextView) findViewById(R.id.receiveNumberDesign);
        moto=(TextView) findViewById(R.id.motodown);
        sendNumberTxt=(TextView) findViewById(R.id.sendNumberTextView);






        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "OpenSans_Light.ttf");
        //Typeface candara = Typeface.createFromAsset(getAssets(), "Candara.ttf");

        yournum.setTypeface(myTypeface);
        receiveDes.setTypeface(myTypeface);
        recName.setTypeface(myTypeface);

        textPhone.setTypeface(myTypeface,Typeface.BOLD_ITALIC);
        receiveNum.setTypeface(myTypeface,Typeface.BOLD_ITALIC);
        nameSave.setTypeface(myTypeface,Typeface.BOLD_ITALIC);

        moto.setTypeface(myTypeface, Typeface.BOLD_ITALIC);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                task();
                handler.postDelayed(this, 10 * 1000);
            }
        }, 1);


        snd=(ImageView) findViewById(R.id.imgSend);
        snd.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if(textPhone.getText().length()>9){
                iv.setVisibility(View.VISIBLE);
                //sendNumberTxt.setText("Sending...");

                //enableName();
                }


                return false;
            }
        });

        snd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sent = false;
                received = false;

//                if (textPhone.getText().length() != 10) {
//                    bt.setText("SEND");
//                }

                createAP();

            }
        });


        saveCnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkPermissionContact()) {
                    alertDial();
                }
                else{

                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_CONTACTS},PERMISSION_REQUEST_CODE);
                }

            }
        });


        clrNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                receiveNum.setText("");
                nameSave.setText("");
                sent=false;

            }
        });

        refPercent.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                refreshAndScan();

                return false;
            }
        });


        //relS= (RelativeLayout) findViewById(R.id.relSend);
        //relS.getBackground().setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));

//        relS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                sent = false;
//                received = false;
//
//                if (textPhone.getText().length() != 10) {
//                    bt.setText("SEND");
//                }
//
//                createAP();
//            }
//        });

           // enableName();
        //alertDial();
       // nameSave.setText("Name");

//        relS.setOnTouchListener(new View.OnTouchListener() {
//            private Rect rect;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                sendNumberTxt.setText("Sending...");
//
//
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
////                    snd.getDrawable().setColorFilter(0xFF00FFF7, PorterDuff.Mode.SRC_ATOP);
////                    rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
////                    snd.invalidate();
//                }
//                if (event.getAction() == MotionEvent.ACTION_UP) {
////                    snd.setColorFilter(Color.BLUE);
////                    snd.getDrawable().setColorFilter(0xf0f0f0, PorterDuff.Mode.SRC_ATOP);
////                    snd.getDrawable().clearColorFilter();
////                    snd.invalidate();
//                }
//                if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    if (!rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
////                        snd.setColorFilter(Color.argb(0, 0, 0, 0));
////                        snd.getDrawable().setColorFilter(0xf0f0f0, PorterDuff.Mode.SRC_ATOP);
////                        snd.getDrawable().clearColorFilter();
////                        snd.invalidate();
//                    }
//                }
//                return false;
//            }
//        });





        try {
            FileInputStream fis = openFileInput("phonesave");
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            try {
                textPhone.setText(sb.toString().replaceAll(" ", "").replaceAll("-", ""));
            }
            catch (Exception e){}
        } catch (FileNotFoundException e) {

        } catch (UnsupportedEncodingException e) {

        } catch (IOException e) {

        }

        disableItems();


        wifiManager = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);


        if(!wifiManager.isWifiEnabled()) {
            try {
                millis=2000;
                wifiManager.setWifiEnabled(true);
            } catch (Exception e) {
            }
        }

        Handler repeatScan = new Handler();

        repeatScan.postDelayed(new Runnable() {
            public void run() {

                wifiManager.startScan();
            }
        }, 1000);
        //wifiManager.startScan();

         sd=new WifiApManager(this);
        ////////
        savedAp=new WifiApManager(this);
        SavedWiFi=new WifiConfiguration();
        SavedWiFi=getWifiApConfiguration(MainActivity.this);
        ////////////////

        netConfig = new WifiConfiguration();


        netConfig.SSID = "" + cast+id + "";
        //sd.setWifiApEnabled(netConfig,true);
        //netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        ///netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        //netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        //netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);


//        bt.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//
//                bt.setText("SENDING...");
//
//
//                return false;
//            }
//        });

//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                sent = false;
//                received = false;
//
//                if (textPhone.getText().length() != 10) {
//                    bt.setText("SEND");
//                }
//
//                createAP();
//
////                Animation moveLefttoRight = new TranslateAnimation(0, 400, 0, 0);
////                moveLefttoRight.setDuration(500);
////                moveLefttoRight.setFillAfter(true);
////
////                //bar.setAnimation(moveLefttoRight);
////                for(int i=0;i<44;i++) {
////                    bar.startAnimation(moveLefttoRight);
////
////
////                }
////                Animation mAnimation = new TranslateAnimation(
////                        TranslateAnimation.ABSOLUTE, 0f,
////                        TranslateAnimation.ABSOLUTE, 80f,
////                        TranslateAnimation.RELATIVE_TO_PARENT, 0.2f,
////                        TranslateAnimation.RELATIVE_TO_PARENT, 0.2f);
////                mAnimation.setDuration(1000);
////                mAnimation.setRepeatCount(22);
////                mAnimation.setRepeatMode(Animation.RESTART);
////                mAnimation.setInterpolator(new LinearInterpolator());
////                bar.setAnimation(mAnimation);
////                bar.startAnimation(mAnimation);
//
//
////                if (textPhone.getText().length() == 10) {
////                    try {
////                        //ed=textPhone.getText().toString();
////                        id = encode(textPhone.getText().toString());
////                        netConfig.SSID = "" + cast + id + "  ";
////                        sd.setWifiApEnabled(netConfig, true);
////                        Method setWifiApMethod = sd.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
////                        boolean apstatus = (Boolean) setWifiApMethod.invoke(sd, netConfig, true);
////
////                        Method isWifiApEnabledmethod = sd.getClass().getMethod("isWifiApEnabled");
////                        while (!(Boolean) isWifiApEnabledmethod.invoke(sd)) {
////                        }
////
////                        Method getWifiApStateMethod = sd.getClass().getMethod("getWifiApState");
////                        int apstate = (Integer) getWifiApStateMethod.invoke(sd);
////                        Method getWifiApConfigurationMethod = sd.getClass().getMethod("getWifiApConfiguration");
////                        netConfig = (WifiConfiguration) getWifiApConfigurationMethod.invoke(sd);
////                        Log.e("CLIENT", "\nSSID:" + netConfig.SSID + "\nPassword:" + netConfig.preSharedKey + "\n");
////
////                        sent=true;
////
////                    } catch (Exception e) {
////                        Log.e(this.getClass().toString(), "", e);
////                    }
////
////                    Runnable runnable = new Runnable() {
////                        @Override
////                        public void run() {
////                            iv.animate().rotationBy(10000).setDuration(22000).setInterpolator(new LinearInterpolator()).start();
////                        }
////                    };
////
////                    iv.animate().rotationBy(10000).setDuration(22000).setInterpolator(new LinearInterpolator()).start();
////
////                    Handler handler = new Handler();
////                    handler.postDelayed(new Runnable() {
////                        public void run() {
////                            // Actions to do after 12 seconds
////                            //sd.setWifiApEnabled(netConfig,false);
////                            sd.setWifiApConfiguration(SavedWiFi);
////                            //sd.setWifiApEnabled(SavedWiFi, false);
////                            wifiManager.setWifiEnabled(true);
////                            iv.animate().cancel();
////
////
////
////                        }
////                    }, 22000);
////
////
////                } else {
////                    Toast.makeText(MainActivity.this, "The number is required to have 10 digits", Toast.LENGTH_SHORT).show();
////                }
//            }
//        });

        clearNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                receiveNum.setText("");
                nameSave.setText("");

                sentCheck.setVisibility(View.INVISIBLE);
                receiveCheck.setVisibility(View.INVISIBLE);
            }
        });



        //ScanResult
        //registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


//        if (checkPermission()&&checkPermissionWrite()) {
//            registerReceiver(broadcastReceiver = new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context c, Intent intent) {
//                    //Toast.makeText(MainActivity.this, wifiManager.getScanResults().toString(), Toast.LENGTH_LONG).show();
//                    int s = wifiManager.getScanResults().size();
//                    //String list[]=new String[s];
//                    //list = wifiManager.getScanResults().toString().split(",");
//
//                    for (int i = 0; i < wifiManager.getScanResults().size(); i++) {
//                        if (wifiManager.getScanResults().get(i).toString().contains("Kyma")) { //Toast.makeText(MainActivity.this,wifiManager.getScanResults().get(i).toString() , Toast.LENGTH_LONG).show();
//
//                            receiveNum.setText(decode(wifiManager.getScanResults().get(i).toString().substring(11, 21)));//
//
//                            received = true;
//
//                            receiveCheck.setVisibility(View.VISIBLE);
//
//                            //////////////
//                            enableName();
//                            //////////////
//
//                            if (!sent) {
//
//
//                                if (textPhone.getText().length() > 9) {
//                                    createAP();
//                                    rotatepng();
//                                }
//
//                            }
//
//
//                        }
//
//                    }
//
//
//                    //wifiManager.startScan();
//                }
//
//
//                //WifiManager wm = (WifiManager) getSystemService (Context.WIFI_SERVICE);
//                //boolean a = wm.startScan();
//
////            @Override
////            public void onDestroy() {
////                if (broadcastReceiver != null) {
////                    unregisterReceiver(broadcastReceiver);
////                    broadcastReceiver = null;
////                }
////                super.onDestroy();
////            }
//
//            }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        }
//        else{
//
//            requestPermission();
//            requestPermissionWrite();
//        }





//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                alertDial();
//                //Toast.makeText(MainActivity.this, "Contact saved", Toast.LENGTH_SHORT).show();
//
//            }
//        });

        textPhone.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                String filename = "phonesave";
                String string = textPhone.getText().toString().replace(" ", "");
                FileOutputStream outputStream;

                try {
                    outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                    outputStream.write(string.replaceAll(" ", "").getBytes());
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                //textPhone.setText(textPhone.getText().toString().replaceAll(" ",""));

            }
        });

//        textPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    //Toast.makeText(getApplicationContext(), "got the focus", Toast.LENGTH_LONG).show();
//
//                } else {
//                    //Toast.makeText(getApplicationContext(), "lost the focus", Toast.LENGTH_LONG).show();
//
//                }
//            }
//        });


    }

    public void createAP(){


        //requestPermission();

        if (checkPermission()) {
           // requestPermission();




        if (textPhone.getText().length() > 9) {
            try {
                //ed=textPhone.getText().toString();
                id = encode(textPhone.getText().toString());
                netConfig.SSID = "" + cast + id + "  ";
//                sd.setWifiApEnabled(netConfig, true);
//                Method setWifiApMethod = sd.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
//                boolean apstatus = (Boolean) setWifiApMethod.invoke(sd, netConfig, true);
//
//                Method isWifiApEnabledmethod = sd.getClass().getMethod("isWifiApEnabled");
//                while (!(Boolean) isWifiApEnabledmethod.invoke(sd)) {
//                }
//
//                Method getWifiApStateMethod = sd.getClass().getMethod("getWifiApState");
//                int apstate = (Integer) getWifiApStateMethod.invoke(sd);
//                Method getWifiApConfigurationMethod = sd.getClass().getMethod("getWifiApConfiguration");
//                netConfig = (WifiConfiguration) getWifiApConfigurationMethod.invoke(sd);
//                Log.e("CLIENT", "\nSSID:" + netConfig.SSID + "\nPassword:" + netConfig.preSharedKey + "\n");

                new WifiAccessManager().setWifiApState(getApplicationContext(),true);
                sendNumberTxt.setText("Sending...");
                millis=100;

                sent=true;

            } catch (Exception e) {
                Log.e(this.getClass().toString(), "", e);
            }

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    iv.setVisibility(View.VISIBLE);
                    iv.animate().rotationBy(10000).setDuration(22000).setInterpolator(new LinearInterpolator()).start();
                }
            };

            iv.animate().rotationBy(10000).setDuration(22000).setInterpolator(new LinearInterpolator()).start();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    //bt.setText("SEND");
                    sendNumberTxt.setText("Send");
                    // Actions to do after 12 seconds
                    //sd.setWifiApEnabled(netConfig,false);
                    sd.setWifiApConfiguration(SavedWiFi);
                    //sd.setWifiApEnabled(SavedWiFi, false);
                    wifiManager.setWifiEnabled(true);
                    iv.animate().cancel();
                    iv.setVisibility(View.INVISIBLE);

                    sentCheck.setVisibility(View.VISIBLE);

                    wifiManager.startScan();

                    try {
                        WifiManager wifiMgr = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                        String name = wifiInfo.getSSID();
                    }
                    catch (Exception e){}


                }
            }, 22000);

            //sent=true;

        } else {
            Toast.makeText(MainActivity.this, "The number is required to have 10 digits", Toast.LENGTH_SHORT).show();
        }




        } else {
            requestPermission();
        }
        if (!checkPermissionWrite()) {

        } else {
            requestPermissionWrite();
        }



    }

    public void refreshAndScan(){

        wifiManager.startScan();

        //refPercent.setVisibility(View.VISIBLE);
        refPercent.animate().rotationBy(1000).setDuration(2000).setInterpolator(new LinearInterpolator()).start();
//            }
//        };

        refPercent.animate().rotationBy(1000).setDuration(2000).setInterpolator(new LinearInterpolator()).start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                refPercent.animate().cancel();
                //refPercent.setVisibility(View.INVISIBLE);

            }
        }, 1000);


    }

    public void rotatepng(){

//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
                iv.setVisibility(View.VISIBLE);
                iv.animate().rotationBy(10000).setDuration(22000).setInterpolator(new LinearInterpolator()).start();
//            }
//        };

        iv.animate().rotationBy(10000).setDuration(22000).setInterpolator(new LinearInterpolator()).start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                iv.animate().cancel();
                iv.setVisibility(View.INVISIBLE);


            }
        }, 22000);
    }

    public class WifiAccessManager {



        private String SSID = "" + cast + id + "  ";
        public boolean setWifiApState(Context context, boolean enabled) {
            //config = Preconditions.checkNotNull(config);
            try {
                WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (enabled) {
                    mWifiManager.setWifiEnabled(false);
                }
                WifiConfiguration conf = getWifiApConfiguration();
                mWifiManager.addNetwork(netConfig);

                return (Boolean) mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class).invoke(mWifiManager, netConfig, enabled);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        public WifiConfiguration getWifiApConfiguration() {
            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID =  SSID;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            return conf;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();



        Handler repeatScan = new Handler();
        repeatScan.postDelayed(new Runnable() {
            public void run() {

                WifiManager wifiMgr = (WifiManager) getBaseContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                String name = wifiInfo.getSSID();

                wifiManager.startScan();
            }
        }, 1000);

                if (checkPermission()&&checkPermissionWrite()) {
            registerReceiver(broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context c, Intent intent) {
                    //Toast.makeText(MainActivity.this, wifiManager.getScanResults().toString(), Toast.LENGTH_LONG).show();
                    int s = wifiManager.getScanResults().size();
                    //Toast.makeText(MainActivity.this,String.valueOf(s),Toast.LENGTH_SHORT).show();
                    //String list[]=new String[s];
                    //list = wifiManager.getScanResults().toString().split(",");

                    for (int i = 0; i < s; i++) {
                        if (wifiManager.getScanResults().get(i).toString().contains("(@$+")) { //Toast.makeText(MainActivity.this,wifiManager.getScanResults().get(i).toString() , Toast.LENGTH_LONG).show();

                            receiveNum.setText(decode(wifiManager.getScanResults().get(i).toString().substring(11, 21)));//

                            received = true;

                            receiveCheck.setVisibility(View.VISIBLE);

                            //////////////
                            enableName();
                            //////////////

                            if (!sent) {


                                if (textPhone.getText().length() > 9) {

                                    Handler h = new Handler();
                                    h.postDelayed(new Runnable() {
                                        public void run() {

                                            createAP();
                                            rotatepng();

                                        }
                                    }, millis);


                                }

                            }


                        }

                    }


                    //wifiManager.startScan();
                }


                //WifiManager wm = (WifiManager) getSystemService (Context.WIFI_SERVICE);
                //boolean a = wm.startScan();

//            @Override
//            public void onDestroy() {
//                if (broadcastReceiver != null) {
//                    unregisterReceiver(broadcastReceiver);
//                    broadcastReceiver = null;
//                }
//                super.onDestroy();
//            }

            }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        }
        else{

            requestPermission();
            requestPermissionWrite();
        }

    }


    public void alertDial (){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Save contact?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {



                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                int rawContactInsertIndex = ops.size();

                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nameSave.getText().toString()) // Name of the person
                        .build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(
                                ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, receiveNum.getText().toString()) // Number of the person
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build()); // Type of mobile number



                try {
                    ContentProviderResult[] res = getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (RemoteException e) {
                    // error
                } catch (OperationApplicationException e) {
                    // error
                }

                Toast.makeText(MainActivity.this, "Contact saved", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        alert = builder.create();
        alert.show();
    }

    public static WifiConfiguration getWifiApConfiguration(final Context ctx) {
        final WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        final Method m = getWifiManagerMethod("getWifiApConfiguration", wifiManager);
        if(m != null) {
            try {
                return (WifiConfiguration) m.invoke(wifiManager);
            } catch(Exception e) {
            }
        }
        return null;
    }

    public void disableItems(){
        //receiveNum.setEnabled(false);
        clearNum.setEnabled(false);
        saveCnt.setEnabled(false);
        nameSave.setEnabled(false);
    }

    public void enableName(){
        //receiveNum.setEnabled(true);
        clearNum.setEnabled(true);
        saveCnt.setEnabled(true);
        nameSave.setEnabled(true);

        recName.setTextColor(Color.parseColor("#f6f6f6"));
        button_save.setTextColor(Color.parseColor("#f6f6f6"));
        button_clear.setTextColor(Color.parseColor("#f6f6f6"));

        nameSave.setBackground(null);
        nameSave.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.appeee));

        saveCnt.setImageResource(0);
        saveCnt.setImageResource(R.drawable.img_buttons);

        clearNum.setImageResource(0);
        clearNum.setImageResource(R.drawable.img_buttons);


    }

    private static Method getWifiManagerMethod(final String methodName, final WifiManager wifiManager) {
        final Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

    protected String encode (String word)
    {
       String out = word.replace("0","a").replace("1", "b").replace("2","c").replace("3","d").replace("4","e").replace("5","f").replace("6","g").replace("7","h").replace("8","i").replace("9","j");
        return out;
    }
    protected String decode (String word)
    {
        String out = word.replace("a","0").replace("b", "1").replace("c","2").replace("d","3").replace("e","4").replace("f","5").replace("g","6").replace("h", "7").replace("i","8").replace("j","9");
        return out;
    }



//    protected String decode2 (String word)
//    {
//        String out = word.replace("C","6").replace("Y","9").replace("T", "3").replace("A","4").replace("F","8").replace("1","5").replace("8","9").replace("B","6").replace("h", "7").replace("i","8").replace("j","9");
//        return out;
//    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
            }
        }
        catch (Exception e){}
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                }
            }
        }
        //textPhone.clearFocus();
        //bt.requestFocus();
        return super.dispatchTouchEvent( event );

    }

    @Override
    protected void onResume() {
        super.onResume();

    //saveWepConfig();

        /*
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(35000, 512, 0);
        dispatcher.addAudioProcessor(new LowPassFS(7000, 35000));
        dispatcher.addAudioProcessor(new HighPass(7100, 35000));

        PitchDetectionHandler pdh = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult result,AudioEvent e) {
                final float pitchInHz = result.getPitch();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                       text.setText(String.valueOf(pitchInHz));
                    }
                });
            }
        };
        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 35000, 512, pdh);
        dispatcher.addAudioProcessor(p);
        new Thread(dispatcher,"Audio Dispatcher").start();
        */

//        // Use a new tread as this can take a while
//        for (int i=0;i<10;i++) {
//            final Thread thread = new Thread(new Runnable() {
//                public void run() {
//                    genTone();
//                    handler.post(new Runnable() {
//
//                        public void run() {
//                            playSound();
//                        }
//                    });
//                }
//            });
//            thread.start();
//
//        }


    }

//    void genTone(){
//        // fill out the array
//        for (int i = 0; i < numSamples; ++i) {
//            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
//        }
//
//        // convert to 16 bit pcm sound array
//        // assumes the sample buffer is normalised.
//        int idx = 0;
//        for (final double dVal : sample) {
//
//            // scale to maximum amplitude
//            final short val = (short) ((dVal * 5000));
//            // in 16 bit wav PCM, first byte is the low order byte
//            generatedSnd[idx++] = (byte) (val & 0x00ff);
//            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
//
//        }
//    }
//
//    void playSound(){
//        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
//                sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
//                AudioFormat.ENCODING_PCM_16BIT, (int) (numSamples/10),
//                AudioTrack.MODE_STATIC);
//        audioTrack.write(generatedSnd, 0, generatedSnd.length);
//        audioTrack.play();
//        try {
//            audioTrack.wait(100);
//        }
//        catch (Throwable e){}
//
//    }

    void saveWepConfig()
    {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"SSID_NAME\""; //IMP! This should be in Quotes!!
        wc.hiddenSSID = true;
        wc.status = WifiConfiguration.Status.DISABLED;
        wc.priority = 40;
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

        wc.wepKeys[0] = "\"aaabbb1234\""; //This is the WEP Password
        wc.wepTxKeyIndex = 0;

        WifiManager  wifiManag = (WifiManager) this.getSystemService(WIFI_SERVICE);
        boolean res1 = wifiManag.setWifiEnabled(true);
        int res = wifi.addNetwork(wc);
        Log.d("WifiPreference", "add Network returned " + res);
        boolean es = wifi.saveConfiguration();
        Log.d("WifiPreference", "saveConfiguration returned " + es );
        boolean b = wifi.enableNetwork(res, true);
        Log.d("WifiPreference", "enableNetwork returned " + b );

    }

    public  class ExecuteAsRootBase
    {
        public boolean canRunRootCommands()
        {
            boolean retval = false;
            Process suProcess;

            try
            {
                suProcess = Runtime.getRuntime().exec("su");

                DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());
                DataInputStream osRes = new DataInputStream(suProcess.getInputStream());

                if (null != os && null != osRes)
                {
                    // Getting the id of the current user to check if this is root
                    os.writeBytes("id\n");
                    os.flush();

                    String currUid = osRes.readLine();
                    boolean exitSu = false;
                    if (null == currUid)
                    {
                        retval = false;
                        exitSu = false;
                        Log.d("ROOT", "Can't get root access or denied by user");
                    }
                    else if (true == currUid.contains("uid=0"))
                    {
                        retval = true;
                        exitSu = true;
                        Log.d("ROOT", "Root access granted");
                    }
                    else
                    {
                        retval = false;
                        exitSu = true;
                        Log.d("ROOT", "Root access rejected: " + currUid);
                    }

                    if (exitSu)
                    {
                        os.writeBytes("exit\n");
                        os.flush();
                    }
                }
            }
            catch (Exception e)
            {
                // Can't get root !
                // Probably broken pipe exception on trying to write to output stream (os) after su failed, meaning that the device is not rooted

                retval = false;
                Log.d("ROOT", "Root access rejected [" + e.getClass().getName() + "] : " + e.getMessage());
            }

            return retval;
        }

        public final boolean execute()
        {
            boolean retval = false;

            try
            {
                ArrayList<String> commands = getCommandsToExecute();
                if (null != commands && commands.size() > 0)
                {
                    Process suProcess = Runtime.getRuntime().exec("su");

                    DataOutputStream os = new DataOutputStream(suProcess.getOutputStream());

                    // Execute commands that require root access
                    for (String currCommand : commands)
                    {
                        os.writeBytes(currCommand + "\n");
                        os.flush();
                    }

                    os.writeBytes("exit\n");
                    os.flush();

                    try
                    {
                        int suProcessRetval = suProcess.waitFor();
                        if (255 != suProcessRetval)
                        {
                            // Root access granted
                            retval = true;
                        }
                        else
                        {
                            // Root access denied
                            retval = false;
                        }
                    }
                    catch (Exception ex)
                    {
                        Log.e("ROOT", "Error executing root action", ex);
                    }
                }
            }
            catch (IOException ex)
            {
                Log.w("ROOT", "Can't get root access", ex);
            }
            catch (SecurityException ex)
            {
                Log.w("ROOT", "Can't get root access", ex);
            }
            catch (Exception ex)
            {
                Log.w("ROOT", "Error executing internal operation", ex);
            }

            return retval;
        }
        protected ArrayList<String> getCommandsToExecute(){
            ArrayList<String> ar=new ArrayList<>();
            return ar;}
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CHANGE_WIFI_STATE);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    private boolean checkPermissionContact(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_CONTACTS);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    private boolean checkPermissionWrite(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_SETTINGS);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }



    private void requestPermission(){

        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CHANGE_WIFI_STATE);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CHANGE_WIFI_STATE},1);

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CHANGE_WIFI_STATE)){

            Toast.makeText(getApplicationContext(),"Network and write settings permissions are required",Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CHANGE_WIFI_STATE},PERMISSION_REQUEST_CODE);
        }
    }

    private void requestPermissionWrite(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_SETTINGS)){

           // Toast.makeText(getApplicationContext(),"Write settings permissions are required",Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_SETTINGS},PERMISSION_REQUEST_CODE_WRITE);
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {


            case PERMISSION_MULTIPLE:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_SETTINGS, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    // if ( (ActivityCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)&&
                    // (ActivityCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED))


                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "All permissions need to be granted", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
        }
    }


}
