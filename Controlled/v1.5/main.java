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
  private boolean prevUpPressed = false;
  private boolean prevDownPressed = false;

  private double intakeSpeed = 0.5; // starting speed, adjustable via dpad left/right
  private static final double SPEED_STEP = 0.1;
  private boolean prevLeftPressed = false;
  private boolean prevRightPressed = false;

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
    boolean upPressed = gamepad1.dpad_up;
    boolean downPressed = gamepad1.dpad_down;
    boolean leftPressed = gamepad1.dpad_left;
    boolean rightPressed = gamepad1.dpad_right;

    if (upPressed && !prevUpPressed) {
      intakeState = (intakeState == IntakeState.FORWARD) ? IntakeState.OFF : IntakeState.FORWARD;
    }
    if (downPressed && !prevDownPressed) {
      intakeState = (intakeState == IntakeState.REVERSE) ? IntakeState.OFF : IntakeState.REVERSE;
    }
    prevUpPressed = upPressed;
    prevDownPressed = downPressed;

    // only adjust speed while a direction is actually active
    if (intakeState != IntakeState.OFF) {
      if (leftPressed && !prevLeftPressed) {
        intakeSpeed = Math.min(1.0, intakeSpeed + SPEED_STEP);
      }
      if (rightPressed && !prevRightPressed) {
        intakeSpeed = Math.max(0.0, intakeSpeed - SPEED_STEP);
      }
    }
    prevLeftPressed = leftPressed;
    prevRightPressed = rightPressed;

    switch (intakeState) {
      case FORWARD:
        activeIntake.setPower(intakeSpeed);
        break;
      case REVERSE:
        activeIntake.setPower(-intakeSpeed);
        break;
      default:
        activeIntake.setPower(0);
        break;
    }
  }
}