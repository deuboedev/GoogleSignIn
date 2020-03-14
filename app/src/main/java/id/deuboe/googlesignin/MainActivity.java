package id.deuboe.googlesignin;

import static id.deuboe.googlesignin.R.id.image_account;
import static id.deuboe.googlesignin.R.id.text_email;
import static id.deuboe.googlesignin.R.id.text_name;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

  private AppCompatTextView textName, textEmail;
  private CircleImageView photoUser;

  private GoogleApiClient mGoogleApiClient;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    init();
    GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
    updateUI(signInAccount);
  }

  private void init() {
    textName = findViewById(text_name);
    textEmail = findViewById(text_email);
    photoUser = findViewById(image_account);
  }

  private void updateUI(GoogleSignInAccount account) {
    if (account != null) {
      textName.setText(String.format("%s, %s", getText(R.string.halo), account.getDisplayName()));
      textEmail.setText(account.getEmail());
      String mPhotoUrl = Objects.requireNonNull(account.getPhotoUrl()).toString();
      Glide.with(this).load(mPhotoUrl).into(photoUser);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflater = getMenuInflater();
    menuInflater.inflate(R.menu.menu_item, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.sign_out_menu) {
      FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
      mFirebaseAuth.signOut();
      startActivity(new Intent(this, SignInActivity.class));
      Toast.makeText(this, "Sampai Jumpa", Toast.LENGTH_SHORT).show();
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
