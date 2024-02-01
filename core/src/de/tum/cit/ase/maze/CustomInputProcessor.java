package de.tum.cit.ase.maze;

import com.badlogic.gdx.InputProcessor;

/**A class which lets us create a custom input processing setting, to better control and handle
        *input in any screen. It mostly helps fix bugs related to undesired input handling. E.g: mouse.
*/
public class CustomInputProcessor implements InputProcessor {
    boolean keyDown, keyUp, keyTyped, touchDown, touchUp,
    touchCancelled, touchDragged, mouseMoved, scrolled;

    public CustomInputProcessor(boolean keyDown, boolean keyUp, boolean keyTyped, boolean touchDown, boolean touchUp, boolean touchCancelled, boolean touchDragged, boolean mouseMoved, boolean scrolled) {
        this.keyDown = keyDown;
        this.keyUp = keyUp;
        this.keyTyped = keyTyped;
        this.touchDown = touchDown;
        this.touchUp = touchUp;
        this.touchCancelled = touchCancelled;
        this.touchDragged = touchDragged;
        this.mouseMoved = mouseMoved;
        this.scrolled = scrolled;
    }

    @Override
    public boolean keyDown(int keycode) {
        return keyDown;
    }

    @Override
    public boolean keyUp(int keycode) {
        return keyUp;
    }

    @Override
    public boolean keyTyped(char character) {
        return keyTyped;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return touchDown;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return touchUp;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return touchCancelled;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return touchDragged;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return mouseMoved;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return scrolled;
    }

}
