package baaahs.shows

import baaahs.*
import kotlin.random.Random

val RandomShow = object : ShowMeta("Random") {
    override fun createShow(sheepModel: SheepModel, showRunner: ShowRunner) = object : Show {
        val pixelShaderBuffers = sheepModel.allPanels.map { showRunner.getPixelShader(it).buffer }
        val movingHeadBuffers = sheepModel.eyes.map { showRunner.getMovingHead(it) }

        override fun nextFrame() {
            pixelShaderBuffers.forEach { shaderBuffer ->
                shaderBuffer.colors.forEachIndexed { i, pixel ->
                    shaderBuffer.colors[i] = Color.random()
                }
            }

            movingHeadBuffers.forEach { shenzarpy ->
                shenzarpy.colorWheel = shenzarpy.closestColorFor(Color.random())
                shenzarpy.pan = Random.nextFloat() * Shenzarpy.panRange.endInclusive
                shenzarpy.tilt = Random.nextFloat() * Shenzarpy.tiltRange.endInclusive
            }
        }
    }
}