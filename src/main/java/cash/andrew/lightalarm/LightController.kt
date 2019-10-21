package cash.andrew.lightalarm

import android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE
import android.hardware.camera2.CameraManager
import cash.andrew.lightalarm.LightController.LightState.OFF
import cash.andrew.lightalarm.LightController.LightState.ON
import timber.log.Timber

class LightController(private val cameraManager: CameraManager) {

    private val cameraWithFlashLightId =
        cameraManager.cameraIdList
            .firstOrNull {
                cameraManager.getCameraCharacteristics(it).get(FLASH_INFO_AVAILABLE) ?: false
            }

    private var state: LightState = OFF

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


    enum class LightState {
        ON,
        OFF
    }
}
