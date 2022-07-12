package ph.gcash.cadet.bernabe.cambarihan.coldpopcorn

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import ph.gcash.cadet.bernabe.cambarihan.coldpopcorn.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            var gotoMainActivity = Intent(this, MainActivity::class.java)
            startActivity(gotoMainActivity)
            finish()
        }, 3000)
    }
}