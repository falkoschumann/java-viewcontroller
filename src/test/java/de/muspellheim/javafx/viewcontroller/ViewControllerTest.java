/*
 * Copyright (c) 2017 Falko Schumann
 * Released under the terms of the MIT License.
 */

package de.muspellheim.javafx.viewcontroller;

import javafx.scene.paint.*;
import javafx.stage.*;
import org.junit.*;
import org.testfx.framework.junit.*;

import java.util.*;

import static org.junit.Assert.*;

public class ViewControllerTest extends ApplicationTest {

    private StageController stageController;

    private List<String> viewEvents = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        stageController = new StageController(stage);
    }

    @Override
    public void stop() throws Exception {
        stageController.hide();
    }

    @Test
    public void testViewEvents() {
        ViewController viewController = new HelloWorldViewController(viewEvents);
        stageController.setRootViewController(viewController);
        assertEquals(Collections.emptyList(), viewEvents);

        viewController.getView();
        assertEquals(Arrays.asList("helloWorld:viewDidLoad"), viewEvents);

        interact(() -> stageController.show());
        assertEquals(Arrays.asList("helloWorld:viewDidLoad", "helloWorld:viewWillAppear", "helloWorld:viewDidAppear"), viewEvents);

        interact(() -> stageController.hide());
        assertEquals(Arrays.asList("helloWorld:viewDidLoad", "helloWorld:viewWillAppear", "helloWorld:viewDidAppear", "helloWorld:viewWillDisappear", "helloWorld:viewDidDisappear"), viewEvents);
    }

    @Test
    public void testPresent() {
        // TODO public void present(ViewController viewControllerToPresent, Runnable completion) -> complete after viewDidAppear

        // View controller hierarchy: green
        ViewController green = new ColoredViewController("green", Color.LIGHTGREEN, viewEvents);
        stageController.setRootViewController(green);
        interact(() -> stageController.show());
        assertSame(green.getView(), stageController.getStage().getScene().getRoot());
        assertNull(green.getPresentingViewController());
        assertNull(green.getPresentedViewController());

        // View controller hierarchy: green -> blue
        ViewController blue = new ColoredViewController("blue", Color.LIGHTBLUE, viewEvents);
        green.present(blue);
        assertSame(blue.getView(), stageController.getStage().getScene().getRoot());
        assertNull(green.getPresentingViewController());
        assertSame(blue, green.getPresentedViewController());
        assertSame(green, blue.getPresentingViewController());
        assertNull(blue.getPresentedViewController());

        // View controller hierarchy: green -> blue -> yellow
        ViewController yellow = new ColoredViewController("yellow", Color.LIGHTYELLOW, viewEvents);
        blue.present(yellow);
        assertSame(yellow.getView(), stageController.getStage().getScene().getRoot());
        assertNull(green.getPresentingViewController());
        assertSame(blue, green.getPresentedViewController());
        assertSame(green, blue.getPresentingViewController());
        assertSame(yellow, blue.getPresentedViewController());
        assertSame(blue, yellow.getPresentingViewController());
        assertNull(yellow.getPresentedViewController());
    }

    @Test
    public void testToString() {
        ViewController viewController = new TestingViewController(viewEvents);

        viewController.setTitle("foo");
        assertEquals("ViewController{title='foo'}", viewController.toString());

        viewController.setTitle("bar");
        assertEquals("ViewController{title='bar'}", viewController.toString());
    }

}
