package com.lostpages.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SmileScreen implements Screen {

    final Page game;

    OrthographicCamera camera;
    Animation smile;
    Music mellohi;
    float elapsed;


    public SmileScreen(final Page gam) {
        game = gam;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        smile = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("ranboosmile.gif").read());

        mellohi = Gdx.audio.newMusic(Gdx.files.internal("mellohi.mp3"));
    }

    @Override
    public void show() {
        mellohi.play();
    }

    @Override
    public void render(float delta) {
        elapsed += Gdx.graphics.getDeltaTime();
//        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw((TextureRegion) smile.getKeyFrame(elapsed), 0, 0);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        mellohi.dispose();
    }

}