package ru.asocial.games.core;

import com.badlogic.gdx.scenes.scene2d.Group;

public class CombinedPlayerController extends Group implements IPlayerController {

    private final IPlayerController[] controllers;

    public CombinedPlayerController(IPlayerController...controllers) {
        if (controllers == null) {
            throw new AssertionError("controllers is null");
        }

        this.controllers = controllers;
    }

    @Override
    public boolean isUpPressed() {
        for (IPlayerController controller : controllers) {
            if (controller.isUpPressed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDownPressed() {
        for (IPlayerController controller : controllers) {
            if (controller.isDownPressed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isLeftPressed() {
        for (IPlayerController controller : controllers) {
            if (controller.isLeftPressed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRightPressed() {
        for (IPlayerController controller : controllers) {
            if (controller.isRightPressed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBombPressed() {
        for (IPlayerController controller : controllers) {
            if (controller.isBombPressed()) {
                return true;
            }
        }
        return false;
    }
}
