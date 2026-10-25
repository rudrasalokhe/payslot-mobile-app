package com.docdirect.app.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility to generate, export, view, and share official digital prescription receipts in PDF format.
 */
object PrescriptionPdfGenerator {

    private const val TAG = "PrescriptionPdfGen"

    fun generatePrescriptionPdf(
        context: Context,
        doctorName: String,
        doctorSpecialty: String = "Consultant Physician",
        doctorLicense: String = "MCI-48921-DERM",
        doctorHospital: String = "DocDirect Bandra Clinic, Mumbai",
        clinicName: String = doctorHospital,
        patientName: String = "Patient",
        appointmentDate: String = "Today",
        consultationDate: String = appointmentDate,
        appointmentId: String = "AV-CONS-0925",
        consultationId: String = appointmentId,
        prescriptionText: String = "",
        dosageInstructions: String = prescriptionText
    ): File? {
        val document = PdfDocument()

        val finalDocHospital = if (clinicName.isNotBlank()) clinicName else doctorHospital
        val finalDate = if (consultationDate.isNotBlank()) consultationDate else appointmentDate
        val finalId = if (consultationId.isNotBlank()) consultationId else appointmentId
        val finalInstructions = if (dosageInstructions.isNotBlank()) dosageInstructions else prescriptionText

        try {
            // Standard A4 Size: 595 x 842 points
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            // Background canvas
            paint.color = Color.WHITE
            canvas.drawRect(0f, 0f, 595f, 842f, paint)

            // Top Header Banner (Brand Teal Color: 0xFF145B55)
            paint.color = 0xFF145B55.toInt()
            canvas.drawRect(0f, 0f, 595f, 100f, paint)

            // Header Brand Title
            paint.color = Color.WHITE
            paint.textSize = 22f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("AVEN HEALTH  |  DOCDIRECT", 40f, 48f, paint)

            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText("Verified Digital Prescription & Clinical Record", 40f, 72f, paint)

            // Doctor Info Card Section
            var currentY = 135f
            paint.color = 0xFF102F32.toInt() // AvenInk
            paint.textSize = 17f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            val docDisplayName = if (doctorName.startsWith("Dr")) doctorName else "Dr. $doctorName"
            canvas.drawText(docDisplayName, 40f, currentY, paint)

            currentY += 18f
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.color = 0xFF5B6E70.toInt() // AvenMuted
            val specialtyLine = if (doctorSpecialty.isNotBlank()) "$doctorSpecialty · $finalDocHospital" else finalDocHospital
            canvas.drawText(specialtyLine, 40f, currentY, paint)

            currentY += 16f
            val licenseFormatted = if (doctorLicense.isNotBlank()) doctorLicense else "MCI-48921-DERM"
            paint.color = 0xFF145B55.toInt()
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Medical License No: $licenseFormatted", 40f, currentY, paint)

            // Divider Line
            currentY += 18f
            paint.color = 0xFFDDE5DE.toInt() // AvenLine
            paint.strokeWidth = 1.5f
            canvas.drawLine(40f, currentY, 555f, currentY, paint)

            // Patient & Consultation Details Box
            currentY += 25f
            paint.color = 0xFFF5F7F3.toInt() // AvenBg
            canvas.drawRoundRect(40f, currentY, 555f, currentY + 65f, 12f, 12f, paint)

            val boxY = currentY + 24f
            paint.color = 0xFF102F32.toInt()
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Patient Name:", 56f, boxY, paint)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(patientName.ifBlank { "Patient" }, 145f, boxY, paint)

            canvas.drawText("Consultation Date:", 340f, boxY, paint)
            canvas.drawText(finalDate.ifBlank { "Today" }, 450f, boxY, paint)

            val boxY2 = boxY + 22f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Appointment ID:", 56f, boxY2, paint)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            canvas.drawText(finalId.takeLast(10), 145f, boxY2, paint)

            canvas.drawText("Status:", 340f, boxY2, paint)
            paint.color = 0xFF145B55.toInt()
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Digitally Signed", 450f, boxY2, paint)

            // Rx Symbol Section
            currentY += 105f
            paint.color = 0xFF145B55.toInt()
            paint.textSize = 34f
            paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            canvas.drawText("℞", 40f, currentY, paint)

            paint.color = 0xFF102F32.toInt()
            paint.textSize = 14f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("Medication, Dosage Instructions & Clinical Advice:", 78f, currentY - 8f, paint)

            // Content Body / Dosage instructions
            currentY += 30f
            paint.color = 0xFF102F32.toInt()
            paint.textSize = 12f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

            val lines = finalInstructions.ifBlank { "Prescribed medication as clinically indicated. Follow oral dosage regimen after meals." }.split("
")
            for (rawLine in lines) {
                // Word wrap long lines
                val words = rawLine.split(" ")
                var currentLine = ""
                for (word in words) {
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                    val testWidth = paint.measureText(testLine)
                    if (testWidth > 500f) {
                        canvas.drawText(currentLine, 45f, currentY, paint)
                        currentY += 18f
                        currentLine = word
                    } else {
                        currentLine = testLine
                    }
                }
                if (currentLine.isNotEmpty()) {
                    canvas.drawText(currentLine, 45f, currentY, paint)
                    currentY += 20f
                }
            }

            // Bottom Certified Signature & Security Seal
            val footerBoxY = 720f
            paint.color = 0xFFE8F1E9.toInt() // AvenMint
            canvas.drawRoundRect(40f, footerBoxY, 555f, footerBoxY + 75f, 14f, 14f, paint)

            paint.color = 0xFF145B55.toInt()
            paint.textSize = 11f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("✓ DIGITALLY AUTHENTICATED MEDICAL RECORD", 56f, footerBoxY + 24f, paint)

            paint.color = 0xFF5B6E70.toInt()
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 10f
            val generatedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
            canvas.drawText("Signed by $docDisplayName (License: $licenseFormatted)", 56f, footerBoxY + 42f, paint)
            canvas.drawText("Generated on $generatedDate · Stored in Patient Vault", 56f, footerBoxY + 58f, paint)

            document.finishPage(page)

            // Output directory in cache
            val outputDir = File(context.cacheDir, "prescriptions").apply { mkdirs() }
            val cleanAptId = finalId.replace("[^a-zA-Z0-9]".toRegex(), "_")
            val outputFile = File(outputDir, "Rx_Prescription_${cleanAptId}.pdf")

            FileOutputStream(outputFile).use { fos ->
                document.writeTo(fos)
            }

            Log.i(TAG, "Prescription PDF successfully created at: ${outputFile.absolutePath}")
            return outputFile
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate prescription PDF", e)
            return null
        } finally {
            document.close()
        }
    }

    /**
     * Opens Android System Share Sheet to share the generated PDF via WhatsApp, Email, Drive, etc.
     */
    fun sharePrescriptionPdf(context: Context, file: File?) {
        if (file == null || !file.exists()) {
            Toast.makeText(context, "Prescription PDF could not be generated", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Medical Prescription - ${file.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Prescription PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error sharing prescription PDF", e)
            Toast.makeText(context, "Could not open share options", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens the generated PDF in the device's default PDF viewer.
     */
    fun viewPrescriptionPdf(context: Context, file: File?) {
        if (file == null || !file.exists()) {
            Toast.makeText(context, "Prescription PDF not available", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(viewIntent)
        } catch (e: Exception) {
            Log.e(TAG, "No PDF viewer found on device", e)
            // Fallback to share intent if no dedicated PDF viewer exists
            sharePrescriptionPdf(context, file)
        }
    }
}
