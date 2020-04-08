package net.cararea.inspector.obd;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.SpacesOffCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;

import net.cararea.inspector.R;
import net.cararea.inspector.TripRecord;
import net.cararea.inspector.utils.L;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import androidx.annotation.Nullable;

public class ObdService extends IntentService implements ConstantKeys {
    private static boolean serviceRunning;
    private static boolean obdConnected;

    //private NotificationCompat.Builder mNotificationBuilder;
    //private NotificationManager mNotificationManager;
    //private static final int NOTIFICATION_ID = 101;
    //private int mLastNotificationType;

    public boolean readFaultCode = true;
    private final IBinder binder = new LocalBinder();

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private boolean isConnected;

    private boolean statusOk;
    private Intent intent = new Intent(ACTION_OBD_REAL_TIME_DATA);

    public ObdService() {
        super("ObdReaderService");
        L.i("ObdReaderService");
    }

    public static void setServiceRunning(boolean running) {
        serviceRunning = running;
    }

    public static boolean isServiceRunning() {
        return serviceRunning;
    }

    public static void setObdConnected(boolean connected) {
        obdConnected = connected;
    }

    public static boolean isObdConnected() {
        return obdConnected;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        L.i("onHandleIntent" + "Thread is :: " + Thread.currentThread().getId());

        //setUpAsForeground();
        if (initiateConnection()) {
            if (!isBluetoothEnabled()) {
                enableBluetooth();
            }
            findObdDevicesAndConnect();
        }

        //mNotificationManager.cancel(NOTIFICATION_ID);
        serviceRunning = false;
        obdConnected = false;
        TripRecord.getInstance().endTrip();
    }

    private void findObdDevicesAndConnect() {
        if (isConnected) {
            executeCommands();
        } else {
            findPairedDevices();
        }

        if (serviceRunning) {
            L.i("findObdDevicesAndConnect");
            findObdDevicesAndConnect();
        }
    }

    private void findPairedDevices() {
        while (!isConnected && serviceRunning) {
            if (bluetoothAdapter != null) {
                boolean deviceFound = false;

                Set<BluetoothDevice> bluetoothDevices = bluetoothAdapter.getBondedDevices();
                for (BluetoothDevice device : bluetoothDevices) {
                    if (device != null) {
                        String name = device.getName();
                        if (name != null && (name.toUpperCase().contains("OBD") ||
                                name.toUpperCase().contains("V-LINK"))) {
                            try {
                                connectToDevice(device);
                            } catch (Exception e) {
                                L.i("connectToDevice return Exception :: " + e.getMessage());
                            }
                            deviceFound = true;
                            break;
                        }
                    }
                }

                if (!deviceFound) {
                    /*
                    if (mLastNotificationType != DEVICE_NOT_PAIRED) {
                        mLastNotificationType = DEVICE_NOT_PAIRED;
                        updateNotification(getString(R.string.waiting_for_obd));
                    }
                    */
                    sendBroadcast(ACTION_OBD_CONNECTION_STATUS, getString(R.string.obd_waiting));
                }
            }
        }
    }

    public void connectToDevice(final BluetoothDevice device) {
        try {
            socket = device.createInsecureRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (Exception e) {
            L.i("createInsecureRfcommSocket failed");
            closeSocket();
        }

        if (socket != null) {
            try {
                bluetoothAdapter.cancelDiscovery();
                socket.connect();
                L.i("Socket connected");
            } catch (Exception e) {
                L.i("Socket connection  exception :: " + e.getMessage());
                closeSocket();
            }

            boolean isSocketConnected = socket.isConnected();
            if (isSocketConnected) {
                try {
                    /*
                    if (mLastNotificationType != INIT_OBD) {
                        mLastNotificationType = INIT_OBD;
                        updateNotification(getString(R.string.connecting_to_ecu));
                    }
                    */
                    L.i("Executing reset command in new Thread :: " +
                            Thread.currentThread().getId());

                    final Thread newThread = new Thread(() -> {
                        try {
                            statusOk = false;
                            new ObdResetCommand().run(socket.getInputStream(),
                                    socket.getOutputStream());
                            new EchoOffCommand().run(socket.getInputStream(),
                                    socket.getOutputStream());
                            new LineFeedOffCommand().run(socket.getInputStream(),
                                    socket.getOutputStream());
                            new SpacesOffCommand().run(socket.getInputStream(),
                                    socket.getOutputStream());
                            new SpacesOffCommand().run(socket.getInputStream(),
                                    socket.getOutputStream());
                            new TimeoutCommand(125).run(socket.getInputStream(),
                                    socket.getOutputStream());
                            //updateNotification(getString(R.string.searching_protocol));
                            new SelectProtocolCommand(ObdProtocols.AUTO).run(
                                    socket.getInputStream(), socket.getOutputStream());
                            new EchoOffCommand().run(socket.getInputStream(),
                                    socket.getOutputStream());
                            //updateNotification(getString(R.string.searching_supported_sensor));
                            statusOk = true;
                            //checkPid0To20(true);
                        } catch (Exception e) {
                            statusOk = false;
                            L.i("In new thread reset command  exception :: " +
                                    e.getMessage());
                        }
                    });
                    newThread.start();
                    newThread.join(15000);
                    L.i("Thread wake to check reset command status  i.e  :: " +
                            Thread.currentThread().getId() + ",  statusOk :: " +
                            statusOk);
                    isSocketConnected = statusOk;
                } catch (Exception e) {
                    L.i(" reset command Exception  :: " + e.getMessage());
                    isSocketConnected = false;
                }
            }

            if (socket != null && socket.isConnected() && isSocketConnected) {
                setConnected();
                /*
                if (mLastNotificationType != OBD_CONNECTED) {
                    mLastNotificationType = OBD_CONNECTED;
                    updateNotification(getString(R.string.connected_ok));
                }
                */
            }
        }
    }

    private void executeCommands() {
        L.i("executing commands thread is :: " + Thread.currentThread().getId());
        TripRecord tripRecord = TripRecord.getInstance();
        List<ObdCommand> commands = AvailableCommands.getCommands();

        int count = 0;
        while (socket != null && socket.isConnected() && commands.size() > count && isConnected &&
                serviceRunning) {
            ObdCommand command = commands.get(count);
            try {
                L.i("command run :: " + command.getName());
                command.run(socket.getInputStream(), socket.getOutputStream());
                L.i("result is :: " + command.getFormattedResult() + " :: name is :: " + command.getName());
                tripRecord.updateTrip(command.getName(), command);
                if (readFaultCode) {
                    try {
                        TroubleCodesCommand troubleCodesCommand = new TroubleCodesCommand();
                        troubleCodesCommand.run(socket.getInputStream(), socket.getOutputStream());
                        tripRecord.updateTrip(troubleCodesCommand.getName(), troubleCodesCommand);
                        readFaultCode = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (intent == null)
                    intent = new Intent(ACTION_OBD_REAL_TIME_DATA);
                sendBroadcast(intent);
            } catch (Exception e) {
                L.i("execute command Exception  :: " + e.getMessage());

                if (!TextUtils.isEmpty(e.getMessage()) && (e.getMessage().equals("Broken pipe") || e.getMessage().equals("Connection reset by peer"))) {
                    L.i("command Exception  :: " + e.getMessage());
                    setDisconnected();
                    /*
                    if (mLastNotificationType != OBD_NOT_RESPONDING) {
                        mLastNotificationType = OBD_NOT_RESPONDING;
                        updateNotification(getString(R.string.obd2_adapter_not_responding));
                    }
                    */
                }
            }
            count++;
            if (count == commands.size()) {
                count = 0;
            }
        }
        // Exit from the loop means no connection
        isConnected = false;
    }

    private void sendBroadcast(final String action, String data) {
        final Intent intent = new Intent(action);
        intent.putExtra(INTENT_OBD_EXTRA_DATA, data);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //fetchLocation();
        serviceRunning = true;
        obdConnected = false;
        //mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        L.i("Service Created :: ");
    }

    protected boolean initiateConnection() {
        boolean isBlueToothSupported = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        boolean isInitialized = initialize();

        if (!isBlueToothSupported || !isInitialized) {
            Toast.makeText(this, getString(R.string.bluetooth_not_supported),
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean initialize() {
        if (bluetoothManager == null) {
            bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                return false;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return bluetoothAdapter != null;
    }

    /*
    // Display foreground service notification.
    private void setUpAsForeground() {

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, NotificationDummyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP
                ), 0);


        mNotificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.waiting_for_obd))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent);

        startForeground(NOTIFICATION_ID, mNotificationBuilder.build());
    }
    */

    /*
    private void updateNotification(String text) {
        mNotificationBuilder.setContentText(text);
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }
    */

    /*
    public void updateNotificationString() {
        String text = "";
        if (mLastNotificationType == OBD_CONNECTED) {
            text = getString(R.string.connected_ok);
        } else if (mLastNotificationType == OBD_NOT_RESPONDING) {
            text = getString(R.string.obd2_adapter_not_responding);
        }
        if (mLastNotificationType == DEVICE_NOT_PAIRED) {
            text = getString(R.string.waiting_for_obd);
        }
        mNotificationBuilder.setContentTitle(getString(R.string.app_name));
        mNotificationBuilder.setContentText(text);
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }
    */

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.i("service onDestroy");
        //mNotificationManager.cancel(NOTIFICATION_ID);
        closeSocket();
        serviceRunning = false;
        obdConnected = false;
        TripRecord.getInstance().endTrip();
    }

    private void closeSocket() {
        L.i("socket closed :: ");
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                L.i("socket closing failed :: ");
            }
        }
    }

    public boolean isBluetoothEnabled() {
        if (bluetoothAdapter == null)
            return false;
        return bluetoothAdapter.isEnabled();

    }

    public void enableBluetooth() {
        if (bluetoothAdapter != null)
            bluetoothAdapter.enable();
    }

    @Override
    public boolean stopService(Intent name) {
        obdConnected = false;
        return super.stopService(name);
    }

    public void setDisconnected() {
        /*
        if (mLastNotificationType != OBD_NOT_RESPONDING) {
            mLastNotificationType = OBD_NOT_RESPONDING;
            updateNotification(getString(R.string.obd2_adapter_not_responding));
        }
        */
        obdConnected = false;
        isConnected = false;
        closeSocket();
        L.i("socket disconnected :: ");
        //broadcastUpdate(ACTION_OBD_DISCONNECTED);
        sendBroadcast(ACTION_OBD_CONNECTION_STATUS, getString(R.string.obd_disconnected));
    }

    private void setConnected() {
        obdConnected = true;
        isConnected = true;
        //sendBroadcast(ACTION_OBD_CONNECTED, String.valueOf(isFromBle));
        sendBroadcast(ACTION_OBD_CONNECTION_STATUS, getString(R.string.obd_connected));
    }

    public class LocalBinder extends Binder {
        public ObdService getService() {
            return ObdService.this;
        }
    }
}

