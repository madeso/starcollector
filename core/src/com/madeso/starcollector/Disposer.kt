package com.madeso.starcollector

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion

class Disposer
{
    fun CreateSprite(texture: Texture, size: Float): Sprite
    {
        val sprite = Sprite(TextureRegion(texture))
        sprite.setSize(size, size)
        sprite.setOrigin(sprite.width / 2,
                sprite.height / 2)
        return sprite
    }

    val textures = mutableListOf<Texture>()
    fun CreateTexture(path: String): Texture
    {
        val texture = Texture(Gdx.files.internal(path))
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        textures.add(texture)
        return texture
    }

    val sounds = mutableListOf<Sound>()
    fun CreateSound(path: String) : Sound
    {
        val sound = Gdx.audio.newSound(Gdx.files.internal(path))
        sounds.add(sound)
        return sound
    }

    val musics = mutableListOf<Music>()
    fun CreateMusic(path: String) : Music
    {
        val music = Gdx.audio.newMusic(Gdx.files.internal(path))
        musics.add(music)
        return music
    }

    fun DisposeAll()
    {
        for(texture in textures)
        {
            texture.dispose()
        }
        textures.clear()

        for(sound in sounds)
        {
            sound.dispose()
        }
        sounds.clear()

        for(music in musics)
        {
            music.dispose()
        }
        musics.clear()
    }
}