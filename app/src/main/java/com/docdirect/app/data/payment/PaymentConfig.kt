package com.docdirect.app.data.payment

object PaymentConfig {
    /**
     * Razorpay Test API Credentials
     * Dashboard: https://dashboard.razorpay.com/app/keys
     */
    const val KEY_ID: String = "rzp_test_TriQuc98eMyCnq"
    const val KEY_SECRET: String = "uiyArrlglbqoMHNYXTYGtmqh"

    // Default UPI Payment Parameters
    const val DEFAULT_UPI_VPA: String = "payslot.telehealth@okaxis"
    const val MERCHANT_NAME: String = "DocDirect Telehealth"
    const val MERCHANT_CATEGORY_CODE: String = "8062" // Healthcare / Doctors
}
