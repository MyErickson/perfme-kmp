package com.perfme.shared.domain.usecase

import com.perfme.shared.domain.model.Keypoint
import com.perfme.shared.domain.model.KeypointType
import com.perfme.shared.domain.model.Point3D
import com.perfme.shared.domain.model.PoseData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BiomechanicsEngineTest {

    private val biomechanicsEngine = BiomechanicsEngine()

    private fun createTestPoseData(timestamp: Long = 123456789L): PoseData {
        val keypoints = listOf(
            Keypoint(Point3D(100f, 50f), 0.9f, KeypointType.NOSE),
            Keypoint(Point3D(80f, 80f), 0.8f, KeypointType.LEFT_SHOULDER),
            Keypoint(Point3D(120f, 80f), 0.8f, KeypointType.RIGHT_SHOULDER),
            Keypoint(Point3D(70f, 120f), 0.7f, KeypointType.LEFT_ELBOW),
            Keypoint(Point3D(130f, 120f), 0.7f, KeypointType.RIGHT_ELBOW),
            Keypoint(Point3D(60f, 160f), 0.6f, KeypointType.LEFT_WRIST),
            Keypoint(Point3D(140f, 160f), 0.6f, KeypointType.RIGHT_WRIST),
            Keypoint(Point3D(85f, 200f), 0.8f, KeypointType.LEFT_HIP),
            Keypoint(Point3D(115f, 200f), 0.8f, KeypointType.RIGHT_HIP),
            Keypoint(Point3D(80f, 280f), 0.7f, KeypointType.LEFT_KNEE),
            Keypoint(Point3D(120f, 280f), 0.7f, KeypointType.RIGHT_KNEE),
            Keypoint(Point3D(75f, 360f), 0.6f, KeypointType.LEFT_ANKLE),
            Keypoint(Point3D(125f, 360f), 0.6f, KeypointType.RIGHT_ANKLE)
        )

        return PoseData(keypoints, 0.8f, timestamp)
    }

    @Test
    fun testAnalyzeSprintPoseWithoutPreviousPose() {
        val currentPose = createTestPoseData()
        val metrics = biomechanicsEngine.analyzeSprintPose(currentPose)

        assertTrue(metrics.kneeAngle > 0.0)
        assertEquals(0.0, metrics.hipVelocity) // Should be 0 without previous pose
        assertTrue(metrics.armSymmetry >= 0.0)
        assertTrue(metrics.overallScore >= 0.0 && metrics.overallScore <= 100.0)
        assertTrue(metrics.timestamp > 0L)
    }

    @Test
    fun testAnalyzeSprintPoseWithPreviousPose() {
        val previousPose = createTestPoseData(123456789L)
        val currentPose = createTestPoseData(123456889L) // 100ms later

        val metrics = biomechanicsEngine.analyzeSprintPose(currentPose, previousPose)

        assertTrue(metrics.kneeAngle > 0.0)
        assertTrue(metrics.hipVelocity >= 0.0) // Should calculate velocity
        assertTrue(metrics.armSymmetry >= 0.0)
        assertTrue(metrics.overallScore >= 0.0 && metrics.overallScore <= 100.0)
    }

    @Test
    fun testAnalyzeSprintPoseWithMovedHips() {
        val previousPose = createTestPoseData(123456789L)

        // Create current pose with moved hips
        val movedKeypoints = listOf(
            Keypoint(Point3D(100f, 50f), 0.9f, KeypointType.NOSE),
            Keypoint(Point3D(80f, 80f), 0.8f, KeypointType.LEFT_SHOULDER),
            Keypoint(Point3D(120f, 80f), 0.8f, KeypointType.RIGHT_SHOULDER),
            Keypoint(Point3D(95f, 200f), 0.8f, KeypointType.LEFT_HIP), // Moved 10 pixels right
            Keypoint(Point3D(125f, 200f), 0.8f, KeypointType.RIGHT_HIP), // Moved 10 pixels right
            Keypoint(Point3D(80f, 280f), 0.7f, KeypointType.LEFT_KNEE),
            Keypoint(Point3D(120f, 280f), 0.7f, KeypointType.RIGHT_KNEE),
            Keypoint(Point3D(75f, 360f), 0.6f, KeypointType.LEFT_ANKLE),
            Keypoint(Point3D(125f, 360f), 0.6f, KeypointType.RIGHT_ANKLE)
        )

        val currentPose = PoseData(movedKeypoints, 0.8f, 123456889L) // 100ms later

        val metrics = biomechanicsEngine.analyzeSprintPose(currentPose, previousPose)

        assertTrue(metrics.hipVelocity > 0.0) // Should detect movement
    }

    @Test
    fun testAnalyzeSprintPoseWithSymmetricArms() {
        // Create pose with symmetric arms
        val symmetricKeypoints = listOf(
            Keypoint(Point3D(100f, 50f), 0.9f, KeypointType.NOSE),
            Keypoint(Point3D(80f, 80f), 0.8f, KeypointType.LEFT_SHOULDER),
            Keypoint(Point3D(120f, 80f), 0.8f, KeypointType.RIGHT_SHOULDER),
            Keypoint(Point3D(70f, 120f), 0.7f, KeypointType.LEFT_ELBOW),
            Keypoint(Point3D(130f, 120f), 0.7f, KeypointType.RIGHT_ELBOW), // Symmetric position
            Keypoint(Point3D(60f, 160f), 0.6f, KeypointType.LEFT_WRIST),
            Keypoint(Point3D(140f, 160f), 0.6f, KeypointType.RIGHT_WRIST), // Symmetric position
            Keypoint(Point3D(85f, 200f), 0.8f, KeypointType.LEFT_HIP),
            Keypoint(Point3D(115f, 200f), 0.8f, KeypointType.RIGHT_HIP)
        )

        val symmetricPose = PoseData(symmetricKeypoints, 0.8f, 123456789L)
        val metrics = biomechanicsEngine.analyzeSprintPose(symmetricPose)

        assertTrue(metrics.armSymmetry < 10.0) // Should be relatively symmetric
    }

    @Test
    fun testAnalyzeSprintPoseWithAsymmetricArms() {
        // Create pose with asymmetric arms
        val asymmetricKeypoints = listOf(
            Keypoint(Point3D(100f, 50f), 0.9f, KeypointType.NOSE),
            Keypoint(Point3D(80f, 80f), 0.8f, KeypointType.LEFT_SHOULDER),
            Keypoint(Point3D(120f, 80f), 0.8f, KeypointType.RIGHT_SHOULDER),
            Keypoint(Point3D(70f, 120f), 0.7f, KeypointType.LEFT_ELBOW),
            Keypoint(Point3D(150f, 100f), 0.7f, KeypointType.RIGHT_ELBOW), // Very different position
            Keypoint(Point3D(60f, 160f), 0.6f, KeypointType.LEFT_WRIST),
            Keypoint(Point3D(180f, 80f), 0.6f, KeypointType.RIGHT_WRIST), // Very different position
            Keypoint(Point3D(85f, 200f), 0.8f, KeypointType.LEFT_HIP),
            Keypoint(Point3D(115f, 200f), 0.8f, KeypointType.RIGHT_HIP)
        )

        val asymmetricPose = PoseData(asymmetricKeypoints, 0.8f, 123456789L)
        val metrics = biomechanicsEngine.analyzeSprintPose(asymmetricPose)

        assertTrue(metrics.armSymmetry > 20.0) // Should be highly asymmetric
    }
}