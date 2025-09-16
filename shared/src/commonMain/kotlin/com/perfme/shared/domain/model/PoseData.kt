package com.perfme.shared.domain.model

import kotlinx.serialization.Serializable

/**
 * Represents a 3D point in space
 */
@Serializable
data class Point3D(
    val x: Float,
    val y: Float,
    val z: Float = 0f
)

/**
 * Represents a pose keypoint with confidence
 */
@Serializable
data class Keypoint(
    val position: Point3D,
    val confidence: Float,
    val type: KeypointType
)

/**
 * Enum representing different body keypoints
 */
@Serializable
enum class KeypointType {
    NOSE,
    LEFT_EYE,
    RIGHT_EYE,
    LEFT_EAR,
    RIGHT_EAR,
    LEFT_SHOULDER,
    RIGHT_SHOULDER,
    LEFT_ELBOW,
    RIGHT_ELBOW,
    LEFT_WRIST,
    RIGHT_WRIST,
    LEFT_HIP,
    RIGHT_HIP,
    LEFT_KNEE,
    RIGHT_KNEE,
    LEFT_ANKLE,
    RIGHT_ANKLE,
    // Additional ML Kit points
    LEFT_PINKY,
    RIGHT_PINKY,
    LEFT_INDEX,
    RIGHT_INDEX,
    LEFT_THUMB,
    RIGHT_THUMB,
    LEFT_HEEL,
    RIGHT_HEEL,
    LEFT_FOOT_INDEX,
    RIGHT_FOOT_INDEX
}

/**
 * Complete pose data with all keypoints
 */
@Serializable
data class PoseData(
    val keypoints: List<Keypoint>,
    val confidence: Float,
    val timestamp: Long
) {
    /**
     * Get keypoint by type
     */
    fun getKeypoint(type: KeypointType): Keypoint? {
        return keypoints.find { it.type == type }
    }

    /**
     * Check if pose detection confidence is above threshold
     */
    fun isValid(threshold: Float = 0.5f): Boolean {
        return confidence >= threshold && keypoints.isNotEmpty()
    }

    /**
     * Get specific body part keypoints
     */
    fun getLeftArm(): List<Keypoint> {
        return keypoints.filter {
            it.type in listOf(
                KeypointType.LEFT_SHOULDER,
                KeypointType.LEFT_ELBOW,
                KeypointType.LEFT_WRIST
            )
        }
    }

    fun getRightArm(): List<Keypoint> {
        return keypoints.filter {
            it.type in listOf(
                KeypointType.RIGHT_SHOULDER,
                KeypointType.RIGHT_ELBOW,
                KeypointType.RIGHT_WRIST
            )
        }
    }

    fun getLeftLeg(): List<Keypoint> {
        return keypoints.filter {
            it.type in listOf(
                KeypointType.LEFT_HIP,
                KeypointType.LEFT_KNEE,
                KeypointType.LEFT_ANKLE
            )
        }
    }

    fun getRightLeg(): List<Keypoint> {
        return keypoints.filter {
            it.type in listOf(
                KeypointType.RIGHT_HIP,
                KeypointType.RIGHT_KNEE,
                KeypointType.RIGHT_ANKLE
            )
        }
    }
}