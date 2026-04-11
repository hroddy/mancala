/**
 * MancalaListener.java
 * 
 * Interface that MancalaView will implement to listen for changes in the MancalaModel and update the view
 * Decouples model from view so model can notify any object that implements this contract without knowing its concrete type.
 * ManaclaView will implement boardChanged() to update the view when the model changes.
 * 
 * @author Hannah Roddy
 * @author Johnny Tsai
 * @author Nishan Bhattarai
 */

public interface MancalaListener {
    public void boardChanged();
}
