package de.mc8051.arma3launcher.interfaces;

/**
 * Created by gurkengewuerz.de on 25.03.2020.
 */
public interface Observable {

    public void addObserver(Observer observer);
    public void removeObserver(Observer observer);
    public void notifyObservers(Object obj);
}
