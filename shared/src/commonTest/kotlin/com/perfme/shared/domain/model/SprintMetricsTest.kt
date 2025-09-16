package com.perfme.shared.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class SprintMetricsTest {

    @Test
    fun testGetKneeAngleFeedback() {
        val optimalMetrics = SprintMetrics(120.0, 3.0, 5.0, 85.0, 123456789L)
        assertEquals(MetricFeedback.OPTIMAL, optimalMetrics.getKneeAngleFeedback())

        val tooLowMetrics = SprintMetrics(100.0, 3.0, 5.0, 70.0, 123456789L)
        assertEquals(MetricFeedback.TOO_LOW, tooLowMetrics.getKneeAngleFeedback())

        val tooHighMetrics = SprintMetrics(140.0, 3.0, 5.0, 70.0, 123456789L)
        assertEquals(MetricFeedback.TOO_HIGH, tooHighMetrics.getKneeAngleFeedback())

        val goodMetrics = SprintMetrics(125.0, 3.0, 5.0, 80.0, 123456789L)
        assertEquals(MetricFeedback.GOOD, goodMetrics.getKneeAngleFeedback())
    }

    @Test
    fun testGetHipVelocityFeedback() {
        val optimalMetrics = SprintMetrics(120.0, 3.5, 5.0, 90.0, 123456789L)
        assertEquals(MetricFeedback.OPTIMAL, optimalMetrics.getHipVelocityFeedback())

        val tooLowMetrics = SprintMetrics(120.0, 1.5, 5.0, 70.0, 123456789L)
        assertEquals(MetricFeedback.TOO_LOW, tooLowMetrics.getHipVelocityFeedback())

        val goodMetrics = SprintMetrics(120.0, 2.5, 5.0, 80.0, 123456789L)
        assertEquals(MetricFeedback.GOOD, goodMetrics.getHipVelocityFeedback())
    }

    @Test
    fun testGetArmSymmetryFeedback() {
        val excellentMetrics = SprintMetrics(120.0, 3.0, 3.0, 90.0, 123456789L)
        assertEquals(MetricFeedback.OPTIMAL, excellentMetrics.getArmSymmetryFeedback())

        val goodMetrics = SprintMetrics(120.0, 3.0, 8.0, 85.0, 123456789L)
        assertEquals(MetricFeedback.GOOD, goodMetrics.getArmSymmetryFeedback())

        val acceptableMetrics = SprintMetrics(120.0, 3.0, 12.0, 75.0, 123456789L)
        assertEquals(MetricFeedback.ACCEPTABLE, acceptableMetrics.getArmSymmetryFeedback())

        val tooHighMetrics = SprintMetrics(120.0, 3.0, 20.0, 60.0, 123456789L)
        assertEquals(MetricFeedback.TOO_HIGH, tooHighMetrics.getArmSymmetryFeedback())
    }

    @Test
    fun testConstants() {
        assertEquals(110.0, SprintMetrics.KNEE_ANGLE_MIN)
        assertEquals(130.0, SprintMetrics.KNEE_ANGLE_MAX)
        assertEquals(120.0, SprintMetrics.KNEE_ANGLE_OPTIMAL)
        assertEquals(2.0, SprintMetrics.HIP_VELOCITY_MIN)
        assertEquals(3.5, SprintMetrics.HIP_VELOCITY_TARGET)
        assertEquals(5.0, SprintMetrics.ARM_SYMMETRY_EXCELLENT)
        assertEquals(10.0, SprintMetrics.ARM_SYMMETRY_GOOD)
        assertEquals(15.0, SprintMetrics.ARM_SYMMETRY_ACCEPTABLE)
    }
}