/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

/**
 * FXML Controller class
 *
 * @author asmin
 */
public class ViewController implements Initializable {

    @FXML
    private Button start_btn;
    @FXML
    private ImageView currentFrame;

    private VideoCapture videoCapture = new VideoCapture();
    private ScheduledExecutorService timer;
    private boolean camerActive = false;
    private static int camerId = 0;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void startCamera(ActionEvent event) {
        if (!this.camerActive) {
            this.videoCapture.open(camerId);
            if (this.videoCapture.isOpened()) {
                this.camerActive = true;
                Runnable frameGrabber = new Runnable() {
                    @Override
                    public void run() {
                        Mat frame = grabFrame();
                        Image imageToShow = Utils.mat2Image(frame);
                        updateImageView(currentFrame, imageToShow);
                    }
                };
                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
                this.start_btn.setText("Stop Camera");
            } else {
                System.err.println("Impossible to open camera operation");
            }
        } else {
            this.camerActive = false;
            this.start_btn.setText("Start Camera");
            this.stopAcquisition();
        }

    }

    private Mat grabFrame() {
        Mat frame = new Mat();
        if (this.videoCapture.isOpened()) {
            try {
                this.videoCapture.read(frame);
                if (!frame.empty()) {
                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
                }
            } catch (Exception e) {
                // log the error
                System.err.println("Exception during the image elaboration: " + e);
            }

        }
        return frame;
    }

    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                System.err.println("Exception in stopping the frame capture, trying to release the camera now...");
            }
        }
        if (this.videoCapture.isOpened()) {
            this.videoCapture.release();
        }
    }

    private void updateImageView(ImageView view, Image image) {
        Utils.onFXThread(view.imageProperty(), image);
    }

    protected void setClosed() {
        this.stopAcquisition();
    }

}
