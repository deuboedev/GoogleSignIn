package id.deuboe.googlesignin;

import static com.google.android.gms.common.api.GoogleApiClient.*;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity implements OnConnectionFailedListener {

  private static final String TAG = "SignInActivity";
  private static final int RC_SIGN_IN = 9001;

  private GoogleApiClient mGoogleApiClient;

  private FirebaseAuth mFirebaseAuth;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in);

    SignInButton mSignInButton = findViewById(R.id.button_sign_in);
    mSignInButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        signIn();
      }
    });

    GoogleSignInOptions googleSignInOptions =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

    mGoogleApiClient = new Builder(this)
        .enableAutoManage(this, this)
        .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
        .build();

    mFirebaseAuth = FirebaseAuth.getInstance();
  }

  private void signIn() {
    Intent intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(intent, RC_SIGN_IN);
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Toast.makeText(this, "Sign In failed!", Toast.LENGTH_SHORT).show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      if (result.isSuccess()) {
        // Google Sign-In was successful, authenticate with Firebase
        GoogleSignInAccount account = result.getSignInAccount();
        authWithGoogle(Objects.requireNonNull(account));
      }
    }
  }

  private void authWithGoogle(GoogleSignInAccount signInAccount) {
    Log.d(TAG, "authWithGoogle: " + signInAccount.getId());
    AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
    mFirebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (!task.isSuccessful()) {
              Log.d(TAG, "Hah");
            } else {
              Log.d(TAG, "It's work");
            }
            Log.d(TAG, "signInWithCredential: onComplete: " + task.isSuccessful());

            if (!task.isSuccessful()) {
              Log.w(TAG, "signInWithCredential", task.getException());
              Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT)
                  .show();
            } else {
              Log.d(TAG, "It's work");
              startActivity(new Intent(SignInActivity.this, MainActivity.class));
              finish();
            }
          }
        });
  }
}
