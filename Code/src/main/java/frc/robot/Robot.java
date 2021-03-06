/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;
import edu.wpi.first.wpilibj.XboxController;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import org.opencv.videoio.VideoCapture;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.PWMVenom;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSource;
import edu.wpi.cscore.VideoMode.PixelFormat;
import edu.wpi.first.cameraserver.CameraServer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private static final int leftDriveMasterCANId = 4;
  private static final int leftDriveSlaveCANId = 3;
  private static final int rightDriveSlaveCANId = 2;
  private static final int rightDriveMasterCANId = 1;
  private static final int ballLauncherChannel = 1;

  private TalonSRX leftDriveMasterController = new TalonSRX(leftDriveMasterCANId);
  private TalonSRX rightDriveMasterController = new TalonSRX(rightDriveMasterCANId);
  private VictorSPX leftDriveSlaveController = new VictorSPX(leftDriveSlaveCANId);
  private VictorSPX rightDriveSlaveController = new VictorSPX(rightDriveSlaveCANId);
  private PWMVictorSPX ballLauncherController = new PWMVictorSPX(ballLauncherChannel);
  XboxController driverJoystick = new XboxController(0); 
  XboxController ballJoystick = new XboxController(1);

  /*
  private Spark FrontLeftWheel = new Spark(FrontLeftWheelChannel);
  private Spark FrontRightWheel = new Spark(FrontRightWheelChannel);
  private Spark RearLeftWheel = new Spark(RearLeftWheelChannel);
  private Spark RearRightWheel = new Spark(RearRightWheelChannel);
  */
  public void directUSBVision()
	{
		// From example project "Simple Vision":
		/**
		 * Uses the CameraServer class to automatically capture video from a USB webcam
		 * and send it to the FRC dashboard without doing any vision processing. This
		 * is the easiest way to get camera images to the dashboard. Just add this to
		 * the robotInit() method in your program.
		 */
    //CameraServer.getInstance().startAutomaticCapture();
    //UsbCamera frontCamera = new UsbCamera("frontCam", 0);
    //VideoMode bestVideoMode = new VideoMode(PixelFormat.kUnknown, 640, 480, 30);
    //frontCamera.setVideoMode(bestVideoMode);
    //frontCamera.setResolution(640, 480);
    //frontCamera.setFPS(30);

		CameraServer server = CameraServer.getInstance();
    //server.startAutomaticCapture(frontCamera);
    server.startAutomaticCapture();
	}



  
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    directUSBVision();
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    // the slave controllers are the secondary motor controllers for the 2nd motor in each gearbox
    // this sets them to mirror each other and behave identically
    // (never drive at different speeds or in different directions)
    // TODO move to teleopInit?
    leftDriveSlaveController.set(ControlMode.Follower, leftDriveMasterCANId);
    rightDriveSlaveController.set(ControlMode.Follower, rightDriveMasterCANId);

    /*
    CTRE Docs: "Follower motor controllers have separate neutral modes than their masters, 
    so you must choose both. Additionally, you may want to mix your neutral modes to 
    achieve a partial electric brake when using multiple motors."
    https://phoenix-documentation.readthedocs.io/en/latest/ch13_MC.html

    This makes the motor controllers brake/stop when the driver stops driving,
    instead of coasting.
    */
    // TODO move to teleopInit?
    leftDriveMasterController.setNeutralMode(NeutralMode.Brake);
    leftDriveSlaveController.setNeutralMode(NeutralMode.Brake);
    rightDriveMasterController.setNeutralMode(NeutralMode.Brake);
    rightDriveSlaveController.setNeutralMode(NeutralMode.Brake);


    if(Math.abs(driverJoystick.getY(Hand.kLeft)) > 0.2)
		{
			leftDriveMasterController.set(ControlMode.PercentOutput, -driverJoystick.getY(Hand.kLeft));
			//leftDriveSlaveController.set(ControlMode.Follower, leftDriveMasterCANId);
		}
		else
		{
			leftDriveMasterController.set(ControlMode.PercentOutput, 0.0);
			//leftDriveSlaveController.set(ControlMode.Follower, leftDriveMasterCANId);
		}
		
		if(Math.abs(driverJoystick.getY(Hand.kRight)) > 0.2)
		{
      rightDriveMasterController.set(ControlMode.PercentOutput, driverJoystick.getY(Hand.kRight));
			//rightDriveSlaveController.set(ControlMode.Follower, rightDriveMasterCANId);
		}
		else
		{
			rightDriveMasterController.set(ControlMode.PercentOutput, 0.0);
			//rightDriveSlaveController.set(ControlMode.Follower, rightDriveMasterCANId);
    }
    //Makes robot ball controller move
    if(ballJoystick.getAButton()){
      ballLauncherController.set(.5);
  
    }
    else if(ballJoystick.getBButton()){
      ballLauncherController.set(2);
    }
    else{
      ballLauncherController.set(0);
    }
  }
  
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
