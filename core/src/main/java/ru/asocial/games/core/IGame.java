package ru.asocial.games.core;

public interface IGame {
    ResourcesManager getResourcesManager();

    void setResourceManager(ResourcesManager resourcesManager);

    void onLoad();

}
