package org.pbochnacki;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Main implements NativeMouseInputListener {
    final int MOUSE_BUTTON_4 = 4; //back mouse button
    final int MOUSE_BUTTON_5 = 5; //forward mouse button
    final int MOUSE_DRAG_DISTANCE_THRESHOLD = 200; //required cursor distance from starting point to current

    boolean rollPressed = false;
    int startingPointX = 0;

    final Robot robot;

    public Main() throws AWTException {
        robot = new Robot();
    }

    @Override
    public void nativeMousePressed(NativeMouseEvent nativeEvent) {
        if (nativeEvent.getButton() == NativeMouseEvent.BUTTON3) {
            rollPressed = true;
            startingPointX = nativeEvent.getX();
        }
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeEvent) {
        if (nativeEvent.getButton() == NativeMouseEvent.BUTTON3) {
            resetRollPressInfo();
        }
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
        //check if roll is pressed and starting point X set
        if (rollPressed) {
            if (startingPointX != 0) {
                int currentDistanceToStartingPointX = startingPointX - nativeEvent.getX();

                //if mouse gets dragged to the left, navigate back
                if (currentDistanceToStartingPointX >= MOUSE_DRAG_DISTANCE_THRESHOLD) {
                    robot.mousePress(MouseEvent.getMaskForButton(MOUSE_BUTTON_4));
                    robot.mouseRelease(MouseEvent.getMaskForButton(MOUSE_BUTTON_4));
                    resetRollPressInfo();
                }

                //if mouse gets dragged to the right, navigate forward
                if (currentDistanceToStartingPointX <= -MOUSE_DRAG_DISTANCE_THRESHOLD) {
                    robot.mousePress(MouseEvent.getMaskForButton(MOUSE_BUTTON_5));
                    robot.mouseRelease(MouseEvent.getMaskForButton(MOUSE_BUTTON_5));
                    resetRollPressInfo();
                }
            }
        }
    }

    //required to avoid navigating multiple times during single press
    private void resetRollPressInfo() {
        rollPressed = false;
        startingPointX = 0;
    }

    public static void main(String[] args) throws AWTException {
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            System.err.println("There was a problem registering the native hook.");
            System.exit(1);
        }

        Main main = new Main();

        GlobalScreen.addNativeMouseListener(main);
        GlobalScreen.addNativeMouseMotionListener(main);

    }
}