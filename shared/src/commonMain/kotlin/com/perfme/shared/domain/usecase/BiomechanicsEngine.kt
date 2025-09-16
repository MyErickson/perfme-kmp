package com.perfme.shared.domain.usecase

import com.perfme.shared.domain.model.KeypointType
import com.perfme.shared.domain.model.Point3D
import com.perfme.shared.domain.model.PoseData
import com.perfme.shared.domain.model.SprintMetrics
import kotlinx.datetime.Clock
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Core biomechanics calculation engine
 * Shared business logic for pose analysis
 */
class BiomechanicsEngine {
    /**
     * Analyze sprint pose and calculate metrics
     */
    fun analyzeSprintPose(
        currentPose: PoseData,
        previousPose: PoseData? = null,
    ): SprintMetrics {
        val kneeAngle = calculateKneeAngle(currentPose)
        val hipVelocity = calculateHipVelocity(currentPose, previousPose)
        val armSymmetry = calculateArmSymmetry(currentPose)
        val overallScore = calculateOverallScore(kneeAngle, hipVelocity, armSymmetry)

        return SprintMetrics(
            kneeAngle = kneeAngle,
            hipVelocity = hipVelocity,
            armSymmetry = armSymmetry,
            overallScore = overallScore,
            timestamp = Clock.System.now().toEpochMilliseconds(),
        )
    }

    /**
     * Calculate knee angle (hip-knee-ankle)
     */
    private fun calculateKneeAngle(pose: PoseData): Double {
        val leftHip = pose.getKeypoint(KeypointType.LEFT_HIP)?.position
        val leftKnee = pose.getKeypoint(KeypointType.LEFT_KNEE)?.position
        val leftAnkle = pose.getKeypoint(KeypointType.LEFT_ANKLE)?.position

        val rightHip = pose.getKeypoint(KeypointType.RIGHT_HIP)?.position
        val rightKnee = pose.getKeypoint(KeypointType.RIGHT_KNEE)?.position
        val rightAnkle = pose.getKeypoint(KeypointType.RIGHT_ANKLE)?.position

        val leftAngle =
            if (leftHip != null && leftKnee != null && leftAnkle != null) {
                calculateAngleBetweenPoints(leftHip, leftKnee, leftAnkle)
            } else {
                null
            }

        val rightAngle =
            if (rightHip != null && rightKnee != null && rightAnkle != null) {
                calculateAngleBetweenPoints(rightHip, rightKnee, rightAnkle)
            } else {
                null
            }

        return when {
            leftAngle != null && rightAngle != null -> (leftAngle + rightAngle) / 2.0
            leftAngle != null -> leftAngle
            rightAngle != null -> rightAngle
            else -> 0.0
        }
    }

    /**
     * Calculate hip velocity between frames
     */
    private fun calculateHipVelocity(
        currentPose: PoseData,
        previousPose: PoseData?,
    ): Double {
        if (previousPose == null) return 0.0

        val currentHip = getAverageHipPosition(currentPose)
        val previousHip = getAverageHipPosition(previousPose)

        if (currentHip == null || previousHip == null) return 0.0

        val distance = calculateDistance(currentHip, previousHip)
        val timeDiff = (currentPose.timestamp - previousPose.timestamp) / 1000.0 // Convert to seconds

        return if (timeDiff > 0) distance / timeDiff else 0.0
    }

    /**
     * Calculate arm symmetry (difference between left and right arm angles)
     */
    private fun calculateArmSymmetry(pose: PoseData): Double {
        val leftShoulder = pose.getKeypoint(KeypointType.LEFT_SHOULDER)?.position
        val leftElbow = pose.getKeypoint(KeypointType.LEFT_ELBOW)?.position
        val leftWrist = pose.getKeypoint(KeypointType.LEFT_WRIST)?.position

        val rightShoulder = pose.getKeypoint(KeypointType.RIGHT_SHOULDER)?.position
        val rightElbow = pose.getKeypoint(KeypointType.RIGHT_ELBOW)?.position
        val rightWrist = pose.getKeypoint(KeypointType.RIGHT_WRIST)?.position

        val leftArmAngle =
            if (leftShoulder != null && leftElbow != null && leftWrist != null) {
                calculateAngleBetweenPoints(leftShoulder, leftElbow, leftWrist)
            } else {
                null
            }

        val rightArmAngle =
            if (rightShoulder != null && rightElbow != null && rightWrist != null) {
                calculateAngleBetweenPoints(rightShoulder, rightElbow, rightWrist)
            } else {
                null
            }

        return if (leftArmAngle != null && rightArmAngle != null) {
            kotlin.math.abs(leftArmAngle - rightArmAngle)
        } else {
            Double.MAX_VALUE // High asymmetry if can't calculate
        }
    }

    /**
     * Calculate overall performance score (0-100)
     */
    private fun calculateOverallScore(
        kneeAngle: Double,
        hipVelocity: Double,
        armSymmetry: Double,
    ): Double {
        val kneeScore = calculateKneeAngleScore(kneeAngle)
        val velocityScore = calculateVelocityScore(hipVelocity)
        val symmetryScore = calculateSymmetryScore(armSymmetry)

        // Weighted average
        return (kneeScore * 0.4 + velocityScore * 0.4 + symmetryScore * 0.2)
    }

    /**
     * Helper functions
     */

    private fun calculateAngleBetweenPoints(
        point1: Point3D,
        vertex: Point3D,
        point3: Point3D,
    ): Double {
        val vector1 = Point3D(point1.x - vertex.x, point1.y - vertex.y, point1.z - vertex.z)
        val vector2 = Point3D(point3.x - vertex.x, point3.y - vertex.y, point3.z - vertex.z)

        val dotProduct = vector1.x * vector2.x + vector1.y * vector2.y + vector1.z * vector2.z
        val magnitude1 = sqrt(vector1.x.pow(2) + vector1.y.pow(2) + vector1.z.pow(2))
        val magnitude2 = sqrt(vector2.x.pow(2) + vector2.y.pow(2) + vector2.z.pow(2))

        if (magnitude1 == 0f || magnitude2 == 0f) return 0.0

        val cosAngle = dotProduct / (magnitude1 * magnitude2)
        val clampedCosAngle = cosAngle.coerceIn(-1f, 1f)

        return kotlin.math.acos(clampedCosAngle.toDouble()) * 180.0 / kotlin.math.PI
    }

    private fun calculateDistance(
        point1: Point3D,
        point2: Point3D,
    ): Double {
        val dx = point1.x - point2.x
        val dy = point1.y - point2.y
        val dz = point1.z - point2.z
        return sqrt(dx.pow(2) + dy.pow(2) + dz.pow(2)).toDouble()
    }

    private fun getAverageHipPosition(pose: PoseData): Point3D? {
        val leftHip = pose.getKeypoint(KeypointType.LEFT_HIP)?.position
        val rightHip = pose.getKeypoint(KeypointType.RIGHT_HIP)?.position

        return when {
            leftHip != null && rightHip != null ->
                Point3D(
                    (leftHip.x + rightHip.x) / 2f,
                    (leftHip.y + rightHip.y) / 2f,
                    (leftHip.z + rightHip.z) / 2f,
                )
            leftHip != null -> leftHip
            rightHip != null -> rightHip
            else -> null
        }
    }

    private fun calculateKneeAngleScore(angle: Double): Double {
        return when {
            angle in SprintMetrics.KNEE_ANGLE_OPTIMAL_RANGE -> 100.0
            angle in SprintMetrics.KNEE_ANGLE_MIN..SprintMetrics.KNEE_ANGLE_MAX -> 80.0
            else -> maxOf(0.0, 100.0 - kotlin.math.abs(angle - SprintMetrics.KNEE_ANGLE_OPTIMAL) * 2)
        }
    }

    private fun calculateVelocityScore(velocity: Double): Double {
        return when {
            velocity >= SprintMetrics.HIP_VELOCITY_TARGET -> 100.0
            velocity >= SprintMetrics.HIP_VELOCITY_MIN -> (velocity / SprintMetrics.HIP_VELOCITY_TARGET * 100.0)
            else -> 0.0
        }
    }

    private fun calculateSymmetryScore(asymmetry: Double): Double {
        return when {
            asymmetry <= SprintMetrics.ARM_SYMMETRY_EXCELLENT -> 100.0
            asymmetry <= SprintMetrics.ARM_SYMMETRY_GOOD -> 80.0
            asymmetry <= SprintMetrics.ARM_SYMMETRY_ACCEPTABLE -> 60.0
            else -> maxOf(0.0, 60.0 - (asymmetry - SprintMetrics.ARM_SYMMETRY_ACCEPTABLE) * 2)
        }
    }
}
