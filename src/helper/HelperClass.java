/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helper;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.CV_AA;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.CV_WHOLE_SEQ;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import static com.googlecode.javacv.cpp.opencv_core.cvCircle;
import static com.googlecode.javacv.cpp.opencv_core.cvCopy;
import static com.googlecode.javacv.cpp.opencv_core.cvCvtSeqToArray;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvLine;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CLOCKWISE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_EXTERNAL;
import com.googlecode.javacv.cpp.opencv_imgproc.CvConvexityDefect;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvApproxPoly;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvContourArea;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvConvexHull2;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvConvexityDefects;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

/**
 *
 * @author ghalib
 */
public class HelperClass {
    
    
    private static int forClick =0;
    
    private static CvPoint prev = null;
    
    public static void findContour(){
        double area, max_area = 0.0;
        CvSeq contours,tmp, contour=null;
        contours = new CvSeq();
        cvCopy(ImageUtils.thr_image, ImageUtils.temp_image1);
        cvFindContours(ImageUtils.temp_image1, 
                ImageUtils.temp_st, 
                contours,
                Loader.sizeof(CvContour.class), 
                CV_RETR_EXTERNAL, 
                CV_CHAIN_APPROX_SIMPLE,
                cvPoint(0, 0));
        for (tmp = contours; tmp!= null && !tmp.isNull(); tmp = tmp.h_next()) {
            area = Math.abs(cvContourArea(tmp, CV_WHOLE_SEQ, 0));
            if (area > max_area) {
			max_area = area;
			contour = tmp;
		}
        }
        
        if (contour != null) {
            contour = cvApproxPoly(contour,
                    Loader.sizeof(CvContour.class),
                    ImageUtils.contour_st,
                    CV_POLY_APPROX_DP,
                    2,
                    1);
            ImageUtils.contour = contour;                
        }
     
    }
    
    public static void findConvexHull(){
        CvSeq defects = new CvSeq();
        CvConvexityDefect[] defect_array;
        int i;
	int x = 0, y = 0;
	int dist = 0;
        ImageUtils.hull = null;
        if(ImageUtils.capture == null || ImageUtils.contour == null)
            return;
        ImageUtils.hull = cvConvexHull2(ImageUtils.contour, ImageUtils.hull_st, CV_CLOCKWISE, 0);
        
        if (ImageUtils.hull != null){
            defects = cvConvexityDefects(ImageUtils.contour, ImageUtils.hull,
					     ImageUtils.defects_st);
            if (defects!=null && defects.total()>0){
                defect_array = new CvConvexityDefect[defects.total()];
                Pointer p = opencv_core.cvAlloc(Loader.sizeof(CvConvexityDefect.class)*defects.total());
                
                cvCvtSeqToArray(defects, p);
                
                for (int j = 0; j < defect_array.length; j++) {
                    defect_array[j] = new CvConvexityDefect(p.position(j));
                }
                for (i = 0; i < defects.total() && i < 8; i++) {
                    x += defect_array[i].depth_point().x();
                    y += defect_array[i].depth_point().y();

                    ImageUtils.defects.add( cvPoint(defect_array[i].depth_point().x(),
                                            defect_array[i].depth_point().y()));
		}
                x /= defects.total();
                y /= defects.total();
               
                ImageUtils.num_defects = defects.total();
                ImageUtils.hand_center = cvPoint(x, y);
                
                for (i = 0; i < defects.total(); i++) {
				int d = (x - defect_array[i].depth_point().x()) *
					(x - defect_array[i].depth_point().x()) +
					(y - defect_array[i].depth_point().y()) *
					(y - defect_array[i].depth_point().y());

				dist += Math.sqrt(d);
                }
                ImageUtils.hand_radius = dist / defects.total();
                ImageUtils.hand_radius *=0.8;
            }
        } 
    }
    
    public static void findFingers(){
        int n;
	int i;
	
	CvPoint max_point = null;
	int dist1 = 0, dist2 = 0;

	ImageUtils.num_fingers = 0;
        if (ImageUtils.contour == null || ImageUtils.hull == null)
		return;
        n = ImageUtils.contour.total();
        CvPoint[] points = new CvPoint[n];
        
        Pointer p = opencv_core.cvAlloc(Loader.sizeof(CvPoint.class)*n);
        
        cvCvtSeqToArray(ImageUtils.contour, p);
        for (int j = 0; j < n; j++) {
                    points[j] = new CvPoint(p.position(j));
        }
        
        for (i = 0; i < n; i++) {
            int dist;
            int cx = ImageUtils.hand_center.x();
            int cy = ImageUtils.hand_center.y();
            max_point = points[i];
            
            
            dist = (cx - points[i].x()) * (cx - points[i].x()) +
		    (cy - points[i].y()) * (cy - points[i].y());
            if (dist < dist1 && dist1 > dist2 && max_point!=null &&max_point.x() != 0
		    && max_point.y() < cvGetSize(ImageUtils.image).height() - 10){
                if(Math.sqrt(dist)>1.5*ImageUtils.hand_radius && (max_point.y()<cy))
                {
                    if(ImageUtils.fingers.size() > ImageUtils.num_fingers)
                        ImageUtils.fingers.remove(ImageUtils.num_fingers) ;
                    ImageUtils.fingers.add(ImageUtils.num_fingers++, max_point);
                }
                
			if (ImageUtils.num_fingers >= 5)
				break;
            }
            dist2 = dist1;
	    dist1 = dist;
        }
        int a =0;
    }
    
    public static void display(){
        int i;
        if (ImageUtils.num_fingers <= 5){
            cvCircle(ImageUtils.image, ImageUtils.hand_center, 5, CV_RGB(255, 0, 255), 1, CV_AA, 0);
		cvCircle(ImageUtils.image, ImageUtils.hand_center, ImageUtils.hand_radius,
			 CV_RGB(255, 0, 0), 1, CV_AA, 0);
                
                double ref = (1.5*ImageUtils.hand_radius);
                
                cvCircle(ImageUtils.image, ImageUtils.hand_center, (int)ref,
			 CV_RGB(255, 0, 0), 1, CV_AA, 0);
                
                for (i = 0; i < ImageUtils.num_fingers; i++) {
                        double distSq = (ImageUtils.hand_center.x()-ImageUtils.fingers.get(i).x())*(ImageUtils.hand_center.x()-ImageUtils.fingers.get(i).x())
                                +(ImageUtils.hand_center.y()-ImageUtils.fingers.get(i).y())*(ImageUtils.hand_center.y()-ImageUtils.fingers.get(i).y());
			double dist = Math.sqrt(distSq);
                        if(dist > ref){
                            cvCircle(ImageUtils.image, ImageUtils.fingers.get(i), 10,
				 CV_RGB(0, 255, 0), 3, CV_AA, 0);

                            cvLine(ImageUtils.image, ImageUtils.hand_center, ImageUtils.fingers.get(i),
                                   CV_RGB(255, 255, 0), 1, CV_AA, 0);
                        }
                        
		}
                
                mouseControl();
               /* for (i = 0; i < ImageUtils.num_defects; i++) {
			cvCircle(ImageUtils.image, ImageUtils.defects.get(i), 2,
				 CV_RGB(200, 200, 200), 2, CV_AA, 0);
		}*/
        }
        
    }
    
    
    public static void mouseControl(){
        
        try {
            Robot robot = new Robot();
            
            switch(ImageUtils.num_fingers){
                case 1:
                    CvPoint point = ImageUtils.fingers.get(ImageUtils.num_fingers-1);                    
                    robot.mouseMove((int)(point.x()*ImageUtils.scaleFactorX), (int)(point.y()*ImageUtils.scaleFactorY));
                    break;
                case 2:
                   
                    if(getAngle() < 0.436332){
                        ImageUtils.leftClickCount++;
                        break;
                    }else{
                        if(ImageUtils.leftClickCount >3 && ImageUtils.leftClickCount < 7){
                             robot.mousePress(InputEvent.BUTTON1_MASK);
                             robot.mouseRelease(InputEvent.BUTTON1_MASK);
                             ImageUtils.leftClickCount=0;
                             break;
                         }
                         if(ImageUtils.leftClickCount >6 && ImageUtils.leftClickCount < 60){
                             robot.mousePress(InputEvent.BUTTON3_MASK);
                             robot.mouseRelease(InputEvent.BUTTON3_MASK);
                             ImageUtils.leftClickCount=0;
                             break;
                         } 
                         break;
                    }
                case 5:
                    if(prev == null){
                        prev = ImageUtils.hand_center;
                        
                    }else{
                        int y = ImageUtils.hand_center.y() - prev.y();
                        y=(int)(0.3*y);
                        robot.mouseWheel(y);
                    }
                    break;
                    
                default:
                    //ImageUtils.leftClickCount=0;
                    prev=null;
                    break;
                    
            }
            
            
        } catch (AWTException ex) {
            ex.printStackTrace();
        } 
    }
    
    private static double getAngle(){
        double a = ImageUtils.hand_center.x()-ImageUtils.fingers.get(0).x()+ ImageUtils.hand_center.y()-ImageUtils.fingers.get(0).y();
        double b = (ImageUtils.hand_center.x()-ImageUtils.fingers.get(1).x()+ ImageUtils.hand_center.y()-ImageUtils.fingers.get(1).y());
        double c = (ImageUtils.fingers.get(1).x()-ImageUtils.fingers.get(0).x()+ ImageUtils.fingers.get(1).y()-ImageUtils.fingers.get(0).y());
        double result = Math.acos( (a+b-c) / Math.sqrt(4*a*b) );
        return result;
    }
   
}
