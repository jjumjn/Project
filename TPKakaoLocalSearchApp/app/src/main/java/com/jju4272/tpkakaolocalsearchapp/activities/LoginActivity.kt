package com.jju4272.tpkakaolocalsearchapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jju4272.tpkakaolocalsearchapp.R
import com.jju4272.tpkakaolocalsearchapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    //1.
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //2.
        setContentView(binding.root)

        //3. 둘러보기 글씨 클릭으로 로그인 없이 메인 화면으로 이동
        binding.tvGo.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        //4. 회원가입 버튼 클릭
        binding.btnSingup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        //5. 이메일 로그인 버튼 클릭
        binding.btnEmailLogin.setOnClickListener {
            startActivity(Intent(this, EmailLoginActivity::class.java))
        }
    }
}