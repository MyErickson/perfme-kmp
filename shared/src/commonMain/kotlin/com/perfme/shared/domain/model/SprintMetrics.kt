package com.perfme.shared.domain.model

import kotlinx.serialization.Serializable

/**
 * Sprint analysis metrics
 */
@Serializable
data class SprintMetrics(
    val kneeAngle: Double,
    val hipVelocity: Double,
    val armSymmetry: Double,
    val overallScore: Double,
    val timestamp: Long
) {
    /**
     * Get feedback based on metrics
     */
    fun getKneeAngleFeedback(): MetricFeedback {
        return when {
            kneeAngle < KNEE_ANGLE_MIN -> MetricFeedback.TOO_LOW
            kneeAngle > KNEE_ANGLE_MAX -> MetricFeedback.TOO_HIGH
            kneeAngle in KNEE_ANGLE_OPTIMAL_RANGE -> MetricFeedback.OPTIMAL
            else -> MetricFeedback.GOOD
        }
    }

    fun getHipVelocityFeedback(): MetricFeedback {
        return when {
            hipVelocity < HIP_VELOCITY_MIN -> MetricFeedback.TOO_LOW
            hipVelocity >= HIP_VELOCITY_TARGET -> MetricFeedback.OPTIMAL
            else -> MetricFeedback.GOOD
        }
    }

    fun getArmSymmetryFeedback(): MetricFeedback {
        return when {
            armSymmetry <= ARM_SYMMETRY_EXCELLENT -> MetricFeedback.OPTIMAL
            armSymmetry <= ARM_SYMMETRY_GOOD -> MetricFeedback.GOOD
            armSymmetry <= ARM_SYMMETRY_ACCEPTABLE -> MetricFeedback.ACCEPTABLE
            else -> MetricFeedback.TOO_HIGH
        }
    }

    companion object {
        // Knee angle thresholds (degrees)
        const val KNEE_ANGLE_MIN = 110.0
        const val KNEE_ANGLE_MAX = 130.0
        const val KNEE_ANGLE_OPTIMAL = 120.0
        val KNEE_ANGLE_OPTIMAL_RANGE = 118.0..122.0

        // Hip velocity thresholds (m/s)
        const val HIP_VELOCITY_MIN = 2.0
        const val HIP_VELOCITY_TARGET = 3.5

        // Arm symmetry thresholds (degrees deviation)
        const val ARM_SYMMETRY_EXCELLENT = 5.0
        const val ARM_SYMMETRY_GOOD = 10.0
        const val ARM_SYMMETRY_ACCEPTABLE = 15.0
    }
}

/**
 * Feedback levels for metrics
 */
@Serializable
enum class MetricFeedback {
    OPTIMAL,
    GOOD,
    ACCEPTABLE,
    TOO_LOW,
    TOO_HIGH
}

/**
 * Sprint analysis result with recommendations
 */
@Serializable
data class SprintAnalysis(
    val metrics: SprintMetrics,
    val recommendations: List<String>,
    val priority: AnalysisPriority
)

/**
 * Priority level for analysis feedback
 */
@Serializable
enum class AnalysisPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}