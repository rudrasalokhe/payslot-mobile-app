package com.docdirect.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.docdirect.app.data.payment.PaymentManager
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PaymentManager.init(applicationContext)
        com.docdirect.app.data.firebase.FirebaseSyncManager.init(applicationContext)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                DocDirectApp()
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        val txnId = razorpayPaymentId ?: "PAY_${System.currentTimeMillis()}"
        PaymentManager.onRazorpaySuccess(txnId, paymentData)
    }

    override fun onPaymentError(errorCode: Int, response: String?, paymentData: PaymentData?) {
        PaymentManager.onRazorpayError(errorCode, response, paymentData)
    }
}
