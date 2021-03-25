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
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.*;
import edu.wpi.first.networktables.*;



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

  public Talon shooter;
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

  @Override
  public void robotInit() {
    m_leftStick    = new Joystick(0);
    m_rightStick   = new Joystick(1);
    xboxController = new Joystick(2);

    intake         = new Talon(6); 
    m_FrontLeft    = new Talon(2);
    m_rearLeft     = new Talon(3);
    m_left         = new SpeedControllerGroup(m_FrontLeft, m_rearLeft);
    m_frontRight   = new Talon(1);
    m_rearRight    = new Talon(0);
    m_Right        = new SpeedControllerGroup(m_frontRight, m_rearRight);
    m_drive        = new DifferentialDrive(m_left, m_Right);
    climb          = new Talon(4);
    feeder         = new Talon(7);
    spinnerMotor   = new Talon(8);
    shooter        = new Talon(5);

    cam1           = CameraServer.getInstance().startAutomaticCapture(0);
    cam2           = CameraServer.getInstance().startAutomaticCapture(1);
    camSelect      = NetworkTableInstance.getDefault().getTable("").getEntry("CameraSelection");
  }
  public void wait(int mili) {
    try {
      Thread.sleep(mili);
    } catch ( InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public void teleopInit( ) {
    
  }

  @Override
  public void teleopPeriodic() {
    //boolean Y = xboxController.getRawButton(4);
    m_drive.tankDrive(-m_leftStick.getRawAxis(1), -m_rightStick.getRawAxis(1));
    if (xboxController.getRawButton(4)) {
      intake.setSpeed(1);
    } else {
      intake.setSpeed(0);
    }
    double rightTrigger = xboxController.getRawAxis(3);
    if (rightTrigger > 0.5) {
      shooter.setSpeed(.95);
    } else {
      shooter.setSpeed(0);
    }
    shooter.set(xboxController.getRawAxis(3));
    boolean A = xboxController.getRawButton(1);
    if (A) {
      spinnerMotor.setSpeed(1);
    } else {
      spinnerMotor.setSpeed(0);
    }
    //double leftTrigger = xboxController.getRawAxis(2);{
   // 
    //}
    boolean B = xboxController.getRawButton(3);
    if (B) {
      feeder.setSpeed(1);
      //spinnerMotor.setSpeed(1);
    }
    else {
      feeder.setSpeed(0);
      //spinnerMotor.setSpeed(0);
    }
    boolean X = xboxController.getRawButton(2);
    if (X) {
      feeder.setSpeed(1);
      intake.setSpeed(1);
    }
    
    boolean RT = m_rightStick.getRawButton(1); //TODO make sure this is the right button
    if (RT){
      camSelect.setString(cam2.getName()); //TODO organize cam1 and 2
    }else{
      camSelect.setString(cam1.getName());
    }

    boolean XRT = xboxController.getRawButton(6);
    boolean XLT = xboxController.getRawButton(5);
    if (XLT){
      climb.setSpeed(0.1);
    }else if (XRT) {
      climb.setSpeed(0.45);
    }else {
      climb.setSpeed(0);
    }

    boolean select  = xboxController.getRawButton(8);
    if (select){
      spinnerMotor.setSpeed(1);
    }else {
      spinnerMotor.setSpeed(0);
    }
}

  @Override
  public void autonomousInit() {
    m_drive.tankDrive(.5, .5);
    Timer.delay(4);
    m_drive.tankDrive(0, 0);

  }

  @Override
  public void autonomousPeriodic() {}
    
}
