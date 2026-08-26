// setup
package org.firstinspires.ftc.teamcode;

import android.graphics.Color;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

// teleop init
@TeleOp(name = "mecnumFullTest (Blocks to Java)")
public class mecnumFullTest extends LinearOpMode {

// def motors
  private DcMotor frontLeft;
  private DcMotor frontRight;
  private DcMotor backLeft;
  private DcMotor backRight;
  private DcMotor activeIntake;

// def sensors
  private NormalizedColorSensor colorSensor;

// set vars
  private enum IntakeState { OFF, FORWARD, REVERSE }
  private IntakeState intakeState = IntakeState.OFF;
  private boolean prevUpPressed = false;
  private boolean prevDownPressed = false;

  private double intakeSpeed = 0.5; // starting speed, adjustable via dpad left/right
  private static final double SPEED_STEP = 0.1;
  private boolean prevLeftPressed = false;
  private boolean prevRightPressed = false;

// init opmode
  @Override
  public void runOpMode() {
    frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
    frontRight = hardwareMap.get(DcMotor.class, "frontRight");
    backLeft = hardwareMap.get(DcMotor.class, "backLeft");
    backRight = hardwareMap.get(DcMotor.class, "backRight");
    activeIntake = hardwareMap.get(DcMotor.class, "activeIntake");

    colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorS");

    float[] hsvValues = new float[3];

    telemetry.addData("Status", "Initialized");
    telemetry.update();

    waitForStart();

// funcs
    MOTOR_SETTINGS();
    waitForStart();
    if (opModeIsActive()) {
      while (opModeIsActive()) {
        COLOR_SENSOR_RUN();
        MECANUM_DRIVE();
        INTAKE_MOTOR_RUN();
      }
    }
  }

// motor settings
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

// color sensor
  private void COLOR_SENSOR_RUN(); {
      NormalizedRGBA colors = colorSensor.getNormalizedColors();

      Color.colorToHSV(colors.toColor(), hsvValues);
      float hue = hsvValues[0];

      String colorName = "Unknown";

      if (colors.alpha > 0.01) {
          if (hue >= 0 && hue < 20 || hue >= 330) {
              colorName = "Red";
          } else if (hue >= 30 && hue < 65) {
              colorName = "Orange";
          } else if (hue >= 65 && hue < 130) {
              colorName = "Yellow";
          } else if (hue >= 130 && hue < 165) {
              colorName = "Green";
          } else if (hue >= 165 && hue < 260) {
              colorName = "Blue";
          }
      } else {
          colorName = "No object";
      }

      telemetry.addData("Color", colorName);
      telemetry.addData("Hue", hue);
      telemetry.update();

  sleep(50);
  }

// driving
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

// intake & output
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