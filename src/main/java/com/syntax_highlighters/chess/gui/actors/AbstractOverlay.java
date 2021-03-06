package com.syntax_highlighters.chess.gui.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.syntax_highlighters.chess.gui.AssetLoader;
import com.syntax_highlighters.chess.gui.LibgdxChessGame;
import com.syntax_highlighters.chess.gui.WobbleDrawable;

/**
 * Abstract overlay class implementing common functionality between overlays.
 */
class AbstractOverlay extends Actor {
    private final Texture pixel;
    private final WobbleDrawable overlay;
    private final Text title;

    AbstractOverlay(String titleText, AssetManager assetManager) {
        pixel = assetManager.get("pixel.png", Texture.class);
        overlay = new WobbleDrawable(assetManager.get("overlay.png", Texture.class), assetManager);
        overlay.doAnimation(false);

        BitmapFont font = AssetLoader.GetDefaultFont(assetManager);
        title = new Text(font);
        title.setText(titleText);
        title.setColor(0,0,0,1);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(0,0,0,075f);
        batch.draw(pixel, 0, 0, LibgdxChessGame.WORLDWIDTH, LibgdxChessGame.WORLDHEIGHT);
        float cw = LibgdxChessGame.WORLDWIDTH / 2.f;
        float ch = LibgdxChessGame.WORLDHEIGHT / 2.f;
        float tw = overlay.getTexture().getWidth() / 1.6f;
        float th = overlay.getTexture().getHeight() / 1.6f;
        setBounds(cw - tw / 2.f, ch - th/2.f, tw, th);
        batch.setColor(1,1,1,1);
        overlay.draw(batch, getX(), getY(), getWidth(), getHeight());

        title.setCenter(getX() + getWidth()/2.f, getY() + getHeight() - 100);
        title.draw(batch, parentAlpha);
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return this;
    }
}
