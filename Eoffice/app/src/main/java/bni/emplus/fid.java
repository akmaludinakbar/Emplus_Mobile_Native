package bni.emplus;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by ZulfahPermataIlliyin on 10/17/2016.
 */
public class fid extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";


    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken =      FirebaseInstanceId.getInstance().getToken();

    }

    // fungsi untuk kirim token ke server kita ( opsional )
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
    }
}
