public interface MancalaListener {
    // Interface that MancalaView will implement to listen for changes in the MancalaModel and update the view
    // Decouples the model from the view so the model can notify any object that implements this contract without knowing its concrete type.
    // ManaclaView will implement boardChanged() to update the view when the model changes.
    public void boardChanged();
}
