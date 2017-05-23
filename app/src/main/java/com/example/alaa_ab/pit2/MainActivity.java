package com.example.alaa_ab.pit2;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.MotionEvent;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends AppCompatActivity {

    public static int PointCounter=2; // counter for point's imageview id
    public static  ArrayList<Point> Points; // list of current points
    RelativeLayout pointsLayout;
    RelativeLayout pathLayout;
    public static boolean drawn=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display mdisp = getWindowManager().getDefaultDisplay();
        pointsLayout= (RelativeLayout) (findViewById(R.id.layout2));
        pathLayout= (RelativeLayout) (findViewById(R.id.layout1));

        // define the button onclicklistener -- by clicking you add a point
        Button addButton = (Button) findViewById(R.id.button);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Point onePoint = new Point();
                cretePoint(400,800);
                onePoint.setX(400);
                onePoint.setY(800);
                onePoint.setMyImageId(PointCounter);
                Points.add(onePoint);
                PointCounter=PointCounter+2;
                Collections.sort(Points, new PointComparator());
                changePath();

            }
        });


        Points = new ArrayList<Point>();
        View pointView = new View(this);
        pointsLayout.addView(pointView);
        createFivePoints();


    }

    //function that is called in oncreate to create the five initial points
    private void createFivePoints()
    {
        int topMargin=600;
        int leftMargin=200;
        for (int i = 1 ; i<6 ; i++)
        {
            Point onePoint = new Point();
            cretePoint(topMargin,leftMargin*i);
            onePoint.setMyImageId(PointCounter);
            onePoint.setX(leftMargin*i);
            onePoint.setY(topMargin);
            Points.add(onePoint);
            PointCounter=PointCounter+2;
        }
    }



    // a function to create a point and add it to the layout
    public void cretePoint( int topMargin , int leftMargin)
    {
        ImageView myImage  = new ImageView(this);
        myImage.setImageResource(R.drawable.dot);
        RelativeLayout.LayoutParams layoutParams =  new RelativeLayout.LayoutParams(150, 150);
        layoutParams.topMargin=topMargin;
        layoutParams.leftMargin=leftMargin;
        myImage.setLayoutParams(layoutParams);
        // set on touch listener to the point
        myImage.setOnTouchListener(new ChoiceTouchListener());
        myImage.setId(PointCounter);
        //add the point to the view
        pointsLayout.addView(myImage);

    }


    public  class ChoiceTouchListener implements View.OnTouchListener {
        private int _xDelta;
        private int _yDelta;

        public boolean onTouch(View view, MotionEvent event) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                            .getLayoutParams();
                    layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);
                    int id=view.getId();
                    Point point = FindPointById(id);
                    point.setX(X - _xDelta);
                    point.setY(Y - _yDelta);
                    break;
            }
            Collections.sort(Points, new PointComparator());
            changePath();
            return true;
        }
    }

    // a function to find a point by the id of it's imageview
    public Point FindPointById(int id){
        Point point=null;
        for(int i=0;i<Points.size();i++){
            if(Points.get(i).getMyImageId()==id){
                point= Points.get(i);
                break;
            }
        }
        return point;
    }

    // a comparator to sort the array of point by it's x cordinate
    class PointComparator implements Comparator<Point> {
        public int compare(Point Point1, Point Point2) {
            return Point1.getX() - Point2.getX();
        }
    }


    // a function to remove the old path from view and create a new one for the array of points
    //had trouble getting the right cordinations to build the path
    public void changePath()
    {
        pathLayout.removeAllViews();
        Paint paint = new Paint();
        paint.setColor(Color.rgb(255, 153, 51));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        Bitmap bitmap = Bitmap.createBitmap((int) getWindowManager()
                .getDefaultDisplay().getWidth(), (int) getWindowManager()
                .getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        ImageView imageview=new ImageView(this);
        imageview.setImageBitmap(bitmap);
        Canvas canvas = new Canvas(bitmap);
        Path path =new Path();
        path.moveTo(Points.get(0).getX(), Points.get(0).getY());

        for(int i=1;i<Points.size();i++)
        {
            path.lineTo(Points.get(i).getX(), Points.get(i).getY());

        }
        canvas.drawPath(path, paint);
        pathLayout.addView(imageview);
        
    }


    // used this to get the location , because without this location is always zero
    // location doesn't give the right position of the imageview
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        if(hasFocus && !drawn) {
            int[] location = new int[2];
            ImageView im;
            for(int i=0;i<Points.size();i++){
                im=(ImageView)findViewById(Points.get(i).getMyImageId()) ;
                im.getLocationInWindow(location);
                Points.get(i).setX(location[0]);
                Points.get(i).setY(location[1]);
            }
            addPath();
        }
    }

    // this function is called is called in onWindowFocusChanged after we created the initial ponts
    // to create the path
    //had trouble getting the right cordinations to build the path
    public void addPath()
    {

        Bitmap bitmap = Bitmap.createBitmap((int) getWindowManager()
                .getDefaultDisplay().getWidth(), (int) getWindowManager()
                .getDefaultDisplay().getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        ImageView imageview=new ImageView(this);
        imageview.setImageBitmap(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.rgb(255, 153, 51));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);
        Path path= new Path();
        path.moveTo(Points.get(0).getX(), Points.get(0).getY());
        for(int i=1;i<Points.size();i++)
        {
            path.lineTo(Points.get(i).getX(), Points.get(i).getY());

        }
        canvas.drawPath(path, paint);
        pathLayout.addView(imageview);


    }




}


