package com.perfme.shared.platform

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import com.perfme.shared.domain.model.Keypoint
import com.perfme.shared.domain.model.KeypointType
import com.perfme.shared.domain.model.Point3D
import com.perfme.shared.domain.model.PoseData
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Android implementation of pose detection using ML Kit
 */
actual class PlatformPoseDetector {

    private val fastDetector: PoseDetector by lazy {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        PoseDetection.getClient(options)
    }

    private val accurateDetector: PoseDetector by lazy {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            .build()
        PoseDetection.getClient(options)
    }

    actual suspend fun detectPose(
        imageData: ByteArray,
        width: Int,
        height: Int,
        useAccurateModel: Boolean
    ): PoseData {
        return suspendCancellableCoroutine { continuation ->
            try {
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                bitmap.copyPixelsFromBuffer(java.nio.ByteBuffer.wrap(imageData))

                val image = InputImage.fromBitmap(bitmap, 0)
                val detector = if (useAccurateModel) accurateDetector else fastDetector

                detector.process(image)
                    .addOnSuccessListener { pose ->
                        val keypoints = pose.allPoseLandmarks.mapNotNull { landmark ->
                            landmark.toKeypoint()
                        }

                        val poseData = PoseData(
                            keypoints = keypoints,
                            confidence = keypoints.minOfOrNull { it.confidence } ?: 0f,
                            timestamp = System.currentTimeMillis()
                        )

                        continuation.resume(poseData)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }

                continuation.invokeOnCancellation {
                    // Clean up if needed
                }
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    actual fun dispose() {
        fastDetector.close()
        accurateDetector.close()
    }

    /**
     * Convert ML Kit PoseLandmark to our Keypoint model
     */
    private fun PoseLandmark.toKeypoint(): Keypoint? {
        val keypointType = this.landmarkType.toKeypointType() ?: return null

        return Keypoint(
            position = Point3D(
                x = this.position.x,
                y = this.position.y,
                z = this.position3D.z
            ),
            confidence = this.inFrameLikelihood,
            type = keypointType
        )
    }

    /**
     * Map ML Kit landmark types to our KeypointType enum
     */
    private fun Int.toKeypointType(): KeypointType? {
        return when (this) {
            PoseLandmark.NOSE -> KeypointType.NOSE
            PoseLandmark.LEFT_EYE_INNER -> KeypointType.LEFT_EYE
            PoseLandmark.LEFT_EYE -> KeypointType.LEFT_EYE
            PoseLandmark.LEFT_EYE_OUTER -> KeypointType.LEFT_EYE
            PoseLandmark.RIGHT_EYE_INNER -> KeypointType.RIGHT_EYE
            PoseLandmark.RIGHT_EYE -> KeypointType.RIGHT_EYE
            PoseLandmark.RIGHT_EYE_OUTER -> KeypointType.RIGHT_EYE
            PoseLandmark.LEFT_EAR -> KeypointType.LEFT_EAR
            PoseLandmark.RIGHT_EAR -> KeypointType.RIGHT_EAR
            PoseLandmark.LEFT_MOUTH -> KeypointType.LEFT_EAR // Approximation
            PoseLandmark.RIGHT_MOUTH -> KeypointType.RIGHT_EAR // Approximation
            PoseLandmark.LEFT_SHOULDER -> KeypointType.LEFT_SHOULDER
            PoseLandmark.RIGHT_SHOULDER -> KeypointType.RIGHT_SHOULDER
            PoseLandmark.LEFT_ELBOW -> KeypointType.LEFT_ELBOW
            PoseLandmark.RIGHT_ELBOW -> KeypointType.RIGHT_ELBOW
            PoseLandmark.LEFT_WRIST -> KeypointType.LEFT_WRIST
            PoseLandmark.RIGHT_WRIST -> KeypointType.RIGHT_WRIST
            PoseLandmark.LEFT_PINKY -> KeypointType.LEFT_PINKY
            PoseLandmark.RIGHT_PINKY -> KeypointType.RIGHT_PINKY
            PoseLandmark.LEFT_INDEX -> KeypointType.LEFT_INDEX
            PoseLandmark.RIGHT_INDEX -> KeypointType.RIGHT_INDEX
            PoseLandmark.LEFT_THUMB -> KeypointType.LEFT_THUMB
            PoseLandmark.RIGHT_THUMB -> KeypointType.RIGHT_THUMB
            PoseLandmark.LEFT_HIP -> KeypointType.LEFT_HIP
            PoseLandmark.RIGHT_HIP -> KeypointType.RIGHT_HIP
            PoseLandmark.LEFT_KNEE -> KeypointType.LEFT_KNEE
            PoseLandmark.RIGHT_KNEE -> KeypointType.RIGHT_KNEE
            PoseLandmark.LEFT_ANKLE -> KeypointType.LEFT_ANKLE
            PoseLandmark.RIGHT_ANKLE -> KeypointType.RIGHT_ANKLE
            PoseLandmark.LEFT_HEEL -> KeypointType.LEFT_HEEL
            PoseLandmark.RIGHT_HEEL -> KeypointType.RIGHT_HEEL
            PoseLandmark.LEFT_FOOT_INDEX -> KeypointType.LEFT_FOOT_INDEX
            PoseLandmark.RIGHT_FOOT_INDEX -> KeypointType.RIGHT_FOOT_INDEX
            else -> null
        }
    }
}