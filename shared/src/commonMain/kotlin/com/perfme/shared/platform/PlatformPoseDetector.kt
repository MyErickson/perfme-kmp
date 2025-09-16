package com.perfme.shared.platform

import com.perfme.shared.domain.model.PoseData

/**
 * Platform-specific pose detector interface
 */
expect class PlatformPoseDetector() {
    /**
     * Detect pose from image data
     * @param imageData Raw image bytes
     * @param width Image width
     * @param height Image height
     * @param useAccurateModel Whether to use accurate (slower) or fast model
     * @return Detected pose data
     */
    suspend fun detectPose(
        imageData: ByteArray,
        width: Int,
        height: Int,
        useAccurateModel: Boolean = false,
    ): PoseData

    /**
     * Dispose detector resources
     */
    fun dispose()
}
