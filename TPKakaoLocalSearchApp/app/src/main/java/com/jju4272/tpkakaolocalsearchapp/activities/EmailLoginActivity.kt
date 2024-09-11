package com.jju4272.tpkakaolocalsearchapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jju4272.tpkakaolocalsearchapp.G
import com.jju4272.tpkakaolocalsearchapp.R
import com.jju4272.tpkakaolocalsearchapp.data.UserAccount
import com.jju4272.tpkakaolocalsearchapp.databinding.ActivityEmailLoginBinding

class EmailLoginActivity : AppCompatActivity() {

    //1.
    val binding by lazy { ActivityEmailLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //2.
        setContentView(binding.root)

        //3. 툴바의 뒤로가기 버튼 반응하기
        binding.toolbar.setNavigationOnClickListener { finish() }
        //4. 로그인 버튼 반응하기
        binding.btnSignin.setOnClickListener { clickSignin() }

    }

    //5.
    private fun clickSignin(){

        var email = binding.etEmail.text.toString()
        var password = binding.etPassword.text.toString()

        //6. firestore DB에서 이메일과 비밀번호가 맞는지 확인 - "account" 컬렉션을 참조
        val accountRef = Firebase.firestore.collection("account")
        accountRef.whereEqualTo("email", email).whereEqualTo("password", password).get()
            .addOnSuccessListener {
                if (it.documents.size>0){
                    // 로그인 성공
                    val id:String = it.documents[0].id
                    G.userAccount = UserAccount(id, email, "email")

                    // 로그인 성공했으니.. 메인액티비티로 이동
                    // 기존 task의 모든 액티비티를 제거하고 새로운 task로 시작
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)


                }else{
                    AlertDialog.Builder(this).setMessage("이메일과 비밀번호 다시 확인").create().show()
                    binding.etEmail.requestFocus()
                    binding.etEmail.selectAll()
                }

            }.addOnFailureListener {
                Toast.makeText(this, "서버오류 : ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }

}