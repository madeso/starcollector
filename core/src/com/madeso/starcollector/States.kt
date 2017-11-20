package com.madeso.starcollector

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation

class States(val cam : OrthographicCamera, val background : Background, val world : World<Int>)
{
    var game : Game? = null
    enum class State
    {
        ZoomDown,
        Create,
        ZoomOut,
        WaitZoomedIn,
        ZoomToPlay,
        Play
    }

    // 0-1 zoom in, 1+ zoom out
    val zoomedOut = 25f
    val zoomedPlay = 1f
    val zoomedClose = 0.5f

    // transition time for each time state
    val zoomdowntime = 0.8f
    val zoomouttime = 1f
    val waitzoomedintime = 0.05f
    val zoomtoplaytime = 0.5f

    var state = State.Create
    var timer = 0f

    fun CanPlay() = state == State.Play

    fun CreateNewWorld()
    {
        state = State.ZoomDown
    }

    fun Update(dt : Float)
    {
        val last_state = state
        when(state)
        {
            State.ZoomDown -> {
                cam.zoom = Interpolation.sineOut.apply(zoomedPlay, zoomedOut, timer/zoomdowntime)
                timer += dt
                if(timer > zoomdowntime)
                {
                    state = State.Create
                }
            }
            State.Create -> {
                cam.zoom = zoomedOut
                state = State.ZoomOut

                game!!.CreateWorld()
            }
            State.ZoomOut -> {
                cam.zoom = Interpolation.exp10.apply(zoomedOut, zoomedClose, timer/zoomouttime)
                timer += dt
                if(timer > zoomouttime)
                {
                    state = State.WaitZoomedIn
                    background.SetRandomBackground()
                }
            }
            State.WaitZoomedIn -> {
                cam.zoom = zoomedClose
                timer += dt
                if(timer > waitzoomedintime)
                {
                    state = State.ZoomToPlay
                }
            }
            State.ZoomToPlay -> {
                cam.zoom = Interpolation.bounceOut.apply(zoomedClose, zoomedPlay, timer/zoomtoplaytime)
                timer += dt
                if(timer > zoomtoplaytime)
                {
                    state = State.Play
                }
            }
            State.Play -> {
                cam.zoom = zoomedPlay
            }
        }

        if(last_state != state)
        {
            timer = 0f
        }
    }
}