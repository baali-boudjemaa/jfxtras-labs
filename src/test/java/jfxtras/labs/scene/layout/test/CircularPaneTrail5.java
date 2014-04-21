package jfxtras.labs.scene.layout.test;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jfxtras.labs.scene.layout.CircularPane;
import jfxtras.scene.layout.HBox;

public class CircularPaneTrail5 extends Application {

    public static void main(String[] args) {
        launch(args);       
    }

	@Override
	public void start(Stage stage) {

		HBox lHBox = new HBox();
		
		CircularPane lCircularPane = new CircularPane();
		lCircularPane.setStyle("-fx-border-color:black;");
		lCircularPane.setShowDebug(Color.GREEN);
//		lCircularPane.setStartAngle(315.0); lCircularPane.setArc(90.0);
//		lCircularPane.setStartAngle(0.0); lCircularPane.setArc(90.0);
//		lCircularPane.setStartAngle(30.0); lCircularPane.setArc(90.0);
//		lCircularPane.setStartAngle(30.0); lCircularPane.setArc(180.0);
//		lCircularPane.setStartAngle(90.0); lCircularPane.setArc(90.0);
//		lCircularPane.setStartAngle(120.0); lCircularPane.setArc(90.0);
		lCircularPane.setAnimate(true);
		for (int i = 0; i < 14; i++) {
			javafx.scene.shape.Rectangle c = new javafx.scene.shape.Rectangle(50,50);
			//c.setStroke(Color.RED);
			lCircularPane.getChildren().add(c);
		}
		lHBox.add(lCircularPane);

        // setup scene
		Scene scene = new Scene(lHBox);
		scene.getStylesheets().add(this.getClass().getName().replace(".", "/") + ".css");
		
        // create stage
        stage.setTitle(this.getClass().getSimpleName());
        stage.setScene(scene);
        stage.show();	
	}
	
	

}

	