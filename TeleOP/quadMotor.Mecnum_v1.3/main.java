package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "mecnumFullTest (Blocks to Java)")
public class mecnumFullTest extends LinearOpMode {

  private DcMotor frontLeft;
  private DcMotor frontRight;
  private DcMotor backLeft;
  private DcMotor backRight;
  private DcMotor activeIntake;

  private enum IntakeState { OFF, FORWARD, REVERSE }
  private IntakeState intakeState = IntakeState.OFF;
  private boolean prevLeftTriggerPressed = false;
  private boolean prevRightTriggerPressed = false;

  private boolean fourWheelDrive = true;
  private boolean prevDriveTogglePressed = false;

  @Override
  public void runOpMode() {
    frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
    frontRight = hardwareMap.get(DcMotor.class, "frontRight");
    backLeft = hardwareMap.get(DcMotor.class, "backLeft");
    backRight = hardwareMap.get(DcMotor.class, "backRight");
    activeIntake = hardwareMap.get(DcMotor.class, "activeIntake");

    MOTOR_SETTINGS();
    waitForStart();
    if (opModeIsActive()) {
      while (opModeIsActive()) {
        TOGGLE_4WHEELDRIVE();
        MECANUM_DRIVE();
        INTAKE_MOTOR_RUN();
      }
    }
  }

  private void MOTOR_SETTINGS() {
    frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    frontLeft.setDirection(DcMotor.Direction.REVERSE);
    frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    frontRight.setDirection(DcMotor.Direction.REVERSE);
    backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    backLeft.setDirection(DcMotor.Direction.REVERSE);
    backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    backRight.setDirection(DcMotor.Direction.REVERSE);
    activeIntake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    activeIntake.setDirection(DcMotor.Direction.REVERSE);
  }

  private void TOGGLE_4WHEELDRIVE() {
    float toggleDrive = gamepad1.right_bumper ? 1.0f : 0.0f; // placeholder, see note below
    boolean drivePressed = toggleDrive >= 0.1;

    if (drivePressed && !prevDriveTogglePressed) {
      fourWheelDrive = !fourWheelDrive;
    }
    prevDriveTogglePressed = drivePressed;
  }

  private void MECANUM_DRIVE() {
    float forwardBack;
    float strafe;
    float turn;
    float leftFrontPower;
    float rightFrontPower;
    float leftBackPower;
    float rightBackPower;

    forwardBack = -gamepad1.left_stick_y;
    strafe = gamepad1.left_stick_x;
    turn = gamepad1.right_stick_x;
    leftFrontPower = forwardBack + strafe + turn;
    rightFrontPower = (forwardBack - strafe) - turn;
    leftBackPower = (forwardBack - strafe) + turn;
    rightBackPower = (forwardBack + strafe) - turn;

    frontLeft.setPower(leftFrontPower);
    frontRight.setPower(rightFrontPower);
    backLeft.setPower(leftBackPower);
    backRight.setPower(rightBackPower);
  }

  private void INTAKE_MOTOR_RUN() {
    boolean leftTriggerPressed = gamepad1.left_trigger > 0.1;
    boolean rightTriggerPressed = gamepad1.right_trigger > 0.1;

    if (leftTriggerPressed && !prevLeftTriggerPressed) {
      intakeState = (intakeState == IntakeState.FORWARD) ? IntakeState.OFF : IntakeState.FORWARD;
    }
    if (rightTriggerPressed && !prevRightTriggerPressed) {
      intakeState = (intakeState == IntakeState.REVERSE) ? IntakeState.OFF : IntakeState.REVERSE;
    }

    prevLeftTriggerPressed = leftTriggerPressed;
    prevRightTriggerPressed = rightTriggerPressed;

    switch (intakeState) {
      case FORWARD:
        activeIntake.setPower(1.0);
        break;
      case REVERSE:
        activeIntake.setPower(-1.0);
        break;
      default:
        activeIntake.setPower(0);
        break;
    }
  }
}