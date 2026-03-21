package com.dualverse.core.security

import timber.log.Timber
import java.security.SecureRandom
import java.util.UUID

/**
 * Handles device identity spoofing for virtual instances.
 * Generates realistic device fingerprints to avoid detection.
 */
class DeviceSpoofer {

    /**
     * Complete virtual device identity.
     */
    data class VirtualDeviceIdentity(
        val imei: String,
        val androidId: String,
        val serialNumber: String,
        val macAddress: String,
        val ssid: String,
        val buildFingerprint: String,
        val hardwareInfo: HardwareInfo,
        val telephonyInfo: TelephonyInfo
    )

    /**
     * Hardware information for device spoofing.
     */
    data class HardwareInfo(
        val manufacturer: String,
        val model: String,
        val device: String,
        val product: String,
        val board: String,
        val brand: String,
        val cpuAbi: String,
        val cpuAbi2: String,
        val hardware: String,
        val bootloader: String
    )

    /**
     * Telephony information for device spoofing.
     */
    data class TelephonyInfo(
        val networkOperator: String,
        val networkOperatorName: String,
        val simOperator: String,
        val simOperatorName: String,
        val networkCountryIso: String,
        val simCountryIso: String,
        val phoneType: Int,
        val networkType: Int
    )

    // Device profiles for realistic spoofing
    private val deviceProfiles = listOf(
        DeviceProfile(
            manufacturer = "Samsung",
            model = "SM-G991B",
            device = "galaxys21",
            product = "galaxys21_eea",
            board = "gs101",
            brand = "samsung",
            hardware = "exynos2100",
            bootloader = "G991BXXU8CVLB"
        ),
        DeviceProfile(
            manufacturer = "Google",
            model = "Pixel 6",
            device = "oriole",
            product = "oriole",
            board = "slider",
            brand = "google",
            hardware = "tensor",
            bootloader = "slider-1.0-8738168"
        ),
        DeviceProfile(
            manufacturer = "OnePlus",
            model = "ONEPLUS A9003",
            device = "OnePlus3",
            product = "OnePlus3",
            board = "msm8996",
            brand = "OnePlus",
            hardware = "qcom",
            bootloader = "unknown"
        ),
        DeviceProfile(
            manufacturer = "Xiaomi",
            model = "M2102J20SG",
            device = "alioth",
            product = "alioth_global",
            board = "sm7325",
            brand = "Xiaomi",
            hardware = "qcom",
            bootloader = "unknown"
        )
    )

    private val secureRandom = SecureRandom()

    /**
     * Generates a complete virtual device identity.
     */
    fun generateIdentity(): VirtualDeviceIdentity {
        val profile = deviceProfiles.random()

        val hardwareInfo = HardwareInfo(
            manufacturer = profile.manufacturer,
            model = profile.model,
            device = profile.device,
            product = profile.product,
            board = profile.board,
            brand = profile.brand,
            cpuAbi = "arm64-v8a",
            cpuAbi2 = "armeabi-v7a",
            hardware = profile.hardware,
            bootloader = profile.bootloader
        )

        val telephonyInfo = generateTelephonyInfo()

        return VirtualDeviceIdentity(
            imei = generateImei(),
            androidId = generateAndroidId(),
            serialNumber = generateSerialNumber(),
            macAddress = generateMacAddress(),
            ssid = generateSsid(),
            buildFingerprint = generateBuildFingerprint(profile),
            hardwareInfo = hardwareInfo,
            telephonyInfo = telephonyInfo
        )
    }

    /**
     * Validates a device identity.
     */
    fun validateIdentity(identity: VirtualDeviceIdentity): Boolean {
        return identity.imei.length == 15 &&
                identity.androidId.length == 16 &&
                identity.serialNumber.isNotBlank() &&
                identity.macAddress.matches(MAC_REGEX)
    }

    /**
     * Injects device identity into a container.
     */
    fun injectIdentity(identity: VirtualDeviceIdentity, containerId: String) {
        nativeInjectIdentity(
            containerId,
            identity.imei,
            identity.androidId,
            identity.serialNumber,
            identity.macAddress,
            identity.buildFingerprint
        )
        Timber.d("Injected device identity into container: $containerId")
    }

    /**
     * Generates a valid IMEI number.
     */
    private fun generateImei(): String {
        val sb = StringBuilder()
        
        // TAC (Type Allocation Code) - first 8 digits
        val tac = TAC_LIST.random()
        sb.append(tac)

        // Serial number - next 6 digits
        for (i in 0 until 6) {
            sb.append(secureRandom.nextInt(10))
        }

        // Calculate Luhn check digit
        val imeiWithoutCheck = sb.toString()
        val checkDigit = calculateLuhnDigit(imeiWithoutCheck)
        sb.append(checkDigit)

        return sb.toString()
    }

    /**
     * Calculates the Luhn check digit for IMEI validation.
     */
    private fun calculateLuhnDigit(number: String): Int {
        var sum = 0
        var alternate = false

        for (i in number.length - 1 downTo 0) {
            var digit = number[i].toString().toInt()

            if (alternate) {
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }

            sum += digit
            alternate = !alternate
        }

        return (10 - (sum % 10)) % 10
    }

    /**
     * Generates an Android ID.
     */
    private fun generateAndroidId(): String {
        return UUID.randomUUID().toString().replace("-", "").take(16)
    }

    /**
     * Generates a serial number.
     */
    private fun generateSerialNumber(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..11)
            .map { chars.random() }
            .joinToString("")
    }

    /**
     * Generates a random MAC address.
     */
    private fun generateMacAddress(): String {
        val mac = StringBuilder()
        for (i in 0 until 6) {
            if (i > 0) mac.append(":")
            mac.append("%02X".format(secureRandom.nextInt(256)))
        }
        return mac.toString()
    }

    /**
     * Generates a random SSID.
     */
    private fun generateSsid(): String {
        val prefixes = listOf("HOME_", "Office_", "WiFi_", "Network_", "Guest_")
        return "${prefixes.random()}${(1000..9999).random()}"
    }

    /**
     * Generates a build fingerprint.
     */
    private fun generateBuildFingerprint(profile: DeviceProfile): String {
        val release = (10..14).random()
        val securityPatch = "${release + 2000}-0${(1..9).random()}-0${(1..9).random()}"
        val buildId = generateRandomString(6).uppercase()
        val buildNumber = "V${(1..15).random()}.${(0..99).random()}"

        return "${profile.brand}/${profile.product}/${profile.device}:" +
                "${release}/$buildId/${buildNumber}:user/release-keys"
    }

    /**
     * Generates telephony information.
     */
    private fun generateTelephonyInfo(): TelephonyInfo {
        val carriers = listOf(
            Carrier("310260", "T-Mobile", "us"),
            Carrier("310410", "AT&T", "us"),
            Carrier("311480", "Verizon", "us"),
            Carrier("23410", "O2", "gb"),
            Carrier("23415", "Vodafone", "gb"),
            Carrier("26201", "Telekom", "de"),
            Carrier("26202", "Vodafone", "de")
        )

        val carrier = carriers.random()

        return TelephonyInfo(
            networkOperator = carrier.mccMnc,
            networkOperatorName = carrier.name,
            simOperator = carrier.mccMnc,
            simOperatorName = carrier.name,
            networkCountryIso = carrier.countryIso,
            simCountryIso = carrier.countryIso,
            phoneType = 1, // GSM
            networkType = listOf(13, 18, 19, 20).random() // LTE, LTE_CA, NR, NR_SA
        )
    }

    private fun generateRandomString(length: Int): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }

    private data class DeviceProfile(
        val manufacturer: String,
        val model: String,
        val device: String,
        val product: String,
        val board: String,
        val brand: String,
        val hardware: String,
        val bootloader: String
    )

    private data class Carrier(
        val mccMnc: String,
        val name: String,
        val countryIso: String
    )

    private external fun nativeInjectIdentity(
        containerId: String,
        imei: String,
        androidId: String,
        serial: String,
        mac: String,
        fingerprint: String
    )

    companion object {
        private val MAC_REGEX = Regex("^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$")

        // Valid TAC codes for common devices
        private val TAC_LIST = listOf(
            "35867311", "35656111", "35215211", "35988711",
            "35742611", "35265411", "35676511", "35904611",
            "35824011", "35304811", "35625711", "35912111"
        )

        init {
            System.loadLibrary("dualverse-native")
        }
    }
}
