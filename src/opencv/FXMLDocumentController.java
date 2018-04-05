/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencv;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

/**
 * FXML Controller class
 *
 * @author asmin
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Button start_btn;
    @FXML
    private ImageView currentFrame;

    private VideoCapture videoCapture = new VideoCapture();
    private ScheduledExecutorService timer;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void startCamera(ActionEvent event) {
        this.videoCapture.open(1);
        if (this.videoCapture.isOpened()) {
            Runnable frameGrabber = new Runnable() {
                @Override
                public void run() {
                    Mat frame = new Mat();
                    videoCapture.read(frame);
                    Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
                    MatOfByte buffer = new MatOfByte();
                    Imgcodecs.imencode(".png", frame, buffer);
                    Image imageToShow = new Image(new ByteArrayInputStream(buffer.toArray()));
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            currentFrame.setImage(imageToShow);
                        }
                    });
                }
            };
            this.timer = Executors.newSingleThreadScheduledExecutor();
            this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
            this.start_btn.setText("Stop Camera");
        } else {
            this.start_btn.setText("Start Camera");
            stopCamera();
        }
    }

    private void stopCamera() {
        this.videoCapture.release();
    }

}
