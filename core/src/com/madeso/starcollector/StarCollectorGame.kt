package com.madeso.starcollector

import com.badlogic.gdx.ApplicationListener


class StarCollectorGame : ApplicationListener {
    private lateinit var game : StarCollector
    var disposer = Disposer()

    override fun create() {
        game = StarCollector(disposer)
    }

    override fun dispose() {
        disposer.DisposeAll()
    }

    override fun render() {
        game.render()
    }

    override fun resize(width: Int, height: Int) {
        game.OnSize()
    }

    override fun pause() {}

    override fun resume() {}
}