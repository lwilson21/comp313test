package edu.luc.etl.cs313.android.shapes.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import java.util.List;

import edu.luc.etl.cs313.android.shapes.model.*;

/**
 * A Visitor for drawing a shape to an Android canvas.
 */
public class Draw implements Visitor<Void> {

	private final Canvas canvas;

	private final Paint paint;

	public Draw(final Canvas canvas, final Paint paint) {
		this.canvas = canvas; 
		this.paint = paint;
		paint.setStyle(Style.STROKE);
	}

	@Override
	public Void onCircle(final Circle c) {
		canvas.drawCircle(0, 0, c.getRadius(), paint);
		return null;
	}

	@Override
	public Void onStrokeColor(final StrokeColor c) {
		int original = paint.getColor();
		this.paint.setColor(c.getColor());
		c.getShape().accept(this);
		this.paint.setColor(original);
		return null;
	}

	@Override
	public Void onFill(final Fill f) {
		Style originalStyle = paint.getStyle();
		this.paint.setStyle(Style.FILL_AND_STROKE);
		if(f.getShape().getClass().equals(Group.class)){
			Group g = ((Group)f.getShape());
			g.accept(this);
		}
		this.paint.setStyle(originalStyle);
		return null;
	}

	@Override
	public Void onGroup(final Group g) {
		for (final Shape s : g.getShapes()){
			s.accept(this);
		}
		return null;
	}

	@Override
	public Void onLocation(final Location l) {
		int x = l.getX();
		int y = l.getY();

		canvas.translate(x, y);
		l.getShape().accept(this);
		canvas.translate(-x,-y);
		return null;
	}

	@Override
	public Void onRectangle(final Rectangle r) {
		canvas.drawRect(0,0, r.getWidth(), r.getHeight(), paint);
		return null;
	}

	@Override
	public Void onOutline(Outline o) {
		Style originalStyle = paint.getStyle();
		this.paint.setStyle(Style.STROKE);
		o.getShape().accept(this);
		this.paint.setStyle(originalStyle);
		return null;
	}

	@Override
	public Void onPolygon(final Polygon s) {
		final float[] pts = new float[(4*s.getPoints().size())];
		int i = 0;	// iterator for loop
		// need to save first point separately using if else statement
		for (Point p : s.getPoints()){
			pts[i] = p.getX();
			pts[i+1] = p.getY();
			if (i>=2 && i<4*s.getPoints().size()-2) {
				pts[i + 2] = p.getX();
				pts[i + 3] = p.getY();
				i=i+4;
			} else {
				i = i +2;
			}
		}
		// need to set last point as first point to complete drawing
		pts[4*s.getPoints().size()-2] = pts[0];	// last point x should be same as first
		pts[4*s.getPoints().size()-1] = pts[1];	// last point y should be same as first
		canvas.drawLines(pts, paint);
		return null;


	}
}
