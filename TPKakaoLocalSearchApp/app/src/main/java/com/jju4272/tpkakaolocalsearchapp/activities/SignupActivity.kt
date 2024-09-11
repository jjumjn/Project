package com.jju4272.tpkakaolocalsearchapp.activities

import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jju4272.tpkakaolocalsearchapp.R
import com.jju4272.tpkakaolocalsearchapp.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    //1.
    val binding by lazy { ActivitySignupBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //2.
        setContentView(binding.root)

        //3. 뒤로가기 버튼 클릭
        binding.toolbar.setNavigationOnClickListener { finish() }

        //4. 가입하기 버튼 클릭
        binding.btnSingup.setOnClickListener { clickSignup() }

    }
    private fun clickSignup(){
        // firebase firestore DB에 사용자 정보 저장하기 - firebase사이트 작업
        //5.
        var email = binding.etEmail.text.toString()
        var password = binding.etPassword.text.toString()
        var passwordConfirm = binding.etPasswordConfirm.text.toString()

        //6. 패스워드와 패스워드확인의 글씨가 같은지 검사
        if (password != passwordConfirm) {
            AlertDialog.Builder(this).setMessage("비밀번호 불일치").create().show()
            binding.etPasswordConfirm.selectAll()
            return
        }

        //7. firestore DB에 데이터 저장 - "account" 컬렉션에 회원정보 저장
        val accountRef:CollectionReference = Firebase.firestore.collection("account")
        // 중복된 이메일 정보가 있는지 확인 후 저장하기
        accountRef.whereEqualTo("email",email).get().addOnSuccessListener {
            // 같은 값을 가진 Document가 존재한다면 같은 이메일이 존재하는 것임
            if (it.documents.size > 0 ){
                AlertDialog.Builder(this).setMessage("중복된 이메일이 있습니다. 다른 이메일 입력해주세요").create().show()
                binding.etEmail.requestFocus()
                binding.etEmail.selectAll()
            }else{
                val user:MutableMap<String, String> = mutableMapOf()
                user.put("email", email)
                user ["password"] = password
                user ["type"] = "email"

                accountRef.document().set(user).addOnSuccessListener {
                    AlertDialog.Builder(this)
                        .setMessage("축하합니다\n 회원가입 완료")
                        .setPositiveButton("확인",object : OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                finish()
                            }
                        }).create().show()

                }.addOnFailureListener {
                    Toast.makeText(this, "회원가입 오류발생 다시시도", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}