package swe.ka.dhbw.control;

public class GUIController {
    private static GUIController instance;

    private GUIController() {}

    public static synchronized GUIController getInstance() {
        if(instance == null) {
            instance = new GUIController();
        }
        return instance;
    }
}
