//TODO possibly unneeded
package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class simpleXboxController extends Joystick {
	public Joystick c;

	public simpleXboxController(joystickNum) {
		c = new Joystick(joystickNum);
	}

	public class xboxControllerInstance {
		double leftTrigger;
		double rightTrigger;
		int leftShoulder;
		int rightShoulder;
		double leftThumbstickHorizontal;
		double leftThumbstickVertical;
		int leftThumbstickPress;
		double rightThumbstickHorizontal;
		double rightThumbstickVertical;
		int rightThumbstickPress;
		double directionalPadHorizontal;
		int backButton;
		int startButton;
		int XButton;
		int YButton;
		int AButton;
		int BButton;

		//numbers may not be accurate, it must be checked with the real controller
		public xboxControllerInstance() {
			leftTrigger   = c.getRawAxis(3);
			rightTrigger  = c.getRawAxis(3);
			leftShoulder  = c.getRawButton(5);
			rightShoulder = c.getRawButton(6);
			leftThumbstickHorizontal  = c.getRawAxis(1);
			leftThumbstickVertical    = c.getRawAxis(2);
			leftThumbstickPress       = c.getRawButton(9);
			rightThumbstickHorizontal = c.getRawAxis(4);
			rightThumbstickVertical   = c.getRawAxis(5);
			rightThumbstickPress      = c.getRawAxis(10);
			directionalPadHorizontal  = c.getRawAxis(6);
			backButton  = c.getRawButton(7);
			startButton = c.getRawButton(8);
			XButton = c.getRawButton(3);
			YButton = c.getRawButton(4);
			AButton = c.getRawButton(1);
			BButton = c.getRawButton(2);
		}
	}
	public xboxControllerInstance getInstance() {
		return new xboxControllerInstance();
	}
}
