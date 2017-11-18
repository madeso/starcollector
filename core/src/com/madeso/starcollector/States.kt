package com.madeso.starcollector

import com.badlogic.gdx.graphics.OrthographicCamera

class States(val cam : OrthographicCamera, val background : Background, val world : World)
{
    var game : Game? = null
    enum class State
    {
        ZoomDown,
        Create,
        ZoomOut,
        ZoomToPlay,
        Play
    }

    // 0-1 zoom in, 1+ zoom out
    val zoomedOut = 17f
    val zoomedPlay = 1f
    val zoomedClose = 0.5f

    // transition time for each time state
    val zoomdowntime = 0.8f
    val zoomouttime = 1f
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
                cam.zoom = Lerp(zoomedPlay, timer/zoomdowntime, zoomedOut)
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
                cam.zoom = Lerp(zoomedOut, timer/zoomouttime, zoomedClose)
                timer += dt
                if(timer > zoomouttime)
                {
                    state = State.ZoomToPlay
                    background.SetRandomBackground()
                }
            }
            State.ZoomToPlay -> {
                cam.zoom = Lerp(zoomedClose, timer/zoomtoplaytime, zoomedPlay)
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

    fun Lerp(v0:Float, t:Float, v1: Float) =  (1f - t) * v0 + t * v1;
}