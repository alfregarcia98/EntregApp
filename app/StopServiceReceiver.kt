import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


public class TestReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equalsIgnoreCase("STOP_TEST_SERVICE")) {
            if (isMyServiceRunning(context, TestService.class)) {
                    Toast.makeText(context,"Service is running!! Stopping...",Toast.LENGTH_LONG).show();
                    context.stopService(new Intent(context, TestService.class));
                }
            else {
                Toast.makeText(context,"Service not running",Toast.LENGTH_LONG).show();
            }
        }

    }
    private boolean isMyServiceRunning(Context context,Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.getName().equals(service.service.getClassName())) {
            return true;
        }
    }
        return false;
    }