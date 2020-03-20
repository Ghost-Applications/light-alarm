package cash.andrew.lightalarm.data

import android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE
import android.hardware.camera2.CameraManager
import cash.andrew.lightalarm.data.LightController.LightState.OFF
import cash.andrew.lightalarm.data.LightController.LightState.ON
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LightController @Inject constructor(private val cameraManager: CameraManager) {

    private val cameraWithFlashLightId =
        cameraManager.cameraIdList
            .firstOrNull {
                cameraManager.getCameraCharacteristics(it).get(FLASH_INFO_AVAILABLE) ?: false
            }

    private var state: LightState = OFF

    val isLightOn get() = state == ON

    fun turnOn() {
        if (cameraWithFlashLightId == null) {
            Timber.w("No flashlights found")
            return
        }
        cameraManager.setTorchMode(cameraWithFlashLightId, true)
        state = ON
    }

    fun turnOff() {
        if (cameraWithFlashLightId == null) {
            Timber.w("No flashlights found")
            return
        }
        cameraManager.setTorchMode(cameraWithFlashLightId, false)
        state = OFF
    }

    val hasFlashLight get() = cameraWithFlashLightId != null

    enum class LightState {
        ON,
        OFF
    }
}
