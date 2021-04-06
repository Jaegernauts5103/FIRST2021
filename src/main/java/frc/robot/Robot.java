/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

/* everything executes in this order:
 * robotInit
 * autonomousInit
 * autonomousPeriodic
 * teleopInit
 * teleopPeriodic
 */

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.*;
import edu.wpi.first.networktables.*;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Core;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.AraryList;


/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {
  //private final Timer m_timer = new Timer();
  public DifferentialDrive m_drive;
  public SpeedControllerGroup m_left;
  public SpeedControllerGroup m_Right;
  public Joystick m_leftStick;
  public Joystick m_rightStick;
  public Joystick xboxController;
  public Talon ballShooter;
  public Talon m_FrontLeft;
  public Talon m_rearLeft;
  public Talon m_frontRight;
  public Talon m_rearRight;
  public Talon spinnerMotor;
  public Talon intake;
  public Talon feeder;
  public Talon climb;
  public UsbCamera cam1;
  public UsbCamera cam2;
  public NetworkTableEntry camSelect;

  public Mat sourceImg = new Mat(); //input from whatever the camera says
  public Mat outputImg = new Mat(); //same thing but blurry and HSV
  public Mat workingImg = new Mat(); //for vision processing output

  public boolean isRedPath;
  public int currBall;
  public double timerValue;
  public Timer autoTimer;

  public int intakeActivationPosition; //TODO this needs to be found
  public Scalar low = new Scalar(25, 50, 50); //TODO this is an estimation
  public Scalar high = new Scalar(35, 255, 255);//TODO estimation
  public Scalar markerLow = new Scalar(20, 25, 25);
  public scalar markerHigh = new Scalar(25, 180, 180);
  public double minBallArea = 1;
  public double minMarkerArea = 1;

  @Override
  public void robotInit() { //TODO what are the "m_"?
    m_leftStick    = new Joystick(0);
    m_rightStick   = new Joystick(1);
    xboxController = new Joystick(2);

    m_FrontLeft    = new Talon(2);
    m_rearLeft     = new Talon(3);
    m_left         = new SpeedControllerGroup(m_FrontLeft, m_rearLeft);

    m_frontRight   = new Talon(1);
    m_rearRight    = new Talon(0);
    m_Right        = new SpeedControllerGroup(m_frontRight, m_rearRight);

    m_drive        = new DifferentialDrive(m_left, m_Right);

    intake         = new Talon(6);
    climb          = new Talon(4);
    feeder         = new Talon(7);
    spinnerMotor   = new Talon(8);
    shooter        = new Talon(5);

    cam1           = CameraServer.getInstance().startAutomaticCapture(0);
    cam2           = CameraServer.getInstance().startAutomaticCapture(1);
    camSelect      = NetworkTableInstance.getDefault().getTable("")
      .getEntry("CameraSelection");

    cam1.setResolution(640,480);
    cam2.setResolution(640,480);
    CvSink cvSink = CameraServer.getInstance().getVideo(0);
    CvSource outputStream = CameraServer.getInstance()
      .putVideo("Blur", 640, 480);
    //continuously processes the image so we see the targets not the field
    new Thread(() -> {
      UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
      camera.setResolution(640, 480);

      CvSink cvSink = CameraServer.getInstance().getVideo();
      CvSource outputStream = CameraServer.getInstance().putVideo("Blur", 640, 480);

      while(!Thread.interrupted()) {
        if (cvSink.grabFrame(sourceImg) == 0) {
          continue;
        }
        Imgproc.cvtColor(sourceImg, outputImg, Imgproc.COLOR_BGR2HSV);
        Core.inRange(outputImg, low, high, outputImg);
        outputStream.putFrame(outputImg);
      }
    }).start();
  }
  //will only run once before teleop
  @Override
  public void teleopInit( ) {}
  //will run over and over agaid during teleop
  @Override
  public void teleopPeriodic() {
    boolean intakeButtonIsPressed = xboxController.getRawButton(4);
    double rightTrigger = xboxController.getRawAxis(3);
    boolean AButton = xboxController.getRawButton(1);
    boolean BButton = xboxController.getRawButton(3);
    boolean XButton = xboxController.getRawButton(2);
    boolean RT = m_rightStick.getRawButton(1);
    boolean XLT = xboxController.getRawButton(5);
    boolean XRT = xboxController.getRawButton(6);
    boolean select  = xboxController.getRawButton(8);

    m_drive.tankDrive(-m_leftStick.getRawAxis(1), -m_rightStick.getRawAxis(1));
    if (intakeButtonIsPressed) {
      intake.setSpeed(1);
    } else {
      intake.setSpeed(0);
    }
    ballShooter.set(rightTrigger);
    if (AButton) {
      spinnerMotor.setSpeed(1);
    } else {
      spinnerMotor.setSpeed(0);
    }
    if (BButton) {
      feeder.setSpeed(1);
      //spinnerMotor.setSpeed(1);
    }
    else {
      feeder.setSpeed(0);
      //spinnerMotor.setSpeed(0);
    }
    if (XButton) {
      feeder.setSpeed(1);
      intake.setSpeed(1);
    }
    if (RT){
      camSelect.setString(cam2.getName()); //TODO organize cam1 and 2
    }else{
      camSelect.setString(cam1.getName());
    }
    if (XLT){
      climb.setSpeed(0.1);
    }else if (XRT) {
      climb.setSpeed(0.45);
    }else {
      climb.setSpeed(0);
    }
    if (select){
      spinnerMotor.setSpeed(1);
    }else {
      spinnerMotor.setSpeed(0);
    }
}
  @Override
  public void autonomousInit() {
    autoTimer = new Timer();
    autoTimer.start();
    m_drive.tankDrive(0.5, 0.5);
    currball = 0;
  }
  @Override
  public void autonomousPeriodic() {
    Point ballPos;
    //finds the 3 balls. If the 3 balls have been found, finds the exit
    if (currBall != 3) ballPos = findTarget;
    else ballPos = findEnd();
    //if a ball is close enough to pick up...
    if (tryPickUpBall(ballPos)){
      if (currBall == 0){ //if it's the first ball...
        double time = autoTimer.get(); //used to determine red or blue path
        if (time < 2) isRedPath = true;
        else isRedPath = false;
      } else if (currBall == 1){
        if (isRedPath){
          m_drive.tankDrive(0.5, 0.25);
          Timer.delay(1);
        } else {
          m_drive.tankDrive(0.1, 1);
          Time.delay(1);
        }
      } else if(currBall == 2){
        if (isRedPath){
          m_drive.tankDrive(0.1, 1);
          Time.delay(1);
        } else {
          m_drive.tankDrive(0.5, 0.25);
          Timer.delay(1);
        }
      } else {
        low = markerLow;
        high = markerHigh;
        if (isRedPath) {
          m_drive.tankDrive(-0.25, -.5);
          Timer.delay(1);
        } else {
          m_drive.tankDrive(-0.5, -0.25);
          Timer.delay(1);
        }
      }
    }
    if (ballPos == null) m_drive.tankDrive(0, 0); //safety
    fullSpeed = 0.5;
    turnBias = 0.5;
    if (ballPos.x < 640/2){
      float missing = (float)ballPos.x/640*2*turnBias;
      m_drive.tankDrive(fullSpeed-missing, fullSpeed);
    } else {
      float missing = (float)(640-ballPos.x)/640*2*turnBias;
      m_drive.tankDrive(fullSpeed, fullSpeed-missing);
    }
  }

  public boolean tryPickUpBall(Point ballPos){
    while (ballPos.y >= intakeActivationPosition){
      intake.setSpeed(1);
      m_drive.tankDrive(0.5, 0.5);
      Timer.delay(1);
      ballPos = findTarget();
    }
    intake.setSpeed(0);
  }

  public Point findEnd(){
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    List<Point> points = new ArrayList<Point>();
    Imgproc.findContours(outputImg, contours, workingImg, Imgproc.RETR_EXTERNAL,
      Imgproc.CHAIN_APPROX_SIMPLE);
    for (int i = 0; i < contours.size(); i++) {
      double contourArea = Imgproc.contourArea(contours.get(i));
      if (contourArea < minMarkerArea) continue;
      Rect boundRect = Imgproc.boundingRect(contours.get(i));
      float ratio = (float)boundRect.width/boundRect.height;
      if (ratio > 0.90) continue;
      float solidity = (float)Imgproc.contourArea(contours.get(i))
        /(boundRect.width*boundRect.height);
      if (solidity < 0.8) continue;
      double centerX = boundRect.x + (boundRect.width / 2);
      double centerY = boundRect.y + (boundRect.height / 2);
      points.add(new Point(centerX, centerY));
    }
    size = points.size();
    if (size > 2) System.err.println("too many targets to find the" +
      "the end");
    int sumx = 0;
    int sumy = 0;
    for (i=0; i<size; i++){
      sumx += points.get(i).x;
      sumy += points.get(i).y;
    }
    return new Point(sumx/size, sumy/size);
  }

  public Point findTarget(){
    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
    Imgproc.findContours(outputImg, contours, workingImg, Imgproc.RETR_EXTERNAL,
      Imgproc.CHAIN_APPROX_SIMPLE);
    for (int i = 0; i < contours.size(); i++) {
      double contourArea = Imgproc.contourArea(contours.get(i));
      if (contourArea < minBallArea) continue;
      Rect boundRect = Imgproc.boundingRect(contours.get(i));
      float ratio = (float)boundRect.width/boundRect.height;
      if (ratio < 0.90 || ratio > 1.10) continue;
      float solidity = (float)Imgproc.contourArea(contours.get(i))
        /(boundRect.width*boundRect.height);
      if (solidity < 0.7 || solidity > 0.9) continue;
      double centerX = boundRect.x + (boundRect.width / 2);
      double centerY = boundRect.y + (boundRect.height / 2);
      return new Point(centerX, centerY);
    }
  }
}
