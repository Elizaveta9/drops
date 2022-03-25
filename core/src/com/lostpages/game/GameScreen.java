package com.lostpages.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;

public class GameScreen implements Screen {

    final Page game;

    OrthographicCamera camera;
    SpriteBatch batch;

    Texture paperImage;
    Texture paperSmileImage;
    Texture handImage;

    Animation bgGif;

    Sound paperSound;
    Sound errorSound;

    Music fallenDown;

    Rectangle hand;

    Vector3 touchPos;

    Array<PageClass> papers;
    Array<Texture> textures;

    // массив слов-объектов
    Array<Memory> memoriesArray;

    // массив слов-строк
    ArrayList<String> memoriesList = new ArrayList<>();

    long lastPaperTime;
    int pagesGathered;
    float elapsed;

    public GameScreen(final Page gam) {
        this.game = gam;

        memoriesList.add("traitor");
        memoriesList.add("You don't remember");
        memoriesList.add("It's your fault");
        memoriesList.add(":)");
        memoriesList.add("You can't remember");
        memoriesList.add("You are lost");
        memoriesList.add("They don't trust you");
        memoriesList.add("Learn your lessons");
        memoriesList.add("If you have the opportunity to gain a favor, take it");
        memoriesList.add("Do not reminisce on what you have lost");
        memoriesList.add("Never fully trust anyone");
        memoriesList.add("Leave no evidence of what you helped with");
        memoriesList.add("DO NOT LET THEM KNOW WHAT YOU HAVE DONE");




        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        batch = new SpriteBatch();
        touchPos = new Vector3();

        memoriesArray = new Array<>();
        textures = new Array<>();

        paperImage = new Texture("paper.png");
        handImage = new Texture("handbw.png");
        paperSmileImage = new Texture("papersmile.png");

        bgGif = GifDecoder.loadGIFAnimation(Animation.PlayMode.LOOP, Gdx.files.internal("obbyroom.gif").read());

        paperSound = Gdx.audio.newSound(Gdx.files.internal("paper16bit.wav"));
        errorSound = Gdx.audio.newSound(Gdx.files.internal("enderman.wav"));
        fallenDown = Gdx.audio.newMusic(Gdx.files.internal("fallendown.mp3"));

        fallenDown.setLooping(true);

        hand = new Rectangle();
        hand.x = 800 / 2 - 60 / 2;
        hand.y = 20;
        hand.height = 15;
        hand.width = 100;

        papers = new Array<>();
        spawnPapers();
    }

    private void spawnPapers() {
        PageClass paper = new PageClass();
        paper.x = MathUtils.random(0, 800 - 64);
        paper.y = 480;
        paper.width = 64;
        paper.height = 64;

        if (MathUtils.random(0, 19) == 0) {
            paper.texture = paperSmileImage;
            paper.isSmile = true;
        } else {
            paper.texture = paperImage;
            paper.isSmile = false;
        }

        papers.add(paper);
        lastPaperTime = TimeUtils.nanoTime();
    }

    private void spawnText() {
        Memory word = new Memory();
        word.memory = memoriesList.get(MathUtils.random(0, memoriesList.toArray().length - 1));
        word.x = MathUtils.random(0, 800);
        word.y = MathUtils.random(0, 480);
        memoriesArray.add(word);
    }


    @Override
    public void render(float delta) {
        elapsed += Gdx.graphics.getDeltaTime();
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        game.batch.draw((TextureRegion) bgGif.getKeyFrame(elapsed), 0, 0);
        game.batch.draw(handImage, hand.x, hand.y);

        for (PageClass paper : papers) {
            game.batch.draw(paper.texture, paper.x, paper.y);
        }

        game.font.draw(game.batch, "Pages Collected: " + pagesGathered, 0, 480);

        for (Memory memory : memoriesArray) {
            game.font.draw(game.batch, memory.memory, memory.x, memory.y);
        }

        game.batch.end();

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            hand.x = (int) (touchPos.x - 60 / 2);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            hand.x -= 600 * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            hand.x += 600 * Gdx.graphics.getDeltaTime();
        }

        if (hand.x < 0) {
            hand.x = 0;
        }

        if (hand.x > 800 - 100) {
            hand.x = 800 - 100;
        }

        if (TimeUtils.nanoTime() - lastPaperTime > MathUtils.random(100000000,1000000000)) {
            spawnPapers();
        }

        Iterator<PageClass> iterator = papers.iterator();
        while (iterator.hasNext()) {
            PageClass paper = iterator.next();
            paper.y -= 200 * Gdx.graphics.getDeltaTime();
            if (paper.y + 64 < 0) {
                iterator.remove();
                spawnText();
            }
            if (paper.overlaps(hand)) {
                if (paper.isSmile) {
                    fallenDown.pause();
                    errorSound.play();
                    game.setScreen(new SmileScreen(game));
                    dispose();
                } else {
                    pagesGathered++;
                    paperSound.play();
                }

                iterator.remove();
            }
        }

    }

    @Override
    public void show() {
        fallenDown.play();
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
        batch.dispose();
        paperImage.dispose();
        paperSmileImage.dispose();
        handImage.dispose();
        paperSound.dispose();
        fallenDown.dispose();
    }
}
