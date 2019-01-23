package net.gotev.sipservice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.pjsip.pjsua2.pjsip_status_code;

import java.util.ArrayList;

public class fofe extends Activity {


private int port= 5061;
private  String realm="172.16.1.197";
private  String name="301";
private  String password="poseidon";
private  String numberToCall="sip:300@172.16.1.197";




    private static final String KEY_SIP_ACCOUNT = "sip_account";
    private SipAccountData mSipAccount;






    private BroadcastEventReceiver sipEvents = new BroadcastEventReceiver() {

        @Override
        public void onRegistration(String accountID, pjsip_status_code registrationStateCode) {
            if (registrationStateCode == pjsip_status_code.PJSIP_SC_OK) {
                Toast.makeText(fofe.this, "Registered", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(fofe.this, "Unregistered", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onReceivedCodecPriorities(ArrayList<CodecPriority> codecPriorities) {
            for (CodecPriority codec : codecPriorities) {
                if (codec.getCodecName().startsWith("PCM")) {
                    codec.setPriority(CodecPriority.PRIORITY_MAX);
                } else {
                    codec.setPriority(CodecPriority.PRIORITY_DISABLED);
                }
            }
            SipServiceCommand.setCodecPriorities(fofe.this, codecPriorities);
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mSipAccount != null) {
            outState.putParcelable(KEY_SIP_ACCOUNT, mSipAccount);
        }

        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fofe);



        Logger.setLogLevel(Logger.LogLevel.DEBUG);

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_SIP_ACCOUNT)) {
            mSipAccount = savedInstanceState.getParcelable(KEY_SIP_ACCOUNT);
        }

        onRegister();
    }




    @Override
    protected void onPause() {
        super.onPause();
        sipEvents.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sipEvents.register(this);
    }
    public void onRegister() {
        mSipAccount = new SipAccountData();
/*
        if (!mSipServer.getText().toString().isEmpty()) {
            mSipAccount.setHost(mSipServer.getText().toString())
                    .setPort(Integer.valueOf(mSipPort.getText().toString()))
                    .setTcpTransport(true)
                    .setUsername(mUsername.getText().toString())
                    .setPassword(mPassword.getText().toString())
                    .setRealm(mRealm.getText().toString());  */
        //   } else {
        mSipAccount.setHost("172.16.1.197")
                .setPort(port)
                .setTcpTransport(true)
                .setUsername(name)
                .setPassword(password)
                .setRealm(realm)
                .setContactUriParams("")




        ;
        //     }
        Log.e("test","test1");
        SipServiceCommand.setAccount(this, mSipAccount);
        SipServiceCommand.getCodecPriorities(this);
    }


    public void onCall() {
        if (mSipAccount == null) {
            Toast.makeText(this, "Add an account and register it first", Toast.LENGTH_LONG).show();
            return;
        }

        String number = numberToCall;

        if (number.isEmpty()) {
            number = "301";
            //Toast.makeText(this, "Provide a number to call", Toast.LENGTH_SHORT).show();
            //return;
        }

        SipServiceCommand.makeCall(this, mSipAccount.getIdUri(), number);
    }


    public void onTerminate() {
        SipServiceCommand.hangUpActiveCalls(this, mSipAccount.getIdUri());
    }



}
