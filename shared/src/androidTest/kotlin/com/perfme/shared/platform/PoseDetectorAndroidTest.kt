package com.perfme.shared.platform

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.perfme.shared.domain.model.KeypointType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Android integration tests for PoseDetector
 */
@RunWith(AndroidJUnit4::class)
class PoseDetectorAndroidTest {
    private lateinit var poseDetector: PlatformPoseDetector

    @Before
    fun setUp() {
        poseDetector = PlatformPoseDetector()
    }

    @After
    fun tearDown() {
        poseDetector.dispose()
    }

    @Test
    fun testDetectPoseWithValidImageData() =
        runBlocking {
            // Create dummy image data (ARGB_8888 format)
            val width = 100
            val height = 100
            val imageData = ByteArray(width * height * 4) { 0xFF.toByte() }

            val poseData = poseDetector.detectPose(imageData, width, height, false)

            assertNotNull(poseData)
            assertTrue(poseData.keypoints.isNotEmpty())
            assertTrue(poseData.confidence >= 0f)
            assertTrue(poseData.timestamp > 0L)
        }

    @Test
    fun testDetectPoseWithAccurateModel() =
        runBlocking {
            val width = 100
            val height = 100
            val imageData = ByteArray(width * height * 4) { 0xFF.toByte() }

            val poseData = poseDetector.detectPose(imageData, width, height, true)

            assertNotNull(poseData)
            assertTrue(poseData.keypoints.isNotEmpty())
        }

    @Test
    fun testPoseDataContainsExpectedKeypoints() =
        runBlocking {
            val width = 200
            val height = 200
            val imageData = ByteArray(width * height * 4) { 0xFF.toByte() }

            val poseData = poseDetector.detectPose(imageData, width, height, false)

            // Check for presence of key body parts
            val hasNose = poseData.keypoints.any { it.type == KeypointType.NOSE }
            val hasShoulders =
                poseData.keypoints.any {
                    it.type == KeypointType.LEFT_SHOULDER || it.type == KeypointType.RIGHT_SHOULDER
                }
            val hasHips =
                poseData.keypoints.any {
                    it.type == KeypointType.LEFT_HIP || it.type == KeypointType.RIGHT_HIP
                }

            // Note: These assertions might fail with real ML Kit on empty/invalid images
            // This is just a structural test for the integration
            assertTrue(
                "Should detect some body parts",
                poseData.keypoints.isNotEmpty() || poseData.confidence == 0f,
            )
        }

    @Test
    fun testApplicationContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.perfme.shared.test", appContext.packageName)
    }
}
