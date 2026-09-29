package com.example.services.emergency

import android.content.Context
import android.content.Intent
import android.net.Uri

data class NearbyEmergencyFacility(
    val name: String,
    val distanceKm: Double,
    val address: String,
    val phone: String,
    val hasTraumaCenter: Boolean,
    val has24HrPharmacy: Boolean
)

class EmergencyService {
    val isSimulationMode: Boolean = true

    fun dialEmergencyAmbulance(context: Context, number: String = "911") {
        try {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$number")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun getNearbyEmergencyFacilities(): List<NearbyEmergencyFacility> {
        return listOf(
            NearbyEmergencyFacility(
                name = "City General Emergency & Trauma Center",
                distanceKm = 1.8,
                address = "400 Health Science Blvd, Metro City",
                phone = "+1 (555) 911-0022",
                hasTraumaCenter = true,
                has24HrPharmacy = true
            ),
            NearbyEmergencyFacility(
                name = "St. Luke's Cardiac & Stroke Emergency Care",
                distanceKm = 3.4,
                address = "120 Heartlands Ave, Metro City",
                phone = "+1 (555) 911-4488",
                hasTraumaCenter = true,
                has24HrPharmacy = false
            ),
            NearbyEmergencyFacility(
                name = "Metro Children's Pediatric Urgent Care",
                distanceKm = 4.2,
                address = "85 Riverbend Rd, Metro City",
                phone = "+1 (555) 911-3311",
                hasTraumaCenter = false,
                has24HrPharmacy = true
            )
        )
    }
}
