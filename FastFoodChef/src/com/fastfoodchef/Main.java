package com.fastfoodchef;

import com.fastfoodchef.controller.GameController;
import com.fastfoodchef.model.GameState;
import com.fastfoodchef.view.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameState model = new GameState();
            MainFrame view = new MainFrame();
            com.fastfoodchef.view.ToastManager.init(view);
            new GameController(model, view);
            
            view.setVisible(true);
        });
    }
}
