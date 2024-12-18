package com.example.messageandfiletransferviawifi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class Server extends AppCompatActivity {

    public ServerSocket serverSocket;
    Thread Thread1 = null;
    TextView tvIP, tvPort,filepath;
    TextView tvmessages;
    EditText etMessage;
    Button send,FILES,showpath;
    public static String SERVER_IP = "";
    public static final int SERVER_PORT = 8080;
    String message;
    public String path;
    int flag=0;
    byte[] bytes;
    File file_get;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        tvIP = findViewById(R.id.textView4);
        tvPort = findViewById(R.id.textView5);
        etMessage = findViewById(R.id.editTextTextPersonName);
        tvmessages = findViewById(R.id.textView6);
        send = findViewById(R.id.button6);
        SERVER_IP=getLocalIpAddress();
        FILES = findViewById(R.id.button9);
        filepath = findViewById(R.id.textView13);
        showpath = findViewById(R.id.button12);
        Thread1 = new Thread((new Thread1()));
        Thread1.start();
        path="";




        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                message = etMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    new Thread(new Thread3(message,bytes)).start();
                }
                if(flag==1) {
                    Toast.makeText(Server.this, "Transferring", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < 1000; i++) ;
                    Toast.makeText(Server.this, "Failed", Toast.LENGTH_SHORT).show();

                }
//                if(!path.isEmpty()){
//                    new Thread(new Thread3(message,bytes)).start();
//                }
            }
        });
        FILES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent file = new Intent(Server.this,FileActivity.class);
                startActivity(file);


            }
        });
        showpath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                path = intent.getStringExtra("file");
                filepath.setText("Path: "+path);
                flag=1;
//                file_get = new File(Environment.getExternalStorageDirectory(),path);
//                bytes = new byte[(int) file_get.length()];


            }
        });


    }
    public static String getLocalIpAddress() {
        String ip="";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ip+=inetAddress.getHostAddress()+""+"\n";
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return ip;
    }
    private DataOutputStream output;
    private DataInputStream input;
    //    private BufferedInputStream f_input;
//    private ObjectOutputStream f_output;
    class Thread1 implements Runnable {

        @Override
        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tvmessages.setText("Not Connected");
                        tvIP.setText(SERVER_IP);
                        tvPort.setText("Port :"+(SERVER_PORT+""));

                    }
                });
                try {
                    socket= serverSocket.accept();
                    socket.setSoTimeout(300000);
                    output = new DataOutputStream(socket.getOutputStream());
                    input=new DataInputStream(socket.getInputStream());
//                    f_input = new BufferedInputStream(new FileInputStream(file_get));
//                    f_output = new ObjectOutputStream(socket.getOutputStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvmessages.setText("Connected\n");
                        }
                    });
                    new Thread(new Thread2(socket)).start();

                }catch (IOException e) {
                    e.printStackTrace();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class Thread2 implements Runnable{
        Socket socket;
        Thread2(Socket socket){
            this.socket=socket;
        }
        @Override
        public void run() {
            while (true){
                try {
                    final String message = input.readUTF();
//                        try {
//                            f_input.read(bytes, 0, bytes.length);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }


                    if(message!=null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvmessages.append("Client: "+message+"\n");
                            }
                        });
                    }
                    else{
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    class Thread3 implements Runnable{
        private String message;
        private byte[] bytes;
        Thread3(String message, byte[] bytes){
            this.message= message;
            this.bytes = bytes;
        }
        @Override
        public void run() {
            try {
                output.writeUTF(message);

//                    f_output.writeObject(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                output.flush();
//                f_output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvmessages.append("Server: "+message+"\n");
                    etMessage.setText("");
                }
            });
        }
    }

}
