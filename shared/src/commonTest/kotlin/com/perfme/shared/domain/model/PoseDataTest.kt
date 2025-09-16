package com.perfme.shared.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PoseDataTest {

    private fun createTestKeypoints(): List<Keypoint> {
        return listOf(
            Keypoint(Point3D(100f, 50f), 0.9f, KeypointType.NOSE),
            Keypoint(Point3D(80f, 80f), 0.8f, KeypointType.LEFT_SHOULDER),
            Keypoint(Point3D(120f, 80f), 0.8f, KeypointType.RIGHT_SHOULDER),
            Keypoint(Point3D(70f, 120f), 0.7f, KeypointType.LEFT_ELBOW),
            Keypoint(Point3D(60f, 160f), 0.6f, KeypointType.LEFT_WRIST),
            Keypoint(Point3D(85f, 200f), 0.8f, KeypointType.LEFT_HIP),
            Keypoint(Point3D(80f, 280f), 0.7f, KeypointType.LEFT_KNEE),
            Keypoint(Point3D(75f, 360f), 0.6f, KeypointType.LEFT_ANKLE)
        )
    }

    @Test
    fun testGetKeypoint() {
        val keypoints = createTestKeypoints()
        val poseData = PoseData(keypoints, 0.8f, 123456789L)

        val nose = poseData.getKeypoint(KeypointType.NOSE)
        assertNotNull(nose)
        assertEquals(KeypointType.NOSE, nose.type)
        assertEquals(100f, nose.position.x)

        val nonExistent = poseData.getKeypoint(KeypointType.RIGHT_WRIST)
        assertNull(nonExistent)
    }

    @Test
    fun testIsValid() {
        val keypoints = createTestKeypoints()
        val validPose = PoseData(keypoints, 0.8f, 123456789L)
        assertTrue(validPose.isValid())
        assertTrue(validPose.isValid(0.5f))
        assertFalse(validPose.isValid(0.9f))

        val invalidPose = PoseData(emptyList(), 0.3f, 123456789L)
        assertFalse(invalidPose.isValid())
    }

    @Test
    fun testGetLeftArm() {
        val keypoints = createTestKeypoints()
        val poseData = PoseData(keypoints, 0.8f, 123456789L)

        val leftArm = poseData.getLeftArm()
        assertEquals(3, leftArm.size)
        assertTrue(leftArm.any { it.type == KeypointType.LEFT_SHOULDER })
        assertTrue(leftArm.any { it.type == KeypointType.LEFT_ELBOW })
        assertTrue(leftArm.any { it.type == KeypointType.LEFT_WRIST })
    }

    @Test
    fun testGetLeftLeg() {
        val keypoints = createTestKeypoints()
        val poseData = PoseData(keypoints, 0.8f, 123456789L)

        val leftLeg = poseData.getLeftLeg()
        assertEquals(3, leftLeg.size)
        assertTrue(leftLeg.any { it.type == KeypointType.LEFT_HIP })
        assertTrue(leftLeg.any { it.type == KeypointType.LEFT_KNEE })
        assertTrue(leftLeg.any { it.type == KeypointType.LEFT_ANKLE })
    }

    @Test
    fun testGetRightArmEmpty() {
        val keypoints = createTestKeypoints()
        val poseData = PoseData(keypoints, 0.8f, 123456789L)

        val rightArm = poseData.getRightArm()
        assertEquals(1, rightArm.size) // Only RIGHT_SHOULDER exists in test data
        assertTrue(rightArm.any { it.type == KeypointType.RIGHT_SHOULDER })
    }
}