package application;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import application.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FaceDetectionController
{
	// FXML buttons
	@FXML
	private Button cameraButton;
	
	@FXML
	private ImageView originalFrame;

	@FXML
	private Button irisButton;
	
	@FXML
	private ImageView irisFrame;

	
	private ScheduledExecutorService timer;
	
	private VideoCapture capture;
	
	private boolean cameraActive;
	
	
	 CascadeClassifier faceCascade;
	
	 CascadeClassifier irisCascade;
	
	private int absoluteFaceSize;
	
	
	protected void init()
	{
		this.capture = new VideoCapture();
		this.faceCascade = new CascadeClassifier("visionResources/haarcascade_frontalface_alt.xml");
		this.irisCascade = new CascadeClassifier("visionResources/haarcascade_eye.xml");
		this.absoluteFaceSize = 0;
	}
	
	@FXML
	protected void startCamera()
	{	
		if (!this.cameraActive)
		{
			
			// start the video capture
			this.capture.open(0);
			
			// is the video stream available?
			if (this.capture.isOpened())
			{
				this.cameraActive = true;
				
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {
					
					@Override
					public void run()
					{
						// effectively grab and process a single frame
						Mat frame = grabFrame(faceCascade);
						// convert and show the frame
						Image imageToShow = Utils.mat2Image(frame);
						updateImageView(originalFrame, imageToShow);
					}
				};
				
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
				
				// update the button content
				this.cameraButton.setText("Stop Camera");
				this.irisButton.setDisable(true);
			}
			else
			{
				// log the error
				System.err.println("Failed to open the camera connection...");
			}
		}
		else
		{
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.cameraButton.setText("Start Camera");
			this.irisButton.setDisable(false);
			this.stopAcquisition();
		}
	}
	
	@FXML
	protected void irisCamera()
	{	
		if (!this.cameraActive)
		{
			
			// start the video capture
			this.capture.open(0);
			
			// is the video stream available?
			if (this.capture.isOpened())
			{
				this.cameraActive = true;
				
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {
					
					@Override
					public void run()
					{
						// effectively grab and process a single frame
						Mat frame = grabFrame(irisCascade);
						// convert and show the frame
						Image imageToShow = Utils.mat2Image(frame);
						updateImageView(irisFrame, imageToShow);
					}
				};
				
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
				
				// update the button content
				this.irisButton.setText("Capture Iris");
				this.cameraButton.setDisable(true);
			}
			else
			{
				// log the error
				System.err.println("Failed to open the camera connection...");
			}
		}
		else
		{
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.irisButton.setText("Start Iris");
			this.cameraButton.setDisable(false);
			this.stopAcquisition();
		}
	}
	
	
	private Mat grabFrame(CascadeClassifier cascade)
	{
		Mat frame = new Mat();
		
		// check if the capture is open
		if (this.capture.isOpened())
		{
			try
			{
				// read the current frame
				this.capture.read(frame);
				
				// if the frame is not empty, process it
				if (!frame.empty())
				{
					// face detection
					this.detectAndDisplay(frame,cascade);
				}
				
			}
			catch (Exception e)
			{
				// log the (full) error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		
		return frame;
	}
	
	
	private void detectAndDisplay(Mat frame,CascadeClassifier cascade)
	{
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();
		
		// convert the frame in gray scale
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);
		
		// compute minimum face size (20% of the frame height, in our case)
		if (this.absoluteFaceSize == 0)
		{
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0)
			{
				
				this.absoluteFaceSize = Math.round(height * 0.2f);
			}
		}
		
		// detect faces
		cascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
				
		// each rectangle in faces is a face: draw them!
		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++)
			Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 0, 255), 3);
		if(cascade==faceCascade)
			saveImage(frame,"face");
		else
			saveImage(frame,"iris");
	}


	

	private void stopAcquisition()
	{
		if (this.timer!=null && !this.timer.isShutdown())
		{
			try
			{
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (this.capture.isOpened())
		{
			// release the camera
			this.capture.release();
		}
	}
	

	private void updateImageView(ImageView view, Image image)
	{
		Utils.onFXThread(view.imageProperty(), image);
	}
	

	protected void setClosed()
	{
		this.stopAcquisition();
	}
	
	
	public void saveImage(Mat frame,String s) {
		String file = "C:/Users/Deepak Gupta/Desktop/Atm Project/snapshot_"+s+".jpg";
		
		Imgcodecs.imwrite(file,frame);
	}
	
}
