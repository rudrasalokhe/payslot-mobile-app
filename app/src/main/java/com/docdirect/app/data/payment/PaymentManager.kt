package com.docdirect.app.data.payment

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.razorpay.Checkout
import com.razorpay.PaymentData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.util.Locale

sealed class PaymentState {
    object Idle : PaymentState()
    object Processing : PaymentState()
    data class Success(
        val transactionId: String,
        val paymentMethod: String,
        val orderId: String? = null
    ) : PaymentState()
    data class Error(
        val code: Int,
        val description: String
    ) : PaymentState()
    object Cancelled : PaymentState()
}

object PaymentManager {

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    // Default test key ID for Razorpay
    private const val DEFAULT_RAZORPAY_KEY = PaymentConfig.KEY_ID

    fun init(context: Context) {
        try {
            Checkout.preload(context.applicationContext)
        } catch (_: Exception) {
            // Ignore if preload encounters issues in test environment
        }
    }

    fun reset() {
        _paymentState.value = PaymentState.Idle
    }

    fun findActivity(context: Context): Activity? {
        var ctx: Context? = context
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }

    fun startRazorpayPayment(
        context: Context,
        amountInRupees: Double,
        appointmentId: String,
        doctorName: String,
        patientName: String,
        patientEmail: String,
        patientPhone: String,
        keyId: String = DEFAULT_RAZORPAY_KEY
    ) {
        val act = findActivity(context)
        if (act == null) {
            _paymentState.value = PaymentState.Error(-1, "Unable to launch Razorpay: Activity context not found.")
            return
        }
        startRazorpayPayment(act, amountInRupees, appointmentId, doctorName, patientName, patientEmail, patientPhone, keyId)
    }

    fun setProcessing() {
        _paymentState.value = PaymentState.Processing
    }

    /**
     * Launch Razorpay Standard Checkout SDK
     */
    fun startRazorpayPayment(
        activity: Activity,
        amountInRupees: Double,
        appointmentId: String,
        doctorName: String,
        patientName: String,
        patientEmail: String,
        patientPhone: String,
        keyId: String = DEFAULT_RAZORPAY_KEY
    ) {
        _paymentState.value = PaymentState.Processing
        try {
            val checkout = Checkout()
            checkout.setKeyID(keyId)

            val options = JSONObject().apply {
                put("name", "Payslot Healthcare")
                put("description", "Consultation with $doctorName")
                put("image", "https://img.icons8.com/color/96/medical-heart.png")
                put("currency", "INR")
                put("amount", (amountInRupees * 100).toLong())

                val prefill = JSONObject().apply {
                    if (patientEmail.isNotBlank()) put("email", patientEmail)
                    if (patientPhone.isNotBlank()) put("contact", patientPhone)
                    if (patientName.isNotBlank()) put("name", patientName)
                }
                put("prefill", prefill)

                val theme = JSONObject().apply {
                    put("color", "#0D9488") // AvenTeal
                }
                put("theme", theme)

                val notes = JSONObject().apply {
                    put("appointment_id", appointmentId)
                    put("doctor_name", doctorName)
                    put("platform", "Payslot Mobile")
                }
                put("notes", notes)

                val retryObj = JSONObject().apply {
                    put("enabled", true)
                    put("max_count", 2)
                }
                put("retry", retryObj)
            }

            checkout.open(activity, options)
        } catch (e: Exception) {
            _paymentState.value = PaymentState.Error(
                code = -1,
                description = e.localizedMessage ?: "Failed to open Razorpay payment gateway."
            )
        }
    }

    /**
     * Start Native Android UPI Payment Intent (GPay, PhonePe, Paytm, BHIM, etc.)
     */
    fun startNativeUpiPayment(
        context: Context,
        amountInRupees: Double,
        doctorName: String,
        appointmentId: String,
        upiVpa: String = PaymentConfig.DEFAULT_UPI_VPA,
        launcher: ActivityResultLauncher<Intent>? = null
    ): Boolean {
        _paymentState.value = PaymentState.Processing
        return try {
            val formattedAmount = String.format(Locale.US, "%.2f", amountInRupees)
            val txnRef = "TXN${System.currentTimeMillis()}"

            val upiUri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiVpa)
                .appendQueryParameter("pn", "Payslot Telehealth")
                .appendQueryParameter("mc", "8062")
                .appendQueryParameter("tr", txnRef)
                .appendQueryParameter("tn", "Doctor Consultation - $doctorName")
                .appendQueryParameter("am", formattedAmount)
                .appendQueryParameter("cu", "INR")
                .build()

            val upiIntent = Intent(Intent.ACTION_VIEW, upiUri)
            val chooser = Intent.createChooser(upiIntent, "Pay with UPI")

            if (launcher != null) {
                launcher.launch(chooser)
            } else {
                context.startActivity(chooser)
            }
            true
        } catch (e: Exception) {
            _paymentState.value = PaymentState.Error(
                code = -2,
                description = "No UPI application found on this device."
            )
            false
        }
    }

    /**
     * Parse the response returned by native UPI applications
     */
    fun onUpiResult(resultCode: Int, responseData: String?) {
        if (responseData.isNullOrBlank()) {
            if (resultCode == Activity.RESULT_CANCELED) {
                _paymentState.value = PaymentState.Cancelled
            } else {
                _paymentState.value = PaymentState.Success(
                    transactionId = "UPI_TXN_${System.currentTimeMillis().toString().takeLast(6)}",
                    paymentMethod = "UPI App"
                )
            }
            return
        }

        val params = responseData.split("&").associate {
            val parts = it.split("=")
            if (parts.size == 2) parts[0].lowercase().trim() to parts[1].trim()
            else parts[0].lowercase().trim() to ""
        }

        val status = params["status"] ?: ""
        val txnId = params["txnid"] ?: params["approvalrefno"] ?: "UPI_${System.currentTimeMillis().toString().takeLast(6)}"

        when {
            status.contains("success", ignoreCase = true) -> {
                _paymentState.value = PaymentState.Success(
                    transactionId = txnId,
                    paymentMethod = "UPI App"
                )
            }
            status.contains("fail", ignoreCase = true) -> {
                _paymentState.value = PaymentState.Error(
                    code = -3,
                    description = "UPI transaction failed. Please check your bank account or retry."
                )
            }
            else -> {
                _paymentState.value = PaymentState.Cancelled
            }
        }
    }

    /**
     * Callback from Razorpay on success
     */
    fun onRazorpaySuccess(paymentId: String, paymentData: PaymentData?) {
        _paymentState.value = PaymentState.Success(
            transactionId = paymentId,
            paymentMethod = "Razorpay Online",
            orderId = paymentData?.orderId
        )
    }

    /**
     * Callback from Razorpay on failure or dismissal
     */
    fun onRazorpayError(errorCode: Int, description: String?, paymentData: PaymentData?) {
        if (errorCode == Checkout.PAYMENT_CANCELED) {
            _paymentState.value = PaymentState.Cancelled
        } else {
            _paymentState.value = PaymentState.Error(
                code = errorCode,
                description = description ?: "Payment failed. Please try again."
            )
        }
    }

    /**
     * Test / Simulation fallback for offline or simulator testing
     */
    fun verifyPaymentSignature(orderId: String, paymentId: String, signature: String): Boolean {
        return try {
            val payload = "$orderId|$paymentId"
            val mac = javax.crypto.Mac.getInstance("HmacSHA256")
            val secretKeySpec = javax.crypto.spec.SecretKeySpec(
                PaymentConfig.KEY_SECRET.toByteArray(Charsets.UTF_8),
                "HmacSHA256"
            )
            mac.init(secretKeySpec)
            val hashBytes = mac.doFinal(payload.toByteArray(Charsets.UTF_8))
            val generatedSignature = hashBytes.joinToString("") { "%02x".format(it) }
            generatedSignature.equals(signature, ignoreCase = true)
        } catch (_: Exception) {
            false
        }
    }

    fun simulatePaymentSuccess(method: String = "Test UPI") {
        _paymentState.value = PaymentState.Success(
            transactionId = "SIM_${System.currentTimeMillis().toString().takeLast(8)}",
            paymentMethod = method
        )
    }
}
