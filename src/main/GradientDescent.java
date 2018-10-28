package main;

import java.util.ArrayList;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GradientDescent extends Application {
	private Canvas can;
	private GraphicsContext gc;
	private Timeline tl_draw;

	private ArrayList<Point2D> points = new ArrayList<Point2D>();

	private double m = 1, b = 0;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() throws Exception {
		tl_draw = new Timeline(new KeyFrame(Duration.millis(1000.0 / 60.0), e -> {
			draw();
		}));
		tl_draw.setCycleCount(Timeline.INDEFINITE);
		tl_draw.play();
	}

	public void start(Stage stage) throws Exception {
		Pane root = new Pane();
		Scene scene = new Scene(root, 800, 800);

		stage.setTitle("Sample Text");

		can = new Canvas(scene.getWidth(), scene.getHeight());
		gc = can.getGraphicsContext2D();

		root.getChildren().add(can);
		// root.setStyle("-fx-background-color: #000000");

		scene.widthProperty().addListener((obsv, oldVal, newVal) -> {
			can.setWidth(newVal.doubleValue());
		});

		scene.heightProperty().addListener((obsv, oldVal, newVal) -> {
			can.setHeight(newVal.doubleValue());
		});

		scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				final double x = map(e.getX(), 0, can.getWidth(), 0, 1);
				final double y = map(e.getY(), 0, can.getHeight(), 1, 0);
				points.add(new Point2D(x, y));
			}
		});

		stage.setScene(scene);
		stage.show();
		
		//setup
		gc.setFill(Color.RED);
		gc.setStroke(Color.BLUE);
		
		
		Random r = new Random();
		for (int i = 0; i < 200; i ++) {
			final double x = r.nextDouble();
			final double y = r.nextDouble();
			points.add(new Point2D(x, y));
		}
	}

	private void calc() {
		final double lRate = 0.01;
		for (Point2D p : points) {
			final double x = p.getX();
			final double y = p.getY();
			final double guess = m * x + b;
			final double error = y - guess;
			b += error * lRate;
			m += error * x * lRate;
		}
	}

	private void draw() {
		drawPoints();
		if (points.size() > 1) {
			drawLine();
			calc();
		}
	}

	private void drawPoints() {
		double s = 10;

		gc.clearRect(0, 0, can.getWidth(), can.getHeight());

		for (Point2D p : points) {
			double x = map(p.getX(), 0, 1, 0, can.getWidth());
			double y = map(p.getY(), 0, 1, can.getHeight(), 0);
			
			gc.fillOval(x - s * 0.5, y - s * 0.5, s, s);
		}
	}

	private void drawLine() {
		double x1 = 0;
		double y1 = b;
		double x2 = 1;
		double y2 = m * x2 + b;
		
		x1 = map(x1, 0, 1, 0, can.getWidth());
		y1 = map(y1, 0, 1, can.getHeight(), 0);
		x2 = map(x2, 0, 1, 0, can.getWidth());
		y2 = map(y2, 0, 1, can.getHeight(), 0);
		
		gc.strokeLine(x1, y1, x2, y2);
	}

	public static double map(double value, double min, double max, double nMin, double nMax) {
		return ((value - min) / (max - min)) * (nMax - nMin) + nMin;
	}
}