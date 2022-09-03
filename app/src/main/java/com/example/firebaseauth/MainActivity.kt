package com.example.firebaseauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    private val TAG = "GoogleActivity"
    private lateinit var editTextEmail : EditText
    private lateinit var editTextPassword : EditText
    private lateinit var callbackManager : CallbackManager //콜백 전역변수
    // [START declare_auth]
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser : auth?.currentUser
    // [END declare_auth]
    private lateinit var mAuthListener : FirebaseAuth.AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        auth = Firebase.getInstance()
        // [START config_signin]
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // [END config_signin]

        //구글로그인
        val btn: SignInButton = findViewById(R.id.Login_Button)
        btn.setOnClickListener {
            googleSignInClient = GoogleSignIn.getClient(this, gso)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        // [END signin]

        //일반이메일 로그인(normal email login)
        editTextEmail = findViewById(R.id.editTextTextEmailAddress)
        editTextPassword = findViewById(R.id.editTextTextPassword)

        val emailloginbtn : Button = findViewById(R.id.Login1_button)
        emailloginbtn.setOnClickListener {
            createAccount(editTextEmail.text.toString(), editTextPassword.text.toString())
        }
        //끝

        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()
        //페이스북로그인(facebook login)
        facebook_login_button.setReadPermissions("email", "public_profile")
        facebook_login_button.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })

       
        mAuthListener = FirebaseAuth.AuthStateListener (object : FirebaseAuth.AuthStateListener, (FirebaseAuth) -> Unit {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = p0.currentUser
                if (user != null) {
                    val intent = Intent(this@MainActivity, HomeActivity::class.java) //원하는 클래스로 데이터 받기
                    startActivity(intent)
                    finish()
                } else {

                }
            }

            override fun invoke(p1: FirebaseAuth) {
            //자주 호출해야하는 함수를 객체+이름()으로 호출가능
               
            }
        })/*{
                val user = it.currentUser
                if (user != null) {
                    val intent = Intent(this, HomeActivity::class.java) //원하는 클래스로 데이터 받기
                    startActivity(intent)
                } else {

                }
        }*/
}

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseAuth.getInstance().addAuthStateListener{mAuthListener}
    }

    public override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener{mAuthListener}
        }
    }

    //facabook 엑세스 토큰받기
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    Toast.makeText(baseContext, "facebook 회원가입 성공",
                        Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "facebook 회원가입 실패",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }


    private fun createAccount(email: String, password: String) { //6월27일 추가
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(baseContext, "회원가입 성공",
                        Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    loginUser(email, password)
                    // If sign in fails, display a message to the user.
                    /*Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()*/
                    //updateUI(null)
                }
            }
        // [END create_user_with_email]
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "이메일 로그인 완료", task.exception)
                    Toast.makeText(baseContext, "이메일로그인완료",
                        Toast.LENGTH_SHORT).show()
                    mainpage(task.result?.user)
                    //여기서 다음 화면으로 넘어가는 코드를 넣을 시
                    //자동 로그인 기능, 다른 소셜 로그인 기능과 충돌할 수 있으니
                    //그런 코드 x
                    //여기말고 firebase인증 부분에서 intent를 넣자
                    auth = FirebaseAuth.getInstance()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "로그인 실패",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }
    

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }
    // [START auth_with_google]
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    // [END auth_with_google]

    private fun updateUI(user: FirebaseUser?) {

    }
    private fun signOut() {
        Firebase.auth.signOut()
        googleSignInClient!!.signOut()
    }
}
